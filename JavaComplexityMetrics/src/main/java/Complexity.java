import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.util.Arrays;

public class Complexity extends VoidVisitorAdapter<Object> {
    private File mJavaFile = null;
    private String mLabelStr;

    private int mLOC, mBlock, mBasicBlock;
    private int mParameter, mLocalVariable, mGlobalVariable;
    private int mLoop, mJump, mDecision, mCondition;
    private int mInstance, mFunctionCall;
    private int mErrorHandler, mThreadHandler;
    private int mThis, mSuper, mNull;
    private int mBoolean, mTernary, mReturn;
    private int mASTNode, mASTToken;

    Complexity() {
        mLOC = 0; mBlock = 0; mBasicBlock = 0;
        mParameter = 0; mLocalVariable = 0; mGlobalVariable = 0;
        mLoop = 0; mJump = 0; mDecision = 0; mCondition = 0;
        mInstance = 0; mFunctionCall = 0;
        mErrorHandler = 0; mThreadHandler = 0;
        mThis = 0;  mSuper = 0; mNull = 0;
        mBoolean = 0; mTernary = 0; mReturn = 0;
        mASTNode = 0; mASTToken = 0;
        mLabelStr = "";
    }

    public String inspectSourceCode(File javaFile) {
        this.mJavaFile = javaFile;
        CompilationUnit root = Common.getParseUnit(mJavaFile);
        if (root != null) {
            this.visit(root.clone(), null);
            return this.toString();
        }
        return null;
    }

    @Override
    public void visit(CompilationUnit cu, Object obj) {
        for (MethodDeclaration md: cu.findAll(MethodDeclaration.class)) {
            complexityASTParser(md);
        }
        String[] parts = mJavaFile.toString().split("/");
        mLabelStr = parts[parts.length - 2]; //folder as sort type
        super.visit(cu, obj);
    }

    private void complexityASTParser(MethodDeclaration md) {

        mLOC += md.findAll(Statement.class).size();

        mBlock += md.findAll(BlockStmt.class).size();

        mBasicBlock += Common.getBasicBlocks(md).size();

        mParameter += md.findAll(Parameter.class).size();

        mLocalVariable += md.findAll(VariableDeclarator.class).size();

        mGlobalVariable += md.findAll(SimpleName.class,
                node -> (
                        node.getParentNode().isPresent()
                        && node.getParentNode().get() instanceof NameExpr
                )).size();

        mLoop += md.findAll(Statement.class,
                stmt -> (
                        stmt instanceof WhileStmt
                        || stmt instanceof DoStmt
                        || stmt instanceof ForStmt
                        || stmt instanceof ForEachStmt
                )).size();

        mJump += md.findAll(Statement.class,
                stmt -> (
                        stmt instanceof BreakStmt
                        || stmt instanceof ContinueStmt
                        || stmt instanceof ReturnStmt
                )).size();

        mDecision += md.findAll(IfStmt.class).size()
                + md.findAll(BlockStmt.class,
                    elseStmt -> (
                                elseStmt.getParentNode().isPresent()
                                && elseStmt.getParentNode().get() instanceof IfStmt
                                && ((IfStmt) elseStmt.getParentNode().get()).getElseStmt().isPresent()
                                && ((IfStmt) elseStmt.getParentNode().get()).getElseStmt().get() == elseStmt
                    )).size()
                + md.findAll(SwitchEntry.class).size();

        mCondition += md.findAll(Expression.class,
                stmt -> (
                        stmt instanceof UnaryExpr
                        || stmt instanceof BinaryExpr
                        || stmt instanceof ConditionalExpr
                )).size();

        mInstance += md.findAll(ObjectCreationExpr.class).size();

        mFunctionCall += md.findAll(MethodCallExpr.class).size();

        mErrorHandler += md.findAll(TryStmt.class).size()
                + md.findAll(CatchClause.class).size()
                + md.findAll(BlockStmt.class,
                    finallyStmt -> (
                                    finallyStmt.getParentNode().isPresent()
                                    && finallyStmt.getParentNode().get() instanceof TryStmt
                                    && ((TryStmt) finallyStmt.getParentNode().get()).getFinallyBlock().isPresent()
                                    && ((TryStmt) finallyStmt.getParentNode().get()).getFinallyBlock().get() == finallyStmt
                    )).size()
                + md.findAll(ThrowStmt.class).size();

        mThreadHandler += md.findAll(ClassOrInterfaceType.class,
                node -> (
                        node.getParentNode().isPresent()
                        && node.getParentNode().get() instanceof ObjectCreationExpr
                        && Arrays.asList("Runnable", "Thread", "Callable").contains(node.getName().toString())
                )).size()
                + md.findAll(SynchronizedStmt.class).size();

        new TreeVisitor() {
            @Override
            public void process(Node node) {
                try {
                    if (node instanceof ThisExpr) {
                        mThis++;
                    } else if (node instanceof SuperExpr) {
                        mSuper++;
                    } else if (node instanceof NullLiteralExpr) {
                        mNull++;
                    } else if (node instanceof BooleanLiteralExpr) {
                        mBoolean++;
                    } else if (node instanceof ConditionalExpr
                            && node.toString().contains("?")) {
                        mTernary++;
                    }  else if (node instanceof ReturnStmt) {
                        mReturn++;
                    }
                } catch (Exception ignored) {}
            }
        }.visitPreOrder(md);

        mASTNode += md.findAll(Node.class).size();
        mASTToken += Common.getAllTokens(md).size();
    }

    @Override
    public String toString() {
        return Common.getCleanPath(mJavaFile) + "," +
                mLabelStr + "," +
                mLOC + "," + mBlock + "," + mBasicBlock + "," +
                mParameter + "," + mLocalVariable + "," + mGlobalVariable + "," +
                mLoop + "," + mJump + "," + mDecision + "," + mCondition + "," +
                mInstance + "," + mFunctionCall + "," +
                mErrorHandler + "," + mThreadHandler + "," +
                mThis + "," + mSuper + "," + mNull + "," +
                mBoolean + "," + mTernary + "," + mReturn + "," +
                mASTNode + "," + mASTToken ;
    }
}
