// Copyright (c) Henry van Merode.
// Licensed under the MIT License.

import azdo.hook.DeleteJUnitPipelineDependency;
import azdo.hook.DeleteTargetFile;
import azdo.hook.Hook;
import azdo.junit.AzDoPipeline;
import azdo.junit.RunResult;
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
            // Given there is a pipeline
            pipeline = new AzDoPipeline("hello-pipeline-my.properties", "./pipeline/pipeline-snapshot.yml");

            // When the pipeline starts
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
            // Given there is a pipeline
            pipeline = new AzDoPipeline("hello-pipeline-my.properties", "./pipeline/pipeline-release.yml");

            // And the 'releaseVersion' variable is not empty before the release build step
            // And the .jar file has been built and exists after the release build step
            pipeline.assertEmptySearchStepByDisplayName("Release build", "releaseVersion", true)
                    .assertFileNotExistsSearchStepByDisplayName("Release build",
                            "$(System.DefaultWorkingDirectory)/target/hello-pipeline-$(releaseVersion).jar",
                            false);

            // When the pipeline starts
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
            // Given there is a pipeline
            pipeline = new AzDoPipeline("hello-pipeline-my.properties", "./pipeline/pipeline-release-and-deploy.yml");

            // TESTTESTTESTTESTTESTTESTTESTTESTTESTTEST
            DeleteJUnitPipelineDependency hook1 = new DeleteJUnitPipelineDependency ("C:\\Users\\Henry\\Documents\\Github\\hello-pipeline-test\\pom.xml", "io.github.hvmerode", "junit-pipeline");
            DeleteTargetFile hook2 = new DeleteTargetFile("C:\\Users\\Henry\\Documents\\Github\\hello-pipeline-test\\src\\test\\java\\PipelineUnit.java");
            List<Hook> hooks = new ArrayList<>();
            hooks.add(hook1);
            hooks.add(hook2);
            // TESTTESTTESTTESTTESTTESTTESTTESTTESTTEST

            // And the pipeline is not tagged with the release version
            // And the artifact does not run on the Azure DevOps agent
            // And a curl command is executed, deploying the artifact
            String htmlOutput = "<html>\n  <head>\n    <title>\n      Mock deployment\n    </title>\n  </head>\n</html>\n";
            pipeline.skipStepSearchByDisplayName("Tag the pipeline with a release version")
                    .skipStepSearchByDisplayName("Execute app on the AzDo agent")
                    .mockBashCommandSearchStepByDisplayName("Deploy to target",
                            "curl",
                            htmlOutput);

            // When the pipeline starts
            pipeline.startPipeline("master", hooks);
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
