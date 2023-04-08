import azdo.hook.DeleteJUnitPipelineDependency;
import azdo.hook.DeleteTargetFile;
import azdo.hook.Hook;
import azdo.junit.AzDoPipeline;
import azdo.junit.RunResult;
import azdo.junit.TestProperties;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PipelineUnit {
    private static Logger logger = LoggerFactory.getLogger(PipelineUnit.class);
    private static AzDoPipeline pipeline;
    private static List<Hook> hookList;

    @BeforeAll
    public static void setUpClass() {
        System.out.println("setUpClass");

        /* Initialize the pipeline with the properties file ('hello-pipeline-my.properties', which must be present in
           src/main/resources), and the primary pipeline file ('./pipeline/pipeline.yml').
           Note, that the file 'hello-pipeline-my.properties' is not present in this repository. In this repo,
           only the file 'hello-pipeline.properties' is present, which must be adjusted to your specific
           situation (e.g. configuring source- and target path, personal access token (target.repository.password),
           project identifier (project.id), ...etc.)
         */
        pipeline = new AzDoPipeline("hello-pipeline-my.properties", "./pipeline/pipeline.yml");

        // Create a list with hooks
        // These hooks take care that stuff causing errors, is removed from the target directory. It removes:
        // - the dependency from pom.xml
        // - the unit tests of the pipeline
        hookList = new ArrayList<>();
        TestProperties properties = pipeline.getProperties();
        hookList.add(new DeleteJUnitPipelineDependency(properties.getTargetPath() + "/" + "pom.xml",
                "org.pipeline",
                "junit-pipeline"));
        String fullQualifiedFileName = properties.getTargetPath() + "/" + "src/test/java/PipelineUnit.java";
        hookList.add(new DeleteTargetFile(fullQualifiedFileName));
    }

    @Test
    @Order(1)
    public void testDefaultBuild() {
        logger.info("");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("Perform unittest: Test default build");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        try {
            // Start the pipeline
            pipeline.startPipeline(hookList);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals (RunResult.Result.succeeded, pipeline.getRunResult().result);
    }

    @Test
    @Order(2)
    public void testReleaseBuild() {
        logger.info("");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("Perform unittest: Test release build");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        // Assign the release version to the parameter
        pipeline.overrideParameterDefault("releaseVersion", "1.0.0");

        try {
            pipeline.startPipeline("release", hookList);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals (RunResult.Result.succeeded, pipeline.getRunResult().result);
    }

    @AfterAll
    public static void tearDown() {
        System.out.println("\ntearDown");
    }
}
