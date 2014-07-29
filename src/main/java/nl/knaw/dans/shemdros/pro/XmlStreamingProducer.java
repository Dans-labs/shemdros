package nl.knaw.dans.shemdros.pro;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import jemdros.EmdrosException;
import jemdros.MQLResult;
import jemdros.Sheaf;
import nl.knaw.dans.shemdros.core.Shemdros;
import nl.knaw.dans.shemdros.core.ShemdrosException;
import nl.knaw.dans.shemdros.core.StreamingMqlResultConsumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class XmlStreamingProducer implements StreamingMqlResultConsumer
{

    private static final Logger logger = LoggerFactory.getLogger(XmlStreamingProducer.class);

    private final String encoding;
    private XMLStreamWriter out;
    private boolean newLine;
    private int indent;
    private String whitespace = "";

    public XmlStreamingProducer()
    {
        encoding = Shemdros.DEFAULT_CHARACTER_ENCODING;
    }

    public XmlStreamingProducer(OutputStream output) throws ShemdrosException
    {
        this(output, Shemdros.DEFAULT_CHARACTER_ENCODING);
    }

    public XmlStreamingProducer(OutputStream output, String charsetName) throws ShemdrosException
    {
        encoding = charsetName;
        setOutputStream(output);
    }

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
        logger.debug("Start writing result with {}.", this.getClass().getName());
        long start = System.currentTimeMillis();
        String nl = "";
        String ident = "";
        if (newLine || indent > 0)
        {
            nl = "\n";
            whitespace = StringUtils.repeat(" ", indent);
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

    protected void handle(MQLResult mqlResult, String nl, String ident) throws ShemdrosException, XMLStreamException
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

    protected XMLStreamWriter getOut()
    {
        if (out == null)
        {
            throw new IllegalStateException("No outputStream set.");
        }
        return out;
    }

    protected void writeDocument(XMLStreamWriter writer, MQLResult mqlResult, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        writer.writeStartDocument(encoding, Shemdros.XML_VERSION);
        writeRootElement(writer, mqlResult, nl, ident);
        writer.writeEndDocument();
    }

    protected abstract void writeRootElement(XMLStreamWriter writer, MQLResult mqlResult, String nl, String ident) throws XMLStreamException, EmdrosException;

    protected void writeStatus(XMLStreamWriter writer, Sheaf sheaf, String nl, String ident) throws XMLStreamException
    {
        writer.writeCharacters(nl);
        writer.writeCharacters(ident);
        writer.writeEmptyElement("status");

        // when we've got this far success is true
        writer.writeAttribute("success", "true");
        // writer.writeAttribute("success", "" + !sheaf.isFail());
    }

    @Override
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

    public String getWhitespace()
    {
        return whitespace;
    }

}
