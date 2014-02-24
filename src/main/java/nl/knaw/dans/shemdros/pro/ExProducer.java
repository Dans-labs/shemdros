package nl.knaw.dans.shemdros.pro;

import jemdros.EMdFValue;
import jemdros.EmdrosEnv;
import jemdros.EmdrosException;
import jemdros.MatchedObject;
import jemdros.RenderObjects;
import jemdros.Sheaf;
import jemdros.SheafConstIterator;
import jemdros.Straw;
import jemdros.StrawConstIterator;
import nl.knaw.dans.shemdros.core.EnvConsumer;
import nl.knaw.dans.shemdros.core.ShemdrosException;

public class ExProducer implements EnvConsumer<Void>
{

    @Override
    public Void consume(EmdrosEnv env) throws ShemdrosException
    {
        
        
        if (env.isSheaf())
        {
            Sheaf sheaf = env.takeOverSheaf();
            try
            {
                handleSheaf(sheaf);
            }
            catch (EmdrosException e)
            {
                throw new ShemdrosException(e);
            }
        }
        return null;
    }

    @Override
    public void close()
    {
        // TODO Auto-generated method stub
        
    }
    
    private void handleSheaf(Sheaf sheaf) throws EmdrosException
    {
        SheafConstIterator sci = sheaf.const_iterator();
        while (sci.hasNext())
        {
            Straw straw = sci.next();
            handleStraw(straw);
        }
        
    }

    private void handleStraw(Straw straw) throws EmdrosException
    {
        StrawConstIterator sci = straw.const_iterator();
        while (sci.hasNext())
        {
            MatchedObject mo = sci.next();
            handleMatchedObject(mo);
        }
        
    }

    private void handleMatchedObject(MatchedObject mo) throws EmdrosException
    {
        String objType = mo.getObjectTypeName();
        System.err.println("objType=" + objType);
        
        long noev = mo.getNoOfEMdFValues();
        for (int i = 0; i < noev; i++)
        {
            EMdFValue ev = mo.getEMdFValue(i);
            System.err.println("ev=" + ev);
        }
        
        if (!mo.sheafIsEmpty())
        {
            Sheaf sheaf = mo.getSheaf();
            handleSheaf(sheaf);
        }
    }

}
