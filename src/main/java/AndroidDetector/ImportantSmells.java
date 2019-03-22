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
import metric.WMC;
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

    private static int contadorFieldStatic = 0;
    public static int contadorArquivosAnalisados = 0;



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
    private static long contadorDurelli = 0;


    //--> Listagem de Arquivos
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
                arquivosAnalise.add(new File(directory.getPath() + ""+File.separator+"" + myFiles[i].toString()));
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
    public static long CompUIIOOraculo(){
        try {

            contadorArquivosAnalisados = 0;
            //arquivosAnalise.clear();
            totalSmells = 0;

            carregaArquivosJAVAAnalise(new File("/Users/rafaeldurelli/Desktop/Repositorio-TESTE/opentasks"));


            System.out.println(ListArquivosAnaliseJava);

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
                        if (classe.getExtendedTypes().size() > 0){
                            if (classe.getExtendedTypes().get(0).toString().contains("Activity") || classe.getExtendedTypes().get(0).toString().contains("Fragment")
                                    || classe.getExtendedTypes().get(0).toString().contains("Adapter")) {

                                //Contados de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados +1;

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


                                classe.findAll(ImportDeclaration.class).forEach(imports -> {
                                    IOClass.getIOClass().forEach(item -> {
                                        if (imports.getName().toString().contains(item)) {
                                            System.out.println("UI IO Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) nos parâmetros do método " + imports.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(imports.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }
                                    });
                                });

                                contadorDurelli = 0;
                                //Procura Libs de IO no TIPO em declaração  de Métodos
                                classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                                    IOClass.getIOClass().forEach(item -> {

                                        String method = metodo.getNameAsString();



                                        //Procura Libs de IO no TIPO em retorno  de Métodos
                                        if (metodo.getType().toString().contains(item)) {
                                            System.out.println(metodo.getType().toString());

                                            if (contadorDurelli == 0){

                                                System.out.println("UI IO Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) no retorno do método " + metodo.getRange().get().begin);
                                                System.out.println("---------------------------------------------------------------------------------------");
                                                JsonOut.setTipoSmell("JAVA");
                                                JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                                JsonOut.setArquivo(nomeArquivo);
                                                ListJsonSmell.add(JsonOut);
                                                totalSmells++;

                                            }
                                            contadorDurelli++;


                                        }


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
                            }}
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

        System.out.println("Identificou UI " + totalSmells);

        return totalSmells;
    }



    //Componente de UI Fazendo IO
    public static long CompUIIO(String pathApp){
        try {

            contadorArquivosAnalisados = 0;
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
                            if (classe.getExtendedTypes().size() > 0){
                             if (classe.getExtendedTypes().get(0).toString().contains("Activity") || classe.getExtendedTypes().get(0).toString().contains("Fragment")
                                    || classe.getExtendedTypes().get(0).toString().contains("Adapter")) {

                                 //Contados de arquivos analisados
                                 contadorArquivosAnalisados = contadorArquivosAnalisados +1;

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
                            }}
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


    //Componente de UI Acoplado
    public static long CoupledUIComponentOraculo() throws FileNotFoundException {

        carregaArquivosJAVAAnalise(new File("/Users/rafaeldurelli/Desktop/Repositorio-TESTE/opentasks"));


        System.out.println(ListArquivosAnaliseJava);

        contadorArquivosAnalisados = 0;
        ListSmells.clear();

        totalSmells = 0;


        for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
            System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
            String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
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

                            //Contados de arquivos analisados
                            contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                            classe.getFields().forEach(item -> {
                                //System.out.println(item.getElementType().toString());
                                if ((item.getElementType().toString().contains("Activity") || item.getElementType().toString().contains("Fragment"))) {
                                    System.out.println("Componente de UI Acoplado " + item.getElementType().toString() + item.getRange());
                                    JsonOut.setTipoSmell("JAVA");
//                                        JsonOut.setArquivo(nomeArquivo);
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
//                                            JsonOut.setArquivo(nomeArquivo);
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
//                                        JsonOut.setArquivo(nomeArquivo);
                                    ListJsonSmell.add(JsonOut);
                                    totalSmells++;
                                }

                                //Procura Libs de IO no TIPO em retorno  de Métodos
                                if (metodo.getType().toString().contains("Activity") || metodo.getType().toString().contains("Fragment")) {
                                    System.out.println("Componente de UI Acoplado  " + classe.getName() + " " + metodo.getRange().get().begin);
                                    System.out.println("---------------------------------------------------------------------------------------");
                                    JsonOut.setTipoSmell("JAVA");
                                    JsonOut.setLinha(metodo.getRange().get().begin.toString());
//                                        JsonOut.setArquivo(nomeArquivo);
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
//                                            JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }
                                });

                            });

                        }
                    }
                }
            }


            System.out.println("Total de smells couple identificado " + totalSmells);
        }
        JsonOut.saveJson(ListJsonSmell,"CoupledUIComponent.json");
        return totalSmells;
    }




    //Componente de UI Acoplado
    public static long CoupledUIComponent(String pathApp) throws FileNotFoundException {
        contadorArquivosAnalisados = 0;
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

                                //Contados de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados +1;

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



    //Comportamento suspeito
    public static long SuspiciousBehaviorOraculo() throws FileNotFoundException {
        ListSmells.clear();
        //arquivosAnalise.clear();
        totalSmells = 0;
        //listar(new File(pathApp),JAVA);


        carregaArquivosJAVAAnalise(new File("/Users/rafaeldurelli/Desktop/Repositorio-TESTE/opentasks"));


        System.out.println(ListArquivosAnaliseJava);


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
                                //Contador de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados +1;
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

        System.out.println("quantidade de smells identificados " + totalSmells);

        JsonOut.saveJson(ListJsonSmell,"SuspiciousBehavior.json");
        return totalSmells;
    }



    //Comportamento suspeito
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
                                //Contador de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados +1;
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

    //Componente de UI Cérebro
    public static long BrainUIComponent(String pathApp) {
        try {
            contadorArquivosAnalisados = 0;
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

                        if (classe.getExtendedTypes().size()>0) {


                            if (classe.getExtendedTypes().get(0).toString().contains("Activity") || classe.getExtendedTypes().get(0).toString().contains("Fragment") || classe.getExtendedTypes().get(0).toString().contains("Adapter")) {

                                //Contados de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                                //chama aqui o WMC
                                WMC wmc = new WMC(cUnit);
                                wmc.run();

                                if (wmc.getCc() > 56) {
                                    JsonOut.setTipoSmell("JAVA");
                                    JsonOut.setLinha(classe.getRange().get().begin.toString());
                                    JsonOut.setArquivo(nomeArquivo);
                                    ListJsonSmell.add(JsonOut);
                                    totalSmells++;
                                }

                                //----------------------------------------------------------------foi removido aqui
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

                            //----------------------------------------------------------------foi removido aqui


//                            //Aqui conta para verificar o threshold de fieldstatic ... mas tem que pensar nisso.
                            ImportantSmells.contadorFieldStatic = 0;
                            classe.getFields().forEach(item -> {
                                if (item.isFieldDeclaration() && item.isStatic()) {
                                    ImportantSmells.contadorFieldStatic++;
                                }
                            });


                            if (ImportantSmells.contadorFieldStatic > 8) {
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
                            }

                                //Procura Libs IO no TIPO  em declaração de campos
                                classe.getFields().forEach(campos -> {
                                    if (IOClass.getIOClass().contains(campos.getElementType().toString())) {
                                        System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) ATRIBUTOS DA CLASSE " + campos.getRange().get().begin);
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
                                        //COMO VAI PROCURAR AQUI ??
                                    if (metodo.getParameters().contains(item)) {
                                        System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) nos parâmetros do método " + metodo.getRange().get().begin);
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }

//                                    //Procura Libs de IO no TIPO em retorno  de Métodos
//                                    if (metodo.getType().toString().contains(item)) {
//                                        System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) no retorno do método " + metodo.getRange().get().begin);
//                                        System.out.println("---------------------------------------------------------------------------------------");
//                                        JsonOut.setTipoSmell("JAVA");
//                                        JsonOut.setLinha(metodo.getRange().get().begin.toString());
//                                        JsonOut.setArquivo(nomeArquivo);
//                                        ListJsonSmell.add(JsonOut);
//                                        totalSmells++;
//                                    }

                                        //Procura Libs IO no TIPO  em declaração de campos
                                        metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                            if (campos.getElementType().toString().contains(item)) {
                                                System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) FIELD Declaration " + campos.getRange().get().begin);
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


    public static void main(String...args) throws FileNotFoundException {

        BrainUIComponentOraculo();

//        CoupledUIComponentOraculo();

//        SuspiciousBehaviorOraculo();

//        FlexAdapterOraculo();

//        FoolAdapterOraculo();

//        CompUIIOOraculo();

//        NotFragmentOraculo();
    }



    //Componente de UI Cérebro
    public static long BrainUIComponentOraculo() {
        try {
            contadorArquivosAnalisados = 0;
            ListSmells.clear();

            totalSmells = 0;



                    CompilationUnit cUnit = JavaParser.parse("package com.seafile.seadroid2.account.ui;\n" +
                            "\n" +
                            "import android.app.ProgressDialog;\n" +
                            "import android.content.Context;\n" +
                            "import android.content.Intent;\n" +
                            "import android.net.ConnectivityManager;\n" +
                            "import android.net.NetworkInfo;\n" +
                            "import android.os.AsyncTask;\n" +
                            "import android.os.Bundle;\n" +
                            "import android.support.design.widget.TextInputLayout;\n" +
                            "import android.support.v4.app.NavUtils;\n" +
                            "import android.support.v4.app.TaskStackBuilder;\n" +
                            "import android.support.v7.widget.Toolbar;\n" +
                            "import android.text.Editable;\n" +
                            "import android.text.TextUtils;\n" +
                            "import android.text.TextWatcher;\n" +
                            "import android.text.method.HideReturnsTransformationMethod;\n" +
                            "import android.text.method.PasswordTransformationMethod;\n" +
                            "import android.util.Log;\n" +
                            "import android.view.MenuItem;\n" +
                            "import android.view.View;\n" +
                            "import android.view.inputmethod.InputMethodManager;\n" +
                            "import android.widget.Button;\n" +
                            "import android.widget.CheckBox;\n" +
                            "import android.widget.EditText;\n" +
                            "import android.widget.ImageView;\n" +
                            "import android.widget.RelativeLayout;\n" +
                            "import android.widget.TextView;\n" +
                            "\n" +
                            "import com.seafile.seadroid2.R;\n" +
                            "import com.seafile.seadroid2.SeafConnection;\n" +
                            "import com.seafile.seadroid2.SeafException;\n" +
                            "import com.seafile.seadroid2.account.Account;\n" +
                            "import com.seafile.seadroid2.account.AccountInfo;\n" +
                            "import com.seafile.seadroid2.account.Authenticator;\n" +
                            "import com.seafile.seadroid2.data.DataManager;\n" +
                            "import com.seafile.seadroid2.ssl.CertsManager;\n" +
                            "import com.seafile.seadroid2.ui.EmailAutoCompleteTextView;\n" +
                            "import com.seafile.seadroid2.ui.activity.AccountsActivity;\n" +
                            "import com.seafile.seadroid2.ui.activity.BaseActivity;\n" +
                            "import com.seafile.seadroid2.ui.dialog.SslConfirmDialog;\n" +
                            "import com.seafile.seadroid2.util.ConcurrentAsyncTask;\n" +
                            "import com.seafile.seadroid2.util.Utils;\n" +
                            "\n" +
                            "import org.json.JSONException;\n" +
                            "\n" +
                            "import java.net.HttpURLConnection;\n" +
                            "import java.net.MalformedURLException;\n" +
                            "\n" +
                            "public class AccountDetailActivity extends BaseActivity implements Toolbar.OnMenuItemClickListener {\n" +
                            "    private static final String DEBUG_TAG = \"AccountDetailActivity\";\n" +
                            "\n" +
                            "    private static final String HTTP_PREFIX = \"http://\";\n" +
                            "    private static final String HTTPS_PREFIX = \"https://\";\n" +
                            "    public static final String TWO_FACTOR_AUTH = \"two_factor_auth\";\n" +
                            "\n" +
                            "    private TextView statusView;\n" +
                            "    private Button loginButton;\n" +
                            "    private EditText serverText;\n" +
                            "    private ProgressDialog progressDialog;\n" +
                            "    private EmailAutoCompleteTextView emailText;\n" +
                            "    private EditText passwdText;\n" +
                            "    private CheckBox httpsCheckBox;\n" +
                            "    private TextView seahubUrlHintText;\n" +
                            "    private ImageView clearEmail, clearPasswd, ivEyeClick;\n" +
                            "    private RelativeLayout rlEye;\n" +
                            "    private TextInputLayout authTokenLayout;\n" +
                            "    private EditText authTokenText;\n" +
                            "\n" +
                            "    private android.accounts.AccountManager mAccountManager;\n" +
                            "    private boolean serverTextHasFocus;\n" +
                            "    private boolean isPasswddVisible;\n" +
                            "    private CheckBox cbRemDevice;\n" +
                            "    private String mSessionKey;\n" +
                            "\n" +
                            "    /** Called when the activity is first created. */\n" +
                            "    @Override\n" +
                            "    public void onCreate(Bundle savedInstanceState) {\n" +
                            "        super.onCreate(savedInstanceState);\n" +
                            "        setContentView(R.layout.account_detail);\n" +
                            "\n" +
                            "        mAccountManager = android.accounts.AccountManager.get(getBaseContext());\n" +
                            "\n" +
                            "        statusView = (TextView) findViewById(R.id.status_view);\n" +
                            "        loginButton = (Button) findViewById(R.id.login_button);\n" +
                            "        httpsCheckBox = (CheckBox) findViewById(R.id.https_checkbox);\n" +
                            "        serverText = (EditText) findViewById(R.id.server_url);\n" +
                            "        emailText = (EmailAutoCompleteTextView) findViewById(R.id.email_address);\n" +
                            "        passwdText = (EditText) findViewById(R.id.password);\n" +
                            "        seahubUrlHintText = (TextView) findViewById(R.id.seahub_url_hint);\n" +
                            "\n" +
                            "        clearEmail = (ImageView) findViewById(R.id.iv_delete_email);\n" +
                            "        clearPasswd = (ImageView) findViewById(R.id.iv_delete_pwd);\n" +
                            "        rlEye = (RelativeLayout) findViewById(R.id.rl_layout_eye);\n" +
                            "        ivEyeClick = (ImageView) findViewById(R.id.iv_eye_click);\n" +
                            "\n" +
                            "        authTokenLayout = (TextInputLayout) findViewById(R.id.auth_token_hint);\n" +
                            "        authTokenText = (EditText) findViewById(R.id.auth_token);\n" +
                            "        authTokenLayout.setVisibility(View.GONE);\n" +
                            "\n" +
                            "        cbRemDevice = findViewById(R.id.remember_device);\n" +
                            "        cbRemDevice.setVisibility(View.GONE);\n" +
                            "        setupServerText();\n" +
                            "\n" +
                            "        Intent intent = getIntent();\n" +
                            "\n" +
                            "        String defaultServerUri = intent.getStringExtra(SeafileAuthenticatorActivity.ARG_SERVER_URI);\n" +
                            "\n" +
                            "        if (intent.getBooleanExtra(\"isEdited\", false)) {\n" +
                            "            String account_name = intent.getStringExtra(SeafileAuthenticatorActivity.ARG_ACCOUNT_NAME);\n" +
                            "            String account_type = intent.getStringExtra(SeafileAuthenticatorActivity.ARG_ACCOUNT_TYPE);\n" +
                            "            android.accounts.Account account = new android.accounts.Account(account_name, account_type);\n" +
                            "\n" +
                            "            String server = mAccountManager.getUserData(account, Authenticator.KEY_SERVER_URI);\n" +
                            "            String email = mAccountManager.getUserData(account, Authenticator.KEY_EMAIL);\n" +
                            "            mSessionKey = mAccountManager.getUserData(account, Authenticator.SESSION_KEY);\n" +
                            "            // isFromEdit = mAccountManager.getUserData(account, Authenticator.KEY_EMAIL);\n" +
                            "\n" +
                            "            if (server.startsWith(HTTPS_PREFIX))\n" +
                            "                httpsCheckBox.setChecked(true);\n" +
                            "\n" +
                            "            serverText.setText(server);\n" +
                            "            emailText.setText(email);\n" +
                            "            emailText.requestFocus();\n" +
                            "            seahubUrlHintText.setVisibility(View.GONE);\n" +
                            "\n" +
                            "\n" +
                            "        } else if (defaultServerUri != null) {\n" +
                            "            if (defaultServerUri.startsWith(HTTPS_PREFIX))\n" +
                            "                httpsCheckBox.setChecked(true);\n" +
                            "            serverText.setText(defaultServerUri);\n" +
                            "            emailText.requestFocus();\n" +
                            "        } else {\n" +
                            "            serverText.setText(HTTP_PREFIX);\n" +
                            "            int prefixLen = HTTP_PREFIX.length();\n" +
                            "            serverText.setSelection(prefixLen, prefixLen);\n" +
                            "        }\n" +
                            "        Toolbar toolbar = getActionBarToolbar();\n" +
                            "        toolbar.setOnMenuItemClickListener(this);\n" +
                            "        setSupportActionBar(toolbar);\n" +
                            "        getSupportActionBar().setDisplayHomeAsUpEnabled(true);\n" +
                            "        getSupportActionBar().setTitle(R.string.login);\n" +
                            "\n" +
                            "        initListener();\n" +
                            "    }\n" +
                            "\n" +
                            "    private void initListener() {\n" +
                            "        emailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {\n" +
                            "            @Override\n" +
                            "            public void onFocusChange(View v, boolean hasFocus) {\n" +
                            "                if (hasFocus && emailText.getText().toString().trim().length() > 0) {\n" +
                            "                    clearEmail.setVisibility(View.VISIBLE);\n" +
                            "                } else {\n" +
                            "                    clearEmail.setVisibility(View.INVISIBLE);\n" +
                            "                }\n" +
                            "            }\n" +
                            "        });\n" +
                            "\n" +
                            "        passwdText.setOnFocusChangeListener(new View.OnFocusChangeListener() {\n" +
                            "            @Override\n" +
                            "            public void onFocusChange(View v, boolean hasFocus) {\n" +
                            "                if (hasFocus && passwdText.getText().toString().trim().length() > 0) {\n" +
                            "                    clearPasswd.setVisibility(View.VISIBLE);\n" +
                            "                } else {\n" +
                            "                    clearPasswd.setVisibility(View.INVISIBLE);\n" +
                            "                }\n" +
                            "            }\n" +
                            "        });\n" +
                            "\n" +
                            "        emailText.addTextChangedListener(new TextWatcher() {\n" +
                            "            @Override\n" +
                            "            public void beforeTextChanged(CharSequence s, int start, int count, int after) {\n" +
                            "            }\n" +
                            "\n" +
                            "            @Override\n" +
                            "            public void onTextChanged(CharSequence s, int start, int before, int count) {\n" +
                            "                if (emailText.getText().toString().trim().length() > 0) {\n" +
                            "                    clearEmail.setVisibility(View.VISIBLE);\n" +
                            "                } else {\n" +
                            "                    clearEmail.setVisibility(View.INVISIBLE);\n" +
                            "                }\n" +
                            "            }\n" +
                            "\n" +
                            "            @Override\n" +
                            "            public void afterTextChanged(Editable s) {\n" +
                            "            }\n" +
                            "        });\n" +
                            "\n" +
                            "\n" +
                            "        passwdText.addTextChangedListener(new TextWatcher() {\n" +
                            "            @Override\n" +
                            "            public void beforeTextChanged(CharSequence s, int start, int count, int after) {\n" +
                            "            }\n" +
                            "\n" +
                            "            @Override\n" +
                            "            public void onTextChanged(CharSequence s, int start, int before, int count) {\n" +
                            "                if (passwdText.getText().toString().trim().length() > 0) {\n" +
                            "                    clearPasswd.setVisibility(View.VISIBLE);\n" +
                            "                } else {\n" +
                            "                    clearPasswd.setVisibility(View.INVISIBLE);\n" +
                            "                }\n" +
                            "            }\n" +
                            "\n" +
                            "            @Override\n" +
                            "            public void afterTextChanged(Editable s) {\n" +
                            "            }\n" +
                            "        });\n" +
                            "\n" +
                            "        clearEmail.setOnClickListener(new View.OnClickListener() {\n" +
                            "            @Override\n" +
                            "            public void onClick(View v) {\n" +
                            "                emailText.setText(\"\");\n" +
                            "            }\n" +
                            "        });\n" +
                            "\n" +
                            "        clearPasswd.setOnClickListener(new View.OnClickListener() {\n" +
                            "            @Override\n" +
                            "            public void onClick(View v) {\n" +
                            "                passwdText.setText(\"\");\n" +
                            "            }\n" +
                            "        });\n" +
                            "\n" +
                            "        rlEye.setOnClickListener(new View.OnClickListener() {\n" +
                            "            @Override\n" +
                            "            public void onClick(View v) {\n" +
                            "                if (!isPasswddVisible) {\n" +
                            "                    ivEyeClick.setImageResource(R.drawable.icon_eye_open);\n" +
                            "                    passwdText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());\n" +
                            "                } else {\n" +
                            "                    ivEyeClick.setImageResource(R.drawable.icon_eye_close);\n" +
                            "                    passwdText.setTransformationMethod(PasswordTransformationMethod.getInstance());\n" +
                            "                }\n" +
                            "                isPasswddVisible = !isPasswddVisible;\n" +
                            "                passwdText.postInvalidate();\n" +
                            "                String input = passwdText.getText().toString().trim();\n" +
                            "                if (!TextUtils.isEmpty(input)) {\n" +
                            "                    passwdText.setSelection(input.length());\n" +
                            "                }\n" +
                            "            }\n" +
                            "        });\n" +
                            "\n" +
                            "    }\n" +
                            "\n" +
                            "    @Override\n" +
                            "    protected void onDestroy() {\n" +
                            "        if (progressDialog != null)\n" +
                            "            progressDialog.dismiss();\n" +
                            "        super.onDestroy();\n" +
                            "    }\n" +
                            "\n" +
                            "    @Override\n" +
                            "    protected void onSaveInstanceState(Bundle savedInstanceState) {\n" +
                            "        savedInstanceState.putString(\"email\", emailText.getText().toString());\n" +
                            "        savedInstanceState.putString(\"password\", passwdText.getText().toString());\n" +
                            "        savedInstanceState.putBoolean(\"rememberDevice\", cbRemDevice.isChecked());\n" +
                            "        super.onSaveInstanceState(savedInstanceState);\n" +
                            "    }\n" +
                            "\n" +
                            "    @Override\n" +
                            "    protected void onRestoreInstanceState(Bundle savedInstanceState) {\n" +
                            "        super.onRestoreInstanceState(savedInstanceState);\n" +
                            "\n" +
                            "        emailText.setText((String) savedInstanceState.get(\"email\"));\n" +
                            "        passwdText.setText((String) savedInstanceState.get(\"password\"));\n" +
                            "        cbRemDevice.setChecked((boolean) savedInstanceState.get(\"rememberDevice\"));\n" +
                            "    }\n" +
                            "\n" +
                            "    @Override\n" +
                            "    public boolean onMenuItemClick(MenuItem item) {\n" +
                            "        return false;\n" +
                            "    }\n" +
                            "\n" +
                            "    @Override\n" +
                            "    public boolean onOptionsItemSelected(MenuItem item) {\n" +
                            "        switch (item.getItemId()) {\n" +
                            "            case android.R.id.home:\n" +
                            "\n" +
                            "                /* FYI {@link http://stackoverflow.com/questions/13293772/how-to-navigate-up-to-the-same-parent-state?rq=1} */\n" +
                            "                Intent upIntent = new Intent(this, AccountsActivity.class);\n" +
                            "                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {\n" +
                            "                    // This activity is NOT part of this app's task, so create a new task\n" +
                            "                    // when navigating up, with a synthesized back stack.\n" +
                            "                    TaskStackBuilder.create(this)\n" +
                            "                            // Add all of this activity's parents to the back stack\n" +
                            "                            .addNextIntentWithParentStack(upIntent)\n" +
                            "                            // Navigate up to the closest parent\n" +
                            "                            .startActivities();\n" +
                            "                } else {\n" +
                            "                    // This activity is part of this app's task, so simply\n" +
                            "                    // navigate up to the logical parent activity.\n" +
                            "                    // NavUtils.navigateUpTo(this, upIntent);\n" +
                            "                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);\n" +
                            "                    startActivity(upIntent);\n" +
                            "                    finish();\n" +
                            "                }\n" +
                            "\n" +
                            "                return true;\n" +
                            "        }\n" +
                            "        return super.onOptionsItemSelected(item);\n" +
                            "    }\n" +
                            "\n" +
                            "    public void onHttpsCheckboxClicked(View view) {\n" +
                            "        refreshServerUrlPrefix();\n" +
                            "    }\n" +
                            "\n" +
                            "    private void refreshServerUrlPrefix() {\n" +
                            "        boolean isHttps = httpsCheckBox.isChecked();\n" +
                            "        String url = serverText.getText().toString();\n" +
                            "        String prefix = isHttps ? HTTPS_PREFIX : HTTP_PREFIX;\n" +
                            "\n" +
                            "        String urlWithoutScheme = url.replace(HTTPS_PREFIX, \"\").replace(HTTP_PREFIX, \"\");\n" +
                            "\n" +
                            "        int oldOffset = serverText.getSelectionStart();\n" +
                            "\n" +
                            "        // Change the text\n" +
                            "        serverText.setText(prefix + urlWithoutScheme);\n" +
                            "\n" +
                            "        if (serverTextHasFocus) {\n" +
                            "            // Change the cursor position since we changed the text\n" +
                            "            if (isHttps) {\n" +
                            "                int offset = oldOffset + 1;\n" +
                            "                serverText.setSelection(offset, offset);\n" +
                            "            } else {\n" +
                            "                int offset = Math.max(0, oldOffset - 1);\n" +
                            "                serverText.setSelection(offset, offset);\n" +
                            "            }\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    private void setupServerText() {\n" +
                            "        serverText.setOnFocusChangeListener(new View.OnFocusChangeListener () {\n" +
                            "            @Override\n" +
                            "            public void onFocusChange(View v, boolean hasFocus) {\n" +
                            "                Log.d(DEBUG_TAG, \"serverText has focus: \" + (hasFocus ? \"yes\" : \"no\"));\n" +
                            "                serverTextHasFocus = hasFocus;\n" +
                            "            }\n" +
                            "        });\n" +
                            "\n" +
                            "        serverText.addTextChangedListener(new TextWatcher() {\n" +
                            "            private String old;\n" +
                            "            @Override\n" +
                            "            public void onTextChanged(CharSequence s, int start, int before, int count) {\n" +
                            "            }\n" +
                            "\n" +
                            "            @Override\n" +
                            "            public void beforeTextChanged(CharSequence s, int start, int count,\n" +
                            "                                          int after) {\n" +
                            "                old = serverText.getText().toString();\n" +
                            "            }\n" +
                            "\n" +
                            "            @Override\n" +
                            "            public void afterTextChanged(Editable s) {\n" +
                            "                // Don't allow the user to edit the \"https://\" or \"http://\" part of the serverText\n" +
                            "                String url = serverText.getText().toString();\n" +
                            "                boolean isHttps = httpsCheckBox.isChecked();\n" +
                            "                String prefix = isHttps ? HTTPS_PREFIX : HTTP_PREFIX;\n" +
                            "                if (!url.startsWith(prefix)) {\n" +
                            "                    int oldOffset = Math.max(prefix.length(), serverText.getSelectionStart());\n" +
                            "                    serverText.setText(old);\n" +
                            "                    serverText.setSelection(oldOffset, oldOffset);\n" +
                            "                }\n" +
                            "            }\n" +
                            "        });\n" +
                            "    }\n" +
                            "\n" +
                            "    /** Called when the user clicks the Login button */\n" +
                            "    public void login(View view) {\n" +
                            "        String serverURL = serverText.getText().toString().trim();\n" +
                            "        String email = emailText.getText().toString().trim();\n" +
                            "        String passwd = passwdText.getText().toString();\n" +
                            "\n" +
                            "        ConnectivityManager connMgr = (ConnectivityManager)\n" +
                            "                getSystemService(Context.CONNECTIVITY_SERVICE);\n" +
                            "        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();\n" +
                            "\n" +
                            "        if (networkInfo != null && networkInfo.isConnected()) {\n" +
                            "            if (serverURL.length() == 0) {\n" +
                            "                statusView.setText(R.string.err_server_andress_empty);\n" +
                            "                return;\n" +
                            "            }\n" +
                            "\n" +
                            "            if (email.length() == 0) {\n" +
                            "                emailText.setError(getResources().getString(R.string.err_email_empty));\n" +
                            "                return;\n" +
                            "            }\n" +
                            "\n" +
                            "            if (passwd.length() == 0) {\n" +
                            "                passwdText.setError(getResources().getString(R.string.err_passwd_empty));\n" +
                            "                return;\n" +
                            "            }\n" +
                            "\n" +
                            "            String authToken = null;\n" +
                            "            if (authTokenLayout.getVisibility() == View.VISIBLE) {\n" +
                            "                authToken = authTokenText.getText().toString().trim();\n" +
                            "                if (TextUtils.isEmpty(authToken)) {\n" +
                            "                    authTokenText.setError(getResources().getString(R.string.two_factor_auth_token_empty));\n" +
                            "                    return;\n" +
                            "                }\n" +
                            "            }\n" +
                            "\n" +
                            "            boolean rememberDevice = false;\n" +
                            "            if (cbRemDevice.getVisibility() == View.VISIBLE) {\n" +
                            "                rememberDevice = cbRemDevice.isChecked();\n" +
                            "            }\n" +
                            "            try {\n" +
                            "                serverURL = Utils.cleanServerURL(serverURL);\n" +
                            "            } catch (MalformedURLException e) {\n" +
                            "                statusView.setText(R.string.invalid_server_address);\n" +
                            "                Log.d(DEBUG_TAG, \"Invalid URL \" + serverURL);\n" +
                            "                return;\n" +
                            "            }\n" +
                            "\n" +
                            "            // force the keyboard to be hidden in all situations\n" +
                            "            if (getCurrentFocus() != null) {\n" +
                            "                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);\n" +
                            "                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);\n" +
                            "            }\n" +
                            "\n" +
                            "            loginButton.setEnabled(false);\n" +
                            "            Account tmpAccount = new Account(serverURL, email, null, false, mSessionKey);\n" +
                            "            progressDialog = new ProgressDialog(this);\n" +
                            "            progressDialog.setMessage(getString(R.string.settings_cuc_loading));\n" +
                            "            progressDialog.setCancelable(false);\n" +
                            "            ConcurrentAsyncTask.execute(new LoginTask(tmpAccount, passwd, authToken,rememberDevice));\n" +
                            "\n" +
                            "        } else {\n" +
                            "            statusView.setText(R.string.network_down);\n" +
                            "        }\n" +
                            "    }\n" +
                            "\n" +
                            "    private class LoginTask extends AsyncTask<Void, Void, String> {\n" +
                            "        Account loginAccount;\n" +
                            "        SeafException err = null;\n" +
                            "        String passwd;\n" +
                            "        String authToken;\n" +
                            "        boolean rememberDevice;\n" +
                            "\n" +
                            "        public LoginTask(Account loginAccount, String passwd, String authToken, boolean rememberDevice) {\n" +
                            "            this.loginAccount = loginAccount;\n" +
                            "            this.passwd = passwd;\n" +
                            "            this.authToken = authToken;\n" +
                            "            this.rememberDevice = rememberDevice;\n" +
                            "        }\n" +
                            "\n" +
                            "        @Override\n" +
                            "        protected void onPreExecute() {\n" +
                            "            //super.onPreExecute();\n" +
                            "            progressDialog.show();\n" +
                            "        }\n" +
                            "\n" +
                            "        @Override\n" +
                            "        protected String doInBackground(Void... params) {\n" +
                            "            if (params.length != 0)\n" +
                            "                return \"Error number of parameter\";\n" +
                            "\n" +
                            "            return doLogin();\n" +
                            "        }\n" +
                            "\n" +
                            "        private void resend() {\n" +
                            "            ConcurrentAsyncTask.execute(new LoginTask(loginAccount, passwd, authToken, rememberDevice));\n" +
                            "        }\n" +
                            "\n" +
                            "        @Override\n" +
                            "        protected void onPostExecute(final String result) {\n" +
                            "            progressDialog.dismiss();\n" +
                            "            if (err == SeafException.sslException) {\n" +
                            "                authTokenLayout.setVisibility(View.GONE);\n" +
                            "                cbRemDevice.setVisibility(View.GONE);\n" +
                            "                SslConfirmDialog dialog = new SslConfirmDialog(loginAccount,\n" +
                            "                        new SslConfirmDialog.Listener() {\n" +
                            "                            @Override\n" +
                            "                            public void onAccepted(boolean rememberChoice) {\n" +
                            "                                CertsManager.instance().saveCertForAccount(loginAccount, rememberChoice);\n" +
                            "                                resend();\n" +
                            "                            }\n" +
                            "\n" +
                            "                            @Override\n" +
                            "                            public void onRejected() {\n" +
                            "                                statusView.setText(result);\n" +
                            "                                loginButton.setEnabled(true);\n" +
                            "                            }\n" +
                            "                        });\n" +
                            "                dialog.show(getSupportFragmentManager(), SslConfirmDialog.FRAGMENT_TAG);\n" +
                            "                return;\n" +
                            "            } else if (err == SeafException.twoFactorAuthTokenMissing) {\n" +
                            "                // show auth token input box\n" +
                            "                authTokenLayout.setVisibility(View.VISIBLE);\n" +
                            "                cbRemDevice.setVisibility(View.VISIBLE);\n" +
                            "                cbRemDevice.setChecked(false);\n" +
                            "                authTokenText.setError(getString(R.string.two_factor_auth_error));\n" +
                            "            } else if (err == SeafException.twoFactorAuthTokenInvalid) {\n" +
                            "                // show auth token input box\n" +
                            "                authTokenLayout.setVisibility(View.VISIBLE);\n" +
                            "                cbRemDevice.setVisibility(View.VISIBLE);\n" +
                            "                cbRemDevice.setChecked(false);\n" +
                            "                authTokenText.setError(getString(R.string.two_factor_auth_invalid));\n" +
                            "            } else {\n" +
                            "                authTokenLayout.setVisibility(View.GONE);\n" +
                            "                cbRemDevice.setVisibility(View.GONE);\n" +
                            "            }\n" +
                            "\n" +
                            "            if (result != null && result.equals(\"Success\")) {\n" +
                            "\n" +
                            "                Intent retData = new Intent();\n" +
                            "                retData.putExtras(getIntent());\n" +
                            "                retData.putExtra(android.accounts.AccountManager.KEY_ACCOUNT_NAME, loginAccount.getSignature());\n" +
                            "                retData.putExtra(android.accounts.AccountManager.KEY_AUTHTOKEN, loginAccount.getToken());\n" +
                            "                retData.putExtra(android.accounts.AccountManager.KEY_ACCOUNT_TYPE, getIntent().getStringExtra(SeafileAuthenticatorActivity.ARG_ACCOUNT_TYPE));\n" +
                            "                retData.putExtra(SeafileAuthenticatorActivity.ARG_EMAIL, loginAccount.getEmail());\n" +
                            "                retData.putExtra(SeafileAuthenticatorActivity.ARG_AUTH_SESSION_KEY, loginAccount.getSessionKey());\n" +
                            "                retData.putExtra(SeafileAuthenticatorActivity.ARG_SERVER_URI, loginAccount.getServer());\n" +
                            "                retData.putExtra(TWO_FACTOR_AUTH, cbRemDevice.isChecked());\n" +
                            "                setResult(RESULT_OK, retData);\n" +
                            "                finish();\n" +
                            "            } else {\n" +
                            "                statusView.setText(result);\n" +
                            "            }\n" +
                            "            loginButton.setEnabled(true);\n" +
                            "        }\n" +
                            "\n" +
                            "        private String doLogin() {\n" +
                            "            SeafConnection sc = new SeafConnection(loginAccount);\n" +
                            "\n" +
                            "            try {\n" +
                            "                // if successful, this will place the auth token into \"loginAccount\"\n" +
                            "                if (!sc.doLogin(passwd, authToken, rememberDevice))\n" +
                            "                    return getString(R.string.err_login_failed);\n" +
                            "\n" +
                            "                // fetch email address from the server\n" +
                            "                DataManager manager = new DataManager(loginAccount);\n" +
                            "                AccountInfo accountInfo = manager.getAccountInfo();\n" +
                            "\n" +
                            "                if (accountInfo == null)\n" +
                            "                    return \"Unknown error\";\n" +
                            "\n" +
                            "                // replace email address/username given by the user with the address known by the server.\n" +
                            "                loginAccount = new Account(loginAccount.server, accountInfo.getEmail(), loginAccount.token, false, loginAccount.sessionKey);\n" +
                            "\n" +
                            "                return \"Success\";\n" +
                            "\n" +
                            "            } catch (SeafException e) {\n" +
                            "                err = e;\n" +
                            "                if (e == SeafException.sslException) {\n" +
                            "                    return getString(R.string.ssl_error);\n" +
                            "                } else if (e == SeafException.twoFactorAuthTokenMissing) {\n" +
                            "                    return getString(R.string.two_factor_auth_error);\n" +
                            "                } else if (e == SeafException.twoFactorAuthTokenInvalid) {\n" +
                            "                    return getString(R.string.two_factor_auth_invalid);\n" +
                            "                }\n" +
                            "                switch (e.getCode()) {\n" +
                            "                    case HttpURLConnection.HTTP_BAD_REQUEST:\n" +
                            "                        return getString(R.string.err_wrong_user_or_passwd);\n" +
                            "                    case HttpURLConnection.HTTP_NOT_FOUND:\n" +
                            "                        return getString(R.string.invalid_server_address);\n" +
                            "                    default:\n" +
                            "                        return e.getMessage();\n" +
                            "                }\n" +
                            "            } catch (JSONException e) {\n" +
                            "                return e.getMessage();\n" +
                            "            }\n" +
                            "        }\n" +
                            "    }\n" +
                            "}");

                    cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {

                        if (classe.getExtendedTypes().size()>0) {


                            if (classe.getExtendedTypes().get(0).toString().contains("Activity") || classe.getExtendedTypes().get(0).toString().contains("Fragment") || classe.getExtendedTypes().get(0).toString().contains("Adapter")) {

                                //Contados de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                                //chama aqui o WMC
                                WMC wmc = new WMC(cUnit);
                                wmc.run();

                                if (wmc.getCc() > 56) {
                                    JsonOut.setTipoSmell("JAVA");
                                    JsonOut.setLinha(classe.getRange().get().begin.toString());
//                                    JsonOut.setArquivo(nomeArquivo);
                                    ListJsonSmell.add(JsonOut);
                                    totalSmells++;
                                }

                                //----------------------------------------------------------------foi removido aqui
                                NodeList<BodyDeclaration<?>> membros = classe.getMembers();
                                for (BodyDeclaration<?> membro : membros) {
                                    //Verifica se o membro é um método
                                    if (membro.isMethodDeclaration()) {

                                        membro.findAll(IfStmt.class).forEach(item -> {
                                            System.out.println("Brain UI Component detectado na classe " + item.getRange().get().begin + " (lógica utilizando if detectada)");
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(item.getRange().get().begin.toString());
//                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        });

                                        membro.findAll(SwitchEntryStmt.class).forEach(item -> {
                                            System.out.println("Brain UI Component detectado na classe " + item.getRange().get().begin + " (lógica utilizando Switch/Case detectada)");
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(item.getRange().get().begin.toString());
//                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        });
                                    }
                                }

                                //----------------------------------------------------------------foi removido aqui


//                            //Aqui conta para verificar o threshold de fieldstatic ... mas tem que pensar nisso.
                                ImportantSmells.contadorFieldStatic = 0;
                                classe.getFields().forEach(item -> {
                                    if (item.isFieldDeclaration() && item.isStatic()) {
                                        ImportantSmells.contadorFieldStatic++;
                                    }
                                });



                                    //Static Fields
                                    classe.getFields().forEach(item -> {
                                        if (item.isFieldDeclaration() && item.isStatic()) {
                                            System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Campo Static) " + item.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(item.getRange().get().begin.toString());
//                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }
                                    });


                                //Procura Libs IO no TIPO  em declaração de campos
                                classe.getFields().forEach(campos -> {
                                    if (IOClass.getIOClass().contains(campos.getElementType().toString())) {
                                        System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) ATRIBUTOS DA CLASSE " + campos.getRange().get().begin);
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(campos.getRange().get().begin.toString());
//                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }
                                });

                                //Procura Libs de IO no TIPO em declaração  de Métodos
                                classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                                    IOClass.getIOClass().forEach(item -> {
                                        //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                        //COMO VAI PROCURAR AQUI ??
                                        if (metodo.getParameters().contains(item)) {
                                            System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) nos parâmetros do método " + metodo.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(metodo.getRange().get().begin.toString());
//                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }

//                                    //Procura Libs de IO no TIPO em retorno  de Métodos
//                                    if (metodo.getType().toString().contains(item)) {
//                                        System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) no retorno do método " + metodo.getRange().get().begin);
//                                        System.out.println("---------------------------------------------------------------------------------------");
//                                        JsonOut.setTipoSmell("JAVA");
//                                        JsonOut.setLinha(metodo.getRange().get().begin.toString());
//                                        JsonOut.setArquivo(nomeArquivo);
//                                        ListJsonSmell.add(JsonOut);
//                                        totalSmells++;
//                                    }

                                        //Procura Libs IO no TIPO  em declaração de campos
                                        metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                            if (campos.getElementType().toString().contains(item)) {
                                                System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) FIELD Declaration " + campos.getRange().get().begin);
                                                System.out.println("---------------------------------------------------------------------------------------");
                                                JsonOut.setTipoSmell("JAVA");
                                                JsonOut.setLinha(campos.getRange().get().begin.toString());
//                                                JsonOut.setArquivo(nomeArquivo);
                                                ListJsonSmell.add(JsonOut);
                                                totalSmells++;
                                            }
                                        });

                                    });
                                });
                            }
                        }
                    });


                    System.out.println("quantidade identificada " + totalSmells);

            JsonOut.saveJson(ListJsonSmell,"BrainUIComponent.json");

        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return totalSmells;
    }


    //Adapter consumista
    public static long FoolAdapterOraculo(){
        try {
            contadorArquivosAnalisados = 0;
            ListSmells.clear();
            //arquivosAnalise.clear();
            totalSmells = 0;


            carregaArquivosJAVAAnalise(new File("/Users/rafaeldurelli/Desktop/Repositorio-TESTE/opentasks"));


            System.out.println(ListArquivosAnaliseJava);

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
                            //eu: Durelli alterei para verificar se contem problema aqui
                            if (implementacao.getName().getIdentifier().contains("Adapter")) {

                                //Contados de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados +1;

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
                                                    if(statement.toString().contains("inflater")) {
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

            System.out.println("Total de FOOL adapter identificado "+ totalSmells);

            JsonOut.saveJson(ListJsonSmell,"FoolAdapter.json");
            return totalSmells;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return totalSmells;
        }
    }


    //Adapter consumista
    public static long FoolAdapter(String pathApp){
        try {
            contadorArquivosAnalisados = 0;
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
                            //eu: Durelli alterei para verificar se contem problema aqui
                            if (implementacao.getName().getIdentifier().contains("Adapter")) {

                                //Contados de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados +1;

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
                                                        if(statement.toString().contains("inflater")) {
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


    public static long FlexAdapterOraculo() {
        try {

            contadorArquivosAnalisados = 0;

            ListSmells.clear();
            //arquivosAnalise.clear();
            totalSmells = 0;

            carregaArquivosJAVAAnalise(new File("/Users/rafaeldurelli/Desktop/Repositorio-TESTE/opentasks"));


            System.out.println(ListArquivosAnaliseJava);


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

                                //Contados de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;


//                                System.out.println("FLEX ADAPTER IDENTIFICADO");
//                                JsonOut.setTipoSmell("JAVA");
//                                JsonOut.setLinha(implementacao.getRange().get().begin.toString());
//                                JsonOut.setArquivo(arquivo);
//                                ListJsonSmell.add(JsonOut);
//                                totalSmells++;


//                                //Se chegou até aqui, temos certeza de que é um adapter.
//                                //Se a classe que extende do BaseAdapter tiver algum método que não seja sobrescrever um método de interface, é um FlexAdapter.
//                                //Pegamos todos os membros da classe
                                NodeList<BodyDeclaration<?>> membros = classe.getMembers();
                                for (BodyDeclaration<?> membro : membros) {
//                                    //Verifica se o membro é um método
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


                                        membro.findAll(ForStmt.class).forEach(item -> {
                                            System.out.println("Flex Adapter detectado na classe " + item.getRange() + " (FOR detectada)");
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

                    JsonOut.saveJson(ListJsonSmell, "FlexAdapter.json");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        System.out.println("Total de smells identificado " + totalSmells);

        return totalSmells;
    }



    public static long FlexAdapter(String pathApp) {
        try {

            contadorArquivosAnalisados = 0;

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

                                //Contados de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados +1;

                                WMC wmc = new WMC(compilationunit);
                                wmc.run();

                                if (wmc.getCc() > 56) {

                                    System.out.println("FLEX ADAPTER IDENTIFICADO");
                                    JsonOut.setTipoSmell("JAVA");
                                    JsonOut.setLinha(implementacao.getRange().get().begin.toString());
                                    JsonOut.setArquivo(arquivo);
                                    ListJsonSmell.add(JsonOut);
                                    totalSmells++;
                                }

//                                //Se chegou até aqui, temos certeza de que é um adapter.
//                                //Se a classe que extende do BaseAdapter tiver algum método que não seja sobrescrever um método de interface, é um FlexAdapter.
//                                //Pegamos todos os membros da classe
//                                NodeList<BodyDeclaration<?>> membros = classe.getMembers();
//                                for (BodyDeclaration<?> membro : membros) {
//                                    //Verifica se o membro é um método
//                                    if (membro.isMethodDeclaration()) {
//
//                                        membro.findAll(IfStmt.class).forEach(item -> {
//                                            System.out.println("Flex Adapter detectado na classe " + item.getRange() + " (lógica utilizando if detectada)");
//                                            System.out.println("---------------------------------------------------------------------------------------");
//                                            JsonOut.setTipoSmell("JAVA");
//                                            JsonOut.setLinha(item.getRange().get().begin.toString());
//                                            JsonOut.setArquivo(arquivo);
//                                            ListJsonSmell.add(JsonOut);
//                                            totalSmells++;
//                                        });
//
//                                        membro.findAll(SwitchStmt.class).forEach(item -> {
//                                            System.out.println("Flex Adapter detectado na classe " + item.getRange() + " (lógica utilizando Switch/Case detectada)");
//                                            System.out.println("---------------------------------------------------------------------------------------");
//                                            JsonOut.setTipoSmell("JAVA");
//                                            JsonOut.setLinha(item.getRange().get().begin.toString());
//                                            JsonOut.setArquivo(arquivo);
//                                            ListJsonSmell.add(JsonOut);
//                                            totalSmells++;
//                                        });
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


    //Longo Recurso de Estilo
    public static long GodStyleResource(String pathApp,int threshold) {
        try {
            contadorArquivosAnalisados = 0;
            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);
            int qtdLimiteStilos = threshold;
            int qtdFilesStyle = 0;

            for (int cont = 0; cont < (ListArquivosAnaliseXML.toArray().length - 1); cont++) {

                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados +1;

                try {
                    //System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);
                    //vamos mudar aqui

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
    public static long godStringResource(String pathApp) {
        try {
            contadorArquivosAnalisados = 0;

            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            int qtdFilesString = 0;

            for (int cont = 0; cont < (ListArquivosAnaliseXML.toArray().length - 1); cont++) {
                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados +1;

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
                        System.out.println("Recurso de String Baguncado detectado (existe apenas um arquivo para strings no aplicativo  ");
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

            JsonOut.saveJson(ListJsonSmell,"godStringResource.json");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }



    //Layout Profundamente Aninhado
    public static long DeepNestedLayout(String pathApp, int threshold) {
        try {
            contadorArquivosAnalisados = 0;

            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            for (int cont = 0; cont < ListArquivosAnaliseXML.toArray().length; cont++) {

                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados +1;


                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseXML.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (ListArquivosAnaliseXML.toArray()[cont].toString().contains(File.separator + "layout" + File.separator )) {
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

    //Atributo de estilo duplicado
    public static long DuplicateStyleAttributes(String pathApp) {
        try {
            contadorArquivosAnalisados = 0;

            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            for (int cont = 0; cont < ListArquivosAnaliseXML.toArray().length; cont++) {
                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados +1;

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
            contadorArquivosAnalisados = 0;

            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            for (int cont = 0; cont < ListArquivosAnaliseXML.toArray().length; cont++) {
                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados +1;

                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseXML.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (ListArquivosAnaliseXML.toArray()[cont].toString().contains(File.separator + "layout" + File.separator )) {
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
    public static long inappropriateStringReuse(String pathApp){
        try{
            contadorArquivosAnalisados = 0;

//            arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;
            textStringArquivo.clear();
            //listar(new File(pathApp),XML);

            for (int cont = 0; cont < ListArquivosAnaliseXML.toArray().length; cont++) {

                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados +1;

                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseXML.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (ListArquivosAnaliseXML.toArray()[cont].toString().contains(File.separator + "layout" + File.separator )) {
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

            JsonOut.saveJson(ListJsonSmell,"inappropriateStringReuse.json");

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


    //No uso de fragment
    public static long NotFragmentOraculo(){
        try{
            contadorArquivosAnalisados = 0;

            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;


            carregaArquivosJAVAAnalise(new File("/Users/rafaeldurelli/Desktop/Repositorio-TESTE/opentasks"));

            //listar(new File(pathApp),JAVA);

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {

                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados +1;

                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit cUnit = JavaParser.parse(f);

                    List<String> ViewsAndroid = new ArrayList<String>();
                    ViewsAndroid.add("TextView");
                    ViewsAndroid.add("EditText");
                    ViewsAndroid.add("Spinner");
                    ViewsAndroid.add("CheckedTextView");
                    ViewsAndroid.add("Button");
                    ViewsAndroid.add("CheckedTextView");
                    ViewsAndroid.add("Chronometer");
                    ViewsAndroid.add("DigitalClock");
                    ViewsAndroid.add("TextClock");

                    ViewsAndroid.add("AutoCompleteTextView");
                    ViewsAndroid.add("CheckBox");
                    ViewsAndroid.add("CompoundButton");
                    ViewsAndroid.add("ExtractEditText");
                    ViewsAndroid.add("MultiAutoCompleteTextView");
                    ViewsAndroid.add("RadioButton");
                    ViewsAndroid.add("Switch");
                    ViewsAndroid.add("ToggleButton");
                    ViewsAndroid.add("AnalogClock");
                    ViewsAndroid.add("ImageView");
                    ViewsAndroid.add("KeyboardView");
                    ViewsAndroid.add("MediaRouteButton");
                    ViewsAndroid.add("ProgressBar");
                    ViewsAndroid.add("Space");
                    ViewsAndroid.add("SurfaceView");
                    ViewsAndroid.add("TextureView");
                    ViewsAndroid.add("ViewGroup");
                    ViewsAndroid.add("ViewStub");
                    ViewsAndroid.add("View");
                    ViewsAndroid.add("RemoteViews");



                    //Não existir fragmentos na aplicação

                    // Uso de Views(EditText, Spinner, ou Outras Views Diretamente pela activity)
                    cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {
                        if (classe.getExtendedTypes().size()>0){
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
                        }
                    });
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            System.out.println("Total de smells identificados " + totalSmells);

            JsonOut.saveJson(ListJsonSmell,"NotFragment.json");


        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return totalSmells;
    }


    //No uso de fragment
    public static long NotFragment(String pathApp){
        try{
            contadorArquivosAnalisados = 0;

            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),JAVA);

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {

                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados +1;

                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit cUnit = JavaParser.parse(f);

                    List<String> ViewsAndroid = new ArrayList<String>();
                    ViewsAndroid.add("TextView");
                    ViewsAndroid.add("EditText");
                    ViewsAndroid.add("Spinner");

                    //Não existir fragmentos na aplicação

                    // Uso de Views(EditText, Spinner, ou Outras Views Diretamente pela activity)
                    cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {
                        if (classe.getExtendedTypes().size()>0){
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

    //Imagem Faltante
    public static long NotFoundImage(String pathApp){
        contadorArquivosAnalisados = 0;

        FilesIMG.clear();
        totalSmells = 0;
        ListSmells.clear();
        ObtemImagensList(new File(pathApp));

        //Contados de arquivos analisados
        contadorArquivosAnalisados = FilesIMG.size();

        FilesIMG.forEach(caminho->{

            File directory = new File(caminho);
            for(File arquivo : directory.listFiles()){

                try{
                    FilesIMG.forEach(item->{

                        File arquivoImg = new File(item + ""+File.separator+"" + arquivo.getName());

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
        return ImportantSmells.totalSmells;
    }

    //Listener Oculto
    public static long HiddenListener(String pathApp){
        try{
            contadorArquivosAnalisados = 0;

            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            for (int cont = 0; cont < ListArquivosAnaliseXML.toArray().length; cont++) {

                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados +1;

                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseXML.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (ListArquivosAnaliseXML.toArray()[cont].toString().contains(File.separator + "layout" + File.separator )) {
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

            JsonOut.saveJson(ListJsonSmell,"HiddenListener.json");

            System.out.println("---------------------------------------------------------------------------------------");

        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return totalSmells;
    }

    //Uso excessivo de Fragments
    public static long ExcessiveFragment(String pathApp, long threshold) throws IOException {

        contadorArquivosAnalisados = 0;

        //arquivosAnalise.clear();
        ListSmells.clear();
        totalSmells = 0;

//        listar(new File(pathApp),JAVA);
        long totalFragments = 0;
        List<ReusoStringData> listaExcessiveFragment = new ArrayList<ReusoStringData>();

        for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
            //Contados de arquivos analisados
            contadorArquivosAnalisados = contadorArquivosAnalisados + 1;
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
                                System.err.println("É um fragmento sim" + classe.getName().getIdentifier());
                            }
                        }
                    }
                }

                if (totalFragments >= threshold) {
                    System.out.println("Uso Excessivo de Fragment " + "(Mais de " + threshold + " Fragments no aplicativo)");
                    JsonOut.setTipoSmell("XML");
                    JsonOut.setArquivo("");
                    ListJsonSmell.add(JsonOut);
                    totalSmells++;
                }


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

