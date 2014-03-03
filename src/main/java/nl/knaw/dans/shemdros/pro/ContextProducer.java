package nl.knaw.dans.shemdros.pro;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ContextProducer implements EnvConsumer<Void>
{
    private static final Logger logger = LoggerFactory.getLogger(ContextProducer.class);
    
    
    private final CmdRenderObjects ro;
    
    private Appendable out;
    
    private int strawLevel = -1;
    private List<MonadSet> focusMonadSets = new ArrayList<>();
    private List<MonadSet> contextMonadSets = new ArrayList<>();
    
    public ContextProducer(File jsonFile)
    {
        this(null, jsonFile);
    }
    
    public ContextProducer(Appendable out, File jsonFile)
    {
        this.out = out;
        ro = new CmdRenderObjects(jsonFile);
    }
    
    protected abstract void addRootAttributes() throws IOException;
    protected abstract boolean isContextMatch();

    @Override
    public Void consume(EmdrosEnv env) throws ShemdrosException
    {
        logger.debug("Start rendering result with {}.", this.getClass().getName());
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
        getOut().append(Shemdros.XML_DECLARATION);
        out.append("\n");
        out.append("<context_list");
        addAttribute("producer", this.getClass().getName());
        addRootAttributes();
        out.append(">\n");
        
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
        if (isContextMatch())
        {
            writeContext();
        }
        strawLevel--;
    }
    
    protected void renderMatchedObject(MatchedObject mo) throws EmdrosException, IOException
    {
        SetOfMonads som = mo.getMonads();
        if (isContextMatch())
        {
            contextMonadSets.add(new MonadSet(som.first(), som.last()));
        }
        
        if (mo.getFocus())
        {
            focusMonadSets.add(new MonadSet(som.first(), som.last()));
        }
        if (!mo.sheafIsEmpty())
        {
            renderSheaf(mo.getSheaf());
        }
    }
    
    protected void writeContext() throws IOException
    {
        for (MonadSet context : getContextMonadSets())
        {
            getRo().getContextPart(context.getFirst(), context.getLast(), getOut());
        }
        clearMonadSets();
    }
    
    protected int getStrawLevel()
    {
        return strawLevel;
    }
    
    protected void clearMonadSets()
    {
        focusMonadSets.clear();
        contextMonadSets.clear();
    }

    protected List<MonadSet> getFocusMonadSets()
    {
        return focusMonadSets;
    }

    protected List<MonadSet> getContextMonadSets()
    {
        return contextMonadSets;
    }
    
    protected Appendable getOut()
    {
        if (out == null)
        {
            throw new IllegalStateException("No Appendable set.");
        }
        return out;
    }
    
    protected void setOut(Appendable out)
    {
        this.out = out;
    }

    protected CmdRenderObjects getRo()
    {
        return ro;
    }
    
    protected void addAttribute(String key, String value) throws IOException
    {
        getOut().append(" ")
            .append(key)
            .append("=\"")
            .append(value)
            .append("\"");
    }

    @Override
    public void close()
    {
        
    }

}
