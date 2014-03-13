package nl.knaw.dans.shemdros.pro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import nl.knaw.dans.shemdros.core.Database;
import nl.knaw.dans.shemdros.core.EmdrosClient;
import nl.knaw.dans.shemdros.core.JsonFile;
import nl.knaw.dans.shemdros.integration.EnvironmentTest;
import nl.knaw.dans.shemdros.integration.IntegrationTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Category(IntegrationTest.class)
public class MarksContextProducerTest extends EnvironmentTest
{

    private static final Logger logger = LoggerFactory.getLogger(MarksContextProducerTest.class);

    @Test
    public void testName() throws Exception
    {
        File queryFile = new File("src/test/resources/queries/bh_lq01.mql");

        String name = queryFile.getName().replace('.', '_');
        OutputStream output = new FileOutputStream("target/" + name + "-markscontext.xml");

        MarksContextProducer mc = new MarksContextProducer(Database.DEFAULT_DATABASE_NAME, JsonFile.DEFAULT);
        // mc.setContextMark("");
        mc.setOffsetFirst(2);
        mc.setOffsetLast(4);
        mc.setOutputStream(output);

        try
        {
            new EmdrosClient().execute(queryFile, mc);
        }
        catch (Exception e)
        {
            logger.debug("Caught Exception: " + e.getMessage());
        }
        finally
        {
            output.close();
        }
    }

}
