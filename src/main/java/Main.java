import br.com.AndroidDetector.AndroidJavaCodeSmells;
import br.com.AndroidDetector.AndroidLayoutSmells;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws Exception, IOException, JDOMException {
        System.out.println("Detector AndroidSmells 1.1 -  UFLA");
        System.out.println("Autores: Julio Verissimo, Rafael Durelli");
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
                    AndroidLayoutSmells.DeepNestedLayout(caminho, 3);       //Layout Profundamente Aninhado
                    AndroidLayoutSmells.DuplicateStyleAttributes(caminho);          //Atributo de Estilo repetido
                    AndroidLayoutSmells.GodStyleResource(caminho, 5);    //Longo Recurso de Estilo
                    AndroidLayoutSmells.HiddenListener(caminho);                 //Listener Escondido
                    AndroidLayoutSmells.magicResource(caminho);               //Recurso Mágico
                    AndroidLayoutSmells.godStringResource(caminho);          //Recurso de String Bagunçado
                    AndroidLayoutSmells.inappropriateStringReuse(caminho);   //Reuso inadequado de String
                    AndroidLayoutSmells.NotFoundImage(caminho);            //Imagem Faltante

                } else {//Smel1ls JAVA
                    AndroidJavaCodeSmells.CoupledUIComponent(caminho);      //Componente de UI Acoplado
                    AndroidJavaCodeSmells.SuspiciousBehavior(caminho);     //Comportamento Suspeito
                    AndroidJavaCodeSmells.FlexAdapter(caminho);           // Adapter Complexo
                    AndroidJavaCodeSmells.FoolAdapter(caminho);           // Adapter
                    AndroidJavaCodeSmells.BrainUIComponent(caminho);     //Componente de Ui Cerebro
                    AndroidJavaCodeSmells.CompUIIO(caminho);            //Componente de UI fazendo IO
                    AndroidJavaCodeSmells.NotFragment(caminho);        //Não Uso de Fragments
                    AndroidJavaCodeSmells.ExcessiveFragment(caminho, 5);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
