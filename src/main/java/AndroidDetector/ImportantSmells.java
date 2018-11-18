package AndroidDetector;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.support.SAXTarget;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import sun.rmi.runtime.NewThreadAction;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.*;
import UTIL.ReusoStringData;

public class ImportantSmells {
    private ImportantSmells(){

    }


    private static long qtdSubelementos = 0;
    private static File arquivos[];
    private static File diretorio = null;
    private static SAXBuilder sb = new SAXBuilder();
    public static  Boolean classeValida = true;

    public static List<File> ListArquivosAnaliseXML =  new ArrayList<File>();
    public static List<File> ListArquivosAnaliseJava =  new ArrayList<File>();
    public static List<File> arquivosAnalise =  new ArrayList<File>();

    public static final String JAVA = ".java";
    public static final String XML = ".xml";
    private static OutputSmells JsonOut = new  OutputSmells();
    private static List<OutputSmells> ListJsonSmell = new ArrayList<OutputSmells>();
    private  static List<OutputSmells> ListSmells = new ArrayList<OutputSmells>();
    private static List<ReusoStringData> textStringArquivo = new ArrayList<ReusoStringData>();
    private  static List<String> FilesIMG = new ArrayList<String>();
    private static long totalSmells = 0;

    public static void carregaArquivosXMLAnalise(File directory){
        arquivosAnalise.clear();
        listar(directory,XML);
        ListArquivosAnaliseXML = arquivosAnalise;
    }

    public static void carregaArquivosJAVAAnalise(File directory){
        arquivosAnalise.clear();
        listar(directory,JAVA);
        ListArquivosAnaliseJava = arquivosAnalise;
    }

    public static void listar(File directory,String tipo) {
        if(directory.isDirectory()) {
            //System.out.println(directory.getPath());

            String[] myFiles = directory.list(new FilenameFilter() {
                public boolean accept(File directory, String fileName) {
                    return fileName.endsWith(tipo);
                }
            });

            for(int i = 0; i < myFiles.length; i++){
                arquivosAnalise.add(new File(directory.getPath() + "\\" + myFiles[i].toString()));
            }

            String[] subDirectory = directory.list();
            if(subDirectory != null) {
                for(String dir : subDirectory){
                    listar(new File(directory + File.separator  + dir),tipo);
                }
            }
        }
    }

    //Componente de UI Fazendo IO
    public static long CompUIIO(String pathApp){
        try {
            //arquivosAnalise.clear();
            totalSmells = 0;

            //listar(new File(pathApp),JAVA);

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
                    try
                    {
                        System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                        String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                        System.out.println("---------------------------------------------------------------------------------------");

                        File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                        CompilationUnit cUnit = JavaParser.parse(f);

                        cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {
                            if (classe.getExtendedTypes().get(0).toString().contains("Activity") || classe.getExtendedTypes().get(0).toString().contains("Fragment") || classe.getExtendedTypes().get(0).toString().contains("Adapter")) {

                                //Procura Libs IO no TIPO  em declaração de campos
                                classe.getFields().forEach(campos -> {
                                    if (IOClass.getIOClass().contains(campos.getElementType().toString())) {
                                        System.out.println("UI IO Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) " + campos.getRange().get().begin);
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(campos.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }
                                });

                                //Procura Libs de IO no TIPO em declaração  de Métodos
                                classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                                    IOClass.getIOClass().forEach(item -> {
                                        //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                        if (metodo.getParameters().contains(item)) {
                                            System.out.println("UI IO Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) nos parâmetros do método " + metodo.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }

                                        //Procura Libs de IO no TIPO em retorno  de Métodos
                                        if (metodo.getType().toString().contains(item)) {
                                            System.out.println("UI IO Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) no retorno do método " + metodo.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }

                                        //Procura Libs IO no TIPO  em declaração de campos
                                        metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                            if (campos.getElementType().toString().contains(item)) {
                                                System.out.println("UI IO Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) " + campos.getRange().get().begin);
                                                System.out.println("---------------------------------------------------------------------------------------");
                                                JsonOut.setTipoSmell("JAVA");
                                                JsonOut.setLinha(campos.getRange().get().begin.toString());
                                                JsonOut.setArquivo(nomeArquivo);
                                                ListJsonSmell.add(JsonOut);
                                                totalSmells++;
                                            }
                                        });
                                    });
                                });
                            }
                        });
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                    }
            }

            JsonOut.saveJson(ListJsonSmell,"UIIOComponent.json");


        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return totalSmells;
    }


    public static long CoupledUIComponent(String pathApp) throws FileNotFoundException {
        ListSmells.clear();
        //arquivosAnalise.clear();
        totalSmells = 0;
        //listar(new File(pathApp),JAVA);

        for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
            try {
                classeValida = true;
                String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);

                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                CompilationUnit cu = JavaParser.parse(f);

                ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<ClassOrInterfaceDeclaration>();
                NodeList<TypeDeclaration<?>> types = cu.getTypes();
                for (int i = 0; i < types.size(); i++) {
                    classes.add((ClassOrInterfaceDeclaration) types.get(i));
                }

                for (ClassOrInterfaceDeclaration classe : classes) {
                    NodeList<ClassOrInterfaceType> implementacoes = classe.getExtendedTypes();
                    if (implementacoes.size() != 0) {
                        for (ClassOrInterfaceType implementacao : implementacoes) {
                            if (implementacao.getName().getIdentifier().contains("Fragment") || implementacao.getName().getIdentifier().contains("Adapter") || implementacao.getName().getIdentifier().contains("Activity")) {
                                classe.getFields().forEach(item -> {
                                    //System.out.println(item.getElementType().toString());
                                    if ((item.getElementType().toString().contains("Activity") || item.getElementType().toString().contains("Fragment"))) {
                                        System.out.println("Componente de UI Acoplado " + item.getElementType().toString() + item.getRange());
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }
                                });

                                classe.findAll(ConstructorDeclaration.class).forEach(metodo -> {
                                    metodo.getParameters().forEach(item -> {
                                        if (item.getType().toString().contains("Activity") || item.getType().toString().contains("Fragment")) {
                                            System.out.println("Componente de UI Acoplado  " + classe.getName() + " " + metodo.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }
                                    });
                                });

                                classe.findAll(MethodDeclaration.class).forEach(metodo -> {

                                    //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                    if (metodo.getParameters().contains("Activity") || metodo.getParameters().contains("Fragment")) {
                                        System.out.println("Componente de UI Acoplado  " + classe.getName() + " " + metodo.getRange().get().begin);
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }

                                    //Procura Libs de IO no TIPO em retorno  de Métodos
                                    if (metodo.getType().toString().contains("Activity") || metodo.getType().toString().contains("Fragment")) {
                                        System.out.println("Componente de UI Acoplado  " + classe.getName() + " " + metodo.getRange().get().begin);
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }

                                    //Procura Libs IO no TIPO  em declaração de campos
                                    metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                        if (campos.getElementType().toString().contains("Activity") || campos.getElementType().toString().contains("Fragment")) {
                                            System.out.println("Componente de UI Acoplado  " + classe.getName() + ") " + campos.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(campos.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }
                                    });

                                });

                            }
                        }
                    }
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
        
        JsonOut.saveJson(ListJsonSmell,"CoupledUIComponent.json");
        return totalSmells;
    }


    public static long SuspiciousBehavior(String pathApp) throws FileNotFoundException {
        ListSmells.clear();
        //arquivosAnalise.clear();
        totalSmells = 0;
        //listar(new File(pathApp),JAVA);

        for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
            try {
                classeValida = true;
                String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                CompilationUnit cu = JavaParser.parse(f);

                ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<ClassOrInterfaceDeclaration>();
                NodeList<TypeDeclaration<?>> types = cu.getTypes();
                for (int i = 0; i < types.size(); i++) {
                    classes.add((ClassOrInterfaceDeclaration) types.get(i));
                }

                for (ClassOrInterfaceDeclaration classe : classes) {
                    NodeList<ClassOrInterfaceType> implementacoes = classe.getExtendedTypes();
                    if (implementacoes.size() != 0) {
                        for (ClassOrInterfaceType implementacao : implementacoes) {
                            if (implementacao.getName().getIdentifier().contains("BaseActivity") || implementacao.getName().getIdentifier().contains("Activity") || implementacao.getName().getIdentifier().contains("Fragments") || implementacao.getName().getIdentifier().contains("BaseAdapter") || implementacao.getName().getIdentifier().endsWith("Listener")) {
                                classeValida = true;
                                classe.getImplementedTypes().forEach(item -> {
                                    //System.out.println(item.getNameAsString());
                                    if (item.getName().toString().contains("Listener")) {
                                        System.out.println("Comportamento suspeito detectado  - " + item.getRange());
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(item.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }

                                });
                            }
                        }
                    } else {
                        classeValida = false;
                    }
                }

                //Se não for válida activity entre outros pula o laço para o próximo arquivo
                if (!classeValida) {
                    continue;
                }

                for (TypeDeclaration<?> typeDec : cu.getTypes()) {
                    //System.out.println(typeDec.getName().toString());
                    for (BodyDeclaration<?> member : typeDec.getMembers()) {

                        member.findAll(MethodDeclaration.class).forEach(item -> {
                            //System.out.println(item);
                            item.getChildNodes().forEach(sub -> {
                                sub.findAll(MethodDeclaration.class).forEach(i -> {
                                    System.out.println("Comportamento suspeito detectado  - " + i.getName() + " - " + i.getRange().get().begin);
                                    JsonOut.setTipoSmell("JAVA");
                                    JsonOut.setLinha(i.getRange().get().begin.toString());
                                    JsonOut.setArquivo(nomeArquivo);
                                    ListJsonSmell.add(JsonOut);
                                    totalSmells++;
                                });
                            });
                        });


                        member.toFieldDeclaration().ifPresent(field -> {
                            for (VariableDeclarator variable : field.getVariables()) {
                                //Print the field's class typr
                                //System.out.println(variable.getType());

                                if (variable.getType().toString().contains("Listener")) {
                                    System.out.println("Comportamento suspeito detectado  - " + variable.getType() + " - " + variable.getRange().get().begin);
                                    JsonOut.setTipoSmell("JAVA");
                                    JsonOut.setLinha(variable.getRange().get().begin.toString());
                                    JsonOut.setArquivo(nomeArquivo);
                                    ListJsonSmell.add(JsonOut);
                                    totalSmells++;
                                }
                                //Print the field's name
                                //System.out.println(variable.getName());
                                //Print the field's init value, if not null

                                variable.getInitializer().ifPresent(initValue -> {
                                    if (initValue.isLambdaExpr()) {
                                        System.out.println("Comportamento suspeito detectado  - " + initValue.getRange());
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(initValue.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }
                                });
                            }
                        });
                    }
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }

        JsonOut.saveJson(ListJsonSmell,"SuspiciousBehavior.json");
        return totalSmells;
    }

    public static long BrainUIComponent(String pathApp) {
        try {
            ListSmells.clear();
            //arquivosAnalise.clear();
            totalSmells = 0;

            //listar(new File(pathApp),JAVA);

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit cUnit = JavaParser.parse(f);

                    cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {
                        if (classe.getExtendedTypes().get(0).toString().contains("Activity") || classe.getExtendedTypes().get(0).toString().contains("Fragment") || classe.getExtendedTypes().get(0).toString().contains("Adapter")) {
                            //ifElseSwitchCase (Regra de negócios)
                            NodeList<BodyDeclaration<?>> membros = classe.getMembers();
                            for (BodyDeclaration<?> membro : membros) {
                                //Verifica se o membro é um método
                                if (membro.isMethodDeclaration()) {

                                    membro.findAll(IfStmt.class).forEach(item -> {
                                        System.out.println("Brain UI Component detectado na classe " + item.getRange().get().begin + " (lógica utilizando if detectada)");
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(item.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    });

                                    membro.findAll(SwitchEntryStmt.class).forEach(item -> {
                                        System.out.println("Brain UI Component detectado na classe " + item.getRange().get().begin + " (lógica utilizando Switch/Case detectada)");
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(item.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    });
                                }
                            }

                            //Static Fields
                            classe.getFields().forEach(item -> {
                                if (item.isFieldDeclaration() && item.isStatic()) {
                                    System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Campo Static) " + item.getRange().get().begin);
                                    System.out.println("---------------------------------------------------------------------------------------");
                                    JsonOut.setTipoSmell("JAVA");
                                    JsonOut.setLinha(item.getRange().get().begin.toString());
                                    JsonOut.setArquivo(nomeArquivo);
                                    ListJsonSmell.add(JsonOut);
                                    totalSmells++;
                                }
                            });

                            //Procura Libs IO no TIPO  em declaração de campos
                            classe.getFields().forEach(campos -> {
                                if (IOClass.getIOClass().contains(campos.getElementType().toString())) {
                                    System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) " + campos.getRange().get().begin);
                                    System.out.println("---------------------------------------------------------------------------------------");
                                    JsonOut.setTipoSmell("JAVA");
                                    JsonOut.setLinha(campos.getRange().get().begin.toString());
                                    JsonOut.setArquivo(nomeArquivo);
                                    ListJsonSmell.add(JsonOut);
                                    totalSmells++;
                                }
                            });

                            //Procura Libs de IO no TIPO em declaração  de Métodos
                            classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                                IOClass.getIOClass().forEach(item -> {
                                    //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                    if (metodo.getParameters().contains(item)) {
                                        System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) nos parâmetros do método " + metodo.getRange().get().begin);
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }

                                    //Procura Libs de IO no TIPO em retorno  de Métodos
                                    if (metodo.getType().toString().contains(item)) {
                                        System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) no retorno do método " + metodo.getRange().get().begin);
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }

                                    //Procura Libs IO no TIPO  em declaração de campos
                                    metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                        if (campos.getElementType().toString().contains(item)) {
                                            System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) " + campos.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(campos.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }
                                    });

                                });
                            });
                        }
                    });
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell,"BrainUIComponent.json");

        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return totalSmells;
    }
    
    public static long FoolAdapter(String pathApp){
        try {
            ListSmells.clear();
            //arquivosAnalise.clear();
            totalSmells = 0;           

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
                try{
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");
                    String arquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit compilationunit = JavaParser.parse(f);

                    //Extrai cada Classe analisada pelo CompilationUnit
                    ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<ClassOrInterfaceDeclaration>();
                    NodeList<TypeDeclaration<?>> types = compilationunit.getTypes();
                    for (int i = 0; i < types.size(); i++) {
                        classes.add((ClassOrInterfaceDeclaration) types.get(i));
                    }

                    //Para cada uma dessas classes, verifica se ela � um Adapter (ou seja, se ela extende de BaseAdapter).
                    for (ClassOrInterfaceDeclaration classe : classes) {

                        //Como a classe vai ser analisada ainda, n�o cont�m smells por enquanto
                        Boolean isFoolAdapter = false;

                        //Para ver se a classe � um Adapter, precisamos ver se ela extende de BaseAdapter
                        //Pegamos todas as classes que ela implementa
                        NodeList<ClassOrInterfaceType> implementacoes = classe.getExtendedTypes();
                        for (ClassOrInterfaceType implementacao : implementacoes) {
                            if (implementacao.getName().getIdentifier().equals("BaseAdapter")) {
                                //Se chegou at� aqui, temos certeza de que � um adapter.
                                //Se a classe que extende do BaseAdapter tiver algum m�todo que n�o seja sobrescrever um m�todo de interface, � um FlexAdapter.
                                //Pegamos todos os membros da classe
                                NodeList<BodyDeclaration<?>> membros = classe.getMembers();
                                //Verifica se o membro � um m�todo
                                for (BodyDeclaration<?> membro : membros)
                                    if (membro.isMethodDeclaration()) {
                                        MethodDeclaration metodo = (MethodDeclaration) membro;
                                        //Verifica se este m�todo chama getView
                                        if (metodo.getName().getIdentifier().equals("getView")) {

                                            //Pega o parametro do tipo View e armazena o nome dele
                                            //Pode ser �til para verificar por findViewById dentro de la�os
                                            Parameter viewParameter = metodo.getParameter(1);
                                            String nomeParametroView = viewParameter.getName().getIdentifier();

                                            //Pega o bloco de declara��es dentro m�todo getView
                                            BlockStmt body = metodo.getBody().get();
                                            NodeList<Statement> statements = body.getStatements();

                                            //Itera sobre as declara��es at� achar express�es
                                            for (Statement statement : statements) {
                                                if (statement.isExpressionStmt()) {
                                                    //Se em alguma dessas express�es tiver o texto findViewById
                                                    //Quer dizer que o ViewHolder n�o est� sendo utilizado, o que caracteriza o smell
                                                    if(statement.toString().contains("findViewById(")) {
                                                        isFoolAdapter = true;
                                                        JsonOut.setTipoSmell("JAVA");
                                                        JsonOut.setLinha(statement.getRange().get().begin.toString());
                                                        JsonOut.setArquivo(arquivo);
                                                        ListJsonSmell.add(JsonOut);
                                                        totalSmells++;
                                                    }

                                                    //Se ele infla um Layout em toda chamada ao getView, isso tamb�m caracteriza o smell
                                                    if(statement.toString().contains("getLayoutInflater(")) {
                                                        isFoolAdapter = true;
                                                        JsonOut.setTipoSmell("JAVA");
                                                        JsonOut.setLinha(statement.getRange().get().begin.toString());
                                                        JsonOut.setArquivo(arquivo);
                                                        ListJsonSmell.add(JsonOut);
                                                        totalSmells++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                            }
                        }

                        //Se a classe for um foolAdapter, imprime o erro na tela
                        if (isFoolAdapter) {
                            System.out.println("Fool Adapter detectado na classe " + classe.getName().getIdentifier());
                        }
                    }
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            JsonOut.saveJson(ListJsonSmell,"FoolAdapter.json");
            return totalSmells;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return totalSmells;
        }  
    }

    public static long FlexAdapter(String pathApp) {
        try {
            ListSmells.clear();
            //arquivosAnalise.clear();
            totalSmells = 0;

            //listar(new File(pathApp),JAVA);

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");
                    String arquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit compilationunit = JavaParser.parse(f);

                    //Extrai cada Classe analisada pelo CompilationUnit
                    ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<ClassOrInterfaceDeclaration>();
                    NodeList<TypeDeclaration<?>> types = compilationunit.getTypes();
                    for (int i = 0; i < types.size(); i++) {
                        classes.add((ClassOrInterfaceDeclaration) types.get(i));
                    }
                    //Para cada uma dessas classes, verifica se ela é um Adapter (ou seja, se ela extende de BaseAdapter).
                    for (ClassOrInterfaceDeclaration classe : classes) {
                        //Como a classe vai ser analisada ainda, não contém smells por enquanto
                        Boolean isFlexAdapter = false;
                        //Para ver se a classe é um Adapter, precisamos ver se ela extende de BaseAdapter
                        //Pegamos todas as classes que ela implementa
                        NodeList<ClassOrInterfaceType> implementacoes = classe.getExtendedTypes();
                        for (ClassOrInterfaceType implementacao : implementacoes) {
                            if (implementacao.getName().getIdentifier().contains("Adapter")) {
                                //Se chegou até aqui, temos certeza de que é um adapter.
                                //Se a classe que extende do BaseAdapter tiver algum método que não seja sobrescrever um método de interface, é um FlexAdapter.
                                //Pegamos todos os membros da classe
                                NodeList<BodyDeclaration<?>> membros = classe.getMembers();
                                for (BodyDeclaration<?> membro : membros) {
                                    //Verifica se o membro é um método
                                    if (membro.isMethodDeclaration()) {

                                        membro.findAll(IfStmt.class).forEach(item -> {
                                            System.out.println("Flex Adapter detectado na classe " + item.getRange() + " (lógica utilizando if detectada)");
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(item.getRange().get().begin.toString());
                                            JsonOut.setArquivo(arquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        });

                                        membro.findAll(SwitchStmt.class).forEach(item -> {
                                            System.out.println("Flex Adapter detectado na classe " + item.getRange() + " (lógica utilizando Switch/Case detectada)");
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(item.getRange().get().begin.toString());
                                            JsonOut.setArquivo(arquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell,"FlexAdapter.json");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return totalSmells;
    }


    public static long GodStyleResource(String pathApp,int threshold) {
        try {
            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);
            int qtdLimiteStilos = threshold;
            int qtdFilesStyle = 0;

            for (int cont = 0; cont < (ListArquivosAnaliseXML.toArray().length - 1); cont++) {
                try {
                    //System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (d.getRootElement().getChildren().size() > 0) {
                        if (d.getRootElement().getChildren().get(0).getName() == "style") {
                            qtdFilesStyle = qtdFilesStyle + 1;
                            System.out.println(ListArquivosAnaliseXML.toArray()[cont]);
                        }
                    }

                    if ((qtdFilesStyle == 1) || (d.getRootElement().getChildren().size() > qtdLimiteStilos)) {
                        //System.out.println("->"+arquivosAnalise.toArray()[cont].toString());
                        System.out.println("Longo recurso de Estilo detectado (existe apenas um arquivo para estilos no aplicativo que possui " + d.getRootElement().getChildren().size() + " estilos)");
                        System.out.println("---------------------------------------------------------------------------------------");
                        JsonOut.setTipoSmell("XML");
                        JsonOut.setArquivo(ListArquivosAnaliseXML.toArray()[cont].toString());
                        ListJsonSmell.add(JsonOut);
                        totalSmells++;
                    }
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell,"GodStyleResource.json");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    //Recurso de String Bagunçado
    public static long BadStringResource(String pathApp) {
        try {
            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            int qtdFilesString = 0;

            for (int cont = 0; cont < (ListArquivosAnaliseXML.toArray().length - 1); cont++) {
                try {
                    //System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (d.getRootElement().getChildren().size() > 0) {
                        if (d.getRootElement().getChildren().get(0).getName() == "string") {
                            qtdFilesString = qtdFilesString + 1;
                        }
                    }

                    if ((qtdFilesString == 1)) {
                        //System.out.println("->"+arquivosAnalise.toArray()[cont].toString());
                        System.out.println("Recurso de String Bagun�ado detectado (existe apenas um arquivo para strings no aplicativo  ");
                        System.out.println("---------------------------------------------------------------------------------------");
                        JsonOut.setTipoSmell("XML");
                        JsonOut.setArquivo(ListArquivosAnaliseXML.toArray()[cont].toString());
                        ListJsonSmell.add(JsonOut);
                        totalSmells++;
                    }
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell,"BadStringResource.json");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }



    public static long DeepNestedLayout(String pathApp, int threshold) {
        try {
            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            for (int cont = 0; cont < ListArquivosAnaliseXML.toArray().length; cont++) {
                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseXML.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (ListArquivosAnaliseXML.toArray()[cont].toString().contains("\\layout\\")) {
                        //ACESSAR O ROOT ELEMENT
                        Element rootElmnt = d.getRootElement();

                        //BUSCAR ELEMENTOS FILHOS DA TAG
                        List elements = rootElmnt.getChildren();

                        recursiveChildrenElement(elements, threshold);

                        System.out.println("---------------------------------------------------------------------------------------");

                    }
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell,"DeepNestedLayout.json");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    public static long DuplicateStyleAttributes(String pathApp) {
        try {
            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            for (int cont = 0; cont < ListArquivosAnaliseXML.toArray().length; cont++) {
                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseXML.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (d.getRootElement().getChildren().get(0).getName().toString() == "style") {
                        List<String> listSmellsEcontradas = new ArrayList<String>();

                        for (int i = 0; i < d.getRootElement().getChildren().size(); i++) {

                            List<Element> filhos = d.getRootElement().getChildren();
                            for (int j = 0; j < filhos.size(); j++) {
                                List<Attribute> attr = filhos.get(j).getAttributes();
                                for (Attribute atributo : attr) {

                                    String atributo_atual = atributo.toString();
                                    System.out.println(atributo_atual);

                                    for (int ii = 0; ii < d.getRootElement().getChildren().size(); ii++) {
                                        List<Element> filhosInterno = d.getRootElement().getChildren();
                                        for (int jj = 0; jj < filhosInterno.size(); jj++) {
                                            List<Attribute> attrInterno = filhosInterno.get(jj).getAttributes();
                                            for (Attribute atributoInterno : attrInterno) {

                                                if (jj > j) {
                                                    if (atributo_atual.toString().equals(atributoInterno.toString()) && !listSmellsEcontradas.contains(atributo_atual.toString())) {
                                                        listSmellsEcontradas.add(atributo_atual.toString());
                                                        System.out.println("Duplicate Style Attributes " + atributoInterno.getName() + " - Considere colocar a formatação das propriedades em um recurso de estilo:");
                                                        JsonOut.setTipoSmell("XML");
                                                        JsonOut.setArquivo(ListArquivosAnaliseXML.toArray()[cont].toString());
                                                        ListJsonSmell.add(JsonOut);
                                                        totalSmells++;
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell,"DuplicateStyleAttributes.json");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    //-----------------------------------------------------------------

    //Recurso Mágico
    public static long magicResource(String pathApp){
        try{
            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            for (int cont = 0; cont < ListArquivosAnaliseXML.toArray().length; cont++) {
                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseXML.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (ListArquivosAnaliseXML.toArray()[cont].toString().contains("\\layout\\")) {
                        //ACESSAR O ROOT ELEMENT
                        Element rootElmnt = d.getRootElement();

                        //BUSCAR ELEMENTOS FILHOS DA TAG
                        List elements = rootElmnt.getChildren();

                        for (int i = 0; i < elements.size(); i++) {
                            org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                            //System.out.println(el.getName());
                            if (el.getChildren().size() > 0) {
                                recursiveChildrenMagic(elements);
                            }
                        }

                        System.out.println("---------------------------------------------------------------------------------------");

                    }
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell,"magicResource.json");

            System.out.println("---------------------------------------------------------------------------------------");

        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return totalSmells;
    }


    private static void recursiveChildrenMagic(List elements) {
        for (int i = 0; i < elements.size(); i++) {
            try {

                org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                List SubElements = el.getChildren();

                //System.out.println(el.getName());

                if (SubElements.size() > 0) {
                    recursiveChildrenMagic(SubElements);
                } else {
                    List<org.jdom2.Attribute> listAttr = (List<org.jdom2.Attribute>) el.getAttributes();
                    for (org.jdom2.Attribute item : listAttr) {
                        //System.out.println(item);
                        if (item.getName() == "text") {
                            if (!item.getValue().matches("@.*/.*")) {
                                System.out.println("Recurso Mágico " + el.getName() + " - text:" + item.getValue());
                                JsonOut.setTipoSmell("XML");
                                JsonOut.setArquivo("");
                                ListJsonSmell.add(JsonOut);
                                totalSmells++;
                            }
                        }
                    }
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }


    //Reuso inadequado de string
    public static long reusoInadequadoDeString(String pathApp){
        try{
            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            for (int cont = 0; cont < ListArquivosAnaliseXML.toArray().length; cont++) {
                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseXML.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (ListArquivosAnaliseXML.toArray()[cont].toString().contains("\\layout\\")) {
                        //ACESSAR O ROOT ELEMENT
                        Element rootElmnt = d.getRootElement();

                        //BUSCAR ELEMENTOS FILHOS DA TAG
                        List elements = rootElmnt.getChildren();

                        for (int i = 0; i < elements.size(); i++) {
                            org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                            //System.out.println(el.getName());
                            if (el.getChildren().size() > 0) {
                                recursiveChildrenReusoInadequadoDeString(elements, ListArquivosAnaliseXML.toArray()[cont].toString());
                            }
                        }

                        System.out.println("---------------------------------------------------------------------------------------");

                    }
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }

            for (ReusoStringData linha : textStringArquivo) {
                textStringArquivo.forEach(itemTexto->{
                    if((linha.strString.equals(itemTexto.strString)) && (!linha.arquivo.equals(itemTexto.arquivo))){
                        System.out.println("Reuso inadequado de String detectado " + itemTexto.strString + "(Arquivo " + linha.arquivo +" e " + itemTexto.arquivo + ")");
                        JsonOut.setTipoSmell("XML");
                        JsonOut.setArquivo(linha.arquivo.toString());
                        ListJsonSmell.add(JsonOut);
                        totalSmells++;
                    }
                });
                //System.out.println(linha.strString + " = " + linha.arquivo);
            }

            JsonOut.saveJson(ListJsonSmell,"reusoInadequadoDeString.json");

            System.out.println("---------------------------------------------------------------------------------------");

        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return totalSmells;
    }


    private static void recursiveChildrenReusoInadequadoDeString(List elements, String arquivo) {
        for (int i = 0; i < elements.size(); i++) {
            try {
                org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                List SubElements = el.getChildren();

                //System.out.println(el.getName());

                if (SubElements.size() > 0) {
                    recursiveChildrenReusoInadequadoDeString(SubElements, arquivo);
                } else {
                    List<org.jdom2.Attribute> listAttr = (List<org.jdom2.Attribute>) el.getAttributes();
                    for (org.jdom2.Attribute item : listAttr) {
                        //System.out.println(item);
                        if (item.getName() == "text") {
                            if (item.getValue().matches("@.*/.*")) {
                                //System.out.println("Recurso Mágico " + el.getName() + " - text:" + item.getValue());
                                //System.out.println(item.getValue());
                                ReusoStringData data = new ReusoStringData();
                                data.arquivo = arquivo;
                                data.strString = item.getValue();
                                textStringArquivo.add(data);
                            }
                        }
                    }
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public static long NotFragment(String pathApp){
        try{
            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),JAVA);

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit cUnit = JavaParser.parse(f);

                    List<String> ViewsAndroid = new ArrayList<String>();
                    ViewsAndroid.add("TextView");
                    ViewsAndroid.add("EditText");
                    ViewsAndroid.add("Sppiner");

                    //Não existir fragmentos na aplicação

                    // Uso de Views(EditText, Spinner, ou Outras Views Diretamente pela activity)
                    cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {
                        if (classe.getExtendedTypes().get(0).toString().contains("Activity")) {
                            NodeList<BodyDeclaration<?>> membros = classe.getMembers();

                            //Procura ViewsAndroid no TIPO  em declaração de campos
                            classe.getFields().forEach(campos -> {
                                if (ViewsAndroid.contains(campos.getElementType().toString())) {
                                    System.out.println("Não Uso de Fragments detectado na classe " + classe.getName() + " () " + campos.getRange().get().begin);
                                    System.out.println("---------------------------------------------------------------------------------------");
                                    JsonOut.setTipoSmell("JAVA");
                                    JsonOut.setLinha(campos.getRange().get().begin.toString());
                                    JsonOut.setArquivo(nomeArquivo);
                                    ListJsonSmell.add(JsonOut);
                                    totalSmells++;
                                }
                            });

                            //Procura ViewsAndroid no TIPO em declaração  de Métodos
                            classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                                ViewsAndroid.forEach(item -> {
                                    //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                    if (metodo.getParameters().contains(item)) {
                                        System.out.println("Não Uso de Fragments detectado na classe " + classe.getName() + " () nos parâmetros do método " + metodo.getRange().get().begin);
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }

                                    //Procura ViewsAndroid no TIPO em retorno  de Métodos
                                    if (metodo.getType().toString().contains(item)) {
                                        System.out.println("Não Uso de Fragments detectado na classe " + classe.getName() + " () no retorno do método " + metodo.getRange().get().begin);
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }

                                    //Procura ViewsAndroid no TIPO  em declaração de campos
                                    metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                        if (campos.getElementType().toString().contains(item)) {
                                            System.out.println("Não Uso de Fragments detectado na classe " + classe.getName() + " () " + campos.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(campos.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }
                                    });

                                });
                            });
                        }
                    });
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell,"NotFragment.json");


        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return totalSmells;
    }

    public static void ObtemImagensList(File directory) {
        if(directory.isDirectory()) {
            if(directory.getPath().contains("mipmap")){
                //System.out.println(directory.getPath());
                FilesIMG.add(directory.getPath());
            }

            String[] subDirectory = directory.list();
            if(subDirectory != null) {
                for(String dir : subDirectory){
                    ObtemImagensList(new File(directory + File.separator  + dir));
                }
            }
        }
    }

    public static long NotFoundImage(String pathApp){
        totalSmells = 0;
        ListSmells.clear();
        ObtemImagensList(new File(pathApp));

        FilesIMG.forEach(caminho->{
            File directory = new File(caminho);
            for(File arquivo : directory.listFiles()){
                try{
                    FilesIMG.forEach(item->{

                        File arquivoImg = new File(item + "\\" + arquivo.getName());

                        if (!arquivoImg.exists()) {
                            System.out.println("Imagem Faltante detectado " + arquivo.getName() + " para pasta " + item);
                            JsonOut.setTipoSmell("XML");
                            JsonOut.setArquivo(arquivoImg.toString());
                            ListJsonSmell.add(JsonOut);
                            totalSmells++;
                            //System.out.println(arquivoImg.length());
                        }
                        else if((arquivo.length() != arquivoImg.length())){
                            System.out.println("Imagem Faltante detectado (Imagem existe porem a resolução é incompatível) " + arquivo.getName() + " para pasta " + item);
                            JsonOut.setTipoSmell("XML");
                            JsonOut.setArquivo(arquivoImg.toString());
                            ListJsonSmell.add(JsonOut);
                            totalSmells++;
                        }
                    });
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        JsonOut.saveJson(ListJsonSmell,"NotFoundImage.json");
        return totalSmells;
    }

    public static long HideListener(String pathApp){
        try{
            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            for (int cont = 0; cont < ListArquivosAnaliseXML.toArray().length; cont++) {
                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseXML.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (ListArquivosAnaliseXML.toArray()[cont].toString().contains("\\layout\\")) {
                        //ACESSAR O ROOT ELEMENT
                        Element rootElmnt = d.getRootElement();

                        //BUSCAR ELEMENTOS FILHOS DA TAG
                        List elements = rootElmnt.getChildren();

                        for (int i = 0; i < elements.size(); i++) {
                            org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                            //System.out.println(el.getName());

                            if (el.getChildren().size() > 0) {
                                recursiveChildrenHideListener(elements);
                            }
                        }

                        System.out.println("---------------------------------------------------------------------------------------");

                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell,"HideListener.json");

            System.out.println("---------------------------------------------------------------------------------------");

        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return totalSmells;
    }

    public static long ExcessiveFragment(String pathApp, long threshold) throws IOException {
        //arquivosAnalise.clear();
        ListSmells.clear();
        totalSmells = 0;

        //listar(new File(pathApp),JAVA);
        long totalFragments = 0;
        List<ReusoStringData> listaExcessiveFragment = new ArrayList<ReusoStringData>();

        for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
            try {

                //System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                //System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                CompilationUnit cu = JavaParser.parse(f);

                ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<ClassOrInterfaceDeclaration>();
                NodeList<TypeDeclaration<?>> types = cu.getTypes();
                for (int i = 0; i < types.size(); i++) {
                    classes.add((ClassOrInterfaceDeclaration) types.get(i));
                }

                for (ClassOrInterfaceDeclaration classe : classes) {
                    NodeList<ClassOrInterfaceType> implementacoes = classe.getExtendedTypes();
                    if (implementacoes.size() != 0) {
                        for (ClassOrInterfaceType implementacao : implementacoes) {
                            if (implementacao.getName().getIdentifier().contains("Fragment")) {
                                totalFragments = totalFragments + 1;
                            }
                        }
                    }
                }

                if (totalFragments > threshold) {
                    System.out.println("Uso Excessivo de Fragment " + "(Mais de " + threshold + " Fragments no aplicativo)");
                    JsonOut.setTipoSmell("XML");
                    JsonOut.setArquivo("");
                    ListJsonSmell.add(JsonOut);
                    totalSmells++;
                }

                return totalSmells;
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }

        JsonOut.saveJson(ListJsonSmell,"ExcessiveFragment.json");
        return totalSmells;

    }


    private static void recursiveChildrenHideListener(List elements) {
        for (int i = 0; i < elements.size(); i++) {
            try {

                org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                List SubElements = el.getChildren();

                //System.out.println(el.getName());

                if (SubElements.size() > 0) {
                    recursiveChildrenHideListener(SubElements);
                } else {
                    List<org.jdom2.Attribute> listAttr = (List<org.jdom2.Attribute>) el.getAttributes();
                    for (org.jdom2.Attribute item : listAttr) {
                        if (item.getName() == "onClick") {
                            System.out.println("Listener Escondido " + el.getName() + " - Onclick:" + item.getValue());
                            JsonOut.setTipoSmell("XML");
                            JsonOut.setArquivo("");
                            ListJsonSmell.add(JsonOut);
                            totalSmells++;
                        }
                    }
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }


        private static void recursiveChildrenElement(List elements, int threshold) {
            for (int i = 0; i < elements.size(); i++) {
                try {

                    org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                    List SubElements = el.getChildren();

                    if (SubElements.size() > 0) {
                        qtdSubelementos = qtdSubelementos + 1;
                        recursiveChildrenElement(SubElements, threshold);
                    } else {
                        if (qtdSubelementos > threshold) {
                            System.out.println("Layout Profundamente Aninhado encontrado " + el.getName() + "(Mais de " + threshold + " níveis)");
                            JsonOut.setTipoSmell("XML");
                            JsonOut.setArquivo("");
                            ListJsonSmell.add(JsonOut);
                            totalSmells++;
                            break;
                        }
                    }
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

