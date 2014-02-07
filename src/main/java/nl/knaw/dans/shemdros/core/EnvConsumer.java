package nl.knaw.dans.shemdros.core;

import jemdros.EmdrosEnv;

public interface EnvConsumer<T>
{

    public T consume(EmdrosEnv env) throws ShemdrosException;

    public void close();

}
