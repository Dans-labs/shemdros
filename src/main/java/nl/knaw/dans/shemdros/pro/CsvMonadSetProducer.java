package nl.knaw.dans.shemdros.pro;

import java.io.IOException;
import java.io.OutputStream;

import jemdros.EmdrosException;
import jemdros.MQLResult;
import jemdros.MatchedObject;
import jemdros.MonadSetElement;
import jemdros.SOMConstIterator;
import jemdros.SetOfMonads;
import jemdros.Sheaf;
import jemdros.SheafConstIterator;
import jemdros.Straw;
import jemdros.StrawConstIterator;
import nl.knaw.dans.shemdros.core.Shemdros;
import nl.knaw.dans.shemdros.core.ShemdrosException;
import nl.knaw.dans.shemdros.core.StreamingMqlResultConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvMonadSetProducer implements StreamingMqlResultConsumer
{

    private static final Logger logger = LoggerFactory.getLogger(CsvMonadSetProducer.class);

    private OutputStream output;

    @Override
    public Void consume(MQLResult mqlResult) throws ShemdrosException
    {
        logger.debug("Start writing result with {}.", this.getClass().getName());
        long start = System.currentTimeMillis();
        handle(mqlResult);
        logger.debug("Finished writing result in {} ms.", System.currentTimeMillis() - start);
        return null;
    }

    private void handle(MQLResult mqlResult) throws ShemdrosException
    {
        if (mqlResult.isSheaf())
        {
            Sheaf sheaf = mqlResult.getSheaf();
            if (sheaf.isFail())
            {
                throw new ShemdrosException("Unsuccessful query.");
            }
            try
            {
                writeSheaf(sheaf);
            }
            catch (EmdrosException e)
            {
                throw new ShemdrosException(e);
            }
            catch (IOException e)
            {
                throw new ShemdrosException(e);
            }
        }
        else if (mqlResult.isFlatSheaf())
        {
            throw new UnsupportedOperationException("not yet implemented");
        }
        else if (mqlResult.isTable())
        {
            throw new UnsupportedOperationException("not yet implemented");
        }

    }

    private void writeSheaf(Sheaf sheaf) throws EmdrosException, IOException
    {
        SheafConstIterator sci = sheaf.const_iterator();
        while (sci.hasNext())
        {
            Straw straw = sci.next();
            writeStraw(straw);
        }
    }

    private void writeStraw(Straw straw) throws EmdrosException, IOException
    {
        StrawConstIterator sci = straw.const_iterator();
        while (sci.hasNext())
        {
            MatchedObject mo = sci.next();
            writeMatchedObject(mo);
        }
    }

    private void writeMatchedObject(MatchedObject mo) throws EmdrosException, IOException
    {
        if (mo.getFocus())
        {
            SetOfMonads som = mo.getMonads();
            writeMonatSets(som);
        }

        if (!mo.sheafIsEmpty())
        {
            Sheaf sheaf = mo.getSheaf();
            writeSheaf(sheaf);
        }
    }

    private void writeMonatSets(SetOfMonads som) throws IOException, EmdrosException
    {
        SOMConstIterator iter = som.const_iterator();
        while (iter.hasNext())
        {
            MonadSetElement mse = iter.next();
            StringBuilder sb = new StringBuilder();
            sb.append(mse.first()).append(Shemdros.CSV_SEPERATOR).append(mse.last()).append(Shemdros.CSV_SEPERATOR).append("\n");
            getOutput().write(sb.toString().getBytes());
        }
    }

    @Override
    public void close()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setOutputStream(OutputStream output) throws ShemdrosException
    {
        this.output = output;
    }

    protected OutputStream getOutput()
    {
        if (output == null)
        {
            throw new IllegalStateException("No outputStream set.");
        }
        return output;
    }

}
