package nl.knaw.dans.shemdros.pro;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.shemdros.core.EmdrosClient;
import nl.knaw.dans.shemdros.integration.EnvironmentTest;
import nl.knaw.dans.shemdros.integration.IntegrationTest;

@Category(IntegrationTest.class)
public class LevelContextProducer3Test extends EnvironmentTest
{
    
    private static final Logger logger = LoggerFactory.getLogger(LevelContextProducer3Test.class);
    
    private static File jsonFile = new File("src/test/resources/core/fetchinfo.json");
    private static String focusElementPart = "<w fm=";
    
    @Test
    public void testName() throws Exception
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
    
    public void testProduction(File queryFile) throws Exception
    {
        String name = queryFile.getName().replace('.', '_');
        OutputStream fout = new FileOutputStream("target/" + name + "-context3.xml");
        Writer out = new BufferedWriter(new OutputStreamWriter(fout));
        
        LevelContextProducer3 lc = new LevelContextProducer3(out, jsonFile, focusElementPart);
        lc.setContextLevel(0);
        
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
            out.close();
        }
    }

}
