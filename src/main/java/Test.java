import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Test {
    private void verificaDuplicateAttribute(String linha){

    }

    public static void main(String[] args) {
        try {
            /*
            //System.out.println("Arquivo analisado:" + arquivos[cont]);
            System.out.println("---------------------------------------------------------------------------------------");

            File f = new File("C:\\Users\\julio\\AndroidStudioProjects\\AppTestAndroidSmells\\app\\src\\main\\java\\br\\com\\julioverissimo\\apptestandroidsmells\\MainActivity.java");
            CompilationUnit compilationunit = JavaParser.parse(f);
            ClassOrInterfaceDeclaration n = new ClassOrInterfaceDeclaration();

            ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<ClassOrInterfaceDeclaration>();
            NodeList<TypeDeclaration<?>> types = compilationunit.getTypes();
            for (int i = 0; i < types.size(); i++) {
                classes.add((ClassOrInterfaceDeclaration) types.get(i));
            }

            for (ClassOrInterfaceDeclaration classe : classes) {
                Boolean isComponentUiBrain = false;
                //Testa se é adapter, activity, Fragment
                NodeList<ClassOrInterfaceType> implementacoes = classe.getExtendedTypes();
                for (ClassOrInterfaceType implementacao : implementacoes) {
                    if (implementacao.getName().getIdentifier().equals("Activity") || implementacao.getName().getIdentifier().equals("Fragments") || implementacao.getName().getIdentifier().equals("BaseAdapter") || implementacao.getName().getIdentifier().endsWith("Listener")) {
                        NodeList<BodyDeclaration<?>> itens = classe.getMembers();

                        //Testa se existe atributo do tipo FINAL
                        for(BodyDeclaration<?> atributos : itens){
                            System.out.println(atributos.toString());
                            if(atributos.isFieldDeclaration()) {
                                if (atributos.toString().contains("final")) {
                                    System.out.println("Componente de UI Cérebro Encontrado - " + atributos.getRange());
                                }
                            }
                        }

                        //Testa se Existem étodos que não sejam override
                        for(BodyDeclaration<?> met : itens){
                            if(met.isMethodDeclaration()) {
                                NodeList<AnnotationExpr> annotations = met.getAnnotations();

                                for (AnnotationExpr annotation : annotations) {
                                    //Se tiver annotacoes que ão seja overide considera que possui implementação indevida logo uma smells
                                    if (!annotation.getName().getIdentifier().equals("Override")) {
                                        System.out.println("Componente de UI Cérebro Encontrado - " + annotation.getRange());
                                    }
                                }
                            }
                        }

                        //Testa se existe Conversão de dados


                        //Testa se existe Operações de IO




                    }
                }
            }
            */

            long qtdSubelementos = 0;
            File arquivos[];
            File diretorio = null;
            SAXBuilder sb = new SAXBuilder();
            int qtdLimiteStilos = 5;

            diretorio = new File("C:\\Users\\julio\\AndroidStudioProjects\\AppTestAndroidSmells\\app\\src\\main\\java\\br\\com\\julioverissimo\\apptestandroidsmells\\");
            arquivos = diretorio.listFiles();
            boolean isGodStyle = false;
            int qtdFilesStyle = 0;


            for (int cont = 0; cont < arquivos.length; cont++) {
                System.out.println("Arquivo analisado:" + arquivos[cont]);
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(arquivos[cont].toString());
                CompilationUnit cu = JavaParser.parse(f);

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
        catch(Exception ex){
            ex.printStackTrace();
        }


    }
}
