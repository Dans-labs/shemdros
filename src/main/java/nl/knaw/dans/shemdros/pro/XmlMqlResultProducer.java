package nl.knaw.dans.shemdros.pro;

import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import jemdros.EMdFValue;
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

import org.apache.commons.lang3.StringUtils;

/**
 * Environment consumer that produces mql-results in the xml-format.
 */
public class XmlMqlResultProducer extends XmlStreamingProducer
{

    public XmlMqlResultProducer()
    {
        super();
    }

    public XmlMqlResultProducer(OutputStream output) throws ShemdrosException
    {
        super(output);
    }

    public XmlMqlResultProducer(OutputStream output, String charsetName) throws ShemdrosException
    {
        super(output, charsetName);
    }

    protected void writeRootElement(XMLStreamWriter writer, MQLResult mqlResult, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        writer.writeCharacters(nl);
        writer.writeStartElement("mql-results");
        writeMqlResult(writer, mqlResult, nl, ident + getWhitespace());
        writer.writeCharacters(nl);
        writer.writeEndElement();
    }

    private void writeMqlResult(XMLStreamWriter writer, MQLResult mqlResult, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeStartElement("mql-result");

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
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeEndElement();
    }

    private void writeSheaf(XMLStreamWriter writer, Sheaf sheaf, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeStartElement("sheaf");
        SheafConstIterator sci = sheaf.const_iterator();
        while (sci.hasNext())
        {
            Straw straw = sci.next();
            writeStraw(writer, straw, nl, ident + getWhitespace());
        }
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeEndElement();
    }

    private void writeStraw(XMLStreamWriter writer, Straw straw, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeStartElement("straw");
        StrawConstIterator sci = straw.const_iterator();
        while (sci.hasNext())
        {
            MatchedObject mo = sci.next();
            writeMatchedObject(writer, mo, nl, ident + getWhitespace());
        }
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeEndElement();
    }

    private void writeMatchedObject(XMLStreamWriter writer, MatchedObject mo, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeStartElement("matched_object");
        writer.writeAttribute("object_type_name", mo.getObjectTypeName());
        writer.writeAttribute("focus", "" + mo.getFocus());
        String marks = mo.getMarksString();
        if (!StringUtils.isBlank(marks))
            writer.writeAttribute("marks", marks);
        writer.writeAttribute("id_d", "" + mo.getID_D());

        SetOfMonads som = mo.getMonads();
        writeMonatSet(writer, som, nl, ident + getWhitespace());

        long noev = mo.getNoOfEMdFValues();
        if (noev > 0)
        {
            writeFeatures(writer, mo, nl, ident + getWhitespace());
        }

        if (!mo.sheafIsEmpty())
        {
            Sheaf sheaf = mo.getSheaf();
            writeSheaf(writer, sheaf, nl, ident + getWhitespace());
        }

        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeEndElement();
    }

    private void writeFeatures(XMLStreamWriter writer, MatchedObject mo, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeStartElement("features");
        long noev = mo.getNoOfEMdFValues();
        for (int i = 0; i < noev; i++)
        {
            writeFeature(writer, i, mo, nl, ident + getWhitespace());
        }
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeEndElement();
    }

    private void writeFeature(XMLStreamWriter writer, int i, MatchedObject mo, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeStartElement("feature");
        EMdFValue ev = mo.getEMdFValue(i);
        // writer.writeAttribute("feature_name", "??duno??");
        writer.writeAttribute("feature_type", ev.getKind().toString());
        // writer.writeAttribute("enum_type", "??duno??");
        writer.writeCharacters(mo.getFeatureAsString(i));
        writer.writeEndElement();
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

    public void close()
    {
        // TODO Auto-generated method stub

    }
}
