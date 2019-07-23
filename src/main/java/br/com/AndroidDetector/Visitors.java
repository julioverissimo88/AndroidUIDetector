package br.com.AndroidDetector;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.*;

public class Visitors {
    private void visitMethodDeclaration(CompilationUnit cUnit) {
        cUnit.findAll(MethodDeclaration.class);
    }

    private void visitForStatement(CompilationUnit cUnit) {
        cUnit.findAll(ForeachStmt.class);
    }

    private void visitConditionalExpression(CompilationUnit cUnit) {
        cUnit.findAll(ConditionalExpr.class);
    }

    private void visitDoStatement(CompilationUnit cUnit) {
        cUnit.findAll(DoStmt.class);
    }

    private void visitWhileStatement(CompilationUnit cUnit) {
        cUnit.findAll(WhileStmt.class);
    }

    private void visitSwitchStatement(CompilationUnit cUnit) {
        cUnit.findAll(SwitchStmt.class);
    }

    private void visitInitializerStatement(CompilationUnit cUnit) {
        cUnit.findAll(InitializerDeclaration.class);
    }

    private void visitCatchStatement(CompilationUnit cUnit) {
        cUnit.findAll(CatchClause.class);
    }

    private void visitIfStatement(CompilationUnit cUnit) {
        cUnit.findAll(IfStmt.class);
    }
}
