package nl.knaw.dans.shemdros.pro;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
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
public class LevelContextProducerTest extends EnvironmentTest
{

    private static final Logger logger = LoggerFactory.getLogger(LevelContextProducerTest.class);

    @Test
    public void testAllQueries() throws Exception
    {
        File queryDir = new File("src/test/resources/queries");
        for (File queryFile : queryDir.listFiles(new FilenameFilter()
        {

            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".mql");
            }
        }))
        {
            testProduction(queryFile);
        }
    }

    @Test
    public void testSingleQuery() throws Exception
    {
        File queryFile = new File("src/test/resources/queries/bh_lq01.mql");
        testProduction(queryFile);
    }

    public void testProduction(File queryFile) throws Exception
    {
        String name = queryFile.getName().replace('.', '_');
        OutputStream output = new FileOutputStream("target/" + name + "-levelcontext.xml");

        LevelContextProducer lc = new LevelContextProducer(Database.DEFAULT, JsonFile.DEFAULT);
        lc.setOffsetFirst(2);
        lc.setOffsetLast(4);
        lc.setContextLevel(0);
        lc.setOutputStream(output);

        try
        {
            new EmdrosClient().execute(queryFile, lc);
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
