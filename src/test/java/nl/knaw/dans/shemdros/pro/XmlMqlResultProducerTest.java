package nl.knaw.dans.shemdros.pro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;

import nl.knaw.dans.shemdros.core.EmdrosClient;
import nl.knaw.dans.shemdros.core.ShemdrosCompileException;
import nl.knaw.dans.shemdros.integration.EnvironmentTest;
import nl.knaw.dans.shemdros.integration.IntegrationTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Category(IntegrationTest.class)
public class XmlMqlResultProducerTest extends EnvironmentTest
{

    private static final Logger logger = LoggerFactory.getLogger(XmlMqlResultProducerTest.class);

    @Test
    public void testProduction() throws Exception
    {
        File query = new File("/data/emdros/wivu/queries/bh_lq01.mql");
        OutputStream out = new FileOutputStream("target/mql-result.xml");
        XmlMqlResultProducer producer = new XmlMqlResultProducer(out);// System.err);
        producer.setIndent(2);
        new EmdrosClient().execute(query, producer);
        out.close();
    }

    @Test
    public void testAllQueries() throws Exception
    {
        File qdir = new File("src/test/resources/queries");
        File[] queries = qdir.listFiles(new FilenameFilter()
        {

            @Override
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".mql");
            }
        });
        new File("target/mql-results").mkdirs();
        for (File query : queries)
        {
            String name = query.getName().replaceAll(".mql", "");
            OutputStream out = new FileOutputStream("target/mql-results/" + name + "-result.xml");
            XmlMqlResultProducer producer = new XmlMqlResultProducer(out);
            producer.setIndent(2);
            try
            {
                new EmdrosClient().execute(query, producer);
            }
            catch (ShemdrosCompileException e)
            {
                logger.debug("Invalid query: " + query.getName() + " " + e.getMessage());
            }
            finally
            {
                out.close();
            }
        }
    }

}
