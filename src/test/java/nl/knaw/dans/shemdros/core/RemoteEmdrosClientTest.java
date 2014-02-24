package nl.knaw.dans.shemdros.core;

import java.io.File;

import nl.knaw.dans.shemdros.integration.EnvironmentTest;
import nl.knaw.dans.shemdros.integration.IntegrationTest;
import nl.knaw.dans.shemdros.pro.XmlMqlResultProducer;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class RemoteEmdrosClientTest extends EnvironmentTest
{
        
    @BeforeClass
    public static void beforeClass()
    {
        Database db = new Database();
        db.setBackendKind(getTestProps().getInt("remote.backend.kind"));
        db.setCharset(getTestProps().getInt("remote.charset"));
        db.setHostname(getTestProps().getString("remote.hostname"));
        db.setInitialDB(getTestProps().getString("remote.initialdb"));
        db.setOutputKind(getTestProps().getInt("remote.output.kind"));
        db.setPassword(getTestProps().getString("remote.password"));
        db.setUsername(getTestProps().getString("remote.username"));
        EnvPool.instance().setDatabase(db);
    }
    
    @Test
    public void connectionTest() throws Exception
    {
        new EmdrosClient().execute(new File("src/test/resources/queries/bh_lq01.mql"), 
                new XmlMqlResultProducer(System.err));
    }
    

}
