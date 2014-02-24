package nl.knaw.dans.shemdros.core;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;

import jemdros.EmdrosEnv;
import jemdros.Sheaf;
import nl.knaw.dans.shemdros.integration.EnvironmentTest;
import nl.knaw.dans.shemdros.integration.IntegrationTest;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Category(IntegrationTest.class)
public class EnvWrapperTest extends EnvironmentTest
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
        Database db = new Database();
        db.setBackendKind(1);
        EnvPool.instance().setDatabase(db);
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
        Database db = new Database();
        db.setInitialDB("foo/bar");
        EnvPool.instance().setDatabase(db);
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
        Database db = new Database();
        db.setInitialDB("foo/bar");
        EnvPool.instance().setDatabase(db);
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
    
    @Test
    public void testSheafextraction() throws Exception
    {
        EnvWrapper wrapper = EnvPool.instance().getPooledEnvironment();
        EmdrosEnv env = wrapper.getEnv();
        wrapper.execute(getFileQuery());
        
        Sheaf sheaf1 = env.takeOverSheaf();
        Sheaf sheaf2 = env.takeOverSheaf();
        Sheaf sheaf3 = env.getSheaf();
        
        assertNotNull(sheaf1);
        assertNull(sheaf2);
        assertNull(sheaf3); 
    }
    
    @Test
    public void testSheafextraction2() throws Exception
    {
        EnvWrapper wrapper = EnvPool.instance().getPooledEnvironment();
        EmdrosEnv env = wrapper.getEnv();
        wrapper.execute(getFileQuery());
        
        Sheaf sheaf1 = env.getSheaf();
        Sheaf sheaf1a = env.getSheaf();
        Sheaf sheaf2 = env.takeOverSheaf();
        Sheaf sheaf3 = env.getSheaf();
        
        assertNotSame(sheaf1, sheaf1a);
        assertNotSame(sheaf1, sheaf2);
        assertNotNull(sheaf1);
        assertNotNull(sheaf1a);
        assertNotNull(sheaf2);
        assertNull(sheaf3); 
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
