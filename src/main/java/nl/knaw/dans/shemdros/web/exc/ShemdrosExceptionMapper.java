package nl.knaw.dans.shemdros.web.exc;

import java.io.ByteArrayOutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.exception.ExceptionUtils;

import nl.knaw.dans.shemdros.core.Shemdros;
import nl.knaw.dans.shemdros.core.ShemdrosCompileException;
import nl.knaw.dans.shemdros.core.ShemdrosException;

@Provider
public class ShemdrosExceptionMapper implements ExceptionMapper<ShemdrosException>
{
    
    
    private static final String NL = "\n";
    private static final String WS = "  ";
    
    private static String stylesheet;
    
    public static Response map(ShemdrosException e)
    {
        return new ShemdrosExceptionMapper().toResponse(e);
    }
    
    private XMLStreamWriter wout;

    public String getStylesheet()
    {
        return stylesheet;
    }

    public void setStylesheet(String stylesheet)
    {
        ShemdrosExceptionMapper.stylesheet = stylesheet;
    }

    @Override
    public Response toResponse(ShemdrosException sex)
    {
        Status status = mapStatus(sex);
        return Response.status(status)
                .type(MediaType.TEXT_XML)
                .entity(composeEntity(sex, status))
                .build();
    }
    
    private Status mapStatus(ShemdrosException sex)
    {
        Status status;
        if (sex instanceof ShemdrosCompileException)
        {
            status = Status.BAD_REQUEST;
        }
        else
        {
            status = Status.INTERNAL_SERVER_ERROR;
        }
        return status;
    }
    
    private String composeEntity(ShemdrosException sex, Status status)
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try
        {
            writeEntity(bout, sex, status);
        }
        catch (XMLStreamException e)
        {
            throw new WebApplicationException(e);
        }
        return bout.toString();
    }

    private void writeEntity(ByteArrayOutputStream bout, ShemdrosException sex, Status status) throws XMLStreamException
    {
        try
        {
            wout = XMLOutputFactory.newInstance().createXMLStreamWriter(bout);
            wout.writeStartDocument(Shemdros.DEFAULT_CHARACTER_ENCODING, Shemdros.XML_VERSION);
            
            if (stylesheet != null)
            {
                wout.writeCharacters(NL);
                wout.writeProcessingInstruction("xml-stylesheet type=\"text/xsl\" href=\"" + stylesheet + "\"");
            }
            wout.writeCharacters(NL);
            wout.writeStartElement("error");
            writeStatus(status, "" + WS);
            writeException(sex, "" + WS);
            wout.writeCharacters(NL);
            wout.writeEndElement();
            wout.writeEndDocument();
        }
        catch (XMLStreamException e)
        {
            throw new WebApplicationException(e);
        }
        catch (FactoryConfigurationError e)
        {
            throw new WebApplicationException(e);
        }
        finally
        {
            if (wout != null)
            {
                wout.close();
            }
        }
    }

    private void writeStatus(Status status, String indent) throws XMLStreamException
    {
        wout.writeCharacters(NL);
        wout.writeCharacters(indent);
        wout.writeStartElement("http-status");
        
        wout.writeCharacters(NL);
        wout.writeCharacters(indent + WS);
        wout.writeStartElement("code");
        wout.writeCharacters("" + status.getStatusCode());
        wout.writeEndElement();
        
        wout.writeCharacters(NL);
        wout.writeCharacters(indent + WS);
        wout.writeStartElement("reason");
        wout.writeCharacters(status.getReasonPhrase());
        wout.writeEndElement();
        
        wout.writeCharacters(NL);
        wout.writeCharacters(indent);
        wout.writeEndElement();
    }

    private void writeException(ShemdrosException sex, String indent) throws XMLStreamException
    {
        wout.writeCharacters(NL);
        wout.writeCharacters(indent);
        wout.writeStartElement("exception");
        
        wout.writeCharacters(NL);
        wout.writeCharacters(indent + WS);
        wout.writeStartElement("class");
        wout.writeCharacters(sex.getClass().getName());
        wout.writeEndElement();
        
        wout.writeCharacters(NL);
        wout.writeCharacters(indent + WS);
        wout.writeStartElement("message");
        wout.writeCharacters(NL);
        wout.writeCharacters(sex.getMessage());
        wout.writeCharacters(NL);
        wout.writeCharacters(indent + WS);
        wout.writeEndElement();
        
        wout.writeCharacters(NL);
        wout.writeCharacters(indent + WS);
        wout.writeStartElement("stacktrace");
        wout.writeCharacters(NL);
        wout.writeCharacters(ExceptionUtils.getStackTrace(sex));
        wout.writeCharacters(NL);
        wout.writeCharacters(indent + WS);
        wout.writeEndElement();
        
        wout.writeCharacters(NL);
        wout.writeCharacters(indent);
        wout.writeEndElement();
    }

}
