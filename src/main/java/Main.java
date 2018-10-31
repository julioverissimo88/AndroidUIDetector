import AndroidDetector.ImportantSmells;
import AndroidDetector.SmellsAndroidUiApp;
import org.jdom2.JDOMException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception, IOException, JDOMException {
        System.out.println("Detector AndroidSmells 1.0 -  UFLA");
        System.out.println("Autores: Julio Verissimo, Rafael Durelli, Matheus Antônio Flausino");
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println("");

        //SmellsAndroidUiApp.frequentStyleResource();
        //SmellsAndroidUiApp.InapropriateString();
        /*
        SmellsAndroidUiApp.CompUIIO("C:\\Users\\julio\\Desktop\\SystemAppMover\\src\\main\\java\\de\\j4velin\\systemappmover");
        SmellsAndroidUiApp.magicResource("C:\\Users\\julio\\Desktop\\SystemAppMover\\src\\main\\res\\layout");
        SmellsAndroidUiApp.HideListener("C:\\Users\\julio\\Desktop\\SystemAppMover\\src\\main\\res\\layout");
        SmellsAndroidUiApp.layoutProfundamenteAninhado("C:\\Users\\julio\\Desktop\\SystemAppMover\\src\\main\\res\\layout");
        SmellsAndroidUiApp.NotFragments("C:\\Users\\julio\\Desktop\\SystemAppMover\\src\\main\\res\\layout");
        SmellsAndroidUiApp.LongStyleResource("C:\\Users\\julio\\Desktop\\SystemAppMover\\src\\main\\res\\values");
        SmellsAndroidUiApp.BadStringResource("C:\\Users\\julio\\Desktop\\SystemAppMover\\src\\main\\res\\values");     
        ImportantSmells.SuspiciousBehavior("C:\\Users\\julio\\Dropbox\\Mestrado\\Detector\\src\\main\\java\\AndroidDetector\\");
        */
        
        Scanner sc = new Scanner(System.in);
        int xmlOrjava = 0;
        String caminho = "";

        System.out.println("Deseja Analisar Xml ou Java (1 - XML, 2 - Java)");
        xmlOrjava = sc.nextInt();

        sc =  new Scanner(System.in);
        System.out.println("Informe a pasta dos arquivos");
        caminho = sc.nextLine();

        try {
            if (caminho != "") {
                if (xmlOrjava == 1) { //Smells XML                                    
                    AndroidDetector.ImportantSmells.DeepNestedLayout(caminho, 3);
                    AndroidDetector.ImportantSmells.DuplicateStyleAttributes(caminho);
                    AndroidDetector.ImportantSmells.GodStyleResource(caminho,5);
                } else {//Smel1ls JAVA
                    AndroidDetector.ImportantSmells.CoupledUIComponent(caminho);
                    ImportantSmells.SuspiciousBehavior(caminho);
                    ImportantSmells.FlexAdapter(caminho);
                    ImportantSmells.BrainUIComponent(caminho);
                }
            }
        }
        catch(Exception ex){
            //ex.printStackTrace();
        }        
    }
}
