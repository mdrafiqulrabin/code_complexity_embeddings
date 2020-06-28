import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class Main {
    /*
        args[0] - root input directory
        args[1] - root output directory
     */
    public static void main(String[] args) {
        if (args.length == 2) {
            Common.ROOT_INPUT_PATH = args[0];
            Common.ROOT_OUTPUT_PATH = args[1];
            inspectDataset();
        } else {
            System.out.println("Arguments Missing: input/output directory");
        }
    }

    private static void inspectDataset() {
        ArrayList<File> javaFiles = new ArrayList<>(
                FileUtils.listFiles(
                        new File(Common.ROOT_INPUT_PATH),
                        new String[]{"java"},
                        true)
        );

        System.out.println(Common.ROOT_INPUT_PATH + " : " + javaFiles.size());
        File targetFile = new File(Common.ROOT_OUTPUT_PATH , "java_complexity_metrics.csv");

        javaFiles.forEach((javaFile) -> {
            try {
                String txtEmbedding = new Complexity().inspectSourceCode(javaFile);
                Common.saveEmbeddings(targetFile, txtEmbedding);
                System.out.println(txtEmbedding);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
