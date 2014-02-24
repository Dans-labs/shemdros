package nl.knaw.dans.shemdros.pro;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import nl.knaw.dans.shemdros.core.EmdrosClient;
import nl.knaw.dans.shemdros.integration.EnvironmentTest;

public class ExProducerTest extends EnvironmentTest
{
    
    @Test
    public void testProduction() throws Exception
    {
        File query = new File("/data/emdros/wivu/queries/bh_lq99.mql");
        new EmdrosClient().execute(query, new ExProducer());
    }

}
