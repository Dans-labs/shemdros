package nl.knaw.dans.shemdros.core;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class CmdRenderObjectsCommandTest
{

    @Test
    public void testBuildContextPartCommand() throws Exception
    {
        Database db = new Database(Database.DEFAULT_DATABASE_NAME);
        JsonFile jf = new JsonFile(JsonFile.DEFAULT, "src/test/resources/core/fetchinfo2.json");
        CmdRenderObjects cro = new CmdRenderObjects(db, jf);

        // System.err.println(cro.buildContextArguments(0, 10).toString());

        // renderobjects -h localhost -u -p -b SQLite3 --fm 1 --lm 100 -s base /opt/she/fetchinfo2.json
        // /data/emdros/wivu/s3/bhs3
        assertTrue(cro.buildContextArguments(0, 100).toString().startsWith("-h "));

    }

}
