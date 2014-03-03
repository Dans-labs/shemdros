package nl.knaw.dans.shemdros.pro;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import nl.knaw.dans.shemdros.core.EmdrosClient;
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
    
    private static File jsonFile = new File("src/test/resources/core/fetchinfo.json");
    private static String focusElementPart = "<w fm=";
    
    @Test
    public void testName() throws Exception
    {
        File queryFile = new File("src/test/resources/queries/bh_lq01.mql");
        
        String name = queryFile.getName().replace('.', '_');
        OutputStream fout = new FileOutputStream("target/" + name + "-markscontext.xml");
        Writer out = new BufferedWriter(new OutputStreamWriter(fout));
        
        MarksContextProducer mc = new MarksContextProducer(out, jsonFile, focusElementPart);
        //mc.setContextMark("");
        
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
            out.close();
        }
    }

}
