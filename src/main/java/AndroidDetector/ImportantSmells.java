package AndroidDetector;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.util.List;

public class ImportantSmells {
    private static long qtdSubelementos = 0;
    private static File arquivos[];
    private static File diretorio = null;
    private static SAXBuilder sb = new SAXBuilder();

    public static void CoupledUIComponent(String pathApp){

    }

    public static void SuspiciousBehavior(String pathApp){

    }

    public static void BrainUIComponent(String pathApp){

    }

    public static void FlexAdapter(String pathApp){

    }

    public static void GodStyleResource(String pathApp){
        try{

            diretorio = new File(pathApp);
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

    public static void DeepNestedLayout(String pathApp){
        try{
            diretorio = new File(pathApp);
            arquivos = diretorio.listFiles();

            for(int cont = 0; cont < arquivos.length; cont++){
                System.out.println("Arquivo analisado:" +arquivos[cont]);
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(arquivos[cont].toString());

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

    public static void DuplicateStyleAttributes(String pathApp){
        try{
            diretorio = new File(pathApp);
            arquivos = diretorio.listFiles();

            for(int cont = 0; cont < arquivos.length; cont++){
                System.out.println("Arquivo analisado:" +arquivos[cont]);
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(arquivos[cont].toString());
                Document d = sb.build(f);
                Element rootElmnt = d.getRootElement();
                List elements = rootElmnt.getChildren();

                for(int i =0; i < elements.size(); i++){
                    org.jdom2.Element el = (org.jdom2.Element)elements.get(i);

                    List SubElements = el.getChildren();

                    for(int j =0; j < SubElements.size(); j++) {
                        org.jdom2.Element elChildren = (org.jdom2.Element) SubElements.get(j);
                        List<org.jdom2.Attribute> listAttr = (List<org.jdom2.Attribute>) elChildren.getAttributes();

                        Boolean style = false;
                        for (org.jdom2.Attribute item : listAttr) {
                            if(item.getName() =="style"){
                                style = true;
                            }
                        }
                        if(!style){
                            System.out.println("Duplicate Style Attributes " + elChildren.getName() + " - Considere colocar a formatação das propriedades em um recurso de estilo:" );
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
