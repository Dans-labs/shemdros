package nl.knaw.dans.shemdros.core;

import java.io.File;

public class EmdrosClient {
	
	private static EmdrosClient INSTANCE;
	
	public static EmdrosClient instance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new EmdrosClient();
		}
		return INSTANCE;
	}
	
	private EmdrosClient()
	{
		// singleton
	}
	
	public <T> T execute(String query, EnvConsumer<T> consumer) throws ShemdrosException
	{
		EnvWrapper wrapper = EnvPool.instance().getPooledEnvironment();
		T product;
		try {
			if (wrapper.execute(query))
			{
				product = consumer.consume(wrapper.getEnv());			
			}
			else
			{
				throw new ShemdrosException("Unable to execute query: \n" + query);
			}
		} 
		finally 
		{
			consumer.close();
			EnvPool.instance().returnPooledEnvironment(wrapper);
		}
		return product;
	}
	
	public <T> T execute(File query, EnvConsumer<T> consumer) throws ShemdrosException
	{
		EnvWrapper wrapper = EnvPool.instance().getPooledEnvironment();
		T product;
		try {
			if (wrapper.execute(query))
			{
				product = consumer.consume(wrapper.getEnv());			
			}
			else
			{
				throw new ShemdrosException("Unable to execute query in file '" + query + "'");
			}
		} 
		finally 
		{
			consumer.close();
			EnvPool.instance().returnPooledEnvironment(wrapper);
		}
		return product;
	}

}
