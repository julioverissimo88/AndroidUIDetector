package metric;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;

public class WMC {

    protected int cc = 0;
    private File fileToIdentifyTheWMC;


    public WMC(File fileToIdentifyTheWMC) {
        this.fileToIdentifyTheWMC = fileToIdentifyTheWMC;
    }

    public void run() {
        try {
            CompilationUnit compilationUnit = JavaParser.parse(this.fileToIdentifyTheWMC);
            this.visitMethodDeclaration(compilationUnit);
            this.visitForStatement(compilationUnit);
            this.visitForStatement2(compilationUnit);
            this.visitConditionalExpression(compilationUnit);
            this.visitDoStatement(compilationUnit);
            this.visitWhileStatement(compilationUnit);
            this.visitSwitchStatement(compilationUnit);
            this.visitInitializerStatement(compilationUnit);
            this.visitCatchStatement(compilationUnit);
            this.visitIfStatement(compilationUnit);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void visitMethodDeclaration(CompilationUnit cUnit) {
        cUnit.findAll(MethodDeclaration.class).stream().
                forEach(m -> {
                    increaseCc();
                });
    }

    private void visitForStatement(CompilationUnit cUnit) {
        cUnit.findAll(ForeachStmt.class).stream().
                forEach(m -> {
                    increaseCc();
                });
    }

    private void visitForStatement2(CompilationUnit cUnit) {
        cUnit.findAll(ForStmt.class).stream().
                forEach(m -> {
                    increaseCc();
                });
    }

    private void visitConditionalExpression(CompilationUnit cUnit) {
        cUnit.findAll(ConditionalExpr.class).stream().
                forEach(m -> {
                    increaseCc();
                });
    }

    private void visitDoStatement(CompilationUnit cUnit) {
        cUnit.findAll(DoStmt.class).stream().
                forEach(m -> {
                    increaseCc();
                });
    }

    private void visitWhileStatement(CompilationUnit cUnit) {
        cUnit.findAll(WhileStmt.class).stream().
                forEach(m -> {
                    increaseCc();
                });
    }

    private void visitSwitchStatement(CompilationUnit cUnit) {
        cUnit.findAll(SwitchStmt.class).stream().
                forEach(m -> {
                    increaseCc();
                });
    }

    private void visitInitializerStatement(CompilationUnit cUnit) {
        cUnit.findAll(InitializerDeclaration.class).stream().
                forEach(m -> {
                    increaseCc();
                });
    }

    private void visitCatchStatement(CompilationUnit cUnit) {
        cUnit.findAll(CatchClause.class).stream().
                forEach(m -> {
                    increaseCc();
                });
    }

    private void visitIfStatement(CompilationUnit cUnit) {
        cUnit.findAll(IfStmt.class).stream().
                forEach(m -> {
                    String expr = m.getCondition().toString().replace("&&", "&").replace("||", "|");
                    int ands = StringUtils.countMatches(expr, "&");
                    int ors = StringUtils.countMatches(expr, "|");

                    increaseCc(ands + ors);
                    increaseCc();
                });
    }

    private void increaseCc() {
        increaseCc(1);
    }

    protected void increaseCc(int qtd) {
        cc += qtd;
    }

}
