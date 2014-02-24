package nl.knaw.dans.shemdros.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import nl.knaw.dans.shemdros.core.EmdrosClient;
import nl.knaw.dans.shemdros.core.EnvPool;
import nl.knaw.dans.shemdros.core.ShemdrosException;
import nl.knaw.dans.shemdros.pro.XmlMqlResultProducer;
import nl.knaw.dans.shemdros.web.exc.ShemdrosExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("mql/")
public class MqlResource
{

    private static final Logger logger = LoggerFactory.getLogger(MqlResource.class);
    
    private EmdrosClient emdrosClient = new EmdrosClient();

    @GET
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public Response test()
    {
        return Response.ok("OK Testing").build();
    }

    @GET
    @Path("state")
    @Produces(MediaType.TEXT_PLAIN)
    public Response state()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Shemdros webservice\n\n").append("EnvPool.size=").append(EnvPool.instance().size());

        return Response.ok(sb.toString()).build();
    }

    @POST
    @Path("query")
    @Produces(MediaType.TEXT_XML)
    public Response query(final String query,
            @QueryParam("result-format") @DefaultValue("mql-xml") String resultFormat) throws ShemdrosException
    {
        logger.debug("recieved POST query, result-format={}", resultFormat);
        if ("mql-xml".equalsIgnoreCase(resultFormat) || "xml".equalsIgnoreCase(resultFormat))
        {
            return getResultFormatMqlXml(query);
        }
        else
        {
            throw new ShemdrosException("Unknown result-format: '" + resultFormat + "'.");
        }
    }

    private Response getResultFormatMqlXml(final String query)
    {
        StreamingOutput stream = new StreamingOutput()
        {

            @Override
            public void write(OutputStream out) throws IOException, WebApplicationException
            {
                try
                {
                    XmlMqlResultProducer producer = new XmlMqlResultProducer(out);
                    producer.setIndent(2);
                    emdrosClient.execute(query, producer);
                }
                catch (ShemdrosException e)
                {
                    throw new WebApplicationException(ShemdrosExceptionMapper.map(e));
                }

            }
        };
        return Response.ok(stream).build();
    }

}
