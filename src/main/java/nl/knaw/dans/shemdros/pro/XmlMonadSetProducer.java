package nl.knaw.dans.shemdros.pro;

import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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
import nl.knaw.dans.shemdros.core.ShemdrosException;

public class XmlMonadSetProducer extends XmlStreamingProducer
{

    public XmlMonadSetProducer()
    {
        super();
    }

    public XmlMonadSetProducer(OutputStream output) throws ShemdrosException
    {
        super(output);
    }

    public XmlMonadSetProducer(OutputStream output, String charsetName) throws ShemdrosException
    {
        super(output, charsetName);
    }

    @Override
    protected void writeRootElement(XMLStreamWriter writer, MQLResult mqlResult, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        writer.writeCharacters(nl);
        writer.writeStartElement("monadsets");
        writeMqlResult(writer, mqlResult, nl, ident + getWhitespace());
        writer.writeCharacters(nl);
        writer.writeEndElement();
    }

    private void writeMqlResult(XMLStreamWriter writer, MQLResult mqlResult, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        if (mqlResult.isSheaf())
        {
            Sheaf sheaf = mqlResult.getSheaf();
            writeStatus(writer, sheaf, nl, ident + getWhitespace());
            writeSheaf(writer, sheaf, nl, ident + getWhitespace());
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

    private void writeSheaf(XMLStreamWriter writer, Sheaf sheaf, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        SheafConstIterator sci = sheaf.const_iterator();
        while (sci.hasNext())
        {
            Straw straw = sci.next();
            writeStraw(writer, straw, nl, ident);
        }
    }

    private void writeStraw(XMLStreamWriter writer, Straw straw, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        StrawConstIterator sci = straw.const_iterator();
        while (sci.hasNext())
        {
            MatchedObject mo = sci.next();
            writeMatchedObject(writer, mo, nl, ident);
        }
    }

    private void writeMatchedObject(XMLStreamWriter writer, MatchedObject mo, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        if (mo.getFocus())
        {
            SetOfMonads som = mo.getMonads();
            writeMonatSet(writer, som, nl, ident);
        }

        if (!mo.sheafIsEmpty())
        {
            Sheaf sheaf = mo.getSheaf();
            writeSheaf(writer, sheaf, nl, ident);
        }
    }

    private void writeMonatSet(XMLStreamWriter writer, SetOfMonads som, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeStartElement("monadset");
        writeMse(writer, som, nl, ident + getWhitespace());
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeEndElement();
    }

    private void writeMse(XMLStreamWriter writer, SetOfMonads som, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        SOMConstIterator iter = som.const_iterator();
        while (iter.hasNext())
        {
            MonadSetElement mse = iter.next();
            writer.writeCharacters(nl);
            writer.writeCharacters(ident);
            writer.writeEmptyElement("mse");
            writer.writeAttribute("first", "" + mse.first());
            writer.writeAttribute("last", "" + mse.last());
        }
    }

}
