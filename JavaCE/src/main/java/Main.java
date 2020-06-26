import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Common.ROOT_INPUT_PATH = args[0];
        Common.ROOT_OUTPUT_PATH = args[1];
        inspectDataset();
    }

    private static void inspectDataset() {
        ArrayList<File> javaFiles = new ArrayList<>(
                FileUtils.listFiles(
                        new File(Common.ROOT_INPUT_PATH),
                        new String[]{"java"},
                        true)
        );

        System.out.println(Common.ROOT_INPUT_PATH + " : " + javaFiles.size());
        File targetFile = new File(Common.ROOT_OUTPUT_PATH , "complexity.csv");

        javaFiles.forEach((javaFile) -> {
            try {
                String txtEmbedding = new Complexity().inspectSourceCode(javaFile);
                Common.saveEmbedding(targetFile, txtEmbedding);
                System.out.println(txtEmbedding);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
