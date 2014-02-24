package nl.knaw.dans.shemdros.pro;

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

@Category(IntegrationTest.class)
public class LevelContextProducerTest  extends EnvironmentTest
{
    
    @Test
    public void testProduction() throws Exception
    {
        File query = new File("/data/emdros/wivu/queries/bh_lq01.mql");
        OutputStream fout = new FileOutputStream("target/level-context.xml");
        Writer out = new BufferedWriter(new OutputStreamWriter(fout));
        
        LevelContextProducer lcp = new LevelContextProducer(out, new File("src/test/resources/core/fetchinfo.json"));
        new EmdrosClient().execute(query, lcp);
        out.close();
    }

}
