import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Test {


    public static void main(String[] args) {
        try {
            long inicio = System.currentTimeMillis();
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date(inicio)));


//            File fileCsv = new File("/Users/rafaeldurelli/Desktop/Analise/AnaliseFinalDURELLI.csv");
            File fileCsv = new File("/Users/rafaeldurelli/Desktop/Repositorio-TESTE/AnaliseFinalREPOSITORIO2.csv");

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
                            "QtdArquivos" + ";" +
                            "CLOC"
            );
            writer.append("\n");


//            File file = new File("/Volumes/MyPassport/Repository/Repositorio03");
            File file = new File("/Users/rafaeldurelli/Desktop/Repositorio-TESTE");
            File afile[] = file.listFiles();

            for (int j = 0; j < afile.length; j++) {
                File f = new File(afile[j].toString());

                //Getting all files to apply resource smells
                ImportantSmells.carregaArquivosXMLAnalise(f);

                System.out.println("*******************************************");
                System.out.println();
                System.out.println("Analisando aplicativo " + f.getName() + " " + j + "/" + afile.length);

                System.out.println();
                System.out.println();
                System.out.println(f.getName() + " - " + j);
                System.out.println();
                System.out.println();
                String caminho = afile[j].toString();

                long CLOC = calCLOC(f.getAbsolutePath());


                //analise de codigo XML
                String app = f.getName();
                long totalDeepNested = AndroidDetector.ImportantSmells.DeepNestedLayout(caminho, 4);
                long totalArquivosDeepNested = ImportantSmells.contadorArquivosAnalisados;
                long totalDuplicateStyleAttributes = AndroidDetector.ImportantSmells.DuplicateStyleAttributes(caminho);
                long totalArquivosDuplicateStyleAttributes = ImportantSmells.contadorArquivosAnalisados;
                long totalGodStyleResource = AndroidDetector.ImportantSmells.GodStyleResource(caminho, 11);
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

                for (File fileJava : ImportantSmells.ListArquivosAnaliseJava) {
                    System.out.println(fileJava.getAbsolutePath());
                }

                long totalCoupledUIComponent = AndroidDetector.ImportantSmells.CoupledUIComponent(caminho);
                long totalArquivosCoupledUIComponent = ImportantSmells.contadorArquivosAnalisados;
                long totalSuspiciousBehavior = AndroidDetector.ImportantSmells.SuspiciousBehavior(caminho);
                long totalArquivosSuspiciousBehavior = ImportantSmells.contadorArquivosAnalisados;
                long totalBrainUIComponent = ImportantSmells.BrainUIComponent(caminho);
                long totalArquivosBrainUIComponent = ImportantSmells.contadorArquivosAnalisados;
                long totalFlexAdapter = AndroidDetector.ImportantSmells.FlexAdapter(caminho);
                long totalArquivosFlexAdapter = ImportantSmells.contadorArquivosAnalisados;
                long totalFoolAdapter = AndroidDetector.ImportantSmells.FoolAdapter(caminho);
                long totalArquivosFoolAdapter = ImportantSmells.contadorArquivosAnalisados;
                long totalCompUIIO = AndroidDetector.ImportantSmells.CompUIIO(caminho);
                long totalArquivosCompUIIO = ImportantSmells.contadorArquivosAnalisados;
                long totalNotFragment = AndroidDetector.ImportantSmells.NotFragment(caminho);
                long totalArquivosNotFragment = ImportantSmells.contadorArquivosAnalisados;
                long totalExcessiveFragment = AndroidDetector.ImportantSmells.ExcessiveFragment(caminho, 10);
                long totalArquivosExcessiveFragment = ImportantSmells.contadorArquivosAnalisados;

                writer.append(
                        app + ";" +
                                totalDeepNested + ";" +
                                totalArquivosDeepNested + ";" +
                                totalDuplicateStyleAttributes + ";" +
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
                                totalArquivosExcessiveFragment + ";" +
                                CLOC
                );


                //XML
//                String app = f.getName();
//                long totalDeepNested = AndroidDetector.ImportantSmells.DeepNestedLayout(caminho, 4);
//                long totalDuplicateStyleAttributes = AndroidDetector.ImportantSmells.DuplicateStyleAttributes(caminho);
//                long totalGodStyleResource = AndroidDetector.ImportantSmells.GodStyleResource(caminho,11);
//                long totalHideListener = AndroidDetector.ImportantSmells.HiddenListener(caminho);
//                long totalmagicResource = AndroidDetector.ImportantSmells.magicResource(caminho);
//                long totalBadStringResource = AndroidDetector.ImportantSmells.godStringResource(caminho);
//                long totalreusoInadequadoDeString = AndroidDetector.ImportantSmells.inappropriateStringReuse(caminho);
//                long totalNotFoundImage = AndroidDetector.ImportantSmells.NotFoundImage(caminho);
//
//                analise de codigo java
//                ImportantSmells.carregaArquivosJAVAAnalise(f);

//                long totalCoupledUIComponent = AndroidDetector.ImportantSmells.CoupledUIComponent(caminho);
//                long totalSuspiciousBehavior = AndroidDetector.ImportantSmells.SuspiciousBehavior(caminho);
//                long totalBrainUIComponent = AndroidDetector.ImportantSmells.BrainUIComponent(caminho);
//                    long totalFlexAdapter = AndroidDetector.ImportantSmells.FlexAdapter(caminho);
//                 long totalFoolAdapter = AndroidDetector.ImportantSmells.FoolAdapter(caminho);

//
//                long totalCompUIIO = AndroidDetector.ImportantSmells.CompUIIO(caminho);
//                long totalNotFragment = AndroidDetector.ImportantSmells.NotFragment(caminho);
//                long totalExcessiveFragment = AndroidDetector.ImportantSmells.ExcessiveFragment(caminho,10);
//
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
//
//                writer.append(
//                        app + ";" + totalExcessiveFragment + ";" +
//                                totalArquivosExcessiveFragment
//                );


                writer.append("\n");
                writer.flush();
            }

            writer.flush();
            writer.close();

            long fim = System.currentTimeMillis();
            System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date(inicio - fim)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static long calCLOC(String appPath) throws IOException, InterruptedException {
        String command = "cloc " + appPath;

        long CLOC = 0;
        Process proc = Runtime.getRuntime().exec(command);

        // Read the output

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String line = "";
        while ((line = reader.readLine()) != null) {

            System.out.print(line + "\n");

            if (line.contains("SUM:")) {

                String[] array = line.split(" ");

                CLOC = new Long(array[array.length - 1]);

                System.out.print("Tamanho do projeto é " + array[array.length - 1] + "\n");
            }

        }

        proc.waitFor();
        return CLOC;

    }
}
