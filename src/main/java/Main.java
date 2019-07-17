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

        sc = new Scanner(System.in);
        System.out.println("Informe a pasta dos arquivos");
        caminho = sc.nextLine();

        try {
            if (caminho != "") {
                if (xmlOrjava == 1) { //Smells XML                                    
                    AndroidDetector.AndroidLayoutSmells.DeepNestedLayout(caminho, 3);       //Layout Profundamente Aninhado
                    AndroidDetector.AndroidLayoutSmells.DuplicateStyleAttributes(caminho);          //Atributo de Estilo repetido
                    AndroidDetector.AndroidLayoutSmells.GodStyleResource(caminho, 5);    //Longo Recurso de Estilo
                    AndroidDetector.AndroidLayoutSmells.HiddenListener(caminho);                 //Listener Escondido
                    AndroidDetector.AndroidLayoutSmells.magicResource(caminho);               //Recurso Mágico
                    AndroidDetector.AndroidLayoutSmells.godStringResource(caminho);          //Recurso de String Bagunçado
                    AndroidDetector.AndroidLayoutSmells.inappropriateStringReuse(caminho);   //Reuso inadequado de String
                    AndroidDetector.AndroidLayoutSmells.NotFoundImage(caminho);            //Imagem Faltante

                } else {//Smel1ls JAVA
                    AndroidDetector.AndroidJavaCodeSmells.CoupledUIComponent(caminho);      //Componente de UI Acoplado
                    AndroidDetector.AndroidJavaCodeSmells.SuspiciousBehavior(caminho);     //Comportamento Suspeito
                    AndroidDetector.AndroidJavaCodeSmells.FlexAdapter(caminho);           // Adapter Complexo
                    AndroidDetector.AndroidJavaCodeSmells.FoolAdapter(caminho);           // Adapter
                    AndroidDetector.AndroidJavaCodeSmells.BrainUIComponent(caminho);     //Componente de Ui Cerebro
                    AndroidDetector.AndroidJavaCodeSmells.CompUIIO(caminho);            //Componente de UI fazendo IO
                    AndroidDetector.AndroidJavaCodeSmells.NotFragment(caminho);        //Não Uso de Fragments
                    AndroidDetector.AndroidJavaCodeSmells.ExcessiveFragment(caminho, 5);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
