package nl.knaw.dans.shemdros.core;

import java.io.File;

import nl.knaw.dans.shemdros.integration.EnvironmentTest;
import nl.knaw.dans.shemdros.integration.IntegrationTest;
import nl.knaw.dans.shemdros.pro.XmlMqlResultProducer;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class RemoteEmdrosClientTest extends EnvironmentTest
{

    @BeforeClass
    public static void beforeClass()
    {
        Database db = new Database("remote");
        db.setBackendKind(getTestProps().getInt("remote.backend.kind"));
        db.setCharset(getTestProps().getInt("remote.charset"));
        db.setHostname(getTestProps().getString("remote.hostname"));
        db.setInitialDB(getTestProps().getString("remote.initialdb"));
        db.setOutputKind(getTestProps().getInt("remote.output.kind"));
        db.setPassword(getTestProps().getString("remote.password"));
        db.setUsername(getTestProps().getString("remote.username"));
        EmdrosFactory.instance().addDatabase(db);
    }

    @Test
    @Ignore("Cannot connect to remote db")
    public void connectionTest() throws Exception
    {
        XmlMqlResultProducer producer = new XmlMqlResultProducer(System.err);
        producer.setIndent(3);
        try
        {
            new EmdrosClient().execute("remote", new File("src/test/resources/queries/bh_lq01.mql"), producer);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
