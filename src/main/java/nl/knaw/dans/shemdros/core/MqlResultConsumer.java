package nl.knaw.dans.shemdros.core;

import jemdros.MQLResult;

public interface MqlResultConsumer<T>
{

    public T consume(MQLResult mqlResult) throws ShemdrosException;

    public void close();

}
