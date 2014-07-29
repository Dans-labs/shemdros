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

    public static EmdrosEnv getEmdrosEnv(eOutputKind output_kind, eCharsets charset, String hostname, String username, String password, String initialDB,
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

    private final Database database;

    private List<EnvWrapper> pool;

    protected EnvPool(Database database)
    {
        this.database = database;
        pool = Collections.synchronizedList(new ArrayList<EnvWrapper>());
        logger.info("Initialized {} for database '{}'.", this, database.getName());
    }

    public Database getDatabase()
    {
        return database.clone();
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
                EmdrosEnv env = getEmdrosEnv(eOutputKind.swigToEnum(database.getOutputKind()),//
                        eCharsets.swigToEnum(database.getCharset()),//
                        database.getHostname(), database.getUsername(), database.getPassword(),//
                        database.getInitialDB(), //
                        eBackendKind.swigToEnum(database.getBackendKind()));
                envWrapper = new EnvWrapper(env, database.getName());
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
            if (wrapper.isObsolete() || !wrapper.getEnv().connectionOk())
            {
                pool.remove(wrapper);
            }
            else
            {
                wrapper.setBusy(false);
                while (pool.size() > database.getMaxPoolSize())
                {
                    pool.remove(0);
                }
            }
        }
    }

    public int size()
    {
        synchronized (pool)
        {
            return pool.size();
        }
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append(this.getClass().getName()) //
                .append(" [").append("database=").append(database.toString())//
                .append(", poolsize=").append(size())//

                .append("]").toString();
    }

}
