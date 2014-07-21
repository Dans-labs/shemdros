package nl.knaw.dans.shemdros.core;

import java.io.File;

import jemdros.EmdrosEnv;
import jemdros.EmdrosException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvWrapper
{

    private static final Logger logger = LoggerFactory.getLogger(EnvWrapper.class);

    private final EmdrosEnv env;
    private final String databaseName;
    private boolean busy;
    private boolean obsolete;

    public EnvWrapper(EmdrosEnv env, String databaseName)
    {
        this.env = env;
        this.databaseName = databaseName;
    }

    public String getDatabaseName()
    {
        return databaseName;
    }

    protected boolean isBusy()
    {
        return busy;
    }

    protected void setBusy(boolean busy)
    {
        this.busy = busy;
        env.clearErrors();
        env.clean();
    }

    public boolean isObsolete()
    {
        return obsolete;
    }

    public void setObsolete(boolean obsolete)
    {
        this.obsolete = obsolete;
    }

    public EmdrosEnv getEnv()
    {
        if (!busy)
        {
            throw new IllegalStateException("Wrapper not active. This is a programming error.");
        }
        return env;
    }

    public boolean execute(String query) throws ShemdrosException
    {
        return execute(query, false, false);
    }

    public boolean execute(String query, boolean printResult, boolean printErrors) throws ShemdrosException
    {
        if (StringUtils.isBlank(query))
        {
            throw new ShemdrosCompileException("The query cannot be blank.");
        }
        long start = System.currentTimeMillis();
        boolean[] bCompilerResult = new boolean[1];
        boolean bDBResult = false;
        try
        {
            bDBResult = env.executeString(query, bCompilerResult, printResult, printErrors);
        }
        catch (EmdrosException e)
        {
            throw new ShemdrosException(e.getClass().getSimpleName() + ": " + e.what() + "\n" + env.getDBError());
        }
        logger.debug("Excecuted query against database '{}'. {}, {}, in {} ms.", databaseName, bCompilerResult, bDBResult, System.currentTimeMillis() - start);
        if (!bCompilerResult[0])
        {
            throw new ShemdrosCompileException(env.getCompilerError());
        }
        if (!bDBResult)
        {
            throw new ShemdrosException(env.getDBError());
        }
        return bCompilerResult[0] && bDBResult;
    }

    public boolean execute(File query) throws ShemdrosException
    {
        return execute(query, false, false);
    }

    public boolean execute(File query, boolean printResult, boolean printErrors) throws ShemdrosException
    {
        String filename = query.getAbsolutePath();
        logger.debug("Executing query at '{}'.", filename);
        boolean[] bCompilerResult = new boolean[1];
        boolean bDBResult = false;
        try
        {
            bDBResult = env.executeFile(filename, bCompilerResult, printResult, printErrors);
        }
        catch (EmdrosException e)
        {
            throw new ShemdrosException(e.getClass().getSimpleName() + ": " + e.what() + "\n" + env.getDBError());
        }
        logger.debug("Excecuted query against database '{}'. {}, {}", databaseName, bCompilerResult, bDBResult);
        if (!bCompilerResult[0])
        {
            throw new ShemdrosCompileException(env.getCompilerError());
        }
        if (!bDBResult)
        {
            throw new ShemdrosException("Emdros DBError: " + env.getDBError());
        }
        return bCompilerResult[0] && bDBResult;
    }

}
