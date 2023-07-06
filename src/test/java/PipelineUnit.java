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
    }

    @Test
    @Order(1)
    public void testSnapshotBuild() {
        logger.info("");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("Perform unittest: Test snapshot build");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        // Given there is a pipeline
        pipeline = new AzDoPipeline("hello-pipeline-my.properties", "./pipeline/pipeline-snapshot.yml");

        try {
            // When the pipeline starts
            pipeline.startPipeline("feature");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        // Then the result must be 'succeeded'
        Assertions.assertEquals (RunResult.Result.succeeded, pipeline.getRunResult().result);
    }

    @Test
    @Order(2)
    public void testReleaseBuild() {
        logger.info("");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("Perform unittest: Test release build");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        // Given there is a pipeline
        pipeline = new AzDoPipeline("hello-pipeline-my.properties", "./pipeline/pipeline-release.yml");

        // And a validation to determine whether the 'releaseVersion' variable is empty
        pipeline.assertEmptySearchStepByDisplayName("Release build", "releaseVersion");

        // And a validation to determine whether the .jar file is build and exists
        pipeline.assertFileNotExistsSearchStepByDisplayName("Release build",
                "$(System.DefaultWorkingDirectory)/target/hello-pipeline-$(releaseVersion).jar",
                false);

        try {
            // When the pipeline starts
            pipeline.startPipeline("master");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        // Then the result must be 'succeeded'
        Assertions.assertEquals (RunResult.Result.succeeded, pipeline.getRunResult().result);
    }

    @AfterAll
    public static void tearDown() {
        logger.info("tearDown");
    }
}
