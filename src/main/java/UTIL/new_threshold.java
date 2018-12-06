package UTIL;

import AndroidDetector.IOClass;
import AndroidDetector.ImportantSmells;
import AndroidDetector.OutputSmells;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class new_threshold {
    private static int contadorFieldStatic = 0;

    private static long qtdSubelementos = 0;
    private static File arquivos[];
    private static File diretorio = null;
    private static SAXBuilder sb = new SAXBuilder();
    public static  Boolean classeValida = true;

    public static List<File> ListArquivosAnaliseXML =  new ArrayList<File>();
    public static List<File> ListArquivosAnaliseJava =  new ArrayList<File>();
    public static List<File> arquivosAnalise =  new ArrayList<File>();

    public static final String JAVA = ".java";
    public static final String XML = ".xml";
    private static OutputSmells JsonOut = new  OutputSmells();
    private static List<OutputSmells> ListJsonSmell = new ArrayList<OutputSmells>();
    private  static List<OutputSmells> ListSmells = new ArrayList<OutputSmells>();
    private static List<ReusoStringData> textStringArquivo = new ArrayList<ReusoStringData>();
    private  static List<String> FilesIMG = new ArrayList<String>();
    private static long totalSmells = 0;

    private static long quantidadeIF = 0;
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
    private static long libsIOnoTIPODeclaracaoDECAMPOS = 0 ;

    public static void listar(File directory,String tipo) {
        if(directory.isDirectory()) {
            //System.out.println(directory.getPath());

            String[] myFiles = directory.list(new FilenameFilter() {
                public boolean accept(File directory, String fileName) {
                    return fileName.endsWith(tipo);
                }
            });

            for(int i = 0; i < myFiles.length; i++){
                arquivosAnalise.add(new File(directory.getPath() + ""+File.separator+"" + myFiles[i].toString()));
            }

            String[] subDirectory = directory.list();
            if(subDirectory != null) {
                for(String dir : subDirectory){
                    listar(new File(directory + File.separator  + dir),tipo);
                }
            }
        }
    }


    //.DeepNestedLayout(caminho, 3);
    public static long DeepNestedLayout(String pathApp, int threshold) {
        try {
            arquivosAnalise.clear();
            ListSmells.clear();


            File fileCsv = new File("C:\\Detector\\DeepNestedLayoutThresholdFinal.csv");
            FileWriter writer = new FileWriter(fileCsv);
            fileCsv.createNewFile();

            writer.append("Arquivo" + ";" + "qtdNiveisLayout");
            writer.append("\n");

            listar(new File(pathApp),XML);

            for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
                totalSmells = 0;
                try {
                    System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(arquivosAnalise.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    //ACESSAR O ROOT ELEMENT
                    Element rootElmnt = d.getRootElement();

                    //BUSCAR ELEMENTOS FILHOS DA TAG
                    List elements = rootElmnt.getChildren();
                    recursiveChildrenElement(elements, threshold);

                    writer.append(f.getName() + ";" + totalSmells);
                    writer.append("\n");
                    System.out.println("---------------------------------------------------------------------------------------");

                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }

            writer.flush();
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    private static void recursiveChildrenElement(List elements, int threshold) {
        for (int i = 0; i < elements.size(); i++) {
            try {
                org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                List SubElements = el.getChildren();

                if (SubElements.size() > 0) {
                    qtdSubelementos = qtdSubelementos + 1;
                    recursiveChildrenElement(SubElements, threshold);
                } else {
                        System.out.println("Layout Profundamente Aninhado encontrado " + el.getName() + "(Mais de " + threshold + " níveis)");
                        totalSmells++;
                        break;
                }
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }


    //.GodStyleResource(caminho,5);
    public static long GodStyleResource(String pathApp,int threshold) {
        try {
            arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            listar(new File(pathApp),XML);
            int qtdLimiteStilos = threshold;
            int qtdFilesStyle = 0;

            File fileCsv = new File("C:\\Detector\\GodStyleResourceThresholdFinal.csv");
            FileWriter writer = new FileWriter(fileCsv);
            fileCsv.createNewFile();

            writer.append("Arquivo" + ";" + "qtdEstilosArquivo");
            writer.append("\n");

            for (int cont = 0; cont < (arquivosAnalise.toArray().length - 1); cont++) {
                totalSmells = 0;
                try {
                    //System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(arquivosAnalise.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);
                    //vamos mudar aqui

                    if (d.getRootElement().getChildren().size() > 0) {
                        if (d.getRootElement().getChildren().get(0).getName() == "style") {
                            qtdFilesStyle = qtdFilesStyle + 1;
                            System.out.println(arquivosAnalise.toArray()[cont]);

                            writer.append(f.getName() + ";" + d.getRootElement().getChildren().size());
                            writer.append("\n");
                        }
                    }

                    if ((qtdFilesStyle == 1)) {
                        //System.out.println("->"+arquivosAnalise.toArray()[cont].toString());
                        System.out.println("Longo recurso de Estilo detectado (existe apenas um arquivo para estilos no aplicativo que possui " + d.getRootElement().getChildren().size() + " estilos)");
                        System.out.println("---------------------------------------------------------------------------------------");

                        totalSmells++;
                    }
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }

            writer.flush();
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    //.ExcessiveFragment(caminho,5);
    public static long ExcessiveFragment(String pathApp, long threshold) throws IOException {
        arquivosAnalise.clear();
        ListSmells.clear();
        totalSmells = 0;

        listar(new File(pathApp),JAVA);
        long totalFragments = 0;

        File fileCsv = new File("C:\\Detector\\ExcessiveFragmentThresholdFinal.csv");
        FileWriter writer = new FileWriter(fileCsv);
        fileCsv.createNewFile();

        writer.append("Arquivo" + ";" + "qtdFragments");
        writer.append("\n");

        for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
            try {
                totalFragments = 0;
                System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
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

                writer.append(f.getName() + ";" + totalFragments);
                writer.append("\n");


            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }

        writer.flush();
        writer.close();

        return totalSmells;

    }

    //.BrainUIComponent(caminho);
    public static void BrainUIComponent(String pathApp) {

        try {
            arquivosAnalise.clear();
            //totalSmells = 0;

            File fileCsv = new File("C:\\Detector\\BrainUIComponentThresholdFinal.csv");
            FileWriter writer = new FileWriter(fileCsv);
            fileCsv.createNewFile();


            writer.append("Aplicativo" + "," +
                    "quantidadeIF" + "," +
                    "quantidadeSWIT" + "," +
                    "quantidadeFieldStatic" + "," +
                    "libsIOnoTIPOemDeclaracaoDeCampos" + "," +
                    "libsIONoTipoemDeclaracaoNosParametrosDeMetodo" + ","
                    + "libsIONoTipoRetornoDeMetodo" + "," +
                    "libsIOnoTIPODeclaracaoDECAMPOS");
            writer.append("\n");


            listar(new File(pathApp),JAVA);

            for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
                quantidadeIF = 0;
                quantidadeSWIFT = 0;
                quantidadeFieldStatic = 0;
                quantidadeLIBSIODeclaracaoDeCampos = 0;
                quantidadeLIBSIODeclaracaoDeMethods = 0;
                quantidadeLIBSIORETORNOMethod = 0;
                quantidadeLIBSIOFieldDeclaracaoCampos = 0;

                System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                String nomeArquivo = arquivosAnalise.toArray()[cont].toString();
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(arquivosAnalise.toArray()[cont].toString());
                try {
                    CompilationUnit cUnit = JavaParser.parse(f);

                    cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {
                        if (classe.getExtendedTypes().get(0).toString().contains("Activity") || classe.getExtendedTypes().get(0).toString().contains("Fragment") || classe.getExtendedTypes().get(0).toString().contains("Adapter")) {
                            //ifElseSwitchCase (Regra de negócios)
                            NodeList<BodyDeclaration<?>> membros = classe.getMembers();
                            for (BodyDeclaration<?> membro : membros) {
                                //Verifica se o membro é um método
                                if (membro.isMethodDeclaration()) {
                                    quantidadeIF =  membro.findAll(IfStmt.class).size();
                                    quantidadeSWIFT =  membro.findAll(SwitchEntryStmt.class).size();
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
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }


                writer.append(f.getName() + ";" + quantidadeIF + ";" + quantidadeSWIFT + ";" + quantidadeFieldStatic + ";" +quantidadeLIBSIODeclaracaoDeCampos + ";" + quantidadeLIBSIODeclaracaoDeMethods + ";" + quantidadeLIBSIORETORNOMethod + ";" + quantidadeLIBSIOFieldDeclaracaoCampos );
                writer.append("\n");


            }

            writer.flush();
            writer.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static long FlexAdapterThreshold(String pathApp) {
        try {
            ListSmells.clear();
            arquivosAnalise.clear();

            File fileCsv = new File("C:\\Detector\\FlexAdapterThresholdThresholdFinal.csv");
            FileWriter writer = new FileWriter(fileCsv);
            fileCsv.createNewFile();


            writer.append("Aplicativo" + ";" +
                    "quantidadeIF" + ";" +
                    "quantidadeSWITCH");
            writer.append("\n");

            listar(new File(pathApp),JAVA);

            for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
                quantidadeIF = 0;
                quantidadeSWIFT = 0;

                try {
                    System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");
                    String arquivo = arquivosAnalise.toArray()[cont].toString();
                    File f = new File(arquivosAnalise.toArray()[cont].toString());
                    CompilationUnit compilationunit = JavaParser.parse(f);

                    //Extrai cada Classe analisada pelo CompilationUnit
                    ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<ClassOrInterfaceDeclaration>();
                    NodeList<TypeDeclaration<?>> types = compilationunit.getTypes();
                    for (int i = 0; i < types.size(); i++) {
                        classes.add((ClassOrInterfaceDeclaration) types.get(i));
                    }
                    //Para cada uma dessas classes, verifica se ela é um Adapter (ou seja, se ela extende de BaseAdapter).
                    for (ClassOrInterfaceDeclaration classe : classes) {
                        //Como a classe vai ser analisada ainda, não contém smells por enquanto
                        Boolean isFlexAdapter = false;
                        //Para ver se a classe é um Adapter, precisamos ver se ela extende de BaseAdapter
                        //Pegamos todas as classes que ela implementa
                        NodeList<ClassOrInterfaceType> implementacoes = classe.getExtendedTypes();
                        for (ClassOrInterfaceType implementacao : implementacoes) {
                            if (implementacao.getName().getIdentifier().contains("Adapter")) {
                                //Se chegou até aqui, temos certeza de que é um adapter.
                                //Se a classe que extende do BaseAdapter tiver algum método que não seja sobrescrever um método de interface, é um FlexAdapter.
                                //Pegamos todos os membros da classe
                                NodeList<BodyDeclaration<?>> membros = classe.getMembers();
                                for (BodyDeclaration<?> membro : membros) {
                                    //Verifica se o membro é um método
                                    if (membro.isMethodDeclaration()) {

                                        membro.findAll(IfStmt.class).forEach(item -> {
                                            System.out.println("Flex Adapter detectado na classe " + item.getRange() + " (lógica utilizando if detectada)");
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            quantidadeIF++;
                                        });

                                        membro.findAll(SwitchStmt.class).forEach(item -> {
                                            System.out.println("Flex Adapter detectado na classe " + item.getRange() + " (lógica utilizando Switch/Case detectada)");
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            quantidadeSWIFT++;
                                        });
                                    }
                                }
                            }
                        }
                    }


                    writer.append(f.getName() + ";" + quantidadeIF + ";" + quantidadeSWIFT );
                    writer.append("\n");


                }
                catch(Exception ex){
                    ex.printStackTrace();
                }
            }

            writer.flush();
            writer.close();

        }
        catch(Exception ex){
            ex.printStackTrace();
        }

        return totalSmells;
    }


    public static void CompUIIOTHRESHOLD(String pathApp){
        try {
            arquivosAnalise.clear();
            totalSmells = 0;

            File fileCsv = new File("C:\\Detector\\CompUIIOTHRESHOLDThresholdFinal.csv");
            FileWriter writer = new FileWriter(fileCsv);
            fileCsv.createNewFile();


            writer.append("Aplicativo" + "," +
                    "libsIOnoTIPOemDeclaracaoDeCampos" + "," +
                    "libsIONoTipoemDeclaracaoNosParametrosDeMetodo" + ","
                    + "libsIONoTipoRetornoDeMetodo" + "," +
                    "libsIOnoTIPODeclaracaoDECAMPOS");
            writer.append("\n");


            listar(new File(pathApp),JAVA);


            for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
                libsIOnoTIPOemDeclaracaoDeCampos = 0;
                libsIONoTipoemDeclaracaoNosParametrosDeMetodo = 0;
                libsIONoTipoRetornoDeMetodo = 0;
                libsIOnoTIPODeclaracaoDECAMPOS = 0 ;


                    System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                    String nomeArquivo = arquivosAnalise.toArray()[cont].toString();
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(arquivosAnalise.toArray()[cont].toString());
                try
                {
                    CompilationUnit cUnit = JavaParser.parse(f);

                    cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {

                        if(classe.getExtendedTypes().size()>0){

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
                }
                catch(Exception ex){
                    ex.printStackTrace();
                }

                writer.append(
                        f.getName() + "," +
                                libsIOnoTIPOemDeclaracaoDeCampos + "," +
                                libsIONoTipoemDeclaracaoNosParametrosDeMetodo  + "," +
                                libsIONoTipoRetornoDeMetodo + "," +
                                libsIOnoTIPODeclaracaoDECAMPOS + ",");

                writer.append("\n");
                writer.flush();

            }

            writer.flush();
            writer.close();

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }


    public static void main (String... args) throws  IOException{
        //DeepNestedLayout("C:\\Users\\julio\\Desktop\\Repositorio01\\Repositorio01",0);
        //GodStyleResource("C:\\Users\\julio\\Desktop\\Repositorio01\\Repositorio01",0);
        ExcessiveFragment("C:\\Users\\julio\\Desktop\\Repositorio01\\Repositorio01",0);
        //FlexAdapterThreshold("C:\\Users\\julio\\Desktop\\Repositorio01\\Repositorio01");
        //CompUIIOTHRESHOLD("C:\\Users\\julio\\Desktop\\Repositorio01\\Repositorio01");
        //BrainUIComponent("C:\\Users\\julio\\Desktop\\Repositorio01\\Repositorio01");

    }
}
