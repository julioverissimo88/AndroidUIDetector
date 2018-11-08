import AndroidDetector.ImportantSmells;
import AndroidDetector.OutputSmells;
import AndroidDetector.SmellsAndroidUiApp;
import UTIL.Trashold;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;



public class Test {

    public static final String JAVA = ".java";
    public static final String XML = ".xml";
    public static List<File> arquivosAnalise =  new ArrayList<File>();
    public static  Boolean classeValida = true;
    private  static List<OutputSmells> ListSmells = new ArrayList<OutputSmells>();
    private  static List<String> FilesIMG = new ArrayList<String>();


    public static void listar(File directory,String tipo) {
        if(directory.isDirectory()) {
            //System.out.println(directory.getPath());

            String[] myFiles = directory.list(new FilenameFilter() {
                public boolean accept(File directory, String fileName) {
                    return fileName.endsWith(tipo);
                }
            });

            for(int i = 0; i < myFiles.length; i++){
                arquivosAnalise.add(new File(directory.getPath() + "\\" + myFiles[i].toString()));
            }

            String[] subDirectory = directory.list();
            if(subDirectory != null) {
                for(String dir : subDirectory){
                    listar(new File(directory + File.separator  + dir),tipo);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            //Brain Component
            //ImportantSmells.BrainUIComponent("C:\\Users\\julio\\Desktop\\codigos\\java\\Brain");

            //Coupled UI Component
            //ImportantSmells.CoupledUIComponent("C:\\Users\\julio\\Desktop\\codigos\\java\\CoupledUI");

            //Suspicious Behavior
            //ImportantSmells.SuspiciousBehavior("C:\\Users\\julio\\Desktop\\codigos\\java\\SuspiciousBehavior");

            //Flex Adapter
            //ImportantSmells.FlexAdapter("C:\\Users\\julio\\Desktop\\codigos\\java\\FlexAdapter");

            //layout profundamente aninhado
            //ImportantSmells.DeepNestedLayout("C:\\Users\\julio\\Desktop\\codigos\\layout\\layoutProfundamenteAninhado",3);

            //God Style Resource
            //ImportantSmells.GodStyleResource("C:\\Users\\julio\\AndroidStudioProjects\\AppTestAndroidSmells");

            //atributos de estilo repetidos
            //ImportantSmells.DuplicateStyleAttributes("C:\\Users\\julio\\Desktop\\codigos\\layout\\atributosEstiloRepetidos\\");

            //SmellsAndroidUiApp.HideListener("C:\\Users\\julio\\AndroidStudioProjects\\AppTestAndroidSmells\\app\\src\\main\\res\\layout");

            //ImportantSmells.magicResource("C:\\Users\\julio\\AndroidStudioProjects\\AppTestAndroidSmells\\app\\src\\main\\res\\layout");

            //ImportantSmells.NotFoundImage("C:\\\\Users\\\\julio\\\\Desktop\\\\codigos\\\\Calculator\\\\mobile\\\\");

            //ImportantSmells.reusoInadequadoDeString("C:\\\\Users\\\\julio\\\\Desktop\\\\codigos\\\\layout\\\\");

            //Trashold.DeepNestedLayout("C:\\\\Users\\\\julio\\\\Desktop\\\\codigos\\\\layout\\\\");

             /*
            listar2(new File("C:\\Users\\julio\\Desktop\\codigos\\Calculator\\mobile\\"));

            FilesIMG.forEach(caminho->{
                File directory = new File(caminho);
                for(File arquivo : directory.listFiles()){
                    FilesIMG.forEach(item->{

                            File arquivoImg = new File(item + "\\" + arquivo.getName());

                            if (!arquivoImg.exists()) {
                                System.out.println("Imagem Faltante detectado " + arquivo.getName() + " para pasta " + item);
                                //System.out.println(arquivoImg.length());
                            }
                            else if((arquivo.length() != arquivoImg.length())){
                                System.out.println("Imagem Faltante detectado (Imagem existe porem a resolução é incompatível) " + arquivo.getName() + " para pasta " + item);
                            }

                    });
                    //System.out.println(arquivo.getName());
                }
            });
            */


        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void listar2(File directory) {
        if(directory.isDirectory()) {
            if(directory.getPath().contains("mipmap")){
                //System.out.println(directory.getPath());
                FilesIMG.add(directory.getPath());
            }

            String[] subDirectory = directory.list();
            if(subDirectory != null) {
                for(String dir : subDirectory){
                    listar2(new File(directory + File.separator  + dir));
                }
            }
        }
    }

    public static void NotFoundImage(String pathApp){
        listar2(new File("C:\\Users\\julio\\Desktop\\codigos\\Calculator"));
    }



    public static void CoupledUIComponent(String pathApp) throws FileNotFoundException {
        ListSmells.clear();
        arquivosAnalise.clear();
        listar(new File(pathApp),JAVA);

        for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
            classeValida = true;
            String nomeArquivo = arquivosAnalise.toArray()[cont].toString();
            System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
            System.out.println("---------------------------------------------------------------------------------------");

            File f = new File(arquivosAnalise.toArray()[cont].toString());
            CompilationUnit cu = JavaParser.parse(f);

            ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<ClassOrInterfaceDeclaration>();
            NodeList<TypeDeclaration<?>> types = cu.getTypes();
            for (int i = 0; i < types.size(); i++) {
                classes.add((ClassOrInterfaceDeclaration) types.get(i));
            }

            for (ClassOrInterfaceDeclaration classe : classes) {
                NodeList<ClassOrInterfaceType> implementacoes = classe.getExtendedTypes();
                if(implementacoes.size() != 0){
                    for (ClassOrInterfaceType implementacao : implementacoes) {
                        if (implementacao.getName().getIdentifier().contains("Activity") || implementacao.getName().getIdentifier().contains("Adapter")) {
                            classeValida  = true;
                        }
                    }
                }
                else{
                    classeValida  = false;
                }
            }

            //Se não for válida activity entre outros pula o laço para o próximo arquivo
            if(!classeValida){
                continue;
            }

            for (TypeDeclaration<?> typeDec : cu.getTypes()) {
                //System.out.println(typeDec.getName().toString());
                for (BodyDeclaration<?> member : typeDec.getMembers()) {
                    if(member.isMethodDeclaration()) {
                        MethodDeclaration field = (MethodDeclaration) member;

                        field.findAll(MethodDeclaration.class).forEach(item-> {
                            //System.out.println(item);
                            item.getChildNodes().forEach(sub ->{
                                sub.findAll(MethodDeclaration.class).forEach(i->{
                                    System.out.println("Comportamento suspeito detectado  - " + i.getName() + " - " + i.getRange().get().begin);
                                });
                            });
                        });

                        if(field.getNameAsString().equals("onCreateView") || field.getNameAsString().equals("onActivityCreated")){
                            //System.out.println("Coupled UI Component  - " + field.getRange());
                        }
                    }
                }
            }
        }
    }
}
