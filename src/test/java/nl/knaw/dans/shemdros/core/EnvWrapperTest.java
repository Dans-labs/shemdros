package nl.knaw.dans.shemdros.core;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;

import jemdros.EmdrosEnv;
import nl.knaw.dans.shemdros.core.EnvPool;
import nl.knaw.dans.shemdros.core.EnvWrapper;
import nl.knaw.dans.shemdros.core.ShemdrosCompileException;
import nl.knaw.dans.shemdros.core.ShemdrosException;
import nl.knaw.dans.shemdros.integration.IntegrationTest;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Category(IntegrationTest.class)
public class EnvWrapperTest
{

    private static final Logger logger = LoggerFactory.getLogger(EnvWrapperTest.class);

    @Before
    public void beforeTests()
    {
        EnvPool.reset();
    }

    @AfterClass
    public static void afterClass()
    {
        EnvPool.reset();
    }

    @Test(expected = ShemdrosException.class)
    public void testConfigurationException() throws Exception
    {
        EnvPool.instance().setBackendKind(1);
        try
        {
            EnvPool.instance().getPooledEnvironment();
        }
        catch (ShemdrosException e)
        {
            logger.debug("Expected exception: {}", e.getMessage());
            throw e;
        }
        fail("No exception was thrown");
    }

    @Test
    public void testStringQueryExecution() throws Exception
    {
        EnvWrapper wrapper = EnvPool.instance().getPooledEnvironment();
        EmdrosEnv env = wrapper.getEnv();
        assertNotNull(env);
        assertTrue(wrapper.execute(getStringQuery()));

    }

    @Test(expected = ShemdrosCompileException.class)
    public void testStringQueryCompilerError() throws Exception
    {
        EnvWrapper wrapper = EnvPool.instance().getPooledEnvironment();
        try
        {
            wrapper.execute(getWrongStringQuery());
        }
        catch (ShemdrosCompileException e)
        {
            logger.debug("Expected exception: {}", e.getMessage());
            throw e;
        }
        fail("No exception was thrown");
    }

    @Test(expected = ShemdrosException.class)
    public void testStringQueryDatabaseException() throws Exception
    {
        EnvPool.instance().setInitialDB("foo/bar");
        EnvWrapper wrapper = EnvPool.instance().getPooledEnvironment();
        try
        {
            wrapper.execute(getStringQuery());
        }
        catch (ShemdrosException e)
        {
            logger.debug("Expected exception: {}", e.getMessage());
            throw e;
        }
        fail("No exception was thrown");
    }

    @Test
    public void testFileQueryExecution() throws Exception
    {
        EnvWrapper wrapper = EnvPool.instance().getPooledEnvironment();
        EmdrosEnv env = wrapper.getEnv();
        assertNotNull(env);
        assertTrue(wrapper.execute(getFileQuery()));

    }

    @Test(expected = ShemdrosCompileException.class)
    public void testFileQueryCompilerError() throws Exception
    {
        EnvWrapper wrapper = EnvPool.instance().getPooledEnvironment();
        try
        {
            wrapper.execute(getWrongFileQuery());
        }
        catch (ShemdrosCompileException e)
        {
            logger.debug("Expected exception: {}", e.getMessage());
            throw e;
        }
        fail("No exception was thrown");
    }

    @Test(expected = ShemdrosException.class)
    public void testFileQueryDatabaseException() throws Exception
    {
        EnvPool.instance().setInitialDB("foo/bar");
        EnvWrapper wrapper = EnvPool.instance().getPooledEnvironment();
        try
        {
            wrapper.execute(getFileQuery());
        }
        catch (ShemdrosException e)
        {
            logger.debug("Expected exception: {}", e.getMessage());
            throw e;
        }
        fail("No exception was thrown");
    }

    private String getStringQuery()
    {
        String query = "[Clause\n" + " [Phrase phrase_function = PreO OR phrase_function = PtcO\n"
                + "   [Word FOCUS part_of_speech = verb AND lexeme = \"FJM[\"]\n" + " ]\n" + " ..\n"
                + " [Phrase FOCUS phrase_function = Objc OR phrase_function = IrpO]\n" + "]\n" + "GO";
        query = "SELECT ALL OBJECTS\n" + "WHERE\n" + query;
        return query;
    }

    private String getWrongStringQuery()
    {
        String query = "[Clause\n" + " [Phrase phrase_function = PreO OR phrase_function = PtcO\n"
                + "   [Word FOCUS part_of_speech = verb AND lexeme = \"FJM[\"]\n" + " ]\n" + " ..\n"
                + " [Phrase FOCUS phrase_function = Objc OR phrase_function = IrpO]\n" + "]\n" + "GO";
        query = "SELECT OBJECTS\n" + "WHERE ALL\n" + query;
        return query;
    }

    public static File getFileQuery()
    {
        return new File("src/test/resources/core/query.mql");
    }

    private File getWrongFileQuery()
    {
        return new File("src/test/resources/core/wrong-query.mql");
    }
}
