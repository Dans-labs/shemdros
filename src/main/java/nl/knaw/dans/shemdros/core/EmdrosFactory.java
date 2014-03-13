package nl.knaw.dans.shemdros.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmdrosFactory
{

    private static final Logger logger = LoggerFactory.getLogger(EmdrosFactory.class);

    private static EmdrosFactory INSTANCE;

    public static Database getDatabase(String databaseName) throws ShemdrosParameterException
    {
        return instance().doGetDatabase(databaseName);
    }

    public static CmdRenderObjects newCmdRenderObjects(String databaseName, String jsonName) throws ShemdrosParameterException
    {
        return instance().newCmdRO(databaseName, jsonName);
    }

    public static EnvPool getEnvPool(String databaseName) throws ShemdrosParameterException
    {
        return instance().doGetEnvPool(databaseName);
    }

    public static JsonFile getJsonFile(String jsonName) throws ShemdrosParameterException
    {
        return instance().doGetJsonFile(jsonName);
    }

    public static EmdrosFactory instance()
    {
        if (INSTANCE == null)
        {
            new EmdrosFactory();
        }
        return INSTANCE;
    }

    private Map<String, JsonFile> jsonMap = Collections.synchronizedMap(new HashMap<String, JsonFile>());
    private Map<String, Database> databaseMap = Collections.synchronizedMap(new HashMap<String, Database>());
    private Map<String, EnvPool> envPoolMap = Collections.synchronizedMap(new HashMap<String, EnvPool>());

    private EmdrosFactory()
    {
        INSTANCE = this;
    }

    public void addJsonFile(JsonFile jsonFile)
    {
        synchronized (jsonMap)
        {
            jsonMap.put(jsonFile.getName(), jsonFile);
            logger.info("Digested JsonFile. name={}, path={}", jsonFile.getName(), jsonFile.getPath());
        }
    }

    public void addDatabase(Database database)
    {
        synchronized (databaseMap)
        {
            databaseMap.put(database.getName(), database);
            EnvPool envPool = new EnvPool(database);
            envPoolMap.put(database.getName(), envPool);
            logger.debug("Digested database: {}", database.toString());
        }
    }

    private Database doGetDatabase(String databaseName) throws ShemdrosParameterException
    {
        synchronized (databaseMap)
        {
            Database database = databaseMap.get(databaseName);
            if (database == null)
            {
                throw new ShemdrosParameterException("No database by the name '" + databaseName + "'");
            }
            return database;
        }
    }

    private EnvPool doGetEnvPool(String databaseName) throws ShemdrosParameterException
    {
        synchronized (databaseMap)
        {
            EnvPool envPool = envPoolMap.get(databaseName);
            if (envPool == null)
            {
                throw new ShemdrosParameterException("No EnvPool. No database by the name '" + databaseName + "'");
            }
            return envPool;
        }
    }

    private JsonFile doGetJsonFile(String jsonName) throws ShemdrosParameterException
    {
        synchronized (jsonMap)
        {
            JsonFile jsonFile = jsonMap.get(jsonName);
            if (jsonFile == null)
            {
                throw new ShemdrosParameterException("No json file by the name '" + jsonName + "'");
            }
            return jsonFile;
        }
    }

    private CmdRenderObjects newCmdRO(String databaseName, String jsonName) throws ShemdrosParameterException
    {
        return new CmdRenderObjects(getDatabase(databaseName), doGetJsonFile(jsonName));
    }

    public List<Database> getDatabases()
    {
        synchronized (databaseMap)
        {
            List<Database> databases = new ArrayList<Database>();
            for (Database database : databaseMap.values())
            {
                databases.add(database.clone());
            }
            return databases;
        }
    }

    public void setDatabases(List<Database> databases)
    {
        for (Database db : databases)
        {
            addDatabase(db);
        }
    }

    public List<JsonFile> getJsonFiles()
    {
        synchronized (jsonMap)
        {
            List<JsonFile> jsonFiles = new ArrayList<JsonFile>();
            for (JsonFile jsonFile : jsonMap.values())
            {
                jsonFiles.add(jsonFile);
            }
            return jsonFiles;
        }
    }

    public void setJsonFiles(List<JsonFile> jsonFiles)
    {
        for (JsonFile jf : jsonFiles)
        {
            addJsonFile(jf.clone());
        }
    }

    public List<EnvPool> getEnvPools()
    {
        synchronized (databaseMap)
        {
            List<EnvPool> envPools = new ArrayList<EnvPool>();
            for (EnvPool envPool : envPoolMap.values())
            {
                envPools.add(envPool);
            }
            return envPools;
        }
    }

}
