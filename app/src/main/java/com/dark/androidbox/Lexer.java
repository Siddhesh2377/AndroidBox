package com.dark.androidbox;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

public class Lexer {

    private CompilationUnit unit;

    public Lexer(StringBuilder code) {
        JavaParser parser = new JavaParser();
        unit = parser.parse(new StringReader(code.toString())).getResult().orElse(null);
    }

    public List<ClassOrInterfaceDeclaration> getClasses() {
        return unit.findAll(ClassOrInterfaceDeclaration.class);
    }

    public List<MethodDeclaration> getMethods() {
        return unit.findAll(MethodDeclaration.class);
    }

    public List<FieldDeclaration> getFields() {
        return unit.findAll(FieldDeclaration.class);
    }

    // Method to get all constructors
    public List<MethodDeclaration> getConstructors() {
        return unit.findAll(MethodDeclaration.class).stream()
                .filter(method -> method.getNameAsString().equals(method.getDeclarationAsString()))
                .filter(method -> method.getParameters().isEmpty())
                .collect(Collectors.toList());
    }

    public List<MethodCallExpr> getMethodCalls() {
        return unit.findAll(MethodCallExpr.class);
    }
}
