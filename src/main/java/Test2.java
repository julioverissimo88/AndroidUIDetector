import AndroidDetector.ImportantSmells;
import AndroidDetector.ImportantSmells2;
import com.dropbox.core.DbxException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Test2 {


    public static void main(String[] args) {

        File fileCsv=null;
        String nameCSV = "AnalisePrecisonRecall3Apps.csv";

        //            File file = new File("/Volumes/MyPassport/Repository/Repositorio03");
        File file = new File("/Users/rafaeldurelli/Desktop/Repositorio-TESTE");
        File afile[] = file.listFiles();

        try {

//            File fileCsv = new File("/Users/rafaeldurelli/Desktop/Analise/AnaliseFinalDURELLI.csv");
            fileCsv = new File("/Users/rafaeldurelli/Desktop/Repositorio-TESTE/AnalisePrecisonRecall3Apps.csv");

            // creates the file
            fileCsv.createNewFile();
            FileWriter writer = new FileWriter(fileCsv);
            writer.append(
                    "Aplicativo" + ";" +
                            "File/Class" + ";" +
                            "DeepNestedLayout" + ";" +
                            "DuplicateStyleAttributes" + ";" +
                            "GodStyleResource" + ";" +
                            "HiddenListener" + ";" +
                            "magicResource" + ";" +
                            "godStringResource" + ";" +
                            "inappropriateStringReuse" + ";" +
                            "NotFoundImage" + ";" +
                            "CoupledUIComponent" + ";" +
                            "SuspiciousBehavior" + ";" +
                            "FlexAdapter" + ";" +
                            "FoolAdapter" + ";" +
                            "BrainUIComponent" + ";" +
                            "CompUIIO" + ";" +
                            "NotFragment" + ";" +
                            "ExcessiveFragment" + ";" +
                            "CLOC"
            );
            writer.append("\n");




            for(int j = 0; j< afile.length; j++) {

                if (afile[j].toString().contains(".DS_Store")) {
                    continue;
                }



                File f = new File(afile[j].toString());

                //Getting all files to apply resource smells
                ImportantSmells2.carregaArquivosXMLAnalise(f);
                //alterar aqqui depois

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

                for (File fileXML : ImportantSmells2.ListArquivosAnaliseXML) {

                    System.out.println(fileXML);

                    long totalDeepNested = ImportantSmells2.DeepNestedLayout(fileXML, 4);

                    if (totalDeepNested != 0) {
                        totalDeepNested = 1;
                    } else {
                        totalDeepNested = 0;
                    }


//                long totalArquivosDeepNested = ImportantSmells2.contadorArquivosAnalisados;
                    long totalDuplicateStyleAttributes = ImportantSmells2.DuplicateStyleAttributes(fileXML);

                    if (totalDuplicateStyleAttributes != 0) {
                        totalDuplicateStyleAttributes = 1;
                    } else {
                        totalDuplicateStyleAttributes = 0;
                    }

//                long totalArquivosDuplicateStyleAttributes = ImportantSmells2.contadorArquivosAnalisados;
                    long totalGodStyleResource = ImportantSmells2.GodStyleResource(fileXML, 11);

                    if (totalGodStyleResource != 0) {
                        totalGodStyleResource = 1;
                    } else {
                        totalGodStyleResource = 0;
                    }

//                long totalArquivosGodStyleResource = ImportantSmells2.contadorArquivosAnalisados;
                    long totalHideListener = ImportantSmells2.HiddenListener(fileXML);

                    if (totalHideListener != 0) {
                        totalHideListener = 1;
                    } else {
                        totalHideListener = 0;
                    }

//                long totalArquivosHideListener = ImportantSmells2.contadorArquivosAnalisados;
                    long totalmagicResource = ImportantSmells2.magicResource(fileXML);

                    if (totalmagicResource != 0) {
                        totalmagicResource = 1;
                    } else {
                        totalmagicResource = 0;
                    }

//                long totalArquivosmagicResource = ImportantSmells2.contadorArquivosAnalisados;
                    long totalBadStringResource = ImportantSmells2.godStringResource(fileXML);

                    if (totalBadStringResource != 0) {
                        totalBadStringResource = 1;
                    } else {
                        totalBadStringResource = 0;
                    }

//                long totalArquivosBadStringResource = ImportantSmells2.contadorArquivosAnalisados;
                    long totalreusoInadequadoDeString = ImportantSmells2.inappropriateStringReuse(fileXML);

                    if (totalreusoInadequadoDeString != 0) {
                        totalreusoInadequadoDeString = 1;
                    } else {
                        totalreusoInadequadoDeString = 0;
                    }

//                long totalArquivosreusoInadequadoDeString = ImportantSmells2.contadorArquivosAnalisados;


//                long totalArquivosNotFoundImage = ImportantSmells2.contadorArquivosAnalisados;

                    writer.append(
                            app + ";" +
                                    fileXML + ";" +
                                    totalDeepNested + ";" +
//                                    totalArquivosDeepNested + ";" +
                                    totalDuplicateStyleAttributes + ";" +
//                                    totalArquivosDuplicateStyleAttributes + ";" +
                                    totalGodStyleResource + ";" +
//                                    totalArquivosGodStyleResource + ";" +
                                    totalHideListener + ";" +
//                                    totalArquivosHideListener + ";" +
                                    totalmagicResource + ";" +
//                                    totalArquivosmagicResource + ";" +
                                    totalBadStringResource + ";" +
//                                    totalArquivosBadStringResource + ";" +
                                    totalreusoInadequadoDeString + ";" +
//                                    totalArquivosreusoInadequadoDeString + ";" +
                                    0 + ";" +
//                                    totalArquivosNotFoundImage + ";" +
                                    0 + ";" +
//                                    totalArquivosCoupledUIComponent + ";" +
                                    0 + ";" +
//                                    totalArquivosSuspiciousBehavior + ";" +
                                    0 + ";" +
//                                    totalArquivosFlexAdapter + ";" +
                                    0 + ";" +
//                                    totalArquivosFoolAdapter + ";" +
                                    0 + ";" +
//                                    totalArquivosBrainUIComponent + ";" +
                                    0 + ";" +
//                                    totalArquivosCompUIIO + ";" +
                                    0 + ";" +
//                                    totalArquivosNotFragment + ";" +
                                    0 + ";" +
//                                    totalArquivosExcessiveFragment + ";" +
                                    CLOC
                    );
                    writer.append("\n");
                    writer.flush();

                }

                long totalNotFoundImage = ImportantSmells2.NotFoundImage(caminho);

                if (totalNotFoundImage != 0) {
                    totalNotFoundImage = 1;
                } else {
                    totalNotFoundImage = 0;
                }

                writer.append(
                        app + ";" +
                                app + ";" +
                                0 + ";" +
//                                    totalArquivosDeepNested + ";" +
                                0 + ";" +
//                                    totalArquivosDuplicateStyleAttributes + ";" +
                                0 + ";" +
//                                    totalArquivosGodStyleResource + ";" +
                                0 + ";" +
//                                    totalArquivosHideListener + ";" +
                                0 + ";" +
//                                    totalArquivosmagicResource + ";" +
                                0 + ";" +
//                                    totalArquivosBadStringResource + ";" +
                                0 + ";" +
//                                    totalArquivosreusoInadequadoDeString + ";" +
                                totalNotFoundImage + ";" +
//                                    totalArquivosNotFoundImage + ";" +
                                0 + ";" +
//                                    totalArquivosCoupledUIComponent + ";" +
                                0 + ";" +
//                                    totalArquivosSuspiciousBehavior + ";" +
                                0 + ";" +
//                                    totalArquivosFlexAdapter + ";" +
                                0 + ";" +
//                                    totalArquivosFoolAdapter + ";" +
                                0 + ";" +
//                                    totalArquivosBrainUIComponent + ";" +
                                0 + ";" +
//                                    totalArquivosCompUIIO + ";" +
                                0 + ";" +
//                                    totalArquivosNotFragment + ";" +
                                0 + ";" +
//                                    totalArquivosExcessiveFragment + ";" +
                                CLOC
                );
                writer.append("\n");
                writer.flush();




                //analise de codigo JAVA
                ImportantSmells2.carregaArquivosJAVAAnalise(f);

                for (File fileJava : ImportantSmells2.ListArquivosAnaliseJava) {
//                    System.out.println(fileJava.getAbsolutePath());

                    long totalCoupledUIComponent = ImportantSmells2.CoupledUIComponent(fileJava);


                    if (totalCoupledUIComponent != 0) {
                        totalCoupledUIComponent = 1;
                    } else {
                        totalCoupledUIComponent = 0;
                    }


//                    long totalArquivosCoupledUIComponent = ImportantSmells2.contadorArquivosAnalisados;



                    long totalSuspiciousBehavior = ImportantSmells2.SuspiciousBehavior(fileJava);

                    if (totalSuspiciousBehavior != 0) {
                        totalSuspiciousBehavior = 1;
                    } else {
                        totalSuspiciousBehavior = 0;
                    }

//                    long totalArquivosSuspiciousBehavior = ImportantSmells2.contadorArquivosAnalisados;
                    long totalBrainUIComponent = ImportantSmells2.BrainUIComponent(fileJava);

                    if (totalBrainUIComponent != 0) {
                        totalBrainUIComponent = 1;
                    } else {
                        totalBrainUIComponent = 0;
                    }



//                    long totalArquivosBrainUIComponent = ImportantSmells2.contadorArquivosAnalisados;
                    long totalFlexAdapter = ImportantSmells2.FlexAdapter(fileJava);

                    if (totalFlexAdapter != 0) {
                        totalFlexAdapter = 1;
                    } else {
                        totalFlexAdapter = 0;
                    }

//                    long totalArquivosFlexAdapter = ImportantSmells2.contadorArquivosAnalisados;
                    long totalFoolAdapter = ImportantSmells2.FoolAdapter(fileJava);


                    if (totalFoolAdapter != 0) {
                        totalFoolAdapter = 1;
                    } else {
                        totalFoolAdapter = 0;
                    }

//                    long totalArquivosFoolAdapter = ImportantSmells2.contadorArquivosAnalisados;
                    long totalCompUIIO = ImportantSmells2.CompUIIO(fileJava);


                    if (totalCompUIIO != 0) {
                        totalCompUIIO = 1;
                    } else {
                        totalCompUIIO = 0;
                    }





//                    long totalArquivosExcessiveFragment = ImportantSmells2.contadorArquivosAnalisados;

                    writer.append(
                            app + ";" +
                                    fileJava + ";" +
                                    0 + ";" +
//                                    totalArquivosDeepNested + ";" +
                                    0 + ";" +
//                                    totalArquivosDuplicateStyleAttributes + ";" +
                                    0 + ";" +
//                                    totalArquivosGodStyleResource + ";" +
                                    0 + ";" +
//                                    totalArquivosHideListener + ";" +
                                    0 + ";" +
//                                    totalArquivosmagicResource + ";" +
                                    0 + ";" +
//                                    totalArquivosBadStringResource + ";" +
                                    0 + ";" +
//                                    totalArquivosreusoInadequadoDeString + ";" +
                                    0 + ";" +
//                                    totalArquivosNotFoundImage + ";" +
                                    totalCoupledUIComponent + ";" +
//                                    totalArquivosCoupledUIComponent + ";" +
                                    totalSuspiciousBehavior + ";" +
//                                    totalArquivosSuspiciousBehavior + ";" +
                                    totalFlexAdapter + ";" +
//                                    totalArquivosFlexAdapter + ";" +
                                    totalFoolAdapter + ";" +
//                                    totalArquivosFoolAdapter + ";" +
                                    totalBrainUIComponent + ";" +
//                                    totalArquivosBrainUIComponent + ";" +
                                    totalCompUIIO + ";" +
//                                    totalArquivosCompUIIO + ";" +
                                    0 + ";" +
//                                    totalArquivosNotFragment + ";" +
                                    0 + ";" +
//                                    totalArquivosExcessiveFragment + ";" +
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

                //                    long totalArquivosCompUIIO = ImportantSmells2.contadorArquivosAnalisados;
                long totalNotFragment = ImportantSmells2.NotFragmentApp();

                if (totalNotFragment != 0) {
                    totalNotFragment = 1;
                } else {
                    totalNotFragment = 0;
                }

                //                    long totalArquivosNotFragment = ImportantSmells2.contadorArquivosAnalisados;
                long totalExcessiveFragment = ImportantSmells2.ExcessiveFragmentApp( 10);

                if (totalExcessiveFragment != 0) {
                    totalExcessiveFragment = 1;
                } else {
                    totalExcessiveFragment = 0;
                }



                writer.append(
                        app + ";" +
                                app + ";" +
                                0 + ";" +
//                                    totalArquivosDeepNested + ";" +
                                0 + ";" +
//                                    totalArquivosDuplicateStyleAttributes + ";" +
                                0 + ";" +
//                                    totalArquivosGodStyleResource + ";" +
                                0 + ";" +
//                                    totalArquivosHideListener + ";" +
                                0 + ";" +
//                                    totalArquivosmagicResource + ";" +
                                0 + ";" +
//                                    totalArquivosBadStringResource + ";" +
                                0 + ";" +
//                                    totalArquivosreusoInadequadoDeString + ";" +
                                0 + ";" +
//                                    totalArquivosNotFoundImage + ";" +
                                0 + ";" +
//                                    totalArquivosCoupledUIComponent + ";" +
                                0 + ";" +
//                                    totalArquivosSuspiciousBehavior + ";" +
                                0 + ";" +
//                                    totalArquivosFlexAdapter + ";" +
                                0 + ";" +
//                                    totalArquivosFoolAdapter + ";" +
                                0 + ";" +
//                                    totalArquivosBrainUIComponent + ";" +
                                0 + ";" +
//                                    totalArquivosCompUIIO + ";" +
                                totalNotFragment + ";" +
//                                    totalArquivosNotFragment + ";" +
                                totalExcessiveFragment + ";" +
//                                    totalArquivosExcessiveFragment + ";" +
                                CLOC
                );



            }
            writer.append("\n");
                writer.flush();
                writer.close();

//                long fim = System.currentTimeMillis();
//                System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date(inicio - fim)));

        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        try {



            DropboxUpload.uploadToDropbox(fileCsv.getAbsolutePath(), nameCSV);



        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static long calCLOC(String appPath) throws IOException, InterruptedException{
        String command = "cloc " + appPath;

        long CLOC = 0;
        Process proc = Runtime.getRuntime().exec(command);

        // Read the output

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String line = "";
        while((line = reader.readLine()) != null) {

            System.out.print(line + "\n");

            if (line.contains("SUM:")){

                String[] array = line.split(" ");

                CLOC = new Long(array[array.length-1]);

                System.out.print("Tamanho do projeto é " + array[array.length-1] + "\n");
            }

        }

        proc.waitFor();
        return CLOC;

    }
}
