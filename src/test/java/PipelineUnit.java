// Copyright (c) Henry van Merode.
// Licensed under the MIT License.

import azdo.junit.AzDoPipeline;
import azdo.junit.RunResult;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PipelineUnit {
    private static Logger logger = LoggerFactory.getLogger(PipelineUnit.class);
    private static AzDoPipeline pipeline;
    private static final String RELEASE_ARTIFACT = "$(System.DefaultWorkingDirectory)/target/hello-pipeline-1.0.0.jar";
    private static final String PIPELINE = "./pipeline/pipeline.yml";
    private static final String STEP_RELEASE_BUILD = "Release build";
    private static final String SCRIPT_TAG_PIPELINE = "Tag the pipeline with a release version";
    private static final String SCRIPT_EXECUTE_SNAPSHOT_ARTIFACT = "Execute snapshot version on the AzDo agent";
    private static final String SCRIPT_EXECUTE_RELEASE_ARTIFACT = "Execute release version on the AzDo agent";

    @BeforeAll
    public static void setUpClass() {
        logger.info("setUpClass");

        pipeline = new AzDoPipeline("hello-pipeline-my.properties", PIPELINE);
    }

    @Test
    @Order(1)
    public void testSnapshotBuild() {
        logger.info("");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("Perform unittest: Test snapshot build");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        try {
            pipeline.startPipeline("feature");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals (RunResult.Result.succeeded, pipeline.getRunResult().result);
    }

    @Test
    @Order(2)
    public void testOnlyReleaseBuild() {
        logger.info("");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("Perform unittest: Test release build");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        try {
            // Given
            pipeline.skipStepSearchByDisplayName(SCRIPT_TAG_PIPELINE);
            pipeline.skipStepSearchByDisplayName(SCRIPT_EXECUTE_RELEASE_ARTIFACT);

            // Then
            pipeline.assertNotEqualsSearchStepByDisplayName(STEP_RELEASE_BUILD,
                    "revisionRelease",
                    "1.0.0",
                    true);
            pipeline.assertFileNotExistsSearchStepByDisplayName(STEP_RELEASE_BUILD, RELEASE_ARTIFACT, false);

            // When
            pipeline.startPipeline("master");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Assertions.assertEquals (RunResult.Result.succeeded, pipeline.getRunResult().result);
    }

    @Test
    @Order(3)
    public void testReleaseBuild() {
        logger.info("");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("Perform unittest: Test release build and execute the artifact");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        try {
            // Given
            // There is a pipeline

            // Then
            pipeline.assertNotEqualsSearchStepByDisplayName(STEP_RELEASE_BUILD,
                    "revisionRelease",
                    "1.0.0",
                    true);
            pipeline.assertFileNotExistsSearchStepByDisplayName(STEP_RELEASE_BUILD, RELEASE_ARTIFACT, false);

            // When
            pipeline.startPipeline("master");
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
