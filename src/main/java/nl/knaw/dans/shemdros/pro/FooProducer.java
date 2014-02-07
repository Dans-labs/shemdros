package nl.knaw.dans.shemdros.pro;

import java.io.IOException;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import jemdros.EmdrosEnv;
import jemdros.EmdrosException;
import jemdros.Sheaf;
import jemdros.SheafIterator;
import jemdros.Straw;
import nl.knaw.dans.shemdros.core.EnvConsumer;
import nl.knaw.dans.shemdros.core.ShemdrosException;

public class FooProducer implements EnvConsumer<Void>, XMLReader
{

    private static final String ROOT = "mql_results";
    private static final String INDENT = "\n      ";

    private ContentHandler handler;

    public Void consume(EmdrosEnv env) throws ShemdrosException
    {
        if (env.isSheaf())
        {
            Sheaf sheaf = env.takeOverSheaf();
            SheafIterator shi = sheaf.iterator();
            while (shi.hasNext())
            {
                try
                {
                    Straw straw = shi.next();
                }
                catch (EmdrosException e)
                {
                    throw new ShemdrosException(e);
                }
            }
        }
        return null;
    }

    public void close()
    {
        // TODO Auto-generated method stub

    }

    public void setContentHandler(ContentHandler handler)
    {
        this.handler = handler;
    }

    public ContentHandler getContentHandler()
    {
        return handler;
    }

    public void parse(String systemId) throws IOException, SAXException
    {
        // TODO Auto-generated method stub

    }

    public void parse(InputSource input) throws IOException, SAXException
    {
        // TODO Auto-generated method stub

    }

    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException
    {
        // TODO Auto-generated method stub

    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException
    {
        // TODO Auto-generated method stub

    }

    public void setEntityResolver(EntityResolver resolver)
    {
        // TODO Auto-generated method stub

    }

    public EntityResolver getEntityResolver()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setDTDHandler(DTDHandler handler)
    {
        // TODO Auto-generated method stub

    }

    public DTDHandler getDTDHandler()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setErrorHandler(ErrorHandler handler)
    {
        // TODO Auto-generated method stub

    }

    public ErrorHandler getErrorHandler()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
