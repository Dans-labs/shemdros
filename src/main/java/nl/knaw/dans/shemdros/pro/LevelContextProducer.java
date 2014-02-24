package nl.knaw.dans.shemdros.pro;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jemdros.EmdrosEnv;
import jemdros.EmdrosException;
import jemdros.MatchedObject;
import jemdros.SetOfMonads;
import jemdros.Sheaf;
import jemdros.SheafConstIterator;
import jemdros.Straw;
import jemdros.StrawConstIterator;
import nl.knaw.dans.shemdros.core.CmdRenderObjects;
import nl.knaw.dans.shemdros.core.EnvConsumer;
import nl.knaw.dans.shemdros.core.Shemdros;
import nl.knaw.dans.shemdros.core.ShemdrosException;
import nl.knaw.dans.shemdros.util.MonadSet;


public class LevelContextProducer implements EnvConsumer<Void>
{
    
    private static final Logger logger = LoggerFactory.getLogger(LevelContextProducer.class);
    
    private final Appendable out;
    private final CmdRenderObjects ro;
    
    private int contextLevel = 0;
    private int strawLevel = -1;
    
    private List<MonadSet> focusMonatSets = new ArrayList<>();
    private List<MonadSet> contextMonatSets = new ArrayList<>();
    
    
    public LevelContextProducer(Appendable out, File jsonFile)
    {
        this.out = out;
        ro = new CmdRenderObjects(jsonFile);
    }

    public int getContextLevel()
    {
        return contextLevel;
    }

    public void setContextLevel(int contextLevel)
    {
        this.contextLevel = contextLevel;
    }

    @Override
    public Void consume(EmdrosEnv env) throws ShemdrosException
    {
        logger.debug("Start rendering result at context level {}.", contextLevel);
        long start = System.currentTimeMillis();
        if (env.isSheaf())
        {
            Sheaf sheaf = env.getSheaf();
            startRender(sheaf);
        }
        else if (env.isFlatSheaf())
        {
            throw new UnsupportedOperationException("not yet implemented");
        }
        else if (env.isTable())
        {
            throw new UnsupportedOperationException("not yet implemented");
        }
        logger.debug("Finished rendering result in {} ms.", System.currentTimeMillis() - start);
        return null;
    }

    private void startRender(Sheaf sheaf) throws ShemdrosException
    {
        if (sheaf.isFail())
        {
            throw new ShemdrosException("Cannot render sheaf: sheaf failed");
        }
        
        try
        {
            startDocument(sheaf);
        }
        catch (IOException e)
        {
            throw new ShemdrosException(e);
        }
    }

    private void startDocument(Sheaf sheaf) throws ShemdrosException, IOException
    {
        out.append(Shemdros.XML_DECLARATION);
        out.append("\n");
        out.append("<context_list level=\"");
        out.append("" + contextLevel);
        out.append("\">\n");
        
        try
        {
            renderSheaf(sheaf);
        }
        catch (EmdrosException e)
        {
            throw new ShemdrosException(e);
        }
        out.append("</context_list>");
    }

    private void renderSheaf(Sheaf sheaf) throws EmdrosException, IOException
    {
        SheafConstIterator sci = sheaf.const_iterator();
        while (sci.hasNext())
        {
            Straw straw = sci.next();
            renderStraw(straw);
        }
    }

    private void renderStraw(Straw straw) throws EmdrosException, IOException
    {
        strawLevel++;
        StrawConstIterator sci = straw.const_iterator();
        while (sci.hasNext())
        {
            MatchedObject mo = sci.next();
            renderMatchedObject(mo);
        }
        if (strawLevel == contextLevel)
        {
            writeContext();
        }
        strawLevel--;
    }

    private void renderMatchedObject(MatchedObject mo) throws EmdrosException, IOException
    {
        SetOfMonads som = mo.getMonads();
        if (strawLevel == contextLevel)
        {
            contextMonatSets.add(new MonadSet(som.first(), som.last()));
        }
        
        if (mo.getFocus())
        {
            focusMonatSets.add(new MonadSet(som.first(), som.last()));
        }
        if (!mo.sheafIsEmpty())
        {
            renderSheaf(mo.getSheaf());
        }
    }
    
    private void writeContext() throws IOException
    {
        out.append("<context>");
        for (MonadSet focus : focusMonatSets)
        {
            out.append("\n<focus first=\"")
                .append("" + focus.getFirst())
                .append("\" last=\"")
                .append("" + focus.getLast())
                .append("\"/>");
            ;
        }
        for (MonadSet context : contextMonatSets)
        {
            ro.getContextPart(context.getFirst(), context.getLast(), out);
        }
        
        out.append("</context>\n");
        focusMonatSets.clear();
        contextMonatSets.clear();
    }

    @Override
    public void close()
    {
        // TODO Auto-generated method stub
        
    }
    
    

}
