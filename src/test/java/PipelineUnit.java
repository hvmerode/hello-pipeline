// Copyright (c) Henry van Merode.
// Licensed under the MIT License.

import azdo.junit.AzDoPipeline;
import azdo.junit.RunResult;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PipelineUnit {
    private static Logger logger = LoggerFactory.getLogger(PipelineUnit.class);
    private static AzDoPipeline pipeline;

    @BeforeAll
    public static void setUpClass() {
        logger.info("setUpClass");

        /* Initialize the pipeline with the properties file ('hello-pipeline-my.properties', which must be present in
           src/main/resources), and the primary pipeline file ('./pipeline/pipeline.yml').
           Note, that the file 'hello-pipeline-my.properties' is not present in this repository. In this repo,
           only the file 'hello-pipeline.properties' is present, which must be adjusted to your specific
           situation (e.g. configuring source- and target path, personal access token (target.repository.password),
           project identifier (project.id), ...etc.)
           For more information: https://github.com/hvmerode/junit-pipeline
         */
        pipeline = new AzDoPipeline("hello-pipeline-my.properties", "./pipeline/pipeline.yml");
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
            pipeline.startPipeline("master");
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
            pipeline.startPipeline("release");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals (RunResult.Result.succeeded, pipeline.getRunResult().result);
    }

    @AfterAll
    public static void tearDown() {
        logger.info("tearDown");
    }
}
