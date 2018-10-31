package AndroidDetector;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import java.util.ArrayList;


public class SmellsAndroidUiApp {

    private static long qtdSubelementos = 0;

    //Componente de UI Fazendo IO
    public static void CompUIIO(String pathApp){
        try {
            File arquivos[];
            File diretorio = new File(pathApp);
            arquivos = diretorio.listFiles();

            for(int cont = 0; cont < arquivos.length; cont++) {
                CompilationUnit cu = JavaParser.parse(new File(arquivos[cont].toString()));
                NodeList<TypeDeclaration<?>> coment = cu.getTypes();

                ClassOrInterfaceDeclaration n = new ClassOrInterfaceDeclaration();

                System.out.println(cu.getClass().getName());
                cu.findAll(LocalClassDeclarationStmt.class);

                System.out.println("Classe analisada: ");

                List<String> listaPacotesBd = new ArrayList<String>();
                listaPacotesBd.add("import java.sql.Connection;");
                listaPacotesBd.add("import java.sql.DriverManager;");
                listaPacotesBd.add("import java.sql.SQLException;");

                System.out.println("---------------------------------------------------------------------------------------");

                // prints the changed compilation unit
                NodeList<ImportDeclaration> imports = cu.getImports();
                for (int i = 0; i < imports.size(); i++) {
                    if (listaPacotesBd.contains(imports.get(i).toString().trim())) {
                        System.out.println("Componente de UI fazendo uso de IO");
                    }

                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    //Não Uso de Fragments
    public static void NotFragments(String pathApp){
        boolean isfragment = false;
        try{
            File arquivos[];
            File diretorio = new File(pathApp);
            arquivos = diretorio.listFiles();

            for(int cont = 0; cont < arquivos.length; cont++){
                System.out.println("Arquivo analisado:" +arquivos[cont]);
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(arquivos[cont].toString());

                //SAX BUILDER PARA PROCESSAR O XML
                SAXBuilder sb = new SAXBuilder();

                //LER TODA A ESTRUTURA DO XML
                Document d = sb.build(f);

                //ACESSAR O ROOT ELEMENT
                Element rootElmnt = d.getRootElement();

                //BUSCAR ELEMENTOS FILHOS DA TAG
                List elements = rootElmnt.getChildren();

                for(int i =0; i < elements.size(); i++){
                    org.jdom2.Element el = (org.jdom2.Element)elements.get(i);

                    if(el.getName() == "fragment"){
                        isfragment = true;
                    }
                }
                if(!isfragment){
                    System.out.println("Não uso de Fragments detectado  " + arquivos[cont].toString());
                }
                System.out.println("---------------------------------------------------------------------------------------");
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }

    //Imagem Faltante
    public static void NotFoundImage(String pathApp){
        try{
            File arquivos[];
            File diretorio = new File(pathApp);
            arquivos = diretorio.listFiles();
            int numStyles = 0;
            List<File[]> lstArquivos = new ArrayList<File[]>();

            for(int cont = 0; cont < arquivos.length; cont++){
                if(arquivos[cont].toString().contains("mipmap")){
                    File arquivosSubDir[];
                    File subDiretorio = new File(pathApp+arquivos[cont].toString());
                    arquivosSubDir = subDiretorio.listFiles();
                    lstArquivos.add(arquivosSubDir);
                }
            }

            Boolean encontrado = true;
            String imgfaltante = null;

            for(int i=0; i < lstArquivos.size(); i++){
                File files[] = lstArquivos.get(i);
                for(int j=0; j < files.length; j++){
                    File files2[] = lstArquivos.get(j);
                    for(int k=0; k < files2.length; k++){
                        if (files[j] != files2[k]) {
                            encontrado = false;
                            imgfaltante = files[j].toString();
                            System.out.println("Imagem faltante detectado (imagem deve ser disponibilizada em todas resoluções)" + imgfaltante);
                            System.out.println("---------------------------------------------------------------------------------------");
                        }
                    }
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    //Longo recurso de Estilo
    public static void LongStyleResource(String pathApp){
        try{

            File arquivos[];
            File diretorio = new File(pathApp);
            arquivos = diretorio.listFiles();
            int numStyles = 0;

            for(int cont = 0; cont < arquivos.length; cont++){
                if(arquivos[cont].toString().contains("styles")){
                    numStyles = numStyles +1;
                }
            }
            if(numStyles <= 1){
                System.out.println("Longo recurso de Estilo detectado (existe apenas um arquivo para estilos no aplicativo)");
                System.out.println("---------------------------------------------------------------------------------------");
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    //Recurso de String Bagunçado
    public static void BadStringResource(String pathApp){
        try{

            File arquivos[];
            File diretorio = new File(pathApp);
            arquivos = diretorio.listFiles();
            int numFileStrings = 0;

            for(int cont = 0; cont < arquivos.length; cont++){
                if(arquivos[cont].toString().contains("styles")){
                    numFileStrings = numFileStrings +1;
                }
            }
            if(numFileStrings <= 1){
                System.out.println("Recurso de String Bagunçado detectado (existe apenas um arquivo para strings no aplicativo) ");
                System.out.println("---------------------------------------------------------------------------------------");
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    //Atributo de Estilo repetidos
    public static void frequentStyleResource(){

    }

    //Reuso inadequado de String
    public static void InapropriateString(){

    }

    //Layout Profundamente Aninhado
    public static void layoutProfundamenteAninhado(String pathApp){
        try{
            File arquivos[];
            File diretorio = new File(pathApp);
            arquivos = diretorio.listFiles();

            for(int cont = 0; cont < arquivos.length; cont++){
                System.out.println("Arquivo analisado:" +arquivos[cont]);
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(arquivos[cont].toString());

                //SAX BUILDER PARA PROCESSAR O XML
                SAXBuilder sb = new SAXBuilder();

                //LER TODA A ESTRUTURA DO XML
                Document d = sb.build(f);

                //ACESSAR O ROOT ELEMENT
                Element rootElmnt = d.getRootElement();

                //BUSCAR ELEMENTOS FILHOS DA TAG
                List elements = rootElmnt.getChildren();

                recursiveChildrenElement(elements);

                System.out.println("---------------------------------------------------------------------------------------");
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    //Listener Escondido
    public static void HideListener(String pathApp){
        try{
            File arquivos[];
            File diretorio = new File(pathApp);
            arquivos = diretorio.listFiles();

            for(int cont = 0; cont < arquivos.length; cont++){
                if(arquivos[cont].toString().endsWith(".xml")) {
                    System.out.println("Arquivo analisado:" + arquivos[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(arquivos[cont].toString());

                    //SAX BUILDER PARA PROCESSAR O XML
                    SAXBuilder sb = new SAXBuilder();

                    //LER TODA A ESTRUTURA DO XML
                    Document d = sb.build(f);

                    //ACESSAR O ROOT ELEMENT
                    Element rootElmnt = d.getRootElement();

                    //BUSCAR ELEMENTOS FILHOS DA TAG
                    List elements = rootElmnt.getChildren();

                    for (int i = 0; i < elements.size(); i++) {
                        org.jdom2.Element el = (org.jdom2.Element) elements.get(i);

                        List SubElements = el.getChildren();
                        subItensListener(SubElements);

                        List<org.jdom2.Attribute> listAttr = (List<org.jdom2.Attribute>) el.getAttributes();

                        for (org.jdom2.Attribute item : listAttr) {
                            System.out.println(item.getName());
                            if (item.getName() == "onClick") {
                                System.out.println("Listener Escondido " + el.getName() + " - Onclick:" + item.getValue());
                            }

                        }
                    }

                    System.out.println("---------------------------------------------------------------------------------------");
                }
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }

    }

    //Recurso Mágico
    public static void magicResource(String pathApp){
        try{

            File arquivos[];
            File diretorio = new File(pathApp);
            arquivos = diretorio.listFiles();

            for(int cont = 0; cont < arquivos.length; cont++){
                System.out.println("Arquivo analisado:" +arquivos[cont]);
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(arquivos[cont].toString());

                //SAX BUILDER PARA PROCESSAR O XML
                SAXBuilder sb = new SAXBuilder();

                //LER TODA A ESTRUTURA DO XML
                Document d = sb.build(f);

                //ACESSAR O ROOT ELEMENT
                Element rootElmnt = d.getRootElement();

                //BUSCAR ELEMENTOS FILHOS DA TAG
                List elements = rootElmnt.getChildren();

                for(int i =0; i < elements.size(); i++){
                    org.jdom2.Element el = (org.jdom2.Element)elements.get(i);

                    List SubElements = el.getChildren();
                    subItens(SubElements);

                    List<org.jdom2.Attribute> listAttr = (List<org.jdom2.Attribute>) el.getAttributes();

                    for (org.jdom2.Attribute item : listAttr) {
                        if(item.getName() =="text"){
                            if(!item.getValue().matches("@.*/.*")){
                                System.out.println("Recurso Mágico " + el.getName() + " - text:" + item.getValue());
                            }
                        }

                    }
                }

                System.out.println("---------------------------------------------------------------------------------------");
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private static void subItens(List SubElements){
        for(int i =0; i < SubElements.size(); i++) {
            org.jdom2.Element el = (org.jdom2.Element) SubElements.get(i);
            List<org.jdom2.Attribute> listAttr = (List<org.jdom2.Attribute>) el.getAttributes();

            for (org.jdom2.Attribute item : listAttr) {
                if(item.getName() =="text"){
                    if(!item.getValue().matches("@[a-z]+\\b/")){
                        System.out.println("Recurso Mágico " + el.getName() + " - text:" + item.getValue());
                    }
                }

            }
        }
    }

    private static void subItensListener(List SubElements){
        for(int i =0; i < SubElements.size(); i++) {
            org.jdom2.Element el = (org.jdom2.Element) SubElements.get(i);
            List<org.jdom2.Attribute> listAttr = (List<org.jdom2.Attribute>) el.getAttributes();

            for (org.jdom2.Attribute item : listAttr) {

                if (item.getName() == "onClick") {
                    if(item.getName() =="onClick"){
                        System.out.println("Listener Escondido "  + el.getName() + " - Onclick:" + item.getValue());
                    }
                }

            }
        }
    }

    private static void recursiveChildrenElement(List elements){
        for(int i = 0; i < elements.size(); i++){

            org.jdom2.Element el = (org.jdom2.Element)elements.get(i);
            List SubElements = el.getChildren();

            if(SubElements.size() > 0){
                qtdSubelementos = qtdSubelementos +1;
                recursiveChildrenElement(SubElements);
            }
            else{
                if(qtdSubelementos > 3){
                    System.out.println("Layout Profundamente Aninhado encontrado " + el.getName());
                    break;
                }
            }
        }

    }

}
