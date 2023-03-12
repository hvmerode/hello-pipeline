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

        /* Initialize the pipeline with the properties file ('hello-pipeline-my.properties', which must be present in
           src/main/resources), and the primary pipeline file ('./pipeline/pipeline.yml').
           Note, that the file 'hello-pipeline-my.properties' is not present in this repository. In this repo,
           only the file 'hello-pipeline.properties' is present, which must be adjusted to your specific
           situation (e.g. configuring source- and target path, personal access token (target.repository.password),
           project identifier (project.id), ...etc.)
         */
        pipeline = new AzDoPipeline("hello-pipeline-my.properties", "./pipeline/pipeline.yml");

        /* Remove the dependency to the 'junit-pipeline' jar, before it is deployed to the AzDo test project.
           Normally, this dependency is stored in a repository or in Azure DevOps artifacts. When building the Maven
           artifact, the location of this dependency is configured and the Maven build will not fail.
           In this test application, the dependency is removed from the pom.xml to prevent build errors (cannot find library).
         */
        pipeline.deleteDependencyFromTargetPom("org.pipeline", "junit-pipeline");

        /* In addition to that, the PipelineUnit.java is also removed when pushed to the Azure DevOps test project.
           The PipelineUnit.java is only executed locally, in your IDE (for example, Intellij), and is not executed as unit test
           in Azure DevOps.
           Reason to remove it completely, is to prevent a compiler error because the 'junit-pipeline' jar cannot be found.
           Unfortunately, Maven isn't so flexible to provide options to exclude a file from compilation; I tried all kinds
           of exclude variations, but only the option "-Dmaven.test.skip=true" seemed to work.

           The pom.xml is configured in such a way that it excludes the PipelineUnit test in normal usage of this repository
           (that is, the 'junit-pipeline' jar can be found in Azure DevOps, the PipelineUnit.java can be compiled, and after compilation
           excluded from unit test execution).
         */
        try {
            pipeline.deleteTargetFile("src/test/java/PipelineUnit.java");
        }
        catch (Exception e) {}
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
