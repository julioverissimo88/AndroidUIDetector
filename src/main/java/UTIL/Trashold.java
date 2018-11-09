package UTIL;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Trashold {
    public static List<File> arquivosAnalise =  new ArrayList<File>();
    public static final String JAVA = ".java";
    public static final String XML = ".xml";
    private static SAXBuilder sb = new SAXBuilder();
    private static long qtdSubelementos = 0;
    private static String arquivo_analisado;

    private static List<ReusoStringData> lista = new ArrayList<ReusoStringData>();
    private static List<ReusoStringData> listaDeepNested = new ArrayList<ReusoStringData>();



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

    public static void DeepNestedLayout(String pathApp) {
        try {
            arquivosAnalise.clear();
            listar(new File(pathApp),XML);

            System.out.println("arquivo" + ";" + "Níveis");

            for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
                //System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                //System.out.println("---------------------------------------------------------------------------------------");
                arquivo_analisado = arquivosAnalise.toArray()[cont].toString();

                File f = new File(arquivosAnalise.toArray()[cont].toString());

                //LER TODA A ESTRUTURA DO XML
                Document d = sb.build(f);

                if (arquivosAnalise.toArray()[cont].toString().contains("\\layout\\")) {
                    //ACESSAR O ROOT ELEMENT
                    Element rootElmnt = d.getRootElement();

                    //BUSCAR ELEMENTOS FILHOS DA TAG
                    List elements = rootElmnt.getChildren();

                    recursiveChildrenElement(elements);

                    //System.out.println("---------------------------------------------------------------------------------------");

                }
            }

            File file = new File("C:\\Detector\\TrasholdDeepNestedLayout.csv");

            // creates the file
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.append("arquivo" + ";" + "Qtd Níveis");
            writer.append("\n");

            for(ReusoStringData item: listaDeepNested) {
                writer.append(item.arquivo + ";" + item.strString + ";");
                writer.append("\n");

            }

            writer.flush();
            writer.close();



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
            }
            else{
                System.out.println(arquivo_analisado + ";" + qtdSubelementos);
                ReusoStringData data = new ReusoStringData();
                data.arquivo = arquivo_analisado;
                data.strString = qtdSubelementos + "";

                listaDeepNested.add(data);
                qtdSubelementos = 0;
            }
        }
    }


    public static void GodStyleResource(String pathApp) {
        try {
            arquivosAnalise.clear();
            listar(new File(pathApp),XML);

            int qtdFilesStyle = 0;
            System.out.println("arquivo" + ";" + "Qtd Stilos");

            for (int cont = 0; cont < (arquivosAnalise.toArray().length - 1); cont++) {
                arquivo_analisado = arquivosAnalise.toArray()[cont].toString();
                File f = new File(arquivosAnalise.toArray()[cont].toString());

                //LER TODA A ESTRUTURA DO XML
                Document d = sb.build(f);

                if(d.getRootElement().getChildren().size() > 0) {
                    if (d.getRootElement().getChildren().get(0).getName() == "style") {
                        //qtdFilesStyle = qtdFilesStyle + 1;
                        //System.out.println(arquivosAnalise.toArray()[cont]);
                        ReusoStringData data = new ReusoStringData();
                        data.arquivo = arquivo_analisado;
                        data.strString = d.getRootElement().getChildren().size() + "";

                        lista.add(data);
                        System.out.println(arquivo_analisado + ";" + d.getRootElement().getChildren().size());
                    }
                }
            }

            File file = new File("C:\\Detector\\TrasholdGodStyleResource.csv");

            // creates the file
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.append("arquivo" + ";" + "Qtd Stilos");
            writer.append("\n");

            for(ReusoStringData item: lista) {
                writer.append(item.arquivo + ";" + item.strString + ";");
                writer.append("\n");

            }

            writer.flush();
            writer.close();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void ExcessiveFragment(String pathApp) throws IOException {
        arquivosAnalise.clear();
        listar(new File(pathApp),JAVA);
        long totalFragments = 0;
        List<ReusoStringData> listaExcessiveFragment = new ArrayList<ReusoStringData>();

        for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
            arquivo_analisado = arquivosAnalise.toArray()[cont].toString();
            //System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
            //System.out.println("---------------------------------------------------------------------------------------");

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
                        if (implementacao.getName().getIdentifier().contains("Fragment")) {
                            totalFragments = totalFragments +1;
                        }
                    }
                }
            }
            ReusoStringData data = new ReusoStringData();
            data.arquivo = arquivo_analisado;
            data.strString = totalFragments + "";

            listaExcessiveFragment.add(data);

            System.out.println(arquivo_analisado + ";" + totalFragments);
            totalFragments = 0;
        }

        File file = new File("C:\\Detector\\TrasholdExcessiveFragment.csv");

        // creates the file
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.append("arquivo" + ";" + "Qtd Fragment");
        writer.append("\n");

        for(ReusoStringData item: listaExcessiveFragment) {
            writer.append(item.arquivo + ";" + item.strString + ";");
            writer.append("\n");

        }

        writer.flush();
        writer.close();

    }




}
