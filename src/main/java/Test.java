import AndroidDetector.ImportantSmells;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Test {

    
    public static void main(String[] args) {
        try {
            long inicio = System.currentTimeMillis();
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date(inicio)));



            File fileCsv = new File("/Users/rafaeldurelli/Desktop/Analise/AnaliseFinal.csv");

            // creates the file
            fileCsv.createNewFile();
            FileWriter writer = new FileWriter(fileCsv);
            writer.append("Aplicativo" + ";" + "DeepNestedLayout" + ";" + "DuplicateStyleAttributes" + ";" + "GodStyleResource" + ";" + "HiddenListener" + ";" + "magicResource" + ";" + "godStringResource" + ";" + "inappropriateStringReuse" + ";" + "NotFoundImage" + ";" + "CoupledUIComponent" + ";" + "SuspiciousBehavior" + ";" + "FlexAdapter" + ";" + "BrainUIComponent" + ";" + "CompUIIO" + ";" + "NotFragment" + ";" + "ExcessiveFragment"+ ";" + "FoolAdapter");
            writer.append("\n");


            File file = new File("/Users/rafaeldurelli/Desktop/Repositorio01");
            File afile[] = file.listFiles();

            for(int j = 0; j< afile.length; j++) {
                File f = new File(afile[j].toString());
                //Getting all files to apply resource smells
                ImportantSmells.carregaArquivosXMLAnalise(f);
                
                System.out.println();
                System.out.println();
                System.out.println(f.getName() + " - " + j);
                System.out.println();
                System.out.println();
                String caminho = afile[j].toString();



                //XML
                String app = f.getName();
//                long totalDeepNested = AndroidDetector.ImportantSmells.DeepNestedLayout(caminho, 4);
//                long totalDuplicateStyleAttributes = AndroidDetector.ImportantSmells.DuplicateStyleAttributes(caminho);
                long totalGodStyleResource = AndroidDetector.ImportantSmells.GodStyleResource(caminho,11);
//                long totalHideListener = AndroidDetector.ImportantSmells.HiddenListener(caminho);
//                long totalmagicResource = AndroidDetector.ImportantSmells.magicResource(caminho);
//                long totalBadStringResource = AndroidDetector.ImportantSmells.godStringResource(caminho);
//                long totalreusoInadequadoDeString = AndroidDetector.ImportantSmells.inappropriateStringReuse(caminho);
//                long totalNotFoundImage = AndroidDetector.ImportantSmells.NotFoundImage(caminho);

                //analise de codigo java
                ImportantSmells.carregaArquivosJAVAAnalise(f);
//
//                long totalCoupledUIComponent = AndroidDetector.ImportantSmells.CoupledUIComponent(caminho);
                long totalSuspiciousBehavior = AndroidDetector.ImportantSmells.SuspiciousBehavior(caminho);
//                long totalBrainUIComponent = AndroidDetector.ImportantSmells.BrainUIComponent(caminho);
                    long totalFlexAdapter = AndroidDetector.ImportantSmells.FlexAdapter(caminho);
                 long totalFoolAdapter = AndroidDetector.ImportantSmells.FoolAdapter(caminho);
//

                long totalCompUIIO = AndroidDetector.ImportantSmells.CompUIIO(caminho);
//                long totalNotFragment = AndroidDetector.ImportantSmells.NotFragment(caminho);
                long totalExcessiveFragment = AndroidDetector.ImportantSmells.ExcessiveFragment(caminho,10);

//                writer.append(
//                        app + ";" +
//                        totalDeepNested + ";" +
//                        totalDuplicateStyleAttributes  + ";" +
//                        totalGodStyleResource + ";" +
//                        totalHideListener + ";" +
//                        totalmagicResource + ";" +
//                        totalBadStringResource + ";" +
//                        totalreusoInadequadoDeString + ";" +
//                        totalNotFoundImage + ";" +
//                        totalCoupledUIComponent + ";" +
//                        totalSuspiciousBehavior + ";" +
//                        totalFlexAdapter + ";" +
//                        totalBrainUIComponent + ";" +
//                        totalCompUIIO + ";" +
//                        totalNotFragment + ";" +
//                        totalExcessiveFragment + ";" +
//                        totalFoolAdapter + ";"
//                );

//                writer.append(
//                        app + ";" +
////                                totalNotFoundImage + ";"
//                );



                writer.append("\n");
                writer.flush();
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
