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
    private static final String DEFAULT_LIBRARY_PATH = "/usr/local/lib/emdros/libjemdros.so";

    private static EnvPool INSTANCE;

    private List<EnvWrapper> pool;

    private int outputKind = 1;
    private int charset = 3;
    private int backendKind = 4;
    private String hostname = "localhost";
    private String username = "";
    private String password = "";
    private String initialDB = "/data/emdros/wivu/s3/bhs3";

    static
    {
        System.load(DEFAULT_LIBRARY_PATH);
    }

    public static EnvPool instance()
    {
        if (INSTANCE == null)
        {
            new EnvPool(null);
        }
        return INSTANCE;
    }

    public static void reset()
    {
        INSTANCE = null;
    }

    protected EnvPool(String libraryPath)
    {
        if (libraryPath == null)
        {
            libraryPath = DEFAULT_LIBRARY_PATH;
        }

        logger.info("Loaded dynamic library at path '{}'.", libraryPath);
        pool = Collections.synchronizedList(new ArrayList<EnvWrapper>());
        INSTANCE = this;
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
                EmdrosEnv env = getEmdrosEnv(eOutputKind.swigToEnum(getOutputKind()), eCharsets.swigToEnum(getCharset()), getHostname(), getUsername(),
                        getPassword(), getInitialDB(), eBackendKind.swigToEnum(getBackendKind()));
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

    public int getOutputKind()
    {
        return outputKind;
    }

    public void setOutputKind(int outputKind)
    {
        this.outputKind = outputKind;
    }

    public int getCharset()
    {
        return charset;
    }

    public void setCharset(int charset)
    {
        this.charset = charset;
    }

    public int getBackendKind()
    {
        return backendKind;
    }

    /**
     * Backend kind Used to distinguish among backends. enum eBackendKind { kBackendNone = 0, < No
     * backend selected kPostgreSQL = 1, < PostgreSQL kMySQL = 2, < MySQL kSQLite2 = 3, < SQLite 2.X.X
     * kSQLite3 = 4 < SQLite 3.X.X };
     */
    public void setBackendKind(int backendKind)
    {
        this.backendKind = backendKind;
    }

    /**
     * @return
     */
    public String getHostname()
    {
        return hostname;
    }

    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getInitialDB()
    {
        return initialDB;
    }

    public void setInitialDB(String initialDB)
    {
        this.initialDB = initialDB;
    }

}
