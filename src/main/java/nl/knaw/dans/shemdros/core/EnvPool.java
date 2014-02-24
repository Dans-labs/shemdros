package nl.knaw.dans.shemdros.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jemdros.EmdrosEnv;
import jemdros.EmdrosException;
import jemdros.eBackendKind;
import jemdros.eCharsets;
import jemdros.eOutputKind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvPool
{

    private static final Logger logger = LoggerFactory.getLogger(EnvPool.class);

    private static EnvPool INSTANCE;
    
    private Database database;
    
    private List<EnvWrapper> pool;

    public static EnvPool instance()
    {
        if (INSTANCE == null)
        {
            new EnvPool();
        }
        return INSTANCE;
    }

    public static void reset()
    {
        INSTANCE = null;
    }

    protected EnvPool()
    {
        pool = Collections.synchronizedList(new ArrayList<EnvWrapper>());
        INSTANCE = this;
        logger.info("Initialized {}", this);
    }
    
    public void setDatabase(Database database)
    {
        this.database = database;
    }
    
    public Database getDatabase()
    {
        if (database == null)
        {
            database = new Database();
        }
        return database;
    }

    public EmdrosEnv getEmdrosEnv(eOutputKind output_kind, eCharsets charset, String hostname, String username, String password, String initialDB,
            eBackendKind backendKind) throws ShemdrosException
    {
        EmdrosEnv env;
        try
        {
            env = new EmdrosEnv(output_kind, charset, hostname, username, password, initialDB, backendKind);
        }
        catch (EmdrosException e)
        {
            throw new ShemdrosException(e.what());
        }
        return env;
    }

    public EnvWrapper getPooledEnvironment() throws ShemdrosException
    {
        EnvWrapper envWrapper = null;
        synchronized (pool)
        {
            for (EnvWrapper wrapper : pool)
            {
                if (!wrapper.isBusy())
                {
                    envWrapper = wrapper;
                    break;
                }
            }
            if (envWrapper == null)
            {
                Database db = getDatabase();
                EmdrosEnv env = getEmdrosEnv(eOutputKind.swigToEnum(db.getOutputKind()), 
                        eCharsets.swigToEnum(db.getCharset()), db.getHostname(), db.getUsername(),
                        db.getPassword(), db.getInitialDB(), eBackendKind.swigToEnum(db.getBackendKind()));
                envWrapper = new EnvWrapper(env);
                pool.add(envWrapper);
            }
            envWrapper.setBusy(true);
        }
        return envWrapper;
    }

    public void returnPooledEnvironment(EnvWrapper wrapper)
    {
        synchronized (pool)
        {
            wrapper.setBusy(false);
        }
    }

    public int size()
    {
        synchronized (pool)
        {
            return pool.size();
        }
    }



}
