package AndroidDetector;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.support.SAXTarget;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ImportantSmells {
    private static long qtdSubelementos = 0;
    private static File arquivos[];
    private static File diretorio = null;
    private static SAXBuilder sb = new SAXBuilder();


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
                            }
                        }
                    }
                }


                System.out.println("---------------------------------------------------------------------------------------");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void SuspiciousBehavior(String pathApp) throws FileNotFoundException {
        diretorio = new File(pathApp);
        arquivos = diretorio.listFiles();

        for (int cont = 0; cont < arquivos.length; cont++) {
            System.out.println("Arquivo analisado:" + arquivos[cont]);
            System.out.println("---------------------------------------------------------------------------------------");

            File f = new File(arquivos[cont].toString());
            CompilationUnit compilationunit = JavaParser.parse(f);



            ClassOrInterfaceDeclaration n = new ClassOrInterfaceDeclaration();

            List<LocalClassDeclarationStmt> classes = compilationunit.findAll(LocalClassDeclarationStmt.class);



            for (LocalClassDeclarationStmt item : classes){
                System.out.println(compilationunit.getTypes().get(0).getName().getIdentifier());
                if (compilationunit.getTypes().get(0).getName().getIdentifier().equals("AppCompatActivity") || compilationunit.getTypes().get(0).getName().getIdentifier().equals("Activity") || compilationunit.getTypes().get(0).getName().getIdentifier().equals("Fragment") || compilationunit.getTypes().get(0).getName().getIdentifier().equals("BaseAdapter") ) {
                    System.out.println("Comportamento suspeito encontrado  na classe " + item.getClassDeclaration().getName() + " - " + item.getRange());
                }
            }
        }

    }

    public static void BrainUIComponent(String pathApp) {
        try {
            for (int cont = 0; cont < arquivos.length; cont++) {
                System.out.println("Arquivo analisado:" + arquivos[cont]);
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(arquivos[cont].toString());
                CompilationUnit compilationunit = JavaParser.parse(f);
                ClassOrInterfaceDeclaration n = new ClassOrInterfaceDeclaration();

                ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<ClassOrInterfaceDeclaration>();
                NodeList<TypeDeclaration<?>> types = compilationunit.getTypes();
                for (int i = 0; i < types.size(); i++) {
                    classes.add((ClassOrInterfaceDeclaration) types.get(i));
                }

                for (ClassOrInterfaceDeclaration classe : classes) {
                    Boolean isComponentUiBrain = false;
                    //Testa se é adapter, activity, Fragment
                    NodeList<ClassOrInterfaceType> implementacoes = classe.getExtendedTypes();
                    for (ClassOrInterfaceType implementacao : implementacoes) {
                        if (implementacao.getName().getIdentifier().equals("Activity") || implementacao.getName().getIdentifier().equals("Fragments") || implementacao.getName().getIdentifier().equals("BaseAdapter") || implementacao.getName().getIdentifier().endsWith("Listener")) {
                            NodeList<BodyDeclaration<?>> itens = classe.getMembers();

                            //Testa se existe atributo do tipo FINAL
                            for (BodyDeclaration<?> atributos : itens) {
                                System.out.println(atributos.toString());
                                if (atributos.isFieldDeclaration()) {
                                    if (atributos.toString().contains("final")) {
                                        System.out.println("Componente de UI Cérebro Encontrado - " + atributos.getRange());
                                    }
                                }
                            }

                            //Testa se Existem étodos que não sejam override
                            for (BodyDeclaration<?> met : itens) {
                                if (met.isMethodDeclaration()) {
                                    NodeList<AnnotationExpr> annotations = met.getAnnotations();

                                    for (AnnotationExpr annotation : annotations) {
                                        //Se tiver annotacoes que ão seja overide considera que possui implementação indevida logo uma smells
                                        if (!annotation.getName().getIdentifier().equals("Override")) {
                                            System.out.println("Componente de UI Cérebro Encontrado - " + annotation.getRange());
                                        }
                                    }
                                }
                            }

                            //Testa se existe Conversão de dados


                            //Testa se existe Operações de IO


                        }
                    }
                }
            }

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void FlexAdapter(String pathApp) {

    }


    public static void GodStyleResource(String pathApp) {
        try {
            int qtdLimiteStilos = 5;
            diretorio = new File(pathApp);
            arquivos = diretorio.listFiles();
            int qtdFilesStyle = 0;

            for (int cont = 0; cont < arquivos.length; cont++) {
                System.out.println("Arquivo analisado:" + arquivos[cont]);
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(arquivos[cont].toString());

                //LER TODA A ESTRUTURA DO XML
                Document d = sb.build(f);

                if(d.getRootElement().getChildren().get(0).getName() == "style") {
                    qtdFilesStyle = qtdFilesStyle +1;
                }

                if((qtdFilesStyle == 1) && (d.getRootElement().getChildren().size() > qtdLimiteStilos )){
                    System.out.println("Longo recurso de Estilo detectado (existe apenas um arquivo para estilos no aplicativo que possui " + d.getRootElement().getChildren().size() + " estilos)");
                    System.out.println("---------------------------------------------------------------------------------------");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void DeepNestedLayout(String pathApp) {
        try {
            diretorio = new File(pathApp);
            arquivos = diretorio.listFiles();

            for (int cont = 0; cont < arquivos.length; cont++) {
                System.out.println("Arquivo analisado:" + arquivos[cont]);
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(arquivos[cont].toString());

                //LER TODA A ESTRUTURA DO XML
                Document d = sb.build(f);

                //ACESSAR O ROOT ELEMENT
                Element rootElmnt = d.getRootElement();

                //BUSCAR ELEMENTOS FILHOS DA TAG
                List elements = rootElmnt.getChildren();

                recursiveChildrenElement(elements);

                System.out.println("---------------------------------------------------------------------------------------");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void DuplicateStyleAttributes(String pathApp) {
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

                        Boolean style = false;
                        for (org.jdom2.Attribute item : listAttr) {
                            if (item.getName() == "style") {
                                style = true;
                            }
                        }
                        if (!style) {
                            System.out.println("Duplicate Style Attributes " + elChildren.getName() + " - Considere colocar a formatação das propriedades em um recurso de estilo:");
                        }
                    }
                }


                System.out.println("---------------------------------------------------------------------------------------");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


        public static void teste() throws Exception {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            SAXParser parser = factory.newSAXParser();
            parser.parse("C:\\Users\\julio\\Desktop\\Amostragem de Apps a Analisar\\Bucket\\app\\src\\main\\res\\layout\\activity_detailed.xml", new SampleOfXmlLocator());
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
                        System.out.println("Layout Profundamente Aninhado encontrado " + el.getName());
                        break;
                    }
                }
            }

        }

    }

