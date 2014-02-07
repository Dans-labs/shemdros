package nl.knaw.dans.shemdros.pro;

import jemdros.EmdrosEnv;
import jemdros.Sheaf;
import nl.knaw.dans.shemdros.core.EnvConsumer;
import nl.knaw.dans.shemdros.core.ShemdrosException;

public class SheafProducer implements EnvConsumer<Sheaf>
{

    public Sheaf consume(EmdrosEnv env) throws ShemdrosException
    {
        if (env.isSheaf())
        {
            return env.getSheaf();
        }
        else
        {
            throw new ShemdrosException("No sheaf in environment: table=" + env.isTable() + ", flatSheaf=" + env.isFlatSheaf());
        }
    }

    public void close()
    {
        // TODO Auto-generated method stub

    }

}
