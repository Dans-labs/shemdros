package nl.knaw.dans.shemdros.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BuildTest
{

    @Test
    public void testVersion() throws Exception
    {

        Build build = new Build();
        assertEquals("build.properties", build.getLocation());
        assertTrue(!"unknown".equals(build.getVersion()));
        assertTrue(!"unknown".equals(build.getBuildDate()));
        System.err.println(build.getVersion());
        System.err.println(build.getBuildDate());
    }

}
