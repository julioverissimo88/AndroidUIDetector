package UTIL;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class Trashold {
    public static List<File> arquivosAnalise =  new ArrayList<File>();
    public static final String JAVA = ".java";
    public static final String XML = ".xml";
    private static SAXBuilder sb = new SAXBuilder();
    private static long qtdSubelementos = 0;
    private static String arquivo_analisado;


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
                qtdSubelementos = 0;
            }
        }
    }
}
