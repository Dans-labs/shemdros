package nl.knaw.dans.shemdros.integration;

import nl.knaw.dans.shemdros.core.Environment;

public abstract class EnvironmentTest
{
    
    private static TestProps testprops;
    
    static
    {
        testprops = new TestProps();
    }
    
    public EnvironmentTest()
    {
        new Environment(testprops.getString("libjemdrospath"),
                testprops.getString("libharvestpath"));
    }
    
    public static TestProps getTestProps()
    {
        return testprops;
    }

}
