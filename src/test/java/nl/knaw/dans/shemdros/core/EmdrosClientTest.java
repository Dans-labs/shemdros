package nl.knaw.dans.shemdros.core;

import java.io.File;

import nl.knaw.dans.shemdros.integration.TestProps;
import nl.knaw.dans.shemdros.pro.XmlMqlResultProducer;

import org.junit.BeforeClass;
import org.junit.Test;

public class EmdrosClientTest
{
    
    private static TestProps testprops;
    
    @BeforeClass
    public static void beforeClass()
    {
        testprops = new TestProps();
        EnvPool.instance().setBackendKind(testprops.getInt("remote.backend.kind"));
        EnvPool.instance().setCharset(testprops.getInt("remote.charset"));
        EnvPool.instance().setHostname(testprops.getString("remote.hostname"));
        EnvPool.instance().setInitialDB(testprops.getString("remote.initialdb"));
        EnvPool.instance().setOutputKind(testprops.getInt("remote.output.kind"));
        EnvPool.instance().setPassword(testprops.getString("remote.password"));
        EnvPool.instance().setUsername(testprops.getString("remote.username"));
    }
    
    @Test
    public void connectionTest() throws Exception
    {
        EmdrosClient.instance().execute(new File("src/test/resources/queries/bh_lq01.mql"), 
                new XmlMqlResultProducer(System.err));
    }
    

}
