package AndroidDetector;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class ImportantSmells {
    private static long qtdSubelementos = 0;
    private static File arquivos[];
    private static File diretorio = null;
    private static SAXBuilder sb = new SAXBuilder();
    public static  Boolean classeValida = true;
    public static List<File> arquivosAnalise =  new ArrayList<File>();
    public static final String JAVA = ".java";
    public static final String XML = ".xml";

    private static OutputSmells JsonOut = new  OutputSmells();
    private static List<OutputSmells> ListJsonSmell = new ArrayList<OutputSmells>();

    private  static List<OutputSmells> ListSmells = new ArrayList<OutputSmells>();

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

    public static void CoupledUIComponent(String pathApp) throws FileNotFoundException {
        ListSmells.clear();
        arquivosAnalise.clear();
        listar(new File(pathApp),JAVA);

        for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
            classeValida = true;
            String nomeArquivo = arquivosAnalise.toArray()[cont].toString();
            System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);

            System.out.println("---------------------------------------------------------------------------------------");

            File f = new File(arquivosAnalise.toArray()[cont].toString());
            CompilationUnit cu = JavaParser.parse(f);

            ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<ClassOrInterfaceDeclaration>();
            NodeList<TypeDeclaration<?>> types = cu.getTypes();
            for (int i = 0; i < types.size(); i++) {
                classes.add((ClassOrInterfaceDeclaration) types.get(i));
            }

            for (ClassOrInterfaceDeclaration classe : classes) {
                NodeList<ClassOrInterfaceType> implementacoes = classe.getExtendedTypes();
                if(implementacoes.size() != 0){
                    for (ClassOrInterfaceType implementacao : implementacoes) {
                        if (implementacao.getName().getIdentifier().contains("Fragment") || implementacao.getName().getIdentifier().contains("Adapter") || implementacao.getName().getIdentifier().contains("Activity")) {
                            classe.getFields().forEach(item->{
                                if(item.getElementType().toString().contains("Activity")){
                                    System.out.println("Componente de UI Acoplado " + item.getElementType().toString() + item.getRange());
                                    JsonOut.setTipoSmell("XML");
                                    JsonOut.setArquivo(nomeArquivo);
                                    ListJsonSmell.add(JsonOut);
                                }
                            });
                        }
                    }
                }
            }
        }
    }


    /*
    public static void CoupledUIComponent(String pathApp) {
        try {
            diretorio = new File(pathApp);
            arquivos = diretorio.listFiles();

            for (int cont = 0; cont < arquivos.length; cont++) {
                System.out.println("Arquivo analisado:" + arquivos[cont]);
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(arquivos[cont].toString());
                Document d = sb.build(f);
                Element rootElmnt = d.getRootElement();
                List elements = rootElmnt.getChildren();

                for (int i = 0; i < elements.size(); i++) {
                    org.jdom2.Element el = (org.jdom2.Element) elements.get(i);

                    List<Element> SubElements = el.getChildren();

                    for (int j = 0; j < SubElements.size(); j++) {
                        org.jdom2.Element elChildren = (org.jdom2.Element) SubElements.get(j);
                        List<org.jdom2.Attribute> listAttr = (List<org.jdom2.Attribute>) elChildren.getAttributes();

                        if(elChildren.getName() == "fragment") {
                            Boolean style = false;
                            for (org.jdom2.Attribute item : listAttr) {
                                if (item.getName() == "name") {
                                    style = true;
                                }
                            }
                            if (!style) {
                                System.out.println("Componente de UI Acoplado " + elChildren.getName() + " - Considere utilizar viewGroup");
                                JsonOut.setTipoSmell("XML");
                                JsonOut.setArquivo(arquivos[cont].toString());
                                ListJsonSmell.add(JsonOut);
                            }
                        }
                    }
                }

                JsonOut.saveJson(ListJsonSmell,"CoupledUIComponent.json");

                System.out.println("---------------------------------------------------------------------------------------");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    */

    public static void SuspiciousBehavior(String pathApp) throws FileNotFoundException {
        ListSmells.clear();
        arquivosAnalise.clear();
        listar(new File(pathApp),JAVA);

        for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
            classeValida = true;
            String nomeArquivo = arquivosAnalise.toArray()[cont].toString();
            System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
            System.out.println("---------------------------------------------------------------------------------------");

            File f = new File(arquivosAnalise.toArray()[cont].toString());
            CompilationUnit cu = JavaParser.parse(f);

            ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<ClassOrInterfaceDeclaration>();
            NodeList<TypeDeclaration<?>> types = cu.getTypes();
            for (int i = 0; i < types.size(); i++) {
                classes.add((ClassOrInterfaceDeclaration) types.get(i));
            }

            for (ClassOrInterfaceDeclaration classe : classes) {
                NodeList<ClassOrInterfaceType> implementacoes = classe.getExtendedTypes();
                if(implementacoes.size() != 0){
                    for (ClassOrInterfaceType implementacao : implementacoes) {
                        if (implementacao.getName().getIdentifier().equals("BaseActivity") || implementacao.getName().getIdentifier().equals("Activity") || implementacao.getName().getIdentifier().equals("Fragments") || implementacao.getName().getIdentifier().equals("BaseAdapter") || implementacao.getName().getIdentifier().endsWith("Listener")) {
                            classeValida  = true;
                        }
                    }
                }
                else{
                    classeValida  = false;
                }
            }

            //Se não for válida activity entre outros pula o laço para o próximo arquivo
            if(!classeValida){
                continue;
            }

            for (TypeDeclaration<?> typeDec : cu.getTypes()) {
                //System.out.println(typeDec.getName().toString());
                for (BodyDeclaration<?> member : typeDec.getMembers()) {

                    member.findAll(MethodDeclaration.class).forEach(item-> {
                        //System.out.println(item);
                        item.getChildNodes().forEach(sub ->{
                            sub.findAll(MethodDeclaration.class).forEach(i->{
                                System.out.println("Comportamento suspeito detectado  - " + i.getName() + " - " + i.getRange().get().begin);
                            });
                        });
                    });


                    member.toFieldDeclaration().ifPresent(field -> {
                        for (VariableDeclarator variable : field.getVariables()) {
                            //Print the field's class typr
                            //System.out.println(variable.getType());

                            if(variable.getType().toString().contains("Listener")) {
                                System.out.println("Comportamento suspeito detectado  - " + variable.getType() + " - " + variable.getRange().get().begin);
                            }
                            //Print the field's name
                            //System.out.println(variable.getName());
                            //Print the field's init value, if not null

                            variable.getInitializer().ifPresent(initValue -> {
                                if(initValue.isLambdaExpr()){
                                    System.out.println("Comportamento suspeito detectado  - " + initValue.getRange());
                                    JsonOut.setTipoSmell("JAVA");
                                    JsonOut.setLinha(initValue.getRange().get().begin.toString());
                                    JsonOut.setArquivo(nomeArquivo);
                                    ListJsonSmell.add(JsonOut);
                                }
                            });
                        }
                    });
                }
            }
        }

        JsonOut.saveJson(ListJsonSmell,"SuspiciousBehavior.json");
    }

    public static void BrainUIComponent(String pathApp) {
        try {
            arquivosAnalise.clear();
            listar(new File(pathApp),JAVA);

            for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
                System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                String nomeArquivo = arquivosAnalise.toArray()[cont].toString();
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(arquivosAnalise.toArray()[cont].toString());
                CompilationUnit cUnit = JavaParser.parse(f);
                cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe->{
                    if(classe.getExtendedTypes().get(0).toString().contains("Activity") || classe.getExtendedTypes().get(0).toString().contains("Fragment") || classe.getExtendedTypes().get(0).toString().contains("Adapter") ) {
                        classe.findAll(FieldDeclaration.class).forEach(campos -> {
                            if (campos.getElementType().toString().contains("PFASQLiteHelper") || campos.getElementType().toString().contains("SQLite")) {
                                System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) " + campos.getRange());
                                System.out.println("---------------------------------------------------------------------------------------");
                                JsonOut.setTipoSmell("JAVA");
                                JsonOut.setLinha(campos.getRange().get().begin.toString());
                                JsonOut.setArquivo(nomeArquivo);
                                ListJsonSmell.add(JsonOut);
                            }
                        });

                        classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                            metodo.findAll(FieldDeclaration.class).forEach(campos->{
                                if (campos.getElementType().toString().contains("PFASQLiteHelper") || campos.getElementType().toString().contains("SQLite")) {
                                    System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) " + campos.getRange());
                                    System.out.println("---------------------------------------------------------------------------------------");
                                    JsonOut.setTipoSmell("JAVA");
                                    JsonOut.setLinha(campos.getRange().get().begin.toString());
                                    JsonOut.setArquivo(nomeArquivo);
                                    ListJsonSmell.add(JsonOut);
                                }
                            });
                        });
                    }
                });
            }

            JsonOut.saveJson(ListJsonSmell,"BrainUIComponent.json");

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void FlexAdapter(String pathApp) {
        try {
            arquivosAnalise.clear();
            listar(new File(pathApp),JAVA);

            for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
                System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                System.out.println("---------------------------------------------------------------------------------------");
                String arquivo = arquivosAnalise.toArray()[cont].toString();
                File f = new File(arquivosAnalise.toArray()[cont].toString());
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

                                    membro.findAll(IfStmt.class).forEach(item->{
                                        System.out.println("Flex Adapter detectado na classe " + item.getRange() + " (lógica utilizando if detectada)");
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(item.getRange().get().begin.toString());
                                        JsonOut.setArquivo(arquivo);
                                        ListJsonSmell.add(JsonOut);
                                    });

                                    membro.findAll(SwitchEntryStmt.class).forEach(item->{
                                        System.out.println("Flex Adapter detectado na classe " + item.getRange() + " (lógica utilizando Switch/Case detectada)");
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(item.getRange().get().begin.toString());
                                        JsonOut.setArquivo(arquivo);
                                        ListJsonSmell.add(JsonOut);
                                    });
                                }
                            }
                        }
                    }
                }
            }

            JsonOut.saveJson(ListJsonSmell,"FlexAdapter.json");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }


    public static void GodStyleResource(String pathApp) {
        try {
            arquivosAnalise.clear();
            listar(new File(pathApp),XML);
            int qtdLimiteStilos = 5;
            int qtdFilesStyle = 0;

            for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
                System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(arquivosAnalise.toArray()[cont].toString());

                //LER TODA A ESTRUTURA DO XML
                Document d = sb.build(f);                

                if(d.getRootElement().getChildren().get(0).getName() == "style") {
                    qtdFilesStyle = qtdFilesStyle +1;
                }

                if((qtdFilesStyle == 1) || (d.getRootElement().getChildren().size() > qtdLimiteStilos )){
                    System.out.println("Longo recurso de Estilo detectado (existe apenas um arquivo para estilos no aplicativo que possui " + d.getRootElement().getChildren().size() + " estilos)");
                    System.out.println("---------------------------------------------------------------------------------------");
                    JsonOut.setTipoSmell("XML");
                    JsonOut.setArquivo(arquivosAnalise.toArray()[cont].toString());
                    ListJsonSmell.add(JsonOut);
                }
            }

            JsonOut.saveJson(ListJsonSmell,"GodStyleResource.json");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void DeepNestedLayout(String pathApp) {
        try {
            arquivosAnalise.clear();
            listar(new File(pathApp),XML);

            for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
                System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(arquivosAnalise.toArray()[cont].toString());

                //LER TODA A ESTRUTURA DO XML
                Document d = sb.build(f);

                if (arquivosAnalise.toArray()[cont].toString().contains("\\layout\\")) {
                    //ACESSAR O ROOT ELEMENT
                    Element rootElmnt = d.getRootElement();

                    //BUSCAR ELEMENTOS FILHOS DA TAG
                    List elements = rootElmnt.getChildren();

                    recursiveChildrenElement(elements);

                    System.out.println("---------------------------------------------------------------------------------------");

                }
            }

            JsonOut.saveJson(ListJsonSmell,"DeepNestedLayout.json");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void DuplicateStyleAttributes(String pathApp) {
        try {
            arquivosAnalise.clear();
            listar(new File(pathApp),XML);

            for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
                System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(arquivosAnalise.toArray()[cont].toString());

                //LER TODA A ESTRUTURA DO XML
                Document d = sb.build(f);

                //if (d.getRootElement().getChildren().get(0).getName().toString() == "style") {

                    List<String> listSmellsEcontradas = new ArrayList<String>();

                    for (int i = 0; i < d.getRootElement().getChildren().size(); i++) {
                        List<Element> filhos = d.getRootElement().getChildren();
                        for (int j = 0; j < filhos.size(); j++) {
                            List<Attribute> attr = filhos.get(j).getAttributes();
                            for (Attribute atributo : attr) {
                                String atributo_atual = atributo.toString();

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
                                                    JsonOut.setArquivo(arquivosAnalise.toArray()[cont].toString());
                                                    ListJsonSmell.add(JsonOut);
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                //}
            }

            JsonOut.saveJson(ListJsonSmell,"DuplicateStyleAttributes.json");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

        private static void recursiveChildrenElement(List elements) {
            for (int i = 0; i < elements.size(); i++) {

                org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                List SubElements = el.getChildren();

                if (SubElements.size() > 0) {
                    qtdSubelementos = qtdSubelementos + 1;
                    recursiveChildrenElement(SubElements);
                } else {
                    if (qtdSubelementos > 3) {
                        System.out.println("Layout Profundamente Aninhado encontrado " + el.getName() + "(Mais de três níveis)");
                        JsonOut.setTipoSmell("XML");
                        JsonOut.setArquivo("");
                        ListJsonSmell.add(JsonOut);
                        break;
                    }
                }
            }
        }
    }

