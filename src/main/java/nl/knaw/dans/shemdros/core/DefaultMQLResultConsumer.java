package nl.knaw.dans.shemdros.core;

import jemdros.MQLResult;

public class DefaultMQLResultConsumer implements MqlResultConsumer<MQLResult>
{

    @Override
    public MQLResult consume(MQLResult mqlResult) throws ShemdrosException
    {
        return mqlResult;
    }

    @Override
    public void close()
    {
        //
    }

}
