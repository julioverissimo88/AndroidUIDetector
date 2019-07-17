package AndroidDetector;

import UTIL.ReusoStringData;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AndroidLayoutSmells {
    //region [Var Declaration]
    public static int contadorArquivosAnalisados = 0;
    private static long qtdSubelementos = 0;
    private static SAXBuilder sb = new SAXBuilder();
    public static List<File> ListArquivosAnaliseXML = new ArrayList<File>();
    public static final String JAVA = ".java";
    public static final String XML = ".xml";
    private static OutputSmells JsonOut = new OutputSmells();
    private static List<OutputSmells> ListJsonSmell = new ArrayList<OutputSmells>();
    private static List<OutputSmells> ListSmells = new ArrayList<OutputSmells>();
    private static List<ReusoStringData> textStringArquivo = new ArrayList<ReusoStringData>();
    private static List<String> FilesIMG = new ArrayList<String>();
    private static long totalSmells = 0;
    //enregion

    //Layout Profundamente Aninhado
    public static long DeepNestedLayout(String pathApp, int threshold) {
        try {
            contadorArquivosAnalisados = 0;

            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            for (int cont = 0; cont < ListArquivosAnaliseXML.toArray().length; cont++) {

                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;


                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseXML.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (ListArquivosAnaliseXML.toArray()[cont].toString().contains(File.separator + "layout" + File.separator)) {
                        //ACESSAR O ROOT ELEMENT
                        Element rootElmnt = d.getRootElement();

                        //BUSCAR ELEMENTOS FILHOS DA TAG
                        List elements = rootElmnt.getChildren();

                        recursiveChildrenElement(elements, threshold);

                        System.out.println("---------------------------------------------------------------------------------------");

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell, "DeepNestedLayout.json");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    //Atributo de estilo duplicado
    public static long DuplicateStyleAttributes(String pathApp) {
        try {
            contadorArquivosAnalisados = 0;

            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            for (int cont = 0; cont < ListArquivosAnaliseXML.toArray().length; cont++) {
                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseXML.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (d.getRootElement().getChildren().get(0).getName().toString() == "style") {
                        List<String> listSmellsEcontradas = new ArrayList<String>();

                        for (int i = 0; i < d.getRootElement().getChildren().size(); i++) {

                            List<Element> filhos = d.getRootElement().getChildren();
                            for (int j = 0; j < filhos.size(); j++) {
                                List<Attribute> attr = filhos.get(j).getAttributes();
                                for (Attribute atributo : attr) {

                                    String atributo_atual = atributo.toString();
                                    System.out.println(atributo_atual);

                                    for (int ii = 0; ii < d.getRootElement().getChildren().size(); ii++) {
                                        List<Element> filhosInterno = d.getRootElement().getChildren();
                                        for (int jj = 0; jj < filhosInterno.size(); jj++) {
                                            List<Attribute> attrInterno = filhosInterno.get(jj).getAttributes();
                                            for (Attribute atributoInterno : attrInterno) {

                                                if (jj > j) {
                                                    if (atributo_atual.toString().equals(atributoInterno.toString()) && !listSmellsEcontradas.contains(atributo_atual.toString())) {
                                                        listSmellsEcontradas.add(atributo_atual.toString());
                                                        System.out.println("Duplicate Style Attributes " + atributoInterno.getName() + " - Considere colocar a formatação das propriedades em um recurso de estilo:");
                                                        JsonOut.setTipoSmell("XML");
                                                        JsonOut.setArquivo(ListArquivosAnaliseXML.toArray()[cont].toString());
                                                        ListJsonSmell.add(JsonOut);
                                                        totalSmells++;
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell, "DuplicateStyleAttributes.json");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    //Longo Recurso de Estilo
    public static long GodStyleResource(String pathApp, int threshold) {
        try {
            contadorArquivosAnalisados = 0;
            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);
            int qtdLimiteStilos = threshold;
            int qtdFilesStyle = 0;

            for (int cont = 0; cont < (ListArquivosAnaliseXML.toArray().length - 1); cont++) {

                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                try {
                    //System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);
                    //vamos mudar aqui

                    if (d.getRootElement().getChildren().size() > 0) {
                        if (d.getRootElement().getChildren().get(0).getName() == "style") {
                            qtdFilesStyle = qtdFilesStyle + 1;
                            System.out.println(ListArquivosAnaliseXML.toArray()[cont]);
                        }
                    }

                    if ((qtdFilesStyle == 1) || (d.getRootElement().getChildren().size() > qtdLimiteStilos)) {
                        //System.out.println("->"+arquivosAnalise.toArray()[cont].toString());
                        System.out.println("Longo recurso de Estilo detectado (existe apenas um arquivo para estilos no aplicativo que possui " + d.getRootElement().getChildren().size() + " estilos)");
                        System.out.println("---------------------------------------------------------------------------------------");
                        JsonOut.setTipoSmell("XML");
                        JsonOut.setArquivo(ListArquivosAnaliseXML.toArray()[cont].toString());
                        ListJsonSmell.add(JsonOut);
                        totalSmells++;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell, "GodStyleResource.json");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    //Listener Oculto
    public static long HiddenListener(String pathApp) {
        try {
            contadorArquivosAnalisados = 0;

            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            for (int cont = 0; cont < ListArquivosAnaliseXML.toArray().length; cont++) {

                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseXML.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (ListArquivosAnaliseXML.toArray()[cont].toString().contains(File.separator + "layout" + File.separator)) {
                        //ACESSAR O ROOT ELEMENT
                        Element rootElmnt = d.getRootElement();

                        //BUSCAR ELEMENTOS FILHOS DA TAG
                        List elements = rootElmnt.getChildren();

                        for (int i = 0; i < elements.size(); i++) {
                            org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                            //System.out.println(el.getName());

                            if (el.getChildren().size() > 0) {
                                recursiveChildrenHideListener(elements);
                            }
                        }

                        System.out.println("---------------------------------------------------------------------------------------");

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell, "HiddenListener.json");

            System.out.println("---------------------------------------------------------------------------------------");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    //Recurso Mágico
    public static long magicResource(String pathApp) {
        try {
            contadorArquivosAnalisados = 0;

            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            for (int cont = 0; cont < ListArquivosAnaliseXML.toArray().length; cont++) {
                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseXML.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (ListArquivosAnaliseXML.toArray()[cont].toString().contains(File.separator + "layout" + File.separator)) {
                        //ACESSAR O ROOT ELEMENT
                        Element rootElmnt = d.getRootElement();

                        //BUSCAR ELEMENTOS FILHOS DA TAG
                        List elements = rootElmnt.getChildren();

                        for (int i = 0; i < elements.size(); i++) {
                            org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                            //System.out.println(el.getName());
                            if (el.getChildren().size() > 0) {
                                recursiveChildrenMagic(elements);
                            }
                        }

                        System.out.println("---------------------------------------------------------------------------------------");

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell, "magicResource.json");

            System.out.println("---------------------------------------------------------------------------------------");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    //Recurso de String Bagunçado
    public static long godStringResource(String pathApp) {
        try {
            contadorArquivosAnalisados = 0;

            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),XML);

            int qtdFilesString = 0;

            for (int cont = 0; cont < (ListArquivosAnaliseXML.toArray().length - 1); cont++) {
                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                try {
                    //System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (d.getRootElement().getChildren().size() > 0) {
                        if (d.getRootElement().getChildren().get(0).getName() == "string") {
                            qtdFilesString = qtdFilesString + 1;
                        }
                    }

                    if ((qtdFilesString == 1)) {
                        //System.out.println("->"+arquivosAnalise.toArray()[cont].toString());
                        System.out.println("Recurso de String Baguncado detectado (existe apenas um arquivo para strings no aplicativo  ");
                        System.out.println("---------------------------------------------------------------------------------------");
                        JsonOut.setTipoSmell("XML");
                        JsonOut.setArquivo(ListArquivosAnaliseXML.toArray()[cont].toString());
                        ListJsonSmell.add(JsonOut);
                        totalSmells++;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell, "godStringResource.json");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    //Reuso inadequado de string
    public static long inappropriateStringReuse(String pathApp) {
        try {
            contadorArquivosAnalisados = 0;

//            arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;
            textStringArquivo.clear();
            //listar(new File(pathApp),XML);

            for (int cont = 0; cont < ListArquivosAnaliseXML.toArray().length; cont++) {

                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseXML.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseXML.toArray()[cont].toString());

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    if (ListArquivosAnaliseXML.toArray()[cont].toString().contains(File.separator + "layout" + File.separator)) {
                        //ACESSAR O ROOT ELEMENT
                        Element rootElmnt = d.getRootElement();

                        //BUSCAR ELEMENTOS FILHOS DA TAG
                        List elements = rootElmnt.getChildren();

                        for (int i = 0; i < elements.size(); i++) {
                            org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                            //System.out.println(el.getName());
                            if (el.getChildren().size() > 0) {
                                recursiveChildrenReusoInadequadoDeString(elements, ListArquivosAnaliseXML.toArray()[cont].toString());
                            }
                        }

                        System.out.println("---------------------------------------------------------------------------------------");

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            for (ReusoStringData linha : textStringArquivo) {
                textStringArquivo.forEach(itemTexto -> {
                    if ((linha.strString.equals(itemTexto.strString)) && (!linha.arquivo.equals(itemTexto.arquivo))) {
                        System.out.println("Reuso inadequado de String detectado " + itemTexto.strString + "(Arquivo " + linha.arquivo + " e " + itemTexto.arquivo + ")");
                        JsonOut.setTipoSmell("XML");
                        JsonOut.setArquivo(linha.arquivo.toString());
                        ListJsonSmell.add(JsonOut);
                        totalSmells++;
                    }
                });
                //System.out.println(linha.strString + " = " + linha.arquivo);
            }

            JsonOut.saveJson(ListJsonSmell, "inappropriateStringReuse.json");

            System.out.println("---------------------------------------------------------------------------------------");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    //Imagem Faltante
    public static long NotFoundImage(String pathApp) {
        contadorArquivosAnalisados = 0;

        FilesIMG.clear();
        totalSmells = 0;
        ListSmells.clear();
        ObtemImagensList(new File(pathApp));

        //Contados de arquivos analisados
        contadorArquivosAnalisados = FilesIMG.size();

        FilesIMG.forEach(caminho -> {

            File directory = new File(caminho);
            for (File arquivo : directory.listFiles()) {

                try {
                    FilesIMG.forEach(item -> {

                        File arquivoImg = new File(item + "" + File.separator + "" + arquivo.getName());

                        if (!arquivoImg.exists()) {
                            System.out.println("Imagem Faltante detectado " + arquivo.getName() + " para pasta " + item);
                            JsonOut.setTipoSmell("XML");
                            JsonOut.setArquivo(arquivoImg.toString());
                            ListJsonSmell.add(JsonOut);
                            totalSmells++;
                            //System.out.println(arquivoImg.length());
                        } else if ((arquivo.length() != arquivoImg.length())) {
                            System.out.println("Imagem Faltante detectado (Imagem existe porem a resolução é incompatível) " + arquivo.getName() + " para pasta " + item);
                            JsonOut.setTipoSmell("XML");
                            JsonOut.setArquivo(arquivoImg.toString());
                            ListJsonSmell.add(JsonOut);
                            totalSmells++;
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JsonOut.saveJson(ListJsonSmell, "NotFoundImage.json");
        return AndroidLayoutSmells.totalSmells;
    }

    //region [Recursive Functions]

    private static void recursiveChildrenMagic(List elements) {
        for (int i = 0; i < elements.size(); i++) {
            try {

                org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                List SubElements = el.getChildren();

                //System.out.println(el.getName());

                if (SubElements.size() > 0) {
                    recursiveChildrenMagic(SubElements);
                } else {
                    List<org.jdom2.Attribute> listAttr = (List<org.jdom2.Attribute>) el.getAttributes();
                    for (org.jdom2.Attribute item : listAttr) {
                        //System.out.println(item);
                        if (item.getName() == "text") {
                            if (!item.getValue().matches("@.*/.*")) {
                                System.out.println("Recurso Mágico " + el.getName() + " - text:" + item.getValue());
                                JsonOut.setTipoSmell("XML");
                                JsonOut.setArquivo("");
                                ListJsonSmell.add(JsonOut);
                                totalSmells++;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void recursiveChildrenHideListener(List elements) {
        for (int i = 0; i < elements.size(); i++) {
            try {

                org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                List SubElements = el.getChildren();

                //System.out.println(el.getName());

                if (SubElements.size() > 0) {
                    recursiveChildrenHideListener(SubElements);
                } else {
                    List<org.jdom2.Attribute> listAttr = (List<org.jdom2.Attribute>) el.getAttributes();
                    for (org.jdom2.Attribute item : listAttr) {
                        if (item.getName() == "onClick") {
                            System.out.println("Listener Escondido " + el.getName() + " - Onclick:" + item.getValue());
                            JsonOut.setTipoSmell("XML");
                            JsonOut.setArquivo("");
                            ListJsonSmell.add(JsonOut);
                            totalSmells++;
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
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
                    if (qtdSubelementos > threshold) {
                        System.out.println("Layout Profundamente Aninhado encontrado " + el.getName() + "(Mais de " + threshold + " níveis)");
                        JsonOut.setTipoSmell("XML");
                        JsonOut.setArquivo("");
                        ListJsonSmell.add(JsonOut);
                        totalSmells++;
                        break;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void ObtemImagensList(File directory) {
        if (directory.isDirectory()) {
            if (directory.getPath().contains("mipmap")) {
                //System.out.println(directory.getPath());
                FilesIMG.add(directory.getPath());
            }

            String[] subDirectory = directory.list();
            if (subDirectory != null) {
                for (String dir : subDirectory) {
                    ObtemImagensList(new File(directory + File.separator + dir));
                }
            }
        }
    }

    private static void recursiveChildrenReusoInadequadoDeString(List elements, String arquivo) {
        for (int i = 0; i < elements.size(); i++) {
            try {
                org.jdom2.Element el = (org.jdom2.Element) elements.get(i);
                List SubElements = el.getChildren();

                //System.out.println(el.getName());

                if (SubElements.size() > 0) {
                    recursiveChildrenReusoInadequadoDeString(SubElements, arquivo);
                } else {
                    List<org.jdom2.Attribute> listAttr = (List<org.jdom2.Attribute>) el.getAttributes();
                    for (org.jdom2.Attribute item : listAttr) {
                        //System.out.println(item);
                        if (item.getName() == "text") {
                            if (item.getValue().matches("@.*/.*")) {
                                //System.out.println("Recurso Mágico " + el.getName() + " - text:" + item.getValue());
                                //System.out.println(item.getValue());
                                ReusoStringData data = new ReusoStringData();
                                data.arquivo = arquivo;
                                data.strString = item.getValue();
                                textStringArquivo.add(data);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    //endregion
}
