import azdo.junit.AzDoPipeline;
import azdo.junit.RunResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PipelineUnit {
    private static Logger logger = LoggerFactory.getLogger(PipelineUnit.class);
    private static AzDoPipeline pipeline;

    @BeforeAll
    public static void setUpClass() {
        System.out.println("setUpClass");

        // Initialize the pipeline (resource path is default)
        pipeline = new AzDoPipeline("hello-pipeline-my.properties", "./pipeline/pipeline.yml");

        // Remove the dependency to artifact 'junit-pipeline' before it is deployed to the AzDo test project
        pipeline.deleteDependencyFromTargetPom("org.pipeline", "junit-pipeline");

        /*
            Add commands to the bundle. These commands are executed for every test, so you only have to do it once.
            The pipeline may not fail because org.pipeline:junit-pipeline:jar cannot be found (it is deleted from the pom.xml).
            The pipeline may also not fails because the PipelineUnit.java cannot be compiled (because the org.pipeline:junit-pipeline:jar was removed).
            So, ignore all unit tests, because we also don't test the app anyway.
         */
        pipeline.commandBundle.overrideLiteral("clean install", "clean install -Dmaven.test.skip=true", true);
    }

    @Test
    public void testDefaultBuild() {
        logger.info("");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("Perform unittest: Test default build");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        try {
            pipeline.startPipeline();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals (RunResult.Result.succeeded, pipeline.getRunResult().result);
    }

    @Test
    public void testReleaseBuild() {
        logger.info("");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("Perform unittest: Test release build");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        // Assign the release version to the parameter
        pipeline.overrideParameterDefault("releaseVersion", "1.0.0");

        try {
            pipeline.startPipeline();
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
