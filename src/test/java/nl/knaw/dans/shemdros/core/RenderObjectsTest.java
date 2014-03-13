package nl.knaw.dans.shemdros.core;

import java.io.File;

import nl.knaw.dans.shemdros.integration.EnvironmentTest;

import org.junit.Ignore;
import org.junit.Test;

public class RenderObjectsTest extends EnvironmentTest
{

    private static jemdros.RenderObjects ro;

    @Test
    @Ignore("Cannot use jemdros.RenderObjects because of error in native library libharvest.so")
    public void testName() throws Exception
    {
        EnvWrapper envWrapper = EmdrosFactory.getEnvPool(Database.DEFAULT_DATABASE_NAME).getPooledEnvironment();
        String db_name = EmdrosFactory.getDatabase(Database.DEFAULT_DATABASE_NAME).getInitialDB();
        String jsonFilename = new File("src/test/resources/core/fetchinfo.json").getAbsolutePath();

        ro = new jemdros.RenderObjects(envWrapper.getEnv(), db_name, jsonFilename);
        ro.process(1, 350);
        String result = ro.getDocument();
        System.out.println(result);

    }

}
