package br.com.UTIL;

import br.com.AndroidDetector.IOClass;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import br.com.metric.WMC;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Threshold {
    public static List<File> arquivosAnalise = new ArrayList<File>();
    public static final String JAVA = ".java";
    public static final String XML = ".xml";
    private static SAXBuilder sb = new SAXBuilder();
    private static long qtdSubelementos = 0;
    private static String arquivo_analisado;

    private static List<ReusoStringData> lista = new ArrayList<ReusoStringData>();
    private static List<ReusoStringData> listaDeepNested = new ArrayList<ReusoStringData>();


    public static List<File> ListArquivosAnaliseJava = new ArrayList<File>();

    private static long quantidadeIF = 0;
    private static long quantidadeWMC = 0;
    private static long quantidadeSWIFT = 0;
    private static long quantidadeFieldStatic = 0;
    private static long quantidadeLIBSIODeclaracaoDeCampos = 0;
    private static long quantidadeLIBSIODeclaracaoDeMethods = 0;
    private static long quantidadeLIBSIORETORNOMethod = 0;
    private static long quantidadeLIBSIOFieldDeclaracaoCampos = 0;

    //component UI doing IO
    private static long libsIOnoTIPOemDeclaracaoDeCampos = 0;
    private static long libsIONoTipoemDeclaracaoNosParametrosDeMetodo = 0;
    private static long libsIONoTipoRetornoDeMetodo = 0;
    private static long libsIOnoTIPODeclaracaoDECAMPOS = 0;


    public static void carregaArquivosJAVAAnalise(File directory) {
        arquivosAnalise.clear();
        listar(directory, JAVA);
        ListArquivosAnaliseJava = arquivosAnalise;
    }


    public static void listar(File directory, String tipo) {
        if (directory.isDirectory()) {
            //System.out.println(directory.getPath());

            String[] myFiles = directory.list(new FilenameFilter() {
                public boolean accept(File directory, String fileName) {
                    return fileName.endsWith(tipo);
                }
            });

            for (int i = 0; i < myFiles.length; i++) {
                arquivosAnalise.add(new File(directory.getPath() + File.separator + myFiles[i].toString()));
            }

            String[] subDirectory = directory.list();
            if (subDirectory != null) {
                for (String dir : subDirectory) {
                    listar(new File(directory + File.separator + dir), tipo);
                }
            }
        }
    }

    public static void DeepNestedLayout(String pathApp) {
        try {
            arquivosAnalise.clear();
            listar(new File(pathApp), XML);

            System.out.println("arquivo" + ";" + "Níveis");

            for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
                try {
                    //System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                    //System.out.println("---------------------------------------------------------------------------------------");
                    arquivo_analisado = arquivosAnalise.toArray()[cont].toString();

                    File f = new File(arquivosAnalise.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (arquivosAnalise.toArray()[cont].toString().contains("\\layout\\")) {
                        //ACESSAR O ROOT ELEMENT
                        Element rootElmnt = d.getRootElement();

                        //BUSCAR ELEMENTOS FILHOS DA TAG
                        List elements = rootElmnt.getChildren();

                        recursiveChildrenElement(elements);

                        //System.out.println("---------------------------------------------------------------------------------------");

                    }
                } catch (Exception ex) {

                }
            }

            File file = new File("C:\\Detector\\TrasholdDeepNestedLayout.csv");

            // creates the file
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.append("arquivo" + ";" + "Qtd Níveis");
            writer.append("\n");

            for (ReusoStringData item : listaDeepNested) {
                writer.append(item.arquivo + ";" + item.strString + ";");
                writer.append("\n");

            }

            writer.flush();
            writer.close();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static void recursiveChildrenElement(List elements) {
        for (int i = 0; i < elements.size(); i++) {
            try {
                org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                List SubElements = el.getChildren();

                if (SubElements.size() > 0) {
                    qtdSubelementos = qtdSubelementos + 1;
                    recursiveChildrenElement(SubElements);
                } else {
                    System.out.println(arquivo_analisado + ";" + qtdSubelementos);
                    ReusoStringData data = new ReusoStringData();
                    data.arquivo = arquivo_analisado;
                    data.strString = qtdSubelementos + "";

                    listaDeepNested.add(data);
                    qtdSubelementos = 0;
                }
            } catch (Exception ex) {

            }
        }
    }


// Main do Brain ....
//    public static void main (String... args) throws  IOException{
//        File fileCsv = new File("/Users/rafaeldurelli/Desktop/Analise/AnaliseFinalThreshold.csv");
//
//        FileWriter writer = new FileWriter(fileCsv);
//
//        File file = new File("/Users/rafaeldurelli/Desktop/Repositorio01");
//        File afile[] = file.listFiles();
//
//        fileCsv.createNewFile();
//
//        writer.append("Aplicativo" + "," + "QuantidadeIF" + "," + "QuantidadeSWIFT" + "," + "quantidadeFieldStatic" + "," +
//                "quantidadeLIBSIODeclaracaoDeCampos" + "," + "quantidadeLIBSIODeclaracaoDeMethods" + "," + "" +
//                "quantidadeLIBSIORETORNOMethod" + "," + "quantidadeLIBSIOFieldDeclaracaoCampos");
//        writer.append("\n");
//
//
//        for(int j = 0; j< afile.length; j++) {
//            File f = new File(afile[j].toString());
//            //Getting all files to apply resource smells
//
//            carregaArquivosJAVAAnalise(f);
//
//            System.out.println();
//            System.out.println();
//            System.out.println(f.getName() + " - " + j);
//            System.out.println();
//            System.out.println();
//
//            String caminho = afile[j].toString();
//
//            String app = f.getName();
//
//            BrainUIComponent(caminho);
//
//
//
//
//        writer.append(
//        app + "," +
//        quantidadeIF + "," +
//        quantidadeSWIFT  + "," +
//        quantidadeFieldStatic + "," +
//        quantidadeLIBSIODeclaracaoDeCampos + "," +
//        quantidadeLIBSIODeclaracaoDeMethods + "," +
//        quantidadeLIBSIORETORNOMethod + "," +
//        quantidadeLIBSIOFieldDeclaracaoCampos + ",");
//
//        writer.append("\n");
//        writer.flush();
//
//    }
//        writer.flush();
//        writer.close();
//    }


    //Main para verificar o threshold do COmpUI doing IO

//    public static void main (String... args) throws  IOException{
//        File fileCsv = new File("/Users/rafaeldurelli/Desktop/Analise/AnaliseThreshouldWMC.csv");
//
//        FileWriter writer = new FileWriter(fileCsv);
//
//        File file = new File("/Users/rafaeldurelli/Desktop/Repositorio01");
//        File afile[] = file.listFiles();
//
//        fileCsv.createNewFile();
//
//
//        writer.append("Aplicativo" + "," +
//                "libsIOnoTIPOemDeclaracaoDeCampos" + "," +
//                "libsIONoTipoemDeclaracaoNosParametrosDeMetodo" + ","
//                + "libsIONoTipoRetornoDeMetodo" + "," +
//                "libsIOnoTIPODeclaracaoDECAMPOS");
//        writer.append("\n");
//
//
//        for(int j = 0; j< afile.length; j++) {
//            File f = new File(afile[j].toString());
//            //Getting all files to apply resource smells
//
//            carregaArquivosJAVAAnalise(f);
//
//            System.out.println();
//            System.out.println();
//            System.out.println(f.getName() + " - " + j);
//            System.out.println();
//            System.out.println();
//
//            String caminho = afile[j].toString();
//
//            String app = f.getName();
//
//            CompUIIOTHRESHOLD(caminho, writer, app);
//
//
//
//
//        }
//        writer.flush();
//        writer.close();
//    }


//    //main para verificar o WMC
//    public static void main (String... args) throws  IOException{
//        File fileCsv = new File("/Users/rafaeldurelli/Desktop/Analise/AnaliseThreshouldWMC.csv");
//
//        FileWriter writer = new FileWriter(fileCsv);
//
//        File file = new File("/Users/rafaeldurelli/Desktop/Repositorio01");
//        File afile[] = file.listFiles();
//
//        fileCsv.createNewFile();
//
//
//        writer.append("Aplicativo" + "," +
//                "WMC");
//        writer.append("\n");
//
//
//        for(int j = 0; j< afile.length; j++) {
//            File f = new File(afile[j].toString());
//            //Getting all files to apply resource smells
//
//            carregaArquivosJAVAAnalise(f);
//
//            System.out.println();
//            System.out.println();
//            System.out.println(f.getName() + " - " + j);
//            System.out.println();
//            System.out.println();
//
//            String caminho = afile[j].toString();
//
//            String app = f.getName();
//
//            comptThresholdWMC(caminho, writer, app);
//
//
//        }
//        writer.flush();
//        writer.close();
//    }

//    //main para verificar threshold para StaticField
//    public static void main (String... args) throws  IOException{
//        File fileCsv = new File("/Users/rafaeldurelli/Desktop/Analise/AnaliseThreshouldStaticField.csv");
//
//        FileWriter writer = new FileWriter(fileCsv);
//
//        File file = new File("/Users/rafaeldurelli/Desktop/Repositorio01");
//        File afile[] = file.listFiles();
//
//        fileCsv.createNewFile();
//
//
//        writer.append("Aplicativo" + "," +
//                "StaticField");
//        writer.append("\n");
//
//
//        for(int j = 0; j< afile.length; j++) {
//            File f = new File(afile[j].toString());
//            //Getting all files to apply resource smells
//
//            carregaArquivosJAVAAnalise(f);
//
//            System.out.println();
//            System.out.println();
//            System.out.println(f.getName() + " - " + j);
//            System.out.println();
//            System.out.println();
//
//            String caminho = afile[j].toString();
//
//            String app = f.getName();
//
//            comptThresholdSTaticField(caminho, writer, app);
//
//
//        }
//        writer.flush();
//        writer.close();
//    }


    public static void comptThresholdWMC(String pathApp, FileWriter writer, String app) {
        try {

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {

                quantidadeWMC = 0;

                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit cUnit = JavaParser.parse(f);

                    WMC wmc = new WMC(cUnit);
                    wmc.run();
                    quantidadeWMC = wmc.getCc();


                    writer.append(
                            app + "," +
                                    quantidadeWMC + ",");

                    writer.append("\n");
                    writer.flush();


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void comptThresholdSTaticField(String pathApp, FileWriter writer, String app) {
        try {

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {

                quantidadeFieldStatic = 0;

                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit cUnit = JavaParser.parse(f);

                    cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {
                        if (classe.getExtendedTypes().size() > 0) {
                            if (classe.getExtendedTypes().get(0).toString().contains("Activity") || classe.getExtendedTypes().get(0).toString().contains("Fragment") || classe.getExtendedTypes().get(0).toString().contains("Adapter")) {

                                //Aqui conta para verificar o threshold de fieldstatic ... mas tem que pensar nisso.
                                quantidadeFieldStatic = 0;
                                classe.getFields().forEach(item -> {
                                    if (item.isFieldDeclaration() && item.isStatic()) {
                                        quantidadeFieldStatic++;
                                    }
                                });
                            }
                        }
                    });


                    writer.append(
                            app + "," +
                                    quantidadeFieldStatic + ",");

                    writer.append("\n");
                    writer.flush();


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    //Componente de UI Fazendo IO
    public static void CompUIIOTHRESHOLD(String pathApp, FileWriter writer, String app) {

        try {

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {

                libsIOnoTIPOemDeclaracaoDeCampos = 0;
                libsIONoTipoemDeclaracaoNosParametrosDeMetodo = 0;
                libsIONoTipoRetornoDeMetodo = 0;
                libsIOnoTIPODeclaracaoDECAMPOS = 0;

                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit cUnit = JavaParser.parse(f);

                    cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {

                        if (classe.getExtendedTypes().size() > 0) {

                            if (classe.getExtendedTypes().get(0).toString().contains("Activity") || classe.getExtendedTypes().get(0).toString().contains("Fragment") || classe.getExtendedTypes().get(0).toString().contains("Adapter")) {

                                //Procura Libs IO no TIPO  em declaração de campos
                                classe.getFields().forEach(campos -> {
                                    if (IOClass.getIOClass().contains(campos.getElementType().toString())) {
                                        libsIOnoTIPOemDeclaracaoDeCampos++;

                                    }
                                });

                                //Procura Libs de IO no TIPO em declaração  de Métodos
                                classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                                    IOClass.getIOClass().forEach(item -> {
                                        //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                        if (metodo.getParameters().contains(item)) {
                                            libsIONoTipoemDeclaracaoNosParametrosDeMetodo++;
                                        }

                                        //Procura Libs de IO no TIPO em retorno  de Métodos
                                        if (metodo.getType().toString().contains(item)) {
                                            libsIONoTipoRetornoDeMetodo++;
                                        }

                                        //Procura Libs IO no TIPO  em declaração de campos
                                        metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                            if (campos.getElementType().toString().contains(item)) {
                                                libsIOnoTIPODeclaracaoDECAMPOS++;
                                            }
                                        });
                                    });
                                });
                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                writer.append(
                        app + "," +
                                libsIOnoTIPOemDeclaracaoDeCampos + "," +
                                libsIONoTipoemDeclaracaoNosParametrosDeMetodo + "," +
                                libsIONoTipoRetornoDeMetodo + "," +
                                libsIOnoTIPODeclaracaoDECAMPOS + ",");

                writer.append("\n");
                writer.flush();

            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void BrainUIComponent(String pathApp) {

        quantidadeIF = 0;
        quantidadeSWIFT = 0;
        quantidadeFieldStatic = 0;
        quantidadeLIBSIODeclaracaoDeCampos = 0;
        quantidadeLIBSIODeclaracaoDeMethods = 0;
        quantidadeLIBSIORETORNOMethod = 0;
        quantidadeLIBSIOFieldDeclaracaoCampos = 0;


        try {
            //arquivosAnalise.clear();
//            totalSmells = 0;

            //listar(new File(pathApp),JAVA);

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit cUnit = JavaParser.parse(f);

                    cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {
                        if (classe.getExtendedTypes().get(0).toString().contains("Activity") || classe.getExtendedTypes().get(0).toString().contains("Fragment") || classe.getExtendedTypes().get(0).toString().contains("Adapter")) {
                            //ifElseSwitchCase (Regra de negócios)
                            NodeList<BodyDeclaration<?>> membros = classe.getMembers();
                            for (BodyDeclaration<?> membro : membros) {
                                //Verifica se o membro é um método
                                if (membro.isMethodDeclaration()) {

                                    quantidadeIF = membro.findAll(IfStmt.class).size();

                                    quantidadeSWIFT = membro.findAll(SwitchEntryStmt.class).size();
                                }
                            }


                            //Aqui conta para verificar o threshold de fieldstatic ... mas tem que pensar nisso.
                            quantidadeFieldStatic = 0;
                            classe.getFields().forEach(item -> {
                                if (item.isFieldDeclaration() && item.isStatic()) {
                                    quantidadeFieldStatic++;
                                }
                            });


                            //Procura Libs IO no TIPO  em declaração de campos
                            classe.getFields().forEach(campos -> {
                                if (IOClass.getIOClass().contains(campos.getElementType().toString())) {
                                    quantidadeLIBSIODeclaracaoDeCampos++;
                                }
                            });

                            //Procura Libs de IO no TIPO em declaração  de Métodos
                            classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                                IOClass.getIOClass().forEach(item -> {
                                    //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                    if (metodo.getParameters().contains(item)) {

                                        quantidadeLIBSIODeclaracaoDeMethods++;

                                    }

                                    //Procura Libs de IO no TIPO em retorno  de Métodos
                                    if (metodo.getType().toString().contains(item)) {
                                        quantidadeLIBSIORETORNOMethod++;

                                        //qualquer coisa tirar o tipo de retorno do método..
                                        //esta me parecendo muito falso positivo....
                                    }

                                    //Procura Libs IO no TIPO  em declaração de campos
                                    metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                        if (campos.getElementType().toString().contains(item)) {
                                            quantidadeLIBSIOFieldDeclaracaoCampos++;
                                        }
                                    });

                                });
                            });


                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    public static void GodStyleResource(String pathApp) {
        try {
            arquivosAnalise.clear();
            listar(new File(pathApp), XML);

            int qtdFilesStyle = 0;
            System.out.println("arquivo" + ";" + "Qtd Stilos");

            for (int cont = 0; cont < (arquivosAnalise.toArray().length - 1); cont++) {
                try {
                    arquivo_analisado = arquivosAnalise.toArray()[cont].toString();
                    File f = new File(arquivosAnalise.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (d.getRootElement().getChildren().size() > 0) {
                        if (d.getRootElement().getChildren().get(0).getName() == "style") {
                            //qtdFilesStyle = qtdFilesStyle + 1;
                            //System.out.println(arquivosAnalise.toArray()[cont]);
                            ReusoStringData data = new ReusoStringData();
                            data.arquivo = arquivo_analisado;
                            data.strString = d.getRootElement().getChildren().size() + "";

                            lista.add(data);
                            System.out.println(arquivo_analisado + ";" + d.getRootElement().getChildren().size());
                        }
                    }
                } catch (Exception ex) {

                }
            }

            File file = new File("C:\\Detector\\TrasholdGodStyleResource.csv");

            // creates the file
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.append("arquivo" + ";" + "Qtd Stilos");
            writer.append("\n");

            for (ReusoStringData item : lista) {
                writer.append(item.arquivo + ";" + item.strString + ";");
                writer.append("\n");

            }

            writer.flush();
            writer.close();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void ExcessiveFragment(String pathApp) throws IOException {

        long totalFragments = 0;
        List<ReusoStringData> listaExcessiveFragment = new ArrayList<ReusoStringData>();

        File file = new File(pathApp);
        File afile[] = file.listFiles();

        for (int j = 0; j < afile.length; j++) {
            //System.out.println(afile[j]);
            arquivosAnalise.clear();

            listar(new File(afile[j].toString()), JAVA);

            for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
                try {
                    arquivo_analisado = arquivosAnalise.toArray()[cont].toString();
                    //System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                    //System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(arquivosAnalise.toArray()[cont].toString());
                    CompilationUnit cu = JavaParser.parse(f);

                    ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<ClassOrInterfaceDeclaration>();
                    NodeList<TypeDeclaration<?>> types = cu.getTypes();
                    for (int i = 0; i < types.size(); i++) {
                        classes.add((ClassOrInterfaceDeclaration) types.get(i));
                    }

                    for (ClassOrInterfaceDeclaration classe : classes) {
                        NodeList<ClassOrInterfaceType> implementacoes = classe.getExtendedTypes();
                        if (implementacoes.size() != 0) {
                            for (ClassOrInterfaceType implementacao : implementacoes) {
                                if (implementacao.getName().getIdentifier().contains("Fragment")) {
                                    totalFragments = totalFragments + 1;
                                }
                            }
                        }
                    }

                } catch (Exception ex) {

                }
            }

            ReusoStringData data = new ReusoStringData();
            data.arquivo = afile[j].toString();
            data.strString = totalFragments + "";

            listaExcessiveFragment.add(data);

            System.out.println(arquivo_analisado + ";" + totalFragments);
            totalFragments = 0;
        }


        File fileCsv = new File("/Users/rafaeldurelli/Desktop/Analise/ThresholdExcessiveFragment.csv");


        // creates the file
        fileCsv.createNewFile();
        FileWriter writer = new FileWriter(fileCsv);
        writer.append("arquivo" + ";" + "Qtd Fragment");
        writer.append("\n");

        for (ReusoStringData item : listaExcessiveFragment) {
            writer.append(item.arquivo + ";" + item.strString + ";");
            writer.append("\n");

        }

        writer.flush();
        writer.close();


    }


}
