import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public static final String JAVA = ".java";
    public static final String XML = ".xml";
    public static List<File> arquivosAnalise =  new ArrayList<File>();
    public static  Boolean classeValida = true;
    public static void listar(File directory) {

        if(directory.isDirectory()) {
            //System.out.println(directory.getPath());

            String[] myFiles = directory.list(new FilenameFilter() {
                public boolean accept(File directory, String fileName) {
                    return fileName.endsWith(JAVA);
                }
            });

            for(int i = 0; i < myFiles.length; i++){
                arquivosAnalise.add(new File(directory.getPath() + "\\" + myFiles[i].toString()));
            }

            String[] subDirectory = directory.list();
            if(subDirectory != null) {
                for(String dir : subDirectory){
                    listar(new File(directory + File.separator  + dir));
                }
            }
        }
    }

    public static void main(String[] args) {
        try {

            SuspiciousBehavior("C:\\Users\\julio\\AndroidStudioProjects\\");
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static void SuspiciousBehavior(String pathApp) throws FileNotFoundException {
        arquivosAnalise.clear();
        listar(new File(pathApp));

        for (int cont = 0; cont < arquivosAnalise.toArray().length; cont++) {
            classeValida = true;
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
                        if (implementacao.getName().getIdentifier().equals("BaseActivity") || implementacao.getName().getIdentifier().equals("Activity") || implementacao.getName().getIdentifier().equals("Fragments") || implementacao.getName().getIdentifier().equals("BaseAdapter") || implementacao.getName().getIdentifier().endsWith("Listener")) {
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


/*

            cu.getTypes().forEach(item -> {
                if (item.getName().getIdentifier().equals("BaseActivity") || item.getName().getIdentifier().equals("Activity") || item.getName().getIdentifier().equals("Fragments") || item.getName().getIdentifier().equals("BaseAdapter") || item.getName().getIdentifier().endsWith("Listener")) {
                    classeValida  = true;
                }
            });

            if(!classeValida){
                continue;
            }
            */

            for (TypeDeclaration<?> typeDec : cu.getTypes()) {

                for (BodyDeclaration<?> member : typeDec.getMembers()) {
                    member.toFieldDeclaration().ifPresent(field -> {
                        for (VariableDeclarator variable : field.getVariables()) {
                            //Print the field's class typr
                            //System.out.println(variable.getType());

                            System.out.println("Comportamento suspeito detectado  - " + variable.getType()+ " - " + variable.getRange().get().begin);
                            //Print the field's name
                            //System.out.println(variable.getName());
                            //Print the field's init value, if not null

                            variable.getInitializer().ifPresent(initValue -> {
                                if(initValue.isLambdaExpr()){
                                    System.out.println("Comportamento suspeito detectado  - " + initValue.getRange());
                                }
                            });
                        }
                    });
                }
            }
        }
    }
}
