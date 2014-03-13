package nl.knaw.dans.shemdros.integration;

import nl.knaw.dans.shemdros.core.EmdrosFactory;
import nl.knaw.dans.shemdros.core.Database;
import nl.knaw.dans.shemdros.core.Environment;
import nl.knaw.dans.shemdros.core.JsonFile;

public abstract class EnvironmentTest
{

    private static final String JSON_FILE = "src/test/resources/core/fetchinfo2.json";
    private static TestProps testprops;

    static
    {
        testprops = new TestProps();
        new Environment(testprops.getString("libjemdrospath"), testprops.getString("libharvestpath"));
        EmdrosFactory.instance().addDatabase(new Database(Database.DEFAULT_DATABASE_NAME));
        JsonFile defaultJsonFile = new JsonFile(JsonFile.DEFAULT, JSON_FILE);
        defaultJsonFile.setFocusElementPart("<w fm=");
        EmdrosFactory.instance().addJsonFile(defaultJsonFile);
    }

    public EnvironmentTest()
    {

    }

    public static TestProps getTestProps()
    {
        return testprops;
    }

}
