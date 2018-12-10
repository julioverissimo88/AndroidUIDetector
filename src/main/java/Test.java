import AndroidDetector.ImportantSmells;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Test {

    
    public static void main(String[] args) {
        try {
            long inicio = System.currentTimeMillis();
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date(inicio)));

            //--->Thresholds
            long ThresholdsDeepNestedLayout = 4;
            long ThresholdsGodStyleResource = 11;
            long ThresholdsExcessiveFragment = 10;
            long ThresholdsFlexAdapterThreshold = 0;
            long ThresholdsCompUIIO = 0;
            long ThresholdsBrainUIComponent = 0;

            //Pasta onde será salvo o arquivo Final da análise
            File fileCsv = new File("C:\\Detector\\AnaliseFinal.csv ");
            //Pasta onde estão armaznados os apps
            File file = new File("C:\\Users\\julio\\Desktop\\Repositorio01\\Repositorio01");

            // creates the file
            fileCsv.createNewFile();
            FileWriter writer = new FileWriter(fileCsv);
            writer.append(
                        "Aplicativo" + ";" +
                                "DeepNestedLayout" + ";" +
                                "QtdArquivos" + ";" +
                                "DuplicateStyleAttributes" + ";" +
                                "QtdArquivos" + ";" +
                                "GodStyleResource" + ";" +
                                "QtdArquivos" + ";" +
                                "HiddenListener" + ";" +
                                "QtdArquivos" + ";" +
                                "magicResource" + ";" +
                                "QtdArquivos" + ";" +
                                "godStringResource" + ";" +
                                "QtdArquivos" + ";" +
                                "inappropriateStringReuse" + ";" +
                                "QtdArquivos" + ";" +
                                "NotFoundImage" + ";" +
                                "QtdArquivos" + ";" +
                                "CoupledUIComponent" + ";" +
                                "QtdArquivos" + ";" +
                                "SuspiciousBehavior" + ";" +
                                "QtdArquivos" + ";" +
                                "FlexAdapter" + ";" +
                                "QtdArquivos" + ";" +
                                "FoolAdapter" + ";" +
                                "QtdArquivos" + ";" +
                                "BrainUIComponent" + ";" +
                                "QtdArquivos" + ";" +
                                "CompUIIO" + ";" +
                                "QtdArquivos" + ";" +
                                "NotFragment" + ";" +
                                "QtdArquivos" + ";" +
                                "ExcessiveFragment" + ";" +
                                "QtdArquivos"
            );
            writer.append("\n");

            File afile[] = file.listFiles();

            for(int j = 0; j< afile.length; j++) {
                File f = new File(afile[j].toString());
                //Getting all files to apply resource smells
                ImportantSmells.carregaArquivosXMLAnalise(f);

                System.out.println("*******************************************");
                System.out.println("Analisando aplicativo " + f.getName() + " "+  j + "/" + afile.length);
                System.out.println(f.getName() + " - " + j);
                System.out.println("*******************************************");
                String caminho = afile[j].toString();

                //analise de codigo XML
                String app = f.getName();
                long totalDeepNested = AndroidDetector.ImportantSmells.DeepNestedLayout(caminho, 4);
                long totalArquivosDeepNested = ImportantSmells.contadorArquivosAnalisados;
                long totalDuplicateStyleAttributes = AndroidDetector.ImportantSmells.DuplicateStyleAttributes(caminho);
                long totalArquivosDuplicateStyleAttributes = ImportantSmells.contadorArquivosAnalisados;
                long totalGodStyleResource = AndroidDetector.ImportantSmells.GodStyleResource(caminho,11);
                long totalArquivosGodStyleResource = ImportantSmells.contadorArquivosAnalisados;
                long totalHideListener = AndroidDetector.ImportantSmells.HiddenListener(caminho);
                long totalArquivosHideListener = ImportantSmells.contadorArquivosAnalisados;
                long totalmagicResource = AndroidDetector.ImportantSmells.magicResource(caminho);
                long totalArquivosmagicResource = ImportantSmells.contadorArquivosAnalisados;
                long totalBadStringResource = AndroidDetector.ImportantSmells.godStringResource(caminho);
                long totalArquivosBadStringResource = ImportantSmells.contadorArquivosAnalisados;
                long totalreusoInadequadoDeString = AndroidDetector.ImportantSmells.inappropriateStringReuse(caminho);
                long totalArquivosreusoInadequadoDeString = ImportantSmells.contadorArquivosAnalisados;
                long totalNotFoundImage = AndroidDetector.ImportantSmells.NotFoundImage(caminho);
                long totalArquivosNotFoundImage = ImportantSmells.contadorArquivosAnalisados;

                //analise de codigo JAVA
                ImportantSmells.carregaArquivosJAVAAnalise(f);

                long totalCoupledUIComponent = AndroidDetector.ImportantSmells.CoupledUIComponent(caminho);
                long totalArquivosCoupledUIComponent = ImportantSmells.contadorArquivosAnalisados;
                long totalSuspiciousBehavior = AndroidDetector.ImportantSmells.SuspiciousBehavior(caminho);
                long totalArquivosSuspiciousBehavior = ImportantSmells.contadorArquivosAnalisados;
                long totalBrainUIComponent = 0;//AndroidDetector.ImportantSmells.BrainUIComponent(caminho);
                long totalArquivosBrainUIComponent = ImportantSmells.contadorArquivosAnalisados;
                long totalFlexAdapter = AndroidDetector.ImportantSmells.FlexAdapter(caminho);
                long totalArquivosFlexAdapter = ImportantSmells.contadorArquivosAnalisados;
                long totalFoolAdapter = AndroidDetector.ImportantSmells.FoolAdapter(caminho);
                long totalArquivosFoolAdapter = ImportantSmells.contadorArquivosAnalisados;
                long totalCompUIIO = 0;//AndroidDetector.ImportantSmells.CompUIIO(caminho);
                long totalArquivosCompUIIO = ImportantSmells.contadorArquivosAnalisados;
                long totalNotFragment = AndroidDetector.ImportantSmells.NotFragment(caminho);
                long totalArquivosNotFragment = ImportantSmells.contadorArquivosAnalisados;
                long totalExcessiveFragment = AndroidDetector.ImportantSmells.ExcessiveFragment(caminho,10);
                long totalArquivosExcessiveFragment = ImportantSmells.contadorArquivosAnalisados;

                writer.append(
                        app + ";" +
                        totalDeepNested + ";" +
                        totalArquivosDeepNested + ";" +
                        totalDuplicateStyleAttributes  + ";" +
                        totalArquivosDuplicateStyleAttributes + ";" +
                        totalGodStyleResource + ";" +
                        totalArquivosGodStyleResource + ";" +
                        totalHideListener + ";" +
                        totalArquivosHideListener + ";" +
                        totalmagicResource + ";" +
                        totalArquivosmagicResource + ";" +
                        totalBadStringResource + ";" +
                        totalArquivosBadStringResource + ";" +
                        totalreusoInadequadoDeString + ";" +
                        totalArquivosreusoInadequadoDeString + ";" +
                        totalNotFoundImage + ";" +
                        totalArquivosNotFoundImage + ";" +
                        totalCoupledUIComponent + ";" +
                        totalArquivosCoupledUIComponent + ";" +
                        totalSuspiciousBehavior + ";" +
                        totalArquivosSuspiciousBehavior + ";" +
                        totalFlexAdapter + ";" +
                        totalArquivosFlexAdapter + ";" +
                        totalFoolAdapter + ";" +
                        totalArquivosFoolAdapter + ";" +
                        totalBrainUIComponent + ";" +
                        totalArquivosBrainUIComponent + ";" +
                        totalCompUIIO + ";" +
                        totalArquivosCompUIIO + ";" +
                        totalNotFragment + ";" +
                        totalArquivosNotFragment + ";" +
                        totalExcessiveFragment + ";" +
                        totalArquivosExcessiveFragment
                );

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
            System.out.println("Finalizado tempo total: " + new SimpleDateFormat("HH:mm:ss").format(new Date(inicio - fim)));
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }   
}
