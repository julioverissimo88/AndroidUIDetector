package br.com.AndroidDetector;

import br.com.UTIL.Constants;
import br.com.UTIL.ReusoStringData;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import br.com.metric.WMC;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static br.com.UTIL.Constants.*;

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

    private static void printAndCountSmels(String msg, String linha, String Type, String file) {
        System.out.println(msg);
        JsonOut = new OutputSmells();
        JsonOut.setTipoSmell(Type);
        JsonOut.setArquivo(file);
        JsonOut.setLinha(linha);
        ListJsonSmell.add(JsonOut);
        totalSmells++;
    }

    //Componente de UI Acoplado
    public static List<OutputSmells> CoupledUIComponent(String pathApp) throws FileNotFoundException {
        contadorArquivosAnalisados = 0;
        File folder = new File(pathApp);
        ListArquivosAnaliseJava = LoadFiles.carregaArquivosJAVAAnalise(folder);
        ListJsonSmell.clear();
        ListSmells.clear();
        totalSmells = 0;

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
                            if (implementacao.getName().getIdentifier().contains(FRAGMENT) || implementacao.getName().getIdentifier().contains(ADAPTER) || implementacao.getName().getIdentifier().contains(ACTIVITY)) {

                                //Contador de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                                classe.getFields().forEach(item -> {
                                    //System.out.println(item.getElementType().toString());
                                    if ((item.getElementType().toString().contains(ACTIVITY) || item.getElementType().toString().contains(FRAGMENT))) {
                                        printAndCountSmels("Componente de UI Acoplado " + item.getElementType().toString() + item.getRange(), item.getRange().toString(), Constants.JAVA, nomeArquivo );
                                    }
                                });

                                classe.findAll(ConstructorDeclaration.class).forEach(metodo -> {
                                    metodo.getParameters().forEach(item -> {
                                        if (item.getType().toString().contains(ACTIVITY) || item.getType().toString().contains(FRAGMENT)) {
                                            printAndCountSmels("Componente de UI Acoplado  " + classe.getName() + " " + metodo.getRange().get().begin, item.getRange().toString(), Constants.JAVA, nomeArquivo);
                                        }
                                    });
                                });

                                classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                                    //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                    if (metodo.getParameters().contains(ACTIVITY) || metodo.getParameters().contains(FRAGMENT)) {
                                        printAndCountSmels("Componente de UI Acoplado  " + classe.getName() + " " + metodo.getRange().get().begin, metodo.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
                                    }

                                    //Procura Libs de IO no TIPO em retorno  de Métodos
                                    if (metodo.getType().toString().contains(ACTIVITY) || metodo.getType().toString().contains(FRAGMENT)) {
                                        printAndCountSmels("Componente de UI Acoplado  " + classe.getName() + " " + metodo.getRange().get().begin, metodo.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
                                    }

                                    //Procura Libs IO no TIPO  em declaração de campos
                                    metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                        if (campos.getElementType().toString().contains(ACTIVITY) || campos.getElementType().toString().contains(FRAGMENT)) {
                                            printAndCountSmels("Componente de UI Acoplado  " + classe.getName() + ") " + campos.getRange().get().begin, metodo.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
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
        return ListJsonSmell;
    }

    //Comportamento suspeito
    public static List<OutputSmells> SuspiciousBehavior(String pathApp) throws FileNotFoundException {
        ListSmells.clear();
        File folder = new File(pathApp);
        ListArquivosAnaliseJava = LoadFiles.carregaArquivosJAVAAnalise(folder);
        ListJsonSmell.clear();
        ListSmells.clear();
        totalSmells = 0;

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
                            if (implementacao.getName().getIdentifier().contains(BASEACTIVITY) || implementacao.getName().getIdentifier().contains(ACTIVITY) || implementacao.getName().getIdentifier().contains(FRAGMENT) || implementacao.getName().getIdentifier().contains(BASEADAPTER) || implementacao.getName().getIdentifier().endsWith(LISTENER)) {
                                classeValida = true;
                                //Contador de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;
                                classe.getImplementedTypes().forEach(item -> {
                                    //System.out.println(item.getNameAsString());
                                    if (item.getName().toString().contains(LISTENER)) {
                                        printAndCountSmels("Comportamento suspeito detectado  - " + item.getRange(), item.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
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
                                    printAndCountSmels("Comportamento suspeito detectado  - " + i.getName() + " - " + i.getRange().get().begin, item.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
                                });
                            });
                        });


                        member.toFieldDeclaration().ifPresent(field -> {
                            for (VariableDeclarator variable : field.getVariables()) {
                                if (variable.getType().toString().contains(LISTENER)) {
                                    printAndCountSmels("Comportamento suspeito detectado  - " + variable.getType() + " - " + variable.getRange().get().begin, variable.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
                                }
                                //Print the field's name
                                //System.out.println(variable.getName());
                                //Print the field's init value, if not null

                                variable.getInitializer().ifPresent(initValue -> {
                                    if (initValue.isLambdaExpr()) {
                                        printAndCountSmels("Comportamento suspeito detectado  - " + initValue.getRange(), initValue.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
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
        return ListJsonSmell;
    }

    //Flex Adapter
    public static List<OutputSmells> FlexAdapter(String pathApp) {
        try {

            contadorArquivosAnalisados = 0;

            File folder = new File(pathApp);
            ListArquivosAnaliseJava = LoadFiles.carregaArquivosJAVAAnalise(folder);
            ListJsonSmell.clear();
            ListSmells.clear();
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

        return ListJsonSmell;
    }

    //Componente de UI Cérebro
    public static List<OutputSmells> BrainUIComponent(String pathApp) {
        try {
            contadorArquivosAnalisados = 0;
            File folder = new File(pathApp);
            ListArquivosAnaliseJava = LoadFiles.carregaArquivosJAVAAnalise(folder);
            ListJsonSmell.clear();
            ListSmells.clear();
            totalSmells = 0;

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit cUnit = JavaParser.parse(f);

                    cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {

                        if (classe.getExtendedTypes().size() > 0) {
                            if (classe.getExtendedTypes().get(0).toString().contains(ACTIVITY) || classe.getExtendedTypes().get(0).toString().contains(FRAGMENT) || classe.getExtendedTypes().get(0).toString().contains(ADAPTER)) {

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
                                            printAndCountSmels("Brain UI Component detectado na classe " + item.getRange().get().begin + " (lógica utilizando if detectada)", item.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
                                        });

                                        membro.findAll(SwitchEntryStmt.class).forEach(item -> {
                                            printAndCountSmels("Brain UI Component detectado na classe " + item.getRange().get().begin + " (lógica utilizando Switch/Case detectada)", item.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
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
                                            printAndCountSmels("Brain UI Component detectado na classe " + classe.getName() + " (Campo Static) " + item.getRange().get().begin, item.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
                                        }
                                    });
                                }

                                //Procura Libs IO no TIPO  em declaração de campos
                                classe.getFields().forEach(campos -> {
                                    if (IOClass.getIOClass().contains(campos.getElementType().toString())) {
                                        printAndCountSmels("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) ATRIBUTOS DA CLASSE " + campos.getRange().get().begin, campos.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
                                    }
                                });

                                //Procura Libs de IO no TIPO em declaração  de Métodos
                                classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                                    IOClass.getIOClass().forEach(item -> {
                                        //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                        //COMO VAI PROCURAR AQUI ??
                                        if (metodo.getParameters().contains(item)) {
                                            printAndCountSmels("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) nos parâmetros do método " + metodo.getRange().get().begin, metodo.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
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
                                                printAndCountSmels("Brain UI Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) FIELD Declaration " + campos.getRange().get().begin, campos.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
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

        return ListJsonSmell;
    }

    //Componente de UI Fazendo IO
    public static List<OutputSmells> CompUIIO(String pathApp) {
        try {
            contadorArquivosAnalisados = 0;
            File folder = new File(pathApp);
            ListArquivosAnaliseJava = LoadFiles.carregaArquivosJAVAAnalise(folder);
            ListJsonSmell.clear();
            ListSmells.clear();
            totalSmells = 0;

            for (int cont = 0; cont < ListArquivosAnaliseJava.toArray().length; cont++) {
                try {
                    System.out.println("Arquivo analisado:" + ListArquivosAnaliseJava.toArray()[cont]);
                    String nomeArquivo = ListArquivosAnaliseJava.toArray()[cont].toString();
                    System.out.println("---------------------------------------------------------------------------------------");

                    File f = new File(ListArquivosAnaliseJava.toArray()[cont].toString());
                    CompilationUnit cUnit = JavaParser.parse(f);

                    cUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classe -> {
                        if (classe.getExtendedTypes().size() > 0) {
                            if (classe.getExtendedTypes().get(0).toString().contains(ACTIVITY) || classe.getExtendedTypes().get(0).toString().contains(FRAGMENT)
                                    || classe.getExtendedTypes().get(0).toString().contains(ADAPTER)) {

                                //Contados de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                                //Procura Libs IO no TIPO  em declaração de campos
                                classe.getFields().forEach(campos -> {
                                    if (IOClass.getIOClass().contains(campos.getElementType().toString())) {
                                        printAndCountSmels("UI IO Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) " + campos.getRange().get().begin, campos.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
                                    }
                                });

                                //Procura Libs de IO no TIPO em declaração  de Métodos
                                classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                                    IOClass.getIOClass().forEach(item -> {
                                        //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                        if (metodo.getParameters().contains(item)) {
                                            printAndCountSmels("UI IO Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) nos parâmetros do método " + metodo.getRange().get().begin, metodo.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
                                        }

                                        //Procura Libs de IO no TIPO em retorno  de Métodos
                                        if (metodo.getType().toString().contains(item)) {
                                            printAndCountSmels("UI IO Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) no retorno do método " + metodo.getRange().get().begin, metodo.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
                                        }

                                        //Procura Libs IO no TIPO  em declaração de campos
                                        metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                            if (campos.getElementType().toString().contains(item)) {
                                                printAndCountSmels("UI IO Component detectado na classe " + classe.getName() + " (Acesso a banco de dados) " + campos.getRange().get().begin, metodo.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
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

        return ListJsonSmell;
    }

    //No uso de fragment
    public static List<OutputSmells> NotFragment(String pathApp) {
        try {
            contadorArquivosAnalisados = 0;

            File folder = new File(pathApp);
            ListArquivosAnaliseJava = LoadFiles.carregaArquivosJAVAAnalise(folder);
            ListJsonSmell.clear();
            ListSmells.clear();
            totalSmells = 0;

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
                            if (classe.getExtendedTypes().get(0).toString().contains(ACTIVITY)) {
                                NodeList<BodyDeclaration<?>> membros = classe.getMembers();

                                //Procura ViewsAndroid no TIPO  em declaração de campos
                                classe.getFields().forEach(campos -> {
                                    if (ViewsAndroid.contains(campos.getElementType().toString())) {
                                        printAndCountSmels("Não Uso de Fragments detectado na classe " + classe.getName() + " () " + campos.getRange().get().begin, campos.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
                                    }
                                });

                                //Procura ViewsAndroid no TIPO em declaração  de Métodos
                                classe.findAll(MethodDeclaration.class).forEach(metodo -> {
                                    ViewsAndroid.forEach(item -> {
                                        //Procura Libs de IO no TIPO em declaração  nos Parametros de  Métodos
                                        if (metodo.getParameters().contains(item)) {
                                            printAndCountSmels("Não Uso de Fragments detectado na classe " + classe.getName() + " () nos parâmetros do método " + metodo.getRange().get().begin, metodo.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
                                        }

                                        //Procura ViewsAndroid no TIPO em retorno  de Métodos
                                        if (metodo.getType().toString().contains(item)) {
                                            printAndCountSmels("Não Uso de Fragments detectado na classe " + classe.getName() + " () no retorno do método " + metodo.getRange().get().begin, metodo.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
                                        }

                                        //Procura ViewsAndroid no TIPO  em declaração de campos
                                        metodo.findAll(FieldDeclaration.class).forEach(campos -> {
                                            if (campos.getElementType().toString().contains(item)) {
                                                printAndCountSmels("Não Uso de Fragments detectado na classe " + classe.getName() + " () " + campos.getRange().get().begin, metodo.getRange().get().begin.toString(), Constants.JAVA, nomeArquivo);
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

        return ListJsonSmell;
    }

    //Uso excessivo de Fragments
    public static List<OutputSmells> ExcessiveFragment(String pathApp, long threshold) throws IOException {
        contadorArquivosAnalisados = 0;
        File folder = new File(pathApp);
        ListArquivosAnaliseJava = LoadFiles.carregaArquivosJAVAAnalise(folder);
        ListJsonSmell.clear();
        ListSmells.clear();
        totalSmells = 0;

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
                            if (implementacao.getName().getIdentifier().contains(FRAGMENT)) {
                                totalFragments = totalFragments + 1;
                            }
                        }
                    }
                }

                if (totalFragments >= threshold) {
                    printAndCountSmels("Uso Excessivo de Fragment " + "(Mais de " + threshold + " Fragments no aplicativo)", "", Constants.XML, "");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        JsonOut.saveJson(ListJsonSmell, "ExcessiveFragment.json");
        return ListJsonSmell;

    }

    //Adapter consumista
    public static List<OutputSmells> FoolAdapter(String pathApp) {
        try {
            contadorArquivosAnalisados = 0;
            File folder = new File(pathApp);
            ListArquivosAnaliseJava = LoadFiles.carregaArquivosJAVAAnalise(folder);
            ListJsonSmell.clear();
            ListSmells.clear();
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

                        //Para ver se a classe é um Adapter, precisamos ver se ela extende de BaseAdapter
                        //Pegamos todas as classes que ela implementa
                        NodeList<ClassOrInterfaceType> implementacoes = classe.getExtendedTypes();
                        for (ClassOrInterfaceType implementacao : implementacoes) {
                            //eu: Durelli alterei para verificar se contem problema aqui
                            if (implementacao.getName().getIdentifier().contains(ADAPTER)) {

                                //Contados de arquivos analisados
                                contadorArquivosAnalisados = contadorArquivosAnalisados + 1;

                                //Se chegou até aqui, temos certeza de que é um adapter.
                                //Se a classe que extende do BaseAdapter tiver algum método que não seja sobrescrever um método de interface, é um FlexAdapter.
                                //Pegamos todos os membros da classe
                                NodeList<BodyDeclaration<?>> membros = classe.getMembers();
                                //Verifica se o membro é um método
                                for (BodyDeclaration<?> membro : membros)
                                    if (membro.isMethodDeclaration()) {
                                        MethodDeclaration metodo = (MethodDeclaration) membro;
                                        //Verifica se este método chama getView
                                        if (metodo.getName().getIdentifier().equals(GETVIEW)) {

                                            //Pega o parametro do tipo View e armazena o nome dele
                                            //Pode ser útil para verificar por findViewById dentro de laços
                                            Parameter viewParameter = metodo.getParameter(1);
                                            String nomeParametroView = viewParameter.getName().getIdentifier();

                                            //Pega o bloco de declarações dentro método getView
                                            BlockStmt body = metodo.getBody().get();
                                            NodeList<Statement> statements = body.getStatements();

                                            //Itera sobre as declarações até achar expressões
                                            for (Statement statement : statements) {
                                                if (statement.isExpressionStmt()) {
                                                    //Se em alguma dessas expressões tiver o texto findViewById
                                                    //Quer dizer que o ViewHolder não está sendo utilizado, o que caracteriza o smell
                                                    if (statement.toString().contains(FINDVIEWBYID)) {
                                                        isFoolAdapter = true;
                                                        printAndCountSmels("Fool Adapter " + statement.getRange().get().begin, "", Constants.JAVA, ListArquivosAnaliseJava.toArray()[cont].toString());
                                                    }

                                                    //Se ele infla um Layout em toda chamada ao getView, isso tamb�m caracteriza o smell
                                                    if (statement.toString().contains(INFLATER)) {
                                                        isFoolAdapter = true;
                                                        printAndCountSmels("Fool Adapter " + statement.getRange().get().begin, "", Constants.JAVA, ListArquivosAnaliseJava.toArray()[cont].toString());
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
            return ListJsonSmell;
        } catch (Exception ex) {
            ex.printStackTrace();
            return ListJsonSmell;
        }
    }
}
