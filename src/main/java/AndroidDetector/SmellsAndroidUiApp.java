package AndroidDetector;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import java.io.File;
import java.util.List;
import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import java.util.ArrayList;


public class SmellsAndroidUiApp {
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
    public static void NotFragments(){

    }

    //Imagem Faltante
    public static void NotFoundImage(){

    }

    //Longo recurso de Estilo
    public static void LongStyleResource(){

    }

    //Recurso de String Bagunçado
    public static void BadStringResource(){

    }

    //Atributo de Estilo repetidos
    public static void frequentStyleResource(){

    }

    //Reuso inadequado de String
    public static void InapropriateString(){

    }

    //Listener Escondido
    public static void HideListener(String pathApp){
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
                    subItensListener(SubElements);

                    List<org.jdom2.Attribute> listAttr = (List<org.jdom2.Attribute>) el.getAttributes();

                    for (org.jdom2.Attribute item : listAttr) {

                        if(item.getName() == "onClick"){
                            System.out.println("Listener Escondido " + el.getName() + " - Onclick:" + item.getValue());
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
                            if(!item.getValue().matches("@[a-z]+\\b/")){
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

}
