package nl.knaw.dans.shemdros.core;

import java.io.File;

public class EmdrosClient
{

    public EmdrosClient()
    {

    }

    public <T> T execute(String query, MqlResultConsumer<T> consumer) throws Exception
    {
        return execute(Database.DEFAULT_DATABASE_NAME, query, consumer);
    }

    public <T> T execute(String databaseName, String query, MqlResultConsumer<T> consumer) throws Exception
    {
        EnvPool envPool = EmdrosFactory.getEnvPool(databaseName);
        EnvWrapper wrapper = envPool.getPooledEnvironment();
        T product;
        try
        {
            if (wrapper.execute(query))
            {
                product = consumer.consume(wrapper.getEnv().takeOverResult());
            }
            else
            {
                throw new ShemdrosException("Unable to execute query: \n" + query);
            }
        }
        catch (Exception e)
        {
            wrapper.setObsolete(true);
            throw e;
        }
        finally
        {
            consumer.close();
            envPool.returnPooledEnvironment(wrapper);
        }
        return product;
    }

    public <T> T execute(File query, MqlResultConsumer<T> consumer) throws Exception
    {
        return execute(Database.DEFAULT_DATABASE_NAME, query, consumer);
    }

    public <T> T execute(String databaseName, File query, MqlResultConsumer<T> consumer) throws Exception
    {
        EnvPool envPool = EmdrosFactory.getEnvPool(databaseName);
        EnvWrapper wrapper = envPool.getPooledEnvironment();
        T product;
        try
        {
            if (wrapper.execute(query))
            {
                product = consumer.consume(wrapper.getEnv().takeOverResult());
            }
            else
            {
                throw new ShemdrosException("Unable to execute query in file '" + query + "'");
            }
        }
        catch (Exception e)
        {
            wrapper.setObsolete(true);
            throw e;
        }
        finally
        {
            consumer.close();
            envPool.returnPooledEnvironment(wrapper);
        }
        return product;
    }

}
