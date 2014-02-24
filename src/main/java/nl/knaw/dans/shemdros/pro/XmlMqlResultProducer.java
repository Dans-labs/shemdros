package nl.knaw.dans.shemdros.pro;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import jemdros.EMdFValue;
import jemdros.EmdrosEnv;
import jemdros.EmdrosException;
import jemdros.MatchedObject;
import jemdros.SetOfMonads;
import jemdros.Sheaf;
import jemdros.SheafConstIterator;
import jemdros.Straw;
import jemdros.StrawConstIterator;
import nl.knaw.dans.shemdros.core.EnvConsumer;
import nl.knaw.dans.shemdros.core.Shemdros;
import nl.knaw.dans.shemdros.core.ShemdrosException;

import org.apache.commons.lang3.StringUtils;

/**
 * Environment consumer that produces mql-results in the xml-format.
 * 
 * @author henk van den berg
 */
public class XmlMqlResultProducer implements EnvConsumer<Void>
{

    private final String encoding;
    private final XMLStreamWriter out;
    private boolean newLine;
    private int indent;
    private String ws = "";

    public XmlMqlResultProducer(OutputStream outputStream) throws ShemdrosException
    {
        this(outputStream, Shemdros.DEFAULT_CHARACTER_ENCODING);
    }

    public XmlMqlResultProducer(OutputStream outputStream, String charsetName) throws ShemdrosException
    {
        encoding = charsetName;
        try
        {
            out = XMLOutputFactory.newInstance().createXMLStreamWriter(new OutputStreamWriter(outputStream, encoding));
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

    public Void consume(EmdrosEnv env) throws ShemdrosException
    {
        String nl = "";
        String ident = "";
        if (newLine || indent > 0)
        {
            nl = "\n";
            ws = StringUtils.repeat(" ", indent);
        }
        try
        {
            handle(env, nl, ident);
        }
        catch (XMLStreamException e)
        {
            throw new ShemdrosException("Could not close XMLStreanWriter: ", e);
        }
        return null;
    }

    private void handle(EmdrosEnv env, String nl, String ident) throws ShemdrosException, XMLStreamException
    {
        try
        {
            writeDocument(env, nl, ident);
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
            out.close();
        }
    }

    private void writeDocument(EmdrosEnv env, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        out.writeStartDocument(encoding, Shemdros.XML_VERSION);
        // writeDocType(nl);
        writeRootElement(env, nl, ident);
        out.writeEndDocument();
    }

    // private void writeDocType(String nl) throws XMLStreamException {
    // out.writeCharacters(nl);
    // out.writeDTD("<!DOCTYPE mql_results [");
    // out.writeCharacters(nl);
    // out.writeDTD("<!ELEMENT mql_results (mql_result)* >");
    // out.writeCharacters(nl);
    // out.writeDTD("]>");
    // }

    protected void writeRootElement(EmdrosEnv env, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        out.writeCharacters(nl);
        out.writeStartElement("mql-results");
        writeMqlResult(env, nl, ident + ws);
        out.writeCharacters(nl);
        out.writeEndElement();
    }

    private void writeMqlResult(EmdrosEnv env, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        out.writeCharacters(nl);
        out.writeCharacters(ident);
        out.writeStartElement("mql-result");

        if (env.isSheaf())
        {
            Sheaf sheaf = env.getSheaf();
            writeStatus(sheaf, nl, ident + ws);
            writeSheaf(sheaf, nl, ident + ws);
        }
        else if (env.isFlatSheaf())
        {
            throw new UnsupportedOperationException("not yet implemented");
        }
        else if (env.isTable())
        {
            throw new UnsupportedOperationException("not yet implemented");
        }
        out.writeCharacters(nl);
        out.writeCharacters(ident);
        out.writeEndElement();
    }

    private void writeStatus(Sheaf sheaf, String nl, String ident) throws XMLStreamException
    {
        out.writeCharacters(nl);
        out.writeCharacters(ident);
        out.writeEmptyElement("status");
        out.writeAttribute("success", "" + !sheaf.isFail());
    }

    private void writeSheaf(Sheaf sheaf, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        out.writeCharacters(nl);
        out.writeCharacters(ident);
        out.writeStartElement("sheaf");
        SheafConstIterator sci = sheaf.const_iterator();
        while (sci.hasNext())
        {
            Straw straw = sci.next();
            writeStraw(straw, nl, ident + ws);
        }
        out.writeCharacters(nl);
        out.writeCharacters(ident);
        out.writeEndElement();
    }

    private void writeStraw(Straw straw, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        out.writeCharacters(nl);
        out.writeCharacters(ident);
        out.writeStartElement("straw");
        StrawConstIterator sci = straw.const_iterator();
        while (sci.hasNext())
        {
            MatchedObject mo = sci.next();
            writeMatchedObject(mo, nl, ident + ws);
        }
        out.writeCharacters(nl);
        out.writeCharacters(ident);
        out.writeEndElement();
    }

    private void writeMatchedObject(MatchedObject mo, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        out.writeCharacters(nl);
        out.writeCharacters(ident);
        out.writeStartElement("matched_object");
        out.writeAttribute("object_type_name", mo.getObjectTypeName());
        out.writeAttribute("focus", "" + mo.getFocus());
        String marks = mo.getMarksString();
        if (!StringUtils.isBlank(marks))
            out.writeAttribute("marks", marks);
        out.writeAttribute("id_d", "" + mo.getID_D());

        SetOfMonads som = mo.getMonads();
        writeMonatSet(som, nl, ident + ws);

        // featureList is always empty, even when mo.getNoOfEMdFValues() != 0.
        // StringList featureList = mo.getFeatureList();
        // if (!featureList.isEmpty())
        // {
        // writeFeatures(mo, nl, ident + ws);
        // }

        long noev = mo.getNoOfEMdFValues();
        if (noev > 0)
        {
            writeFeatures(mo, nl, ident + ws);
        }

        if (!mo.sheafIsEmpty())
        {
            Sheaf sheaf = mo.getSheaf();
            writeSheaf(sheaf, nl, ident + ws);
        }

        out.writeCharacters(nl);
        out.writeCharacters(ident);
        out.writeEndElement();
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

    private void writeFeatures(MatchedObject mo, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        out.writeCharacters(nl);
        out.writeCharacters(ident);
        out.writeStartElement("features");
        long noev = mo.getNoOfEMdFValues();
        for (int i = 0; i < noev; i++)
        {
            writeFeature(i, mo, nl, ident + ws);
        }
        out.writeCharacters(nl);
        out.writeCharacters(ident);
        out.writeEndElement();
    }

    private void writeFeature(int i, MatchedObject mo, String nl, String ident) throws XMLStreamException, EmdrosException
    {
        out.writeCharacters(nl);
        out.writeCharacters(ident);
        out.writeStartElement("feature");
        EMdFValue ev = mo.getEMdFValue(i);
        // out.writeAttribute("feature_name", "??duno??");
        out.writeAttribute("feature_type", ev.getKind().toString());
        // out.writeAttribute("enum_type", "??duno??");
        out.writeCharacters(mo.getFeatureAsString(i));
        out.writeEndElement();
    }

    private void writeMonatSet(SetOfMonads som, String nl, String ident) throws XMLStreamException
    {
        out.writeCharacters(nl);
        out.writeCharacters(ident);
        out.writeStartElement("monad_set");
        writeMse(som, nl, ident + ws);
        out.writeCharacters(nl);
        out.writeCharacters(ident);
        out.writeEndElement();
    }

    private void writeMse(SetOfMonads som, String nl, String ident) throws XMLStreamException
    {
        out.writeCharacters(nl);
        out.writeCharacters(ident);
        out.writeEmptyElement("mse");
        out.writeAttribute("first", "" + som.first());
        out.writeAttribute("last", "" + som.last());

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
