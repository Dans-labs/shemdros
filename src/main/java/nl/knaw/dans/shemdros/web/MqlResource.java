package nl.knaw.dans.shemdros.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import nl.knaw.dans.shemdros.core.EmdrosClient;
import nl.knaw.dans.shemdros.core.EnvPool;
import nl.knaw.dans.shemdros.core.ShemdrosCompileException;
import nl.knaw.dans.shemdros.core.ShemdrosException;
import nl.knaw.dans.shemdros.pro.XmlMqlResultProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("mql/")
public class MqlResource
{

    private static final Logger logger = LoggerFactory.getLogger(MqlResource.class);

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
    public Response query(final String query)
    {
        logger.debug("recieved POST query");
        StreamingOutput stream = new StreamingOutput()
        {

            @Override
            public void write(OutputStream out) throws IOException, WebApplicationException
            {
                try
                {
                    XmlMqlResultProducer producer = new XmlMqlResultProducer(out);
                    producer.setIndent(2);
                    EmdrosClient.instance().execute(query, producer);
                }
                catch (ShemdrosCompileException e)
                {
                    throw new BadRequestException(
                            Response.status(Status.BAD_REQUEST)
                            .type(MediaType.TEXT_PLAIN)
                            .entity(e.getMessage()).build());
                }
                catch (ShemdrosException e)
                {
                    throw new WebApplicationException(e);
                }

            }
        };
        return Response.ok(stream).build();
    }

}
