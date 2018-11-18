import AndroidDetector.ImportantSmells;
import AndroidDetector.OutputSmells;
import AndroidDetector.SmellsAndroidUiApp;
import UTIL.Trashold;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class Test {

    public static final String JAVA = ".java";
    public static final String XML = ".xml";
    public static List<File> arquivosAnalise =  new ArrayList<File>();
    public static  Boolean classeValida = true;
    private  static List<OutputSmells> ListSmells = new ArrayList<OutputSmells>();
    private  static List<String> FilesIMG = new ArrayList<String>();

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
    
    
    public static void main(String[] args) {
        try {
            //Brain Component
            //ImportantSmells.BrainUIComponent("C:\\Users\\julio\\Desktop\\codigos\\java\\Brain");

            //Coupled UI Component
            //ImportantSmells.CoupledUIComponent("C:\\Users\\julio\\Desktop\\codigos\\java\\CoupledUI");

            //Suspicious Behavior
            //ImportantSmells.SuspiciousBehavior("C:\\Users\\julio\\Desktop\\codigos\\java\\SuspiciousBehavior");

            //Flex Adapter
            //ImportantSmells.FlexAdapter("C:\\Users\\julio\\Desktop\\codigos\\java\\FlexAdapter");

            //layout profundamente aninhado
            //ImportantSmells.DeepNestedLayout("C:\\Users\\julio\\Desktop\\codigos\\layout\\layoutProfundamenteAninhado",3);

            //God Style Resource
            //ImportantSmells.GodStyleResource("C:\\Users\\julio\\AndroidStudioProjects\\AppTestAndroidSmells");

            //atributos de estilo repetidos
            //ImportantSmells.DuplicateStyleAttributes("C:\\Users\\julio\\Desktop\\codigos\\layout\\atributosEstiloRepetidos\\");

            //SmellsAndroidUiApp.HideListener("C:\\Users\\julio\\AndroidStudioProjects\\AppTestAndroidSmells\\app\\src\\main\\res\\layout");

            //ImportantSmells.magicResource("C:\\Users\\julio\\AndroidStudioProjects\\AppTestAndroidSmells\\app\\src\\main\\res\\layout");

            //ImportantSmells.NotFoundImage("C:\\\\Users\\\\julio\\\\Desktop\\\\codigos\\\\Calculator\\\\mobile\\\\");

            //ImportantSmells.reusoInadequadoDeString("C:\\\\Users\\\\julio\\\\Desktop\\\\codigos\\\\layout\\\\");

            //Trashold.DeepNestedLayout("C:\\\\Users\\\\julio\\\\Desktop\\\\codigos\\\\layout\\\\");

            //ImportantSmells.NotFragment("C:\\Users\\julio\\Desktop\\codigos\\java\\SuspiciousBehavior\\");

            //Trashold.DeepNestedLayout("C:\\Users\\julio\\Desktop\\Repositorio01\\Repositorio01");
            //Trashold.GodStyleResource("C:\\Users\\julio\\Desktop\\Repositorio01\\Repositorio01");

            //Trashold.ExcessiveFragment("C:\\Users\\julio\\Desktop\\Repositorio01\\Repositorio01");


            long inicio = System.currentTimeMillis();
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date(inicio)));

            File fileCsv = new File("C:\\Detector\\AnaliseFinal.csv");

            // creates the file
            fileCsv.createNewFile();
            FileWriter writer = new FileWriter(fileCsv);
            writer.append("Aplicativo" + ";" + "DeepNestedLayout" + ";" + "DuplicateStyleAttributes" + ";" + "GodStyleResource" + ";" + "HideListener" + ";" + "magicResource" + ";" + "BadStringResource" + ";" + "reusoInadequadoDeString" + ";" + "NotFoundImage" + ";" + "CoupledUIComponent" + ";" + "SuspiciousBehavior" + ";" + "FlexAdapter" + ";" + "BrainUIComponent" + ";" + "CompUIIO" + ";" + "NotFragment" + ";" + "ExcessiveFragment"+ ";" + "FoolAdapter");
            writer.append("\n");


            File file = new File("C:\\Users\\julio\\Desktop\\Repositorio01\\Repositorio01");
            File afile[] = file.listFiles();

            for(int j = 0; j< afile.length; j++) {
                File f = new File(afile[j].toString());
                ImportantSmells.carregaArquivosXMLAnalise(f);
                
                System.out.println();
                System.out.println();
                System.out.println(f.getName() + " - " + j);
                System.out.println();
                System.out.println();
                String caminho = afile[j].toString();


                String app = f.getName();
                long totalDeepNested = AndroidDetector.ImportantSmells.DeepNestedLayout(caminho, 4);
                long totalDuplicateStyleAttributes = AndroidDetector.ImportantSmells.DuplicateStyleAttributes(caminho);
                long totalGodStyleResource = AndroidDetector.ImportantSmells.GodStyleResource(caminho,11);
                long totalHideListener = AndroidDetector.ImportantSmells.HideListener(caminho);
                long totalmagicResource = AndroidDetector.ImportantSmells.magicResource(caminho);
                long totalBadStringResource = AndroidDetector.ImportantSmells.BadStringResource(caminho);
                long totalreusoInadequadoDeString = AndroidDetector.ImportantSmells.reusoInadequadoDeString(caminho);
                long totalNotFoundImage = AndroidDetector.ImportantSmells.NotFoundImage(caminho);

                //analise de codigo java
                ImportantSmells.carregaArquivosJAVAAnalise(f);

                long totalCoupledUIComponent = AndroidDetector.ImportantSmells.CoupledUIComponent(caminho);
                long totalSuspiciousBehavior = AndroidDetector.ImportantSmells.SuspiciousBehavior(caminho);
                long totalFoolAdapter = AndroidDetector.ImportantSmells.FoolAdapter(caminho);
                long totalFlexAdapter = AndroidDetector.ImportantSmells.FlexAdapter(caminho);
                long totalBrainUIComponent = AndroidDetector.ImportantSmells.BrainUIComponent(caminho);
                long totalCompUIIO = AndroidDetector.ImportantSmells.CompUIIO(caminho);
                long totalNotFragment = AndroidDetector.ImportantSmells.NotFragment(caminho);
                long totalExcessiveFragment = AndroidDetector.ImportantSmells.ExcessiveFragment(caminho,10);

                writer.append(
                        app + ";" +
                        totalDeepNested + ";" +
                        totalDuplicateStyleAttributes  + ";" +
                        totalGodStyleResource + ";" +
                        totalHideListener + ";" +
                        totalmagicResource + ";" +
                        totalBadStringResource + ";" +
                        totalreusoInadequadoDeString + ";" +
                        totalNotFoundImage + ";" +
                        totalCoupledUIComponent + ";" +
                        totalSuspiciousBehavior + ";" +
                        totalFlexAdapter + ";" +
                        totalBrainUIComponent + ";" +
                        totalCompUIIO + ";" +
                        totalNotFragment + ";" +
                        totalExcessiveFragment + ";" +
                        totalFoolAdapter + ";"
                );



                writer.append("\n");
            }

            writer.flush();
            writer.close();

            long fim  = System.currentTimeMillis();
            System.out.println( new SimpleDateFormat("HH:mm:ss").format(new Date(inicio - fim)));     
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }   
}
