package AndroidDetector;

import UTIL.ReusoStringData;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class LoadFiles {
    public static List<File> ListArquivosAnaliseXML =  new ArrayList<File>();
    public static List<File> ListArquivosAnaliseJava =  new ArrayList<File>();
    public static List<File> arquivosAnalise =  new ArrayList<File>();
    public static final String JAVA = ".java";
    public static final String XML = ".xml";


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
                arquivosAnalise.add(new File(directory.getPath() + "" + File.separator + "" + myFiles[i].toString()));
            }

            String[] subDirectory = directory.list();
            if(subDirectory != null) {
                for(String dir : subDirectory){
                    listar(new File(directory + File.separator  + dir),tipo);
                }
            }
        }
    }
}
