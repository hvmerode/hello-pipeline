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
        logger.info("Perform unit test 1: Test snapshot build");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        try {
            // Given there is a pipeline with a snapshot build
            pipeline = new AzDoPipeline("hello-pipeline-my.properties", "./pipeline/pipeline-snapshot.yml");

            // When the pipeline starts from the feature branch
            pipeline.startPipeline("feature");
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }

        // Then the result must be 'succeeded'
        Assertions.assertEquals (RunResult.Result.succeeded, pipeline.getRunResult().result);
    }

    @Test
    @Order(2)
    public void testReleaseBuild() {
        logger.info("");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("Perform unit test 2: Test release build");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        try {
            // Given there is a pipeline with a release build
            pipeline = new AzDoPipeline("hello-pipeline-my.properties", "./pipeline/pipeline-release.yml");

            // And the 'releaseVersion' variable is not empty before the release build is executed
            // And the .jar file has been built and exists after the release build has been executed
            pipeline.assertVariableNotEmptySearchStepByDisplayName("Release build", "releaseVersion", true)
                    .assertFileExistsSearchStepByDisplayName("Release build",
                            "$(System.DefaultWorkingDirectory)/target/hello-pipeline-$(releaseVersion).jar",
                            false);

            // When the pipeline starts from the master branch
            pipeline.startPipeline("master");
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
        // Then the result must be 'succeeded'
        Assertions.assertEquals (RunResult.Result.succeeded, pipeline.getRunResult().result);
    }

    @Test
    @Order(3)
    public void testReleaseAndDeploy() {
        logger.info("");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("Perform unit test 3: Test release build and deployment");
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        try {
            // Given there is a pipeline with a release build and a deployment to a target
            pipeline = new AzDoPipeline("hello-pipeline-my.properties", "./pipeline/pipeline-release-and-deploy.yml");

            // And the Azure DevOps pipeline run is not tagged with the release version
            // And the artifact does not run on the Azure DevOps agent
            // And a curl command to deploy the artifact is executed
            String htmlOutput = "<html>\n  <head>\n    <title>\n      Mock deployment\n    </title>\n  </head>\n</html>\n";
            pipeline.skipStepSearchByDisplayName("Tag the pipeline with a release version")
                    .skipStepSearchByDisplayName("Execute app on the AzDo agent")
                    .mockBashCommandSearchStepByDisplayName("Deploy to target",
                            "curl",
                            htmlOutput);

            // When the pipeline starts from the master branch
            pipeline.startPipeline("master");
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
        // Then the result must be 'succeeded'
        Assertions.assertEquals (RunResult.Result.succeeded, pipeline.getRunResult().result);
    }

    @AfterAll
    public static void tearDown() {
        logger.info("tearDown");
    }
}
