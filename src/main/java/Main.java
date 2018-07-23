import AndroidDetector.SmellsAndroidUiApp;
import org.jdom2.JDOMException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, JDOMException {
        System.out.println("Detector 1.0 -  UFLA");
        System.out.println("Autores: Julio Verissimo, Rafael Durelli, Matheus Antônio Flausino");

        //SmellsAndroidUiApp.NotFragments();
        //SmellsAndroidUiApp.BadStringResource();
        //SmellsAndroidUiApp.frequentStyleResource();
        //SmellsAndroidUiApp.LongStyleResource();
        //SmellsAndroidUiApp.InapropriateString();
        //SmellsAndroidUiApp.NotFoundImage();

        SmellsAndroidUiApp.CompUIIO("C:\\Users\\julio\\Desktop\\SystemAppMover\\src\\main\\java\\de\\j4velin\\systemappmover");
        SmellsAndroidUiApp.magicResource("C:\\Users\\julio\\Desktop\\SystemAppMover\\src\\main\\res\\layout");
        SmellsAndroidUiApp.HideListener("C:\\Users\\julio\\Desktop\\SystemAppMover\\src\\main\\res\\layout");





    }



    
}
