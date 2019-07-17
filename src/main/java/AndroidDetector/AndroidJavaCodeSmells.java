package AndroidDetector;

import UTIL.ReusoStringData;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import metric.WMC;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AndroidJavaCodeSmells {
    //region [Var Declaration]
    public static int contadorArquivosAnalisados = 0;
    public static Boolean classeValida = true;
    public static List<File> ListArquivosAnaliseJava = new ArrayList<File>();
    public static final String JAVA = ".java";
    public static final String XML = ".xml";
    private static OutputSmells JsonOut = new OutputSmells();
    private static List<OutputSmells> ListJsonSmell = new ArrayList<OutputSmells>();
    private static List<OutputSmells> ListSmells = new ArrayList<OutputSmells>();
    private static long totalSmells = 0;
    private static int contadorFieldStatic = 0;
    //endregion

    //Componente de UI Acoplado
    public static long CoupledUIComponent(String pathApp) throws FileNotFoundException {
        contadorArquivosAnalisados = 0;
        ListSmells.clear();
        //arquivosAnalise.clear();
        totalSmells = 0;
        //listar(new File(pathApp),JAVA);

        for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
            try {
                classeValida = true;
                String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                System.out.println(" Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);

                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
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
                            if (implementacao.getName().getIdentifier().contains("Fragment") || implementacao.getName().getIdentifier().contains("Adapter") || implementacao.getName().getIdentifier().contains("Activity")) {

                                //Contados de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                                classe.getFields().forEach(item -> {
                                    //System.out.println(item.getElementType().toString());
                                    if ((item.getElementType().toString().contains("Activity") || item.getElementType().toString().contains("Fragment"))) {
                                        System.out.println("Componente de UI Acoplado " + item.getElementType().toString() + item.getRange());
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;

                                    }
                                });

                                classe.findAll(ConstructorDeclaration.class).forEach(metodo -> {
                                    metodo.getParameters().forEach(item -> {
                                        if (item.getType().toString().contains("Activity") || item.getType().toString().contains("Fragment")) {
                                            System.out.println("Componente de UI Acoplado  " + classe.getName() + " " + metodo.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }
                                    });
                                });

                                classe.findAll(MethodDeclaration.class).forEach(metodo -> {

                                    //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                    if (metodo.getParameters().contains("Activity") || metodo.getParameters().contains("Fragment")) {
                                        System.out.println("Componente de UI Acoplado  " + classe.getName() + " " + metodo.getRange().get().begin);
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }

                                    //Procura Libs de IO no TIPO em retorno  de Métodos
                                    if (metodo.getType().toString().contains("Activity") || metodo.getType().toString().contains("Fragment")) {
                                        System.out.println("Componente de UI Acoplado  " + classe.getName() + " " + metodo.getRange().get().begin);
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }

                                    //Procura Libs IO no TIPO  em declaração de campos
                                    metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                        if (campos.getElementType().toString().contains("Activity") || campos.getElementType().toString().contains("Fragment")) {
                                            System.out.println("Componente de UI Acoplado  " + classe.getName() + ") " + campos.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(campos.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }
                                    });

                                });

                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        JsonOut.saveJson(ListJsonSmell, "CoupledUIComponent.json");
        return totalSmells;
    }

    //Comportamento suspeito
    public static long SuspiciousBehavior(String pathApp) throws FileNotFoundException {
        ListSmells.clear();
        //arquivosAnalise.clear();
        totalSmells = 0;
        //listar(new File(pathApp),JAVA);

        for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
            try {
                classeValida = true;
                String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
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
                            if (implementacao.getName().getIdentifier().contains("BaseActivity") || implementacao.getName().getIdentifier().contains("Activity") || implementacao.getName().getIdentifier().contains("Fragments") || implementacao.getName().getIdentifier().contains("BaseAdapter") || implementacao.getName().getIdentifier().endsWith("Listener")) {
                                classeValida = true;
                                //Contador de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;
                                classe.getImplementedTypes().forEach(item -> {
                                    //System.out.println(item.getNameAsString());
                                    if (item.getName().toString().contains("Listener")) {
                                        System.out.println("Comportamento suspeito detectado  - " + item.getRange());
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(item.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }

                                });
                            }
                        }
                    } else {
                        classeValida = false;
                    }
                }

                //Se não for válida activity entre outros pula o laço para o próximo arquivo
                if (!classeValida) {
                    continue;
                }

                for (TypeDeclaration<?> typeDec : cu.getTypes()) {
                    //System.out.println(typeDec.getName().toString());
                    for (BodyDeclaration<?> member : typeDec.getMembers()) {

                        member.findAll(MethodDeclaration.class).forEach(item -> {
                            //System.out.println(item);
                            item.getChildNodes().forEach(sub -> {
                                sub.findAll(MethodDeclaration.class).forEach(i -> {
                                    System.out.println("Comportamento suspeito detectado  - " + i.getName() + " - " + i.getRange().get().begin);
                                    JsonOut.setTipoSmell("JAVA");
                                    JsonOut.setLinha(i.getRange().get().begin.toString());
                                    JsonOut.setArquivo(nomeArquivo);
                                    ListJsonSmell.add(JsonOut);
                                    totalSmells++;
                                });
                            });
                        });


                        member.toFieldDeclaration().ifPresent(field -> {
                            for (VariableDeclarator variable : field.getVariables()) {
                                //Print the field's class typr
                                //System.out.println(variable.getType());

                                if (variable.getType().toString().contains("Listener")) {
                                    System.out.println("Comportamento suspeito detectado  - " + variable.getType() + " - " + variable.getRange().get().begin);
                                    JsonOut.setTipoSmell("JAVA");
                                    JsonOut.setLinha(variable.getRange().get().begin.toString());
                                    JsonOut.setArquivo(nomeArquivo);
                                    ListJsonSmell.add(JsonOut);
                                    totalSmells++;
                                }
                                //Print the field's name
                                //System.out.println(variable.getName());
                                //Print the field's init value, if not null

                                variable.getInitializer().ifPresent(initValue -> {
                                    if (initValue.isLambdaExpr()) {
                                        System.out.println("Comportamento suspeito detectado  - " + initValue.getRange());
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(initValue.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }
                                });
                            }
                        });
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        JsonOut.saveJson(ListJsonSmell, "SuspiciousBehavior.json");
        return totalSmells;
    }

    //Flex Adapter
    public static long FlexAdapter(String pathApp) {
        try {

            contadorArquivosAnalisados = 0;

            ListSmells.clear();
            //arquivosAnalise.clear();
            totalSmells = 0;

            //listar(new File(pathApp),JAVA);

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");
                    String arquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
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

                                //Contados de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                                WMC wmc = new WMC(compilationunit);
                                wmc.run();

                                if (wmc.getCc() > 56) {

                                    System.out.println("FLEX ADAPTER IDENTIFICADO");
                                    JsonOut.setTipoSmell("JAVA");
                                    JsonOut.setLinha(implementacao.getRange().get().begin.toString());
                                    JsonOut.setArquivo(arquivo);
                                    ListJsonSmell.add(JsonOut);
                                    totalSmells++;
                                }

//                                //Se chegou até aqui, temos certeza de que é um adapter.
//                                //Se a classe que extende do BaseAdapter tiver algum método que não seja sobrescrever um método de interface, é um FlexAdapter.
//                                //Pegamos todos os membros da classe
//                                NodeList<BodyDeclaration<?>> membros = classe.getMembers();
//                                for (BodyDeclaration<?> membro : membros) {
//                                    //Verifica se o membro é um método
//                                    if (membro.isMethodDeclaration()) {
//
//                                        membro.findAll(IfStmt.class).forEach(item -> {
//                                            System.out.println("Flex Adapter detectado na classe " + item.getRange() + " (lógica utilizando if detectada)");
//                                            System.out.println("---------------------------------------------------------------------------------------");
//                                            JsonOut.setTipoSmell("JAVA");
//                                            JsonOut.setLinha(item.getRange().get().begin.toString());
//                                            JsonOut.setArquivo(arquivo);
//                                            ListJsonSmell.add(JsonOut);
//                                            totalSmells++;
//                                        });
//
//                                        membro.findAll(SwitchStmt.class).forEach(item -> {
//                                            System.out.println("Flex Adapter detectado na classe " + item.getRange() + " (lógica utilizando Switch/Case detectada)");
//                                            System.out.println("---------------------------------------------------------------------------------------");
//                                            JsonOut.setTipoSmell("JAVA");
//                                            JsonOut.setLinha(item.getRange().get().begin.toString());
//                                            JsonOut.setArquivo(arquivo);
//                                            ListJsonSmell.add(JsonOut);
//                                            totalSmells++;
//                                        });
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            JsonOut.saveJson(ListJsonSmell, "FlexAdapter.json");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    //Componente de UI Cérebro
    public static long BrainUIComponent(String pathApp) {
        try {
            contadorArquivosAnalisados = 0;
            ListSmells.clear();
            //arquivosAnalise.clear();
            totalSmells = 0;

            //listar(new File(pathApp),JAVA);

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit cUnit = JavaParser.parse(f);

                    cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {

                        if (classe.getExtendedTypes().size() > 0) {
                            if (classe.getExtendedTypes().get(0).toString().contains("Activity") || classe.getExtendedTypes().get(0).toString().contains("Fragment") || classe.getExtendedTypes().get(0).toString().contains("Adapter")) {

                                //Contados de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                                //chama aqui o WMC
                                WMC wmc = new WMC(cUnit);
                                wmc.run();

                                if (wmc.getCc() > 56) {
                                    JsonOut.setTipoSmell("JAVA");
                                    JsonOut.setLinha(classe.getRange().get().begin.toString());
                                    JsonOut.setArquivo(nomeArquivo);
                                    ListJsonSmell.add(JsonOut);
                                    totalSmells++;
                                }

                                //----------------------------------------------------------------foi removido aqui
                                NodeList<BodyDeclaration<?>> membros = classe.getMembers();
                                for (BodyDeclaration<?> membro : membros) {
                                    //Verifica se o membro é um método
                                    if (membro.isMethodDeclaration()) {

                                        membro.findAll(IfStmt.class).forEach(item -> {
                                            System.out.println("Brain UI Component detectado na classe " + item.getRange().get().begin + " (lógica utilizando if detectada)");
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(item.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        });

                                        membro.findAll(SwitchEntryStmt.class).forEach(item -> {
                                            System.out.println("Brain UI Component detectado na classe " + item.getRange().get().begin + " (lógica utilizando Switch/Case detectada)");
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(item.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        });
                                    }
                                }

                                //----------------------------------------------------------------foi removido aqui

                                //Aqui conta para verificar o threshold de fieldstatic
                                AndroidJavaCodeSmells.contadorFieldStatic = 0;
                                classe.getFields().forEach(item -> {
                                    if (item.isFieldDeclaration() && item.isStatic()) {
                                        AndroidJavaCodeSmells.contadorFieldStatic++;
                                    }
                                });

                                if (AndroidJavaCodeSmells.contadorFieldStatic > 8) {
                                    //Static Fields
                                    classe.getFields().forEach(item -> {
                                        if (item.isFieldDeclaration() && item.isStatic()) {
                                            System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Campo Static) " + item.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(item.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }
                                    });
                                }

                                //Procura Libs IO no TIPO  em declaração de campos
                                classe.getFields().forEach(campos -> {
                                    if (IOClass.getIOClass().contains(campos.getElementType().toString())) {
                                        System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) ATRIBUTOS DA CLASSE " + campos.getRange().get().begin);
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(campos.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }
                                });

                                //Procura Libs de IO no TIPO em declaração  de Métodos
                                classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                                    IOClass.getIOClass().forEach(item -> {
                                        //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                        //COMO VAI PROCURAR AQUI ??
                                        if (metodo.getParameters().contains(item)) {
                                            System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) nos parâmetros do método " + metodo.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }

//                                    //Procura Libs de IO no TIPO em retorno  de Métodos
//                                    if (metodo.getType().toString().contains(item)) {
//                                        System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) no retorno do método " + metodo.getRange().get().begin);
//                                        System.out.println("---------------------------------------------------------------------------------------");
//                                        JsonOut.setTipoSmell("JAVA");
//                                        JsonOut.setLinha(metodo.getRange().get().begin.toString());
//                                        JsonOut.setArquivo(nomeArquivo);
//                                        ListJsonSmell.add(JsonOut);
//                                        totalSmells++;
//                                    }

                                        //Procura Libs IO no TIPO  em declaração de campos
                                        metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                            if (campos.getElementType().toString().contains(item)) {
                                                System.out.println("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) FIELD Declaration " + campos.getRange().get().begin);
                                                System.out.println("---------------------------------------------------------------------------------------");
                                                JsonOut.setTipoSmell("JAVA");
                                                JsonOut.setLinha(campos.getRange().get().begin.toString());
                                                JsonOut.setArquivo(nomeArquivo);
                                                ListJsonSmell.add(JsonOut);
                                                totalSmells++;
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
            }

            JsonOut.saveJson(ListJsonSmell, "BrainUIComponent.json");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    //Componente de UI Fazendo IO
    public static long CompUIIO(String pathApp) {
        try {

            contadorArquivosAnalisados = 0;
            //arquivosAnalise.clear();
            totalSmells = 0;

            //listar(new File(pathApp),JAVA);

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit cUnit = JavaParser.parse(f);

                    cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {
                        if (classe.getExtendedTypes().size() > 0) {
                            if (classe.getExtendedTypes().get(0).toString().contains("Activity") || classe.getExtendedTypes().get(0).toString().contains("Fragment")
                                    || classe.getExtendedTypes().get(0).toString().contains("Adapter")) {

                                //Contados de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                                //Procura Libs IO no TIPO  em declaração de campos
                                classe.getFields().forEach(campos -> {
                                    if (IOClass.getIOClass().contains(campos.getElementType().toString())) {
                                        System.out.println("UI IO Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) " + campos.getRange().get().begin);
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(campos.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }
                                });

                                //Procura Libs de IO no TIPO em declaração  de Métodos
                                classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                                    IOClass.getIOClass().forEach(item -> {
                                        //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                        if (metodo.getParameters().contains(item)) {
                                            System.out.println("UI IO Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) nos parâmetros do método " + metodo.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }

                                        //Procura Libs de IO no TIPO em retorno  de Métodos
                                        if (metodo.getType().toString().contains(item)) {
                                            System.out.println("UI IO Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) no retorno do método " + metodo.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }

                                        //Procura Libs IO no TIPO  em declaração de campos
                                        metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                            if (campos.getElementType().toString().contains(item)) {
                                                System.out.println("UI IO Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) " + campos.getRange().get().begin);
                                                System.out.println("---------------------------------------------------------------------------------------");
                                                JsonOut.setTipoSmell("JAVA");
                                                JsonOut.setLinha(campos.getRange().get().begin.toString());
                                                JsonOut.setArquivo(nomeArquivo);
                                                ListJsonSmell.add(JsonOut);
                                                totalSmells++;
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
            }

            JsonOut.saveJson(ListJsonSmell, "UIIOComponent.json");


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    //No uso de fragment
    public static long NotFragment(String pathApp) {
        try {
            contadorArquivosAnalisados = 0;

            //arquivosAnalise.clear();
            ListSmells.clear();
            totalSmells = 0;

            //listar(new File(pathApp),JAVA);

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {

                //Contados de arquivos analisados
                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit cUnit = JavaParser.parse(f);

                    List<String> ViewsAndroid = new ArrayList<String>();
                    ViewsAndroid.add("TextView");
                    ViewsAndroid.add("EditText");
                    ViewsAndroid.add("Spinner");

                    //Não existir fragmentos na aplicação

                    // Uso de Views(EditText, Spinner, ou Outras Views Diretamente pela activity)
                    cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {
                        if (classe.getExtendedTypes().size() > 0) {
                            if (classe.getExtendedTypes().get(0).toString().contains("Activity")) {
                                NodeList<BodyDeclaration<?>> membros = classe.getMembers();

                                //Procura ViewsAndroid no TIPO  em declaração de campos
                                classe.getFields().forEach(campos -> {
                                    if (ViewsAndroid.contains(campos.getElementType().toString())) {
                                        System.out.println("Não Uso de Fragments detectado na classe " + classe.getName() + " () " + campos.getRange().get().begin);
                                        System.out.println("---------------------------------------------------------------------------------------");
                                        JsonOut.setTipoSmell("JAVA");
                                        JsonOut.setLinha(campos.getRange().get().begin.toString());
                                        JsonOut.setArquivo(nomeArquivo);
                                        ListJsonSmell.add(JsonOut);
                                        totalSmells++;
                                    }
                                });

                                //Procura ViewsAndroid no TIPO em declaração  de Métodos
                                classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                                    ViewsAndroid.forEach(item -> {
                                        //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                        if (metodo.getParameters().contains(item)) {
                                            System.out.println("Não Uso de Fragments detectado na classe " + classe.getName() + " () nos parâmetros do método " + metodo.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }

                                        //Procura ViewsAndroid no TIPO em retorno  de Métodos
                                        if (metodo.getType().toString().contains(item)) {
                                            System.out.println("Não Uso de Fragments detectado na classe " + classe.getName() + " () no retorno do método " + metodo.getRange().get().begin);
                                            System.out.println("---------------------------------------------------------------------------------------");
                                            JsonOut.setTipoSmell("JAVA");
                                            JsonOut.setLinha(metodo.getRange().get().begin.toString());
                                            JsonOut.setArquivo(nomeArquivo);
                                            ListJsonSmell.add(JsonOut);
                                            totalSmells++;
                                        }

                                        //Procura ViewsAndroid no TIPO  em declaração de campos
                                        metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                            if (campos.getElementType().toString().contains(item)) {
                                                System.out.println("Não Uso de Fragments detectado na classe " + classe.getName() + " () " + campos.getRange().get().begin);
                                                System.out.println("---------------------------------------------------------------------------------------");
                                                JsonOut.setTipoSmell("JAVA");
                                                JsonOut.setLinha(campos.getRange().get().begin.toString());
                                                JsonOut.setArquivo(nomeArquivo);
                                                ListJsonSmell.add(JsonOut);
                                                totalSmells++;
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
            }

            JsonOut.saveJson(ListJsonSmell, "NotFragment.json");


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return totalSmells;
    }

    //Uso excessivo de Fragments
    public static long ExcessiveFragment(String pathApp, long threshold) throws IOException {

        contadorArquivosAnalisados = 0;

        //arquivosAnalise.clear();
        ListSmells.clear();
        totalSmells = 0;

//        listar(new File(pathApp),JAVA);
        long totalFragments = 0;
        List<ReusoStringData> listaExcessiveFragment = new ArrayList<ReusoStringData>();

        for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
            //Contados de arquivos analisados
            contadorArquivosAnalisados = contadorArquivosAnalisados + 1;
            try {

                //System.out.println("Arquivo analisado:" + arquivosAnalise.toArray()[cont]);
                //System.out.println("---------------------------------------------------------------------------------------");

                File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
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
                                System.err.println("É um fragmento sim" + classe.getName().getIdentifier());
                            }
                        }
                    }
                }

                if (totalFragments >= threshold) {
                    System.out.println("Uso Excessivo de Fragment " + "(Mais de " + threshold + " Fragments no aplicativo)");
                    JsonOut.setTipoSmell("XML");
                    JsonOut.setArquivo("");
                    ListJsonSmell.add(JsonOut);
                    totalSmells++;
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        JsonOut.saveJson(ListJsonSmell, "ExcessiveFragment.json");
        return totalSmells;

    }

    //Adapter consumista
    public static long FoolAdapter(String pathApp) {
        try {
            contadorArquivosAnalisados = 0;
            ListSmells.clear();
            //arquivosAnalise.clear();
            totalSmells = 0;

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    System.out.println("---------------------------------------------------------------------------------------");
                    String arquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit compilationunit = JavaParser.parse(f);

                    //Extrai cada Classe analisada pelo CompilationUnit
                    ArrayList<ClassOrInterfaceDeclaration> classes = new ArrayList<ClassOrInterfaceDeclaration>();
                    NodeList<TypeDeclaration<?>> types = compilationunit.getTypes();
                    for (int i = 0; i < types.size(); i++) {
                        classes.add((ClassOrInterfaceDeclaration) types.get(i));
                    }

                    //Para cada uma dessas classes, verifica se ela � um Adapter (ou seja, se ela extende de BaseAdapter).
                    for (ClassOrInterfaceDeclaration classe : classes) {

                        //Como a classe vai ser analisada ainda, n�o cont�m smells por enquanto
                        Boolean isFoolAdapter = false;

                        //Para ver se a classe � um Adapter, precisamos ver se ela extende de BaseAdapter
                        //Pegamos todas as classes que ela implementa
                        NodeList<ClassOrInterfaceType> implementacoes = classe.getExtendedTypes();
                        for (ClassOrInterfaceType implementacao : implementacoes) {
                            //eu: Durelli alterei para verificar se contem problema aqui
                            if (implementacao.getName().getIdentifier().contains("Adapter")) {

                                //Contados de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                                //Se chegou at� aqui, temos certeza de que � um adapter.
                                //Se a classe que extende do BaseAdapter tiver algum m�todo que n�o seja sobrescrever um m�todo de interface, � um FlexAdapter.
                                //Pegamos todos os membros da classe
                                NodeList<BodyDeclaration<?>> membros = classe.getMembers();
                                //Verifica se o membro � um m�todo
                                for (BodyDeclaration<?> membro : membros)
                                    if (membro.isMethodDeclaration()) {
                                        MethodDeclaration metodo = (MethodDeclaration) membro;
                                        //Verifica se este m�todo chama getView
                                        if (metodo.getName().getIdentifier().equals("getView")) {

                                            //Pega o parametro do tipo View e armazena o nome dele
                                            //Pode ser �til para verificar por findViewById dentro de la�os
                                            Parameter viewParameter = metodo.getParameter(1);
                                            String nomeParametroView = viewParameter.getName().getIdentifier();

                                            //Pega o bloco de declara��es dentro m�todo getView
                                            BlockStmt body = metodo.getBody().get();
                                            NodeList<Statement> statements = body.getStatements();

                                            //Itera sobre as declara��es at� achar express�es
                                            for (Statement statement : statements) {
                                                if (statement.isExpressionStmt()) {
                                                    //Se em alguma dessas express�es tiver o texto findViewById
                                                    //Quer dizer que o ViewHolder n�o est� sendo utilizado, o que caracteriza o smell
                                                    if (statement.toString().contains("findViewById(")) {
                                                        isFoolAdapter = true;
                                                        JsonOut.setTipoSmell("JAVA");
                                                        JsonOut.setLinha(statement.getRange().get().begin.toString());
                                                        JsonOut.setArquivo(arquivo);
                                                        ListJsonSmell.add(JsonOut);
                                                        totalSmells++;
                                                    }

                                                    //Se ele infla um Layout em toda chamada ao getView, isso tamb�m caracteriza o smell
                                                    if (statement.toString().contains("inflater")) {
                                                        isFoolAdapter = true;
                                                        JsonOut.setTipoSmell("JAVA");
                                                        JsonOut.setLinha(statement.getRange().get().begin.toString());
                                                        JsonOut.setArquivo(arquivo);
                                                        ListJsonSmell.add(JsonOut);
                                                        totalSmells++;
                                                    }
                                                }
                                            }
                                        }
                                    }
                            }
                        }

                        //Se a classe for um foolAdapter, imprime o erro na tela
                        if (isFoolAdapter) {
                            System.out.println("Fool Adapter detectado na classe " + classe.getName().getIdentifier());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            JsonOut.saveJson(ListJsonSmell, "FoolAdapter.json");
            return totalSmells;
        } catch (Exception ex) {
            ex.printStackTrace();
            return totalSmells;
        }
    }
}
