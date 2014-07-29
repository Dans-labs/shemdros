package nl.knaw.dans.shemdros.pro;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;

import nl.knaw.dans.shemdros.core.EmdrosClient;
import nl.knaw.dans.shemdros.integration.EnvironmentTest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvMonadSetProducerTest extends EnvironmentTest
{

    private static final Logger logger = LoggerFactory.getLogger(CsvMonadSetProducerTest.class);

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
    public void testNoYieldQuery() throws Exception
    {
        File queryFile = new File("src/test/resources/queries/no_yield_query.mql");
        testProduction(queryFile);
    }

    public boolean testProduction(File queryFile) throws Exception
    {
        boolean executed = false;
        String name = queryFile.getName().replace('.', '_');
        OutputStream output = new FileOutputStream("target/" + name + "-monadset.csv");

        CsvMonadSetProducer cmp = new CsvMonadSetProducer();
        cmp.setOutputStream(output);

        try
        {
            new EmdrosClient().execute(queryFile, cmp);
            executed = true;
        }
        catch (Exception e)
        {
            logger.debug("Caught Exception: " + e + "\n\n" + e.getMessage());
        }
        finally
        {
            output.close();
        }
        return executed;
    }

}
