package nl.knaw.dans.shemdros.pro;

import java.io.BufferedWriter;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import jemdros.EmdrosException;
import jemdros.MQLResult;
import jemdros.MatchedObject;
import jemdros.SetOfMonads;
import jemdros.Sheaf;
import jemdros.SheafConstIterator;
import jemdros.Straw;
import jemdros.StrawConstIterator;
import nl.knaw.dans.shemdros.core.CmdRenderObjects;
import nl.knaw.dans.shemdros.core.EmdrosFactory;
import nl.knaw.dans.shemdros.core.JsonFile;
import nl.knaw.dans.shemdros.core.Shemdros;
import nl.knaw.dans.shemdros.core.ShemdrosException;
import nl.knaw.dans.shemdros.core.ShemdrosParameterException;
import nl.knaw.dans.shemdros.core.StreamingMqlResultConsumer;
import nl.knaw.dans.shemdros.util.MonadSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ContextProducer implements StreamingMqlResultConsumer
{
    private static final Logger logger = LoggerFactory.getLogger(ContextProducer.class);

    private final String jsonName;
    private final CmdRenderObjects ro;

    private Appendable out;

    private int strawLevel = -1;
    private List<MonadSet> focusMonadSets = new ArrayList<MonadSet>();
    private List<MonadSet> contextMonadSets = new ArrayList<MonadSet>();

    private int offsetFirst = 0;
    private int offsetLast = 0;

    public ContextProducer(String databaseName, String jsonName) throws ShemdrosParameterException
    {
        this.jsonName = jsonName;
        ro = EmdrosFactory.newCmdRenderObjects(databaseName, jsonName);
    }

    protected abstract void addRootAttributes() throws IOException;

    protected abstract boolean isContextMatch();

    @Override
    public Void consume(MQLResult mqlResult) throws ShemdrosException
    {
        logger.debug("Start rendering result with {}.", this.getClass().getName());
        long start = System.currentTimeMillis();
        if (mqlResult.isSheaf())
        {
            Sheaf sheaf = mqlResult.getSheaf();
            startRender(sheaf);
        }
        else if (mqlResult.isFlatSheaf())
        {
            throw new UnsupportedOperationException("not yet implemented");
        }
        else if (mqlResult.isTable())
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
        addAttribute("offset-first", offsetFirst);
        addAttribute("offset-last", offsetLast);
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
        if (out instanceof Flushable)
        {
            ((Flushable) out).flush();
        }
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
            contextMonadSets.add(new MonadSet(som.first(), som.last(), getOffsetFirst(), getOffsetLast()));
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
            throw new IllegalStateException("No Appendable or Outputstream set.");
        }
        return out;
    }

    @Override
    public void setOutputStream(OutputStream output) throws ShemdrosParameterException
    {
        BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(output));

        JsonFile jsonFile = EmdrosFactory.getJsonFile(jsonName);
        String focusElementPart = jsonFile.getFocusElementPart();
        FocusInterventionist focusProducer = new FocusInterventionist(buffer, focusElementPart);
        focusProducer.setFocusList(getFocusMonadSets());

        // VerseSentenceOrderInterventionist orderInterventionist = new
        // VerseSentenceOrderInterventionist(focusProducer);

        // out = orderInterventionist;

        out = focusProducer;
    }

    protected CmdRenderObjects getRo()
    {
        return ro;
    }

    protected void addAttribute(String key, String value) throws IOException
    {
        getOut().append(" ").append(key).append("=\"").append(value).append("\"");
    }

    protected void addAttribute(String key, int value) throws IOException
    {
        addAttribute(key, Integer.toString(value));
    }

    public int getOffsetFirst()
    {
        return offsetFirst;
    }

    public void setOffsetFirst(int offsetFirst)
    {
        this.offsetFirst = offsetFirst;
    }

    public int getOffsetLast()
    {
        return offsetLast;
    }

    public void setOffsetLast(int offsetLast)
    {
        this.offsetLast = offsetLast;
    }

    @Override
    public void close()
    {

    }

}
