package nl.knaw.dans.shemdros.core;

import java.util.Objects;

public class Database
{

    public static final String DEFAULT = "default";

    private final String name;
    private int outputKind = 1;
    private int charset = 3;
    private int backendKind = 4;
    private String hostname = "localhost";
    private String username = "";
    private String password = "";
    private String initialDB = "/data/emdros/wivu/s3/bhs3";

    public Database(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
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
        return clone;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Database)
        {
            Database other = (Database) obj;
            return Objects.equals(name, other.name) //
                    && backendKind == other.backendKind //
                    && charset == other.charset //
                    && Objects.equals(hostname, other.hostname) && Objects.equals(initialDB, other.initialDB) //
                    && outputKind == other.outputKind //
                    && Objects.equals(password, other.password) //
                    && Objects.equals(username, other.username);
        }
        return false;
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append(this.getClass().getName()) //
                .append(" [").append("name=").append(name)//
                .append(", hostname=").append(hostname).append(", inirialDB=").append(initialDB)//
                .append("' backendname=").append(getBackendName())//
                .append("]").toString();
    }

}
