package nl.knaw.dans.shemdros.pro;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import jemdros.EMdFValue;
import jemdros.EmdrosException;
import jemdros.MQLResult;
import jemdros.MatchedObject;
import jemdros.SetOfMonads;
import jemdros.Sheaf;
import jemdros.SheafConstIterator;
import jemdros.Straw;
import jemdros.StrawConstIterator;
import nl.knaw.dans.shemdros.core.Shemdros;
import nl.knaw.dans.shemdros.core.ShemdrosException;
import nl.knaw.dans.shemdros.core.StreamingMqlResultConsumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Environment consumer that produces mql-results in the xml-format.
 * 
 * @author Gendan Vehnberk
 */
public class XmlMqlResultProducer implements StreamingMqlResultConsumer
{

    private static final Logger logger = LoggerFactory.getLogger(XmlMqlResultProducer.class);

    private final String encoding;
    private XMLStreamWriter out;
    private boolean newLine;
    private int indent;
    private String ws = "";

    public XmlMqlResultProducer()
    {
        encoding = Shemdros.DEFAULT_CHARACTER_ENCODING;
    }

    public XmlMqlResultProducer(OutputStream output) throws ShemdrosException
    {
        this(output, Shemdros.DEFAULT_CHARACTER_ENCODING);
    }

    public XmlMqlResultProducer(OutputStream output, String charsetName) throws ShemdrosException
    {
        encoding = charsetName;
        setOutputStream(output);
    }

    @Override
    public void setOutputStream(OutputStream output) throws ShemdrosException
    {
        try
        {
            out = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(output, encoding));
        }
        catch (UnsupportedEncodingException e)
        {
            throw new ShemdrosException(e);
        }
        catch (XMLStreamException e)
        {
            throw new ShemdrosException(e);
        }
        catch (FactoryConfigurationError e)
        {
            throw new ShemdrosException(e);
        }
    }

    public Void consume(MQLResult mqlResult) throws ShemdrosException
    {
        logger.debug("Start writing result to mql-xml with {}.", this.getClass().getName());
        long start = System.currentTimeMillis();
        String nl = "";
        String ident = "";
        if (newLine || indent > 0)
        {
            nl = "\n";
            ws = StringUtils.repeat(" ", indent);
        }
        try
        {
            handle(mqlResult, nl, ident);
        }
        catch (XMLStreamException e)
        {
            throw new ShemdrosException("Could not close XMLStreanWriter: ", e);
        }
        logger.debug("Finished writing result in {} ms.", System.currentTimeMillis() - start);
        return null;
    }

    private XMLStreamWriter getOut()
    {
        if (out == null)
        {
            throw new IllegalStateException("No outputStream set.");
        }
        return out;
    }

    private void handle(MQLResult mqlResult, String nl, String ident) throws ShemdrosException, XMLStreamException
    {
        XMLStreamWriter writer = getOut();
        try
        {
            writeDocument(writer, mqlResult, nl, ident);
        }
        catch (XMLStreamException e)
        {
            throw new ShemdrosException(e);
        }
        catch (EmdrosException e)
        {
            throw new ShemdrosException(e);
        }
        finally
        {
            writer.flush();
            // do not close streamWriter. it will close the underlying outputStream, preventing further
            // response.
        }
    }

    private void writeDocument(XMLStreamWriter writer, MQLResult mqlResult, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        writer.writeStartDocument(encoding, Shemdros.XML_VERSION);
        // writeDocType(writer, nl);
        writeRootElement(writer, mqlResult, nl, ident);
        writer.writeEndDocument();
    }

    protected void writeRootElement(XMLStreamWriter writer, MQLResult mqlResult, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        writer.writeCharacters(nl);
        writer.writeStartElement("mql-results");
        writeMqlResult(writer, mqlResult, nl, ident + ws);
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
            writeStatus(writer, sheaf, nl, ident + ws);
            writeSheaf(writer, sheaf, nl, ident + ws);
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

    private void writeStatus(XMLStreamWriter writer, Sheaf sheaf, String nl, String ident) throws XMLStreamException
    {
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeEmptyElement("status");
        writer.writeAttribute("success", "" + !sheaf.isFail());
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
            writeStraw(writer, straw, nl, ident + ws);
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
            writeMatchedObject(writer, mo, nl, ident + ws);
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
        writeMonatSet(writer, som, nl, ident + ws);

        // featureList is always empty, even when mo.getNoOfEMdFValues() != 0.
        // StringList featureList = mo.getFeatureList();
        // if (!featureList.isEmpty())
        // {
        // writeFeatures(mo, nl, ident + ws);
        // }

        long noev = mo.getNoOfEMdFValues();
        if (noev > 0)
        {
            writeFeatures(writer, mo, nl, ident + ws);
        }

        if (!mo.sheafIsEmpty())
        {
            Sheaf sheaf = mo.getSheaf();
            writeSheaf(writer, sheaf, nl, ident + ws);
        }

        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeEndElement();
        //
        // StringList sl = mo.getFeatureList();
        //
        // //long noev = mo.getNoOfEMdFValues();
        // System.out.println("NoOfEMdFValues=" + noev + " featurelist.isEmpty="
        // + sl.isEmpty());
        //
        // for (int i = 0; i < noev; i++)
        // {
        //
        // EMdFValue ev = mo.getEMdFValue(i);
        // System.err.println(i + " EMdFValue=" + ev);
        // System.err.println("\tasString=" + mo.getFeatureAsString(i));
        // System.err.println("\tid_d=" + ev.getID_D());
        // System.err.println("\tenum=" + ev.getEnum());
        // System.err.println("\tint=" + ev.getInt());
        //
        // //System.err.println("\tstring=" + ev.getString());
        // //System.err.println("\tintegerList=" + ev.getIntegerList());
        // System.err.println("\tkind=" + ev.getKind().toString());
        // //System.err.println("\tsom=" + ev.getSOM().toString());
        // }
    }

    private void writeFeatures(XMLStreamWriter writer, MatchedObject mo, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeStartElement("features");
        long noev = mo.getNoOfEMdFValues();
        for (int i = 0; i < noev; i++)
        {
            writeFeature(writer, i, mo, nl, ident + ws);
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

    private void writeMonatSet(XMLStreamWriter writer, SetOfMonads som, String nl, String ident) throws XMLStreamException
    {
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeStartElement("monad_set");
        writeMse(writer, som, nl, ident + ws);
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeEndElement();
    }

    private void writeMse(XMLStreamWriter writer, SetOfMonads som, String nl, String ident) throws XMLStreamException
    {
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeEmptyElement("mse");
        writer.writeAttribute("first", "" + som.first());
        writer.writeAttribute("last", "" + som.last());

    }

    public void close()
    {
        // TODO Auto-generated method stub

    }

    public boolean printsNewLine()
    {
        return newLine;
    }

    public void setNewLine(boolean newLine)
    {
        this.newLine = newLine;
    }

    public int getIndent()
    {
        return indent;
    }

    public void setIndent(int indent)
    {
        if (indent < 0)
        {
            throw new IllegalArgumentException("indent cannot be smaller than 0.");
        }
        this.indent = indent;
    }

}
