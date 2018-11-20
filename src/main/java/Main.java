import org.jdom2.JDOMException;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) throws Exception, IOException, JDOMException {
        System.out.println("Detector AndroidSmells 1.0 -  UFLA");
        System.out.println("Autores: Julio Verissimo, Rafael Durelli, Matheus Antônio Flausino");
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println("");

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
                    AndroidDetector.ImportantSmells.DeepNestedLayout(caminho, 3);   //Layout Profundamente Aninhado
                    AndroidDetector.ImportantSmells.DuplicateStyleAttributes(caminho);       //Atributo de Estilo repetido
                    AndroidDetector.ImportantSmells.GodStyleResource(caminho,5);  //Longo Recurso de Estilo
                    AndroidDetector.ImportantSmells.HiddenListener(caminho);                 //Listener Escondido
                    AndroidDetector.ImportantSmells.magicResource(caminho);               //Recurso Mágico
                    AndroidDetector.ImportantSmells.godStringResource(caminho);          //Recurso de String Bagunçado
                    AndroidDetector.ImportantSmells.inappropriateStringReuse(caminho);   //Reuso inadequado de String
                    AndroidDetector.ImportantSmells.NotFoundImage(caminho);            //Imagem Faltante

                } else {//Smel1ls JAVA
                    AndroidDetector.ImportantSmells.CoupledUIComponent(caminho);      //Componente de UI Acoplado
                    AndroidDetector.ImportantSmells.SuspiciousBehavior(caminho);     //Comportamento Suspeito
                    AndroidDetector.ImportantSmells.FlexAdapter(caminho);           // Adapter Complexo
                    AndroidDetector.ImportantSmells.BrainUIComponent(caminho);     //Componente de Ui Cerebro
                    AndroidDetector.ImportantSmells.CompUIIO(caminho);            //Componente de UI fazendo IO
                    AndroidDetector.ImportantSmells.NotFragment(caminho);        //Não Uso de Fragments
                    AndroidDetector.ImportantSmells.ExcessiveFragment(caminho,5);
                }
            }
        }
        catch(Exception ex){
            //ex.printStackTrace();
        }        
    }
}
