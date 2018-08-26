import AndroidDetector.ImportantSmells;
import AndroidDetector.SmellsAndroidUiApp;
import org.jdom2.JDOMException;
import java.io.IOException;

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

        */


//        ImportantSmells.SuspiciousBehavior("C:\\Users\\julio\\Dropbox\\Mestrado\\Detector\\src\\main\\java\\AndroidDetector\\");

        ImportantSmells.FlexAdapter("/home/gtbono/Cursos-Alura/app/src/main/java/br/com/alura/cursos/adapter/");


    }



    
}
