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

        // Add commands to the bundle. These commands are executed for every test, so you only have to do it once
        // The pipeline may not fail because org.pipeline:junit-pipeline:jar cannot be found
        pipeline.commandBundle.overrideLiteral("clean install", "-fn clean install", true);
    }

    @Test
    public void test1() {
        logger.info("");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("Perform unittest: test1");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        // Manipulate the pipeline
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
