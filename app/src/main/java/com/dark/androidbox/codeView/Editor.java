package com.dark.androidbox.codeView;

import android.graphics.Color;
import com.amrdeveloper.codeview.CodeView;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Editor {
    // Define new color constants for white background
    private static final int KEYWORD_COLOR = Color.parseColor("#0077AA");   // Dark Blue for keywords
    private static final int CLASS_COLOR = Color.parseColor("#006600");     // Dark Green for classes
    private static final int METHOD_COLOR = Color.parseColor("#AA00FF");    // Violet for methods
    private static final int VARIABLE_COLOR = Color.parseColor("#AA5500");  // Brownish for variables
    private static final int PACKAGE_COLOR = Color.parseColor("#0055AA");   // Blue for package imports
    private static final int COMMENT_COLOR = Color.parseColor("#808080");   // Gray for comments
    private static final int TEXT_COLOR = Color.parseColor("#000000");      // Black for default text

    // Define patterns for Java keywords, class names, methods, variables, packages, and comments
    private final Pattern keywordsPattern = Pattern.compile(
            "\\b(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|null|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile|while)\\b");
    private final Pattern classPattern = Pattern.compile(
            "\\b([A-Z][a-zA-Z0-9_]*)\\b");  // Captures class names starting with capital letters
    private final Pattern methodPattern = Pattern.compile(
            "\\b([a-zA-Z_$][a-zA-Z\\d_$]*)\\s*\\("); // Methods followed by parentheses
    private final Pattern variablePattern = Pattern.compile(
            "\\b([a-zA-Z_$][a-zA-Z\\d_$]*)\\b(?=\\s*=|;)");  // Variable assignment or declaration
    private final Pattern packagePattern = Pattern.compile(
            "\\b([a-z]+(?:\\.[a-z]+)*)\\b");  // Package names in `import` statements
    private final Pattern commentPattern = Pattern.compile(
            "//[^\n]*|/\\*(.|\\R)*?\\*/"); // Single-line and multi-line comments

    public CodeView txtCode;

    public Editor(CodeView codeView) {
        this.txtCode = codeView;
        setUpEditor();
    }

    private void setUpEditor() {
        // Setup line numbering
        txtCode.setEnableLineNumber(true);
        txtCode.setLineNumberTextColor(Color.parseColor("#B0B0B0"));  // Light gray for line numbers
        txtCode.setLineNumberTextSize(30);

        // Setup auto-complete for code pairs
        Map<Character, Character> pairCompleteMap = new HashMap<>();
        pairCompleteMap.put('{', '}');
        pairCompleteMap.put('[', ']');
        pairCompleteMap.put('(', ')');
        pairCompleteMap.put('<', '>');
        pairCompleteMap.put('"', '"');
        txtCode.enablePairComplete(true);
        txtCode.setPairCompleteMap(pairCompleteMap);

        // Set editor background to white and default text color to black
        txtCode.setBackgroundColor(Color.parseColor("#FFFFFF"));  // White background
        txtCode.setTextColor(TEXT_COLOR);  // Default text color is black

        // Apply syntax highlighting patterns
        applySyntaxHighlighting();
    }

    private void applySyntaxHighlighting() {
        // Apply syntax highlighting patterns with their respective colors
        txtCode.addSyntaxPattern(keywordsPattern, KEYWORD_COLOR);  // Java keywords
        txtCode.addSyntaxPattern(classPattern, CLASS_COLOR);       // Class names
        txtCode.addSyntaxPattern(methodPattern, METHOD_COLOR);     // Method names
        txtCode.addSyntaxPattern(variablePattern, VARIABLE_COLOR); // Variables
        txtCode.addSyntaxPattern(packagePattern, PACKAGE_COLOR);   // Package names
        txtCode.addSyntaxPattern(commentPattern, COMMENT_COLOR);   // Comments
    }

    // Method to dynamically set color for specific target strings
    public void setTxtColor(String targetString, int color) {
        Pattern pattern = Pattern.compile(Pattern.quote(targetString));
        txtCode.addSyntaxPattern(pattern, color);
    }
}
