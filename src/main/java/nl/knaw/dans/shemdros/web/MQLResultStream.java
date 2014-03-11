package nl.knaw.dans.shemdros.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import jemdros.MQLResult;
import nl.knaw.dans.shemdros.core.ShemdrosException;
import nl.knaw.dans.shemdros.core.ShemdrosParameterException;
import nl.knaw.dans.shemdros.core.StreamingMqlResultConsumer;
import nl.knaw.dans.shemdros.web.exc.ShemdrosExceptionMapper;

public class MQLResultStream implements StreamingOutput
{

    private final StreamingMqlResultConsumer producer;
    private final MQLResult mqlResult;

    public MQLResultStream(MQLResult mqlResult, StreamingMqlResultConsumer producer) throws ShemdrosParameterException
    {
        this.producer = producer;
        this.mqlResult = mqlResult;
    }

    @Override
    public void write(OutputStream output) throws IOException, WebApplicationException
    {
        try
        {
            producer.setOutputStream(output);
            producer.consume(mqlResult);
        }
        catch (ShemdrosException e)
        {
            throw new WebApplicationException(ShemdrosExceptionMapper.map(e));
        }
    }

}
