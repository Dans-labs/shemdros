package nl.knaw.dans.shemdros.pro;

import nl.knaw.dans.shemdros.core.EnvConsumer;
import nl.knaw.dans.shemdros.core.ShemdrosException;
import jemdros.EmdrosEnv;
import jemdros.Sheaf;

public class SheafProducer implements EnvConsumer<Sheaf>
{

    @Override
    public Sheaf consume(EmdrosEnv env) throws ShemdrosException
    {
        if (!env.isSheaf())
        {
            throw new ShemdrosException("Environment does noet contain a sheaf.");
        }
        return env.takeOverSheaf();
    }

    @Override
    public void close()
    {
        // TODO Auto-generated method stub

    }

}
