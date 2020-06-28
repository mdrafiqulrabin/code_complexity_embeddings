import com.github.javaparser.JavaToken;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public final class Common {

    static String ROOT_INPUT_PATH = "";
    static String ROOT_OUTPUT_PATH = "";

    static String readJavaCode(File javaFile) {
        String txtCode = "";
        try {
            txtCode = new String(Files.readAllBytes(javaFile.toPath()));
            if(!txtCode.startsWith("class")) txtCode = "class T { \n" + txtCode + "\n}";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return txtCode;
    }

    static CompilationUnit getParseUnit(File javaFile) {
        CompilationUnit root = null;
        try {
            try {
                root = StaticJavaParser.parse(javaFile);
            } catch (Exception ex) {
                String codeText = new String(Files.readAllBytes(javaFile.toPath()));
                codeText = "class C { \n" + codeText + "\n}";
                root = StaticJavaParser.parse(codeText);
            }
        } catch (Exception ex) {
            System.out.println("\nError: " + javaFile);
            ex.printStackTrace();
        }
        return root;
    }

    static void saveEmbedding(File targetFile, String txtEmbedding) {
        try {
            if (targetFile.getParentFile().exists() || targetFile.getParentFile().mkdirs()) {
                if (targetFile.exists() || targetFile.createNewFile()) {
                    Files.write(targetFile.toPath(),
                            (txtEmbedding + "\n").getBytes(),
                            StandardOpenOption.APPEND);
                }
            }
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    public static ArrayList<ArrayList<Statement>> getBasicBlocks(MethodDeclaration md) {
        ArrayList<Statement> innerStmts = new ArrayList<>();
        ArrayList<ArrayList<Statement>> basicBlockStmts = new ArrayList<>();
        ArrayList<Statement> allStatements = (ArrayList<Statement>) md.findAll(Statement.class);
        for ( Statement stmt: allStatements) {
            if (stmt instanceof ExpressionStmt
                    && stmt.findAll(MethodCallExpr.class).size() == 0
                    && Common.isPermuteApplicable(stmt)) {
                innerStmts.add(stmt);
            } else {
                if (innerStmts.size() > 1) {
                    basicBlockStmts.add(new ArrayList<>(innerStmts));
                }
                innerStmts.clear();
            }
        }
        return basicBlockStmts;
    }

    public static boolean isPermuteApplicable(Statement stmt) {
        return !(
                stmt instanceof EmptyStmt ||
                stmt instanceof LabeledStmt ||
                stmt instanceof BreakStmt ||
                stmt instanceof ContinueStmt ||
                stmt instanceof ReturnStmt
        );
    }

    public static String getLabelStr(CompilationUnit cu) {
        List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
        return methods.get(0).getName().toString();
    }

    public static List<JavaToken> getAllTokens (MethodDeclaration md) {
        List<JavaToken> allTokens = new ArrayList<>();
        if (md.getTokenRange().isPresent()) {
            md.getTokenRange().get().forEach(token -> {
                    if (token.getKind() >= JavaToken.Kind.COMMENT_CONTENT.getKind()) {
                        allTokens.add(token);
                    }
                }
            );
        }
        return allTokens;
    }

    public static String getCleanPath(File javaFile) {
        String cleanPath = javaFile.toString();
        cleanPath = cleanPath.replaceAll(" ", "__s__");
        cleanPath = cleanPath.replaceAll(",", "__c__");
        return cleanPath;
    }
}
