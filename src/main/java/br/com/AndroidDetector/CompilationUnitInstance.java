package br.com.AndroidDetector;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileNotFoundException;

public class CompilationUnitInstance  {
    private static CompilationUnit uniqueInstance;

    private CompilationUnitInstance() {

    }

    public static synchronized CompilationUnit getInstance(File f) throws FileNotFoundException {
        if (uniqueInstance == null)
            uniqueInstance = JavaParser.parse(f);

        return uniqueInstance;
    }
}
