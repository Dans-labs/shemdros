package nl.knaw.dans.shemdros.core;

// import java.util.Objects;

/**
 * Denomination of the properties of an Emdros database connection.
 */
public class Database
{

    /**
     * Standard name for a default database. <br/>
     * {@value}
     */
    public static final String DEFAULT_DATABASE_NAME = "default";

    /**
     * Default 'kOKConsole'.
     */
    public static final int DEFAULT_OUTPUTKIND = 1;

    /**
     * Default 'kCSUTF8'.
     */
    public static final int DEFAULT_CHARSET = 3;

    /**
     * Default 'kSQLite3'.
     */
    public static final int DEFAULT_BACKENDKIND = 4;

    /**
     * Default value for hostname. <br/>
     * {@value}
     */
    public static final String DEFAULT_HOSTNAME = "localhost";

    /**
     * Default value for username. <br/>
     * {@value}
     */
    public static final String DEFAULT_USERNAME = "";

    /**
     * Default value for password. <br/>
     * {@value}
     */
    public static final String DEFAULT_PASSWORD = "";

    /**
     * Default value for initialDB. <br/>
     * {@value}
     */
    public static final String DEFAULT_INITIAL_DB = "/data/emdros/wivu/s3/bhs3";

    /**
     * Default value for maximum pool size in EnvPools for this database. <br/>
     * {@value}
     */
    public static final int DEFAULT_MAX_POOLSIZE = 5;

    private final String name;

    private int outputKind = DEFAULT_OUTPUTKIND;
    private int charset = DEFAULT_CHARSET;
    private int backendKind = DEFAULT_BACKENDKIND;
    private String hostname = DEFAULT_HOSTNAME;
    private String username = DEFAULT_USERNAME;
    private String password = DEFAULT_PASSWORD;
    private String initialDB = DEFAULT_INITIAL_DB;
    private int maxPoolSize = DEFAULT_MAX_POOLSIZE;

    /**
     * Create a new database denomination that shall be known under the given name.
     * 
     * @param name
     *        a unique name for the database within the scope of the application.
     */
    public Database(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public int getMaxPoolSize()
    {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize)
    {
        this.maxPoolSize = maxPoolSize;
    }

    public int getOutputKind()
    {
        return outputKind;
    }

    /**
     * Set the outputKind for the outputStream; we do not use the outputStream.
     * 
     * <pre>
     * typedef enum {
     *   kOKXML,
     *   kOKConsole
     * } eOutputKind;
     * </pre>
     * 
     * @param outputKind
     *        outputKind for the outputStream.
     */
    public void setOutputKind(int outputKind)
    {
        this.outputKind = outputKind;
    }

    public int getCharset()
    {
        return charset;
    }

    /**
     * Set the character encoding for the outputStream; we do not use the outputStream.
     * 
     * <pre>
     * typedef enum {
     *   kCSASCII,
     *   kCSISO_8859_1,
     *   kCSISO_8859_8,
     *   kCSUTF8
     * } eCharsets;
     * </pre>
     * 
     * @param charset
     *        the character encoding for the outputStream
     */
    public void setCharset(int charset)
    {
        this.charset = charset;
    }

    public int getBackendKind()
    {
        return backendKind;
    }

    /**
     * <pre>
     * enum eBackendKind {
     *  kBackendNone = 0,/**< No backend selected 
     *  kPostgreSQL = 1, /**< PostgreSQL 
     *  kMySQL = 2,      /**< MySQL 
     *  kSQLite2 = 3,    /**< SQLite 2.X.X 
     *  kSQLite3 = 4     /**< SQLite 3.X.X
     * };
     * 
     * <pre>
     */
    public void setBackendKind(int backendKind)
    {
        this.backendKind = backendKind;
    }

    public String getBackendName()
    {
        String backendName;
        switch (backendKind)
        {
        case 0:
            backendName = "None";
            break;
        case 1:
            backendName = "PostgreSQL";
            break;
        case 2:
            backendName = "MySQL";
            break;
        case 3:
            backendName = "SQLite2";
            break;
        case 4:
            backendName = "SQLite3";
            break;
        default:
            backendName = "Unknown";
            break;
        }
        return backendName;
    }

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

    @Override
    public Database clone()
    {
        Database clone = new Database(name);
        clone.backendKind = backendKind;
        clone.charset = charset;
        clone.hostname = hostname;
        clone.initialDB = initialDB;
        clone.outputKind = outputKind;
        clone.password = password;
        clone.username = username;
        clone.maxPoolSize = maxPoolSize;
        return clone;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Database)
        {
            Database other = (Database) obj;
            boolean eq = true;
            if (this.name == null)
            {
                eq &= other.name == null;
            }
            else
            {
                eq &= this.name.equals(other.name);
            }
            eq &= this.backendKind == other.backendKind;
            eq &= this.charset == other.charset;
            eq &= this.outputKind == other.outputKind;
            eq &= this.maxPoolSize == other.maxPoolSize;
            if (this.hostname == null)
            {
                eq &= other.hostname == null;
            }
            else
            {
                eq &= this.hostname.equals(other.hostname);
            }
            if (this.password == null)
            {
                eq &= other.password == null;
            }
            else
            {
                eq &= this.password.equals(other.password);
            }
            if (this.initialDB == null)
            {
                eq &= other.initialDB == null;
            }
            else
            {
                eq &= this.initialDB.equals(other.initialDB);
            }
            if (this.username == null)
            {
                eq &= other.username == null;
            }
            else
            {
                eq &= this.username.equals(other.username);
            }
            return eq;
            // return Objects.equals(name, other.name) //
            // && backendKind == other.backendKind //
            // && charset == other.charset //
            // && Objects.equals(hostname, other.hostname) //
            // && Objects.equals(initialDB, other.initialDB) //
            // && outputKind == other.outputKind //
            // && Objects.equals(password, other.password) //
            // && Objects.equals(username, other.username) //
            // && Objects.equals(maxPoolSize, other.maxPoolSize);
        }
        return false;
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append(this.getClass().getName()) //
                .append(" [").append("name=").append(name)//
                .append(", hostname=").append(hostname)//
                .append(", initialDB=").append(initialDB)//
                .append(", backendname=").append(getBackendName())//
                .append(", maxPoolSize=").append(maxPoolSize)//
                .append("]").toString();
    }

}
