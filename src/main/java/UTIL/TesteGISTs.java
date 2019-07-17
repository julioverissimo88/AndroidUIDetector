package UTIL;

import AndroidDetector.IOClass;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;

import java.io.File;

public class TesteGISTs {

    private static long totalSmells;

    public static void main(String[] args) {
        try {


            File file = new File("/Users/rafaeldurelli/Desktop/Gist/code3.java");


               BrainUIComponent(file.toString());

            }

        catch(Exception ex){
            ex.printStackTrace();
        }
    }



    public static void BrainUIComponent(String pathApp) {
        try {
            try {


                File f = new File(pathApp);
                CompilationUnit cUnit = JavaParser.parse(f);

                cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {
                    if (classe.getExtendedTypes().get(0).toString().contains("Activity") || classe.getExtendedTypes().get(0).toString().contains("Fragment") || classe.getExtendedTypes().get(0).toString().contains("Adapter")) {
                        //ifElseSwitchCase (Regra de negócios)
                        NodeList<BodyDeclaration<?>> membros = classe.getMembers();
                        for (BodyDeclaration<?> membro : membros) {
                            //Verifica se o membro é um método
                            if (membro.isMethodDeclaration()) {

                                membro.findAll(IfStmt.class).forEach(item -> {
                                    System.out.println("Brain UI Component detectado na classe " + item.getRange().get().begin + " (lógica utilizando if detectada)");
                                    System.out.println("---------------------------------------------------------------------------------------");
                                    totalSmells++;
                                });

                                membro.findAll(SwitchEntryStmt.class).forEach(item -> {
                                    System.out.println("Brain UI Component detectado na classe " + item.getRange().get().begin + " (lógica utilizando Switch/Case detectada)");
                                    System.out.println("---------------------------------------------------------------------------------------");

                                    totalSmells++;
                                });

                                membro.findAll(Statement.class).forEach(item ->{
//                                    System.out.println(item);

                                    ///arrumar aqui..
                                });

                            }
                        }


                        //Static Fields
                        classe.getFields().forEach(item -> {
                            if (item.isFieldDeclaration() && item.isStatic()) {
                                System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Campo Static) " + item.getRange().get().begin);
                                System.out.println("---------------------------------------------------------------------------------------");

                                totalSmells++;
                            }
                        });

                        //Procura Libs IO no TIPO  em declaração de campos
                        classe.getFields().forEach(campos -> {
                            if (IOClass.getIOClass().contains(campos.getElementType().toString())) {
                                System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) " + campos.getRange().get().begin);
                                System.out.println("---------------------------------------------------------------------------------------");

                                totalSmells++;
                            }
                        });

                        //Procura Libs de IO no TIPO em declaração  de Métodos
                        classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                            IOClass.getIOClass().forEach(item -> {
                                //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                if (metodo.getParameters().contains(item)) {
                                    System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) nos parâmetros do método " + metodo.getRange().get().begin);
                                    System.out.println("---------------------------------------------------------------------------------------");
                                    totalSmells++;
                                }

                                //Procura Libs de IO no TIPO em retorno  de Métodos
                                if (metodo.getType().toString().contains(item)) {
                                    System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) no retorno do método " + metodo.getRange().get().begin);
                                    System.out.println("---------------------------------------------------------------------------------------");

                                    totalSmells++;
                                }

                                //Procura Libs IO no TIPO  em declaração de campos
                                metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                    if (campos.getElementType().toString().contains(item)) {
                                        System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) " + campos.getRange().get().begin);
                                        System.out.println("---------------------------------------------------------------------------------------");

                                        totalSmells++;
                                    }
                                });

                            });
                        });
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }catch (Exception ex){
        }
    }
}





