package nl.knaw.dans.shemdros.web;

import java.util.Date;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jemdros.MQLResult;
import nl.knaw.dans.shemdros.core.Database;
import nl.knaw.dans.shemdros.core.DefaultMQLResultConsumer;
import nl.knaw.dans.shemdros.core.EmdrosClient;
import nl.knaw.dans.shemdros.core.JsonFile;
import nl.knaw.dans.shemdros.core.ShemdrosException;
import nl.knaw.dans.shemdros.core.ShemdrosParameterException;
import nl.knaw.dans.shemdros.pro.ContextProducer;
import nl.knaw.dans.shemdros.pro.LevelContextProducer;
import nl.knaw.dans.shemdros.pro.MarksContextProducer;
import nl.knaw.dans.shemdros.pro.XmlMqlResultProducer;

import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartMediaTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("mql/")
public class MqlResource
{

    public static final String RENDER_AT_LEVEL = "level";
    public static final String RENDER_AT_MARK = "mark";
    private static final Logger logger = LoggerFactory.getLogger(MqlResource.class);

    private EmdrosClient emdrosClient = new EmdrosClient();

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response noPath()
    {
        return Response.ok("Methods for querying an Emdros database. See <a href=\"application.wadl\">application.wadl</a> for details").build();
    }

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
        // sb.append("Shemdros webservice\n\n").append("EnvPool.size=").append(EnvPool.instance().size());
        sb.append("Comming soon...");
        return Response.ok(sb.toString()).build();
    }

    @POST
    @Path("query")
    @Produces(MediaType.TEXT_XML)
    public Response query(final String query, //
            @QueryParam("database")
            @DefaultValue(Database.DEFAULT)
            String database, //
            @QueryParam("result-format")
            @DefaultValue("mql-xml")
            String resultFormat) throws ShemdrosException
    {
        logger.debug("recieved POST query, result-format={}", resultFormat);
        MQLResult mqlResult = emdrosClient.execute(database, query, new DefaultMQLResultConsumer());
        return Response.ok(getQueryResult(mqlResult, resultFormat)).type(MediaType.TEXT_XML_TYPE).build();
    }

    @POST
    @Path("render")
    @Produces(MediaType.TEXT_XML)
    public Response render(final String query, //
            @QueryParam("database")
            @DefaultValue(Database.DEFAULT)
            String database, //
            @QueryParam("json-name")
            @DefaultValue(JsonFile.DEFAULT)
            String jsonName, //
            @QueryParam("renderer")
            @DefaultValue(RENDER_AT_LEVEL)
            String renderer, //
            @QueryParam("context-level")
            @DefaultValue("0")
            int contextLevel, //
            @QueryParam("context-mark")
            @DefaultValue("context")
            String contextMark, //
            @QueryParam("offset-first")
            @DefaultValue("0")
            int offsetFirst, //
            @QueryParam("offset-last")
            @DefaultValue("0")
            int offsetLast) throws ShemdrosException
    {
        logger.debug("recieved POST render, renderer={}", renderer);
        MQLResult mqlResult = emdrosClient.execute(database, query, new DefaultMQLResultConsumer());
        MQLResultStream resultStream = getRenderResult(mqlResult, renderer, database, jsonName, contextLevel, contextMark, offsetFirst, offsetLast);
        return Response.ok(resultStream).type(MediaType.TEXT_XML_TYPE).build();
    }

    @POST
    @Path("execute")
    @Produces(MultiPartMediaTypes.MULTIPART_MIXED)
    public MultiPart queryAndRender(final String query, //
            @QueryParam("database")
            @DefaultValue(Database.DEFAULT)
            String database, //
            @QueryParam("result-format")
            @DefaultValue("mql-xml")
            String resultFormat, //
            @QueryParam("json-name")
            @DefaultValue(JsonFile.DEFAULT)
            String jsonName, //
            @QueryParam("renderer")
            @DefaultValue(RENDER_AT_LEVEL)
            String renderer, //
            @QueryParam("context-level")
            @DefaultValue("0")
            int contextLevel, //
            @QueryParam("context-mark")
            @DefaultValue("context")
            String contextMark, //
            @QueryParam("offset-first")
            @DefaultValue("0")
            int offsetFirst, //
            @QueryParam("offset-last")
            @DefaultValue("0")
            int offsetLast) throws ShemdrosException
    {
        logger.debug("recieved POST execute, result-format={}, renderer={}", resultFormat, renderer);
        MQLResult mqlResult = emdrosClient.execute(database, query, new DefaultMQLResultConsumer());
        MQLResultStream queryResult = getQueryResult(mqlResult, resultFormat);
        MQLResultStream renderResult = getRenderResult(mqlResult, renderer, database, //
                jsonName, contextLevel, contextMark, offsetFirst, offsetLast);
        MultiPart multiPart = new MultiPart();
        
        multiPart //
            .bodyPart(new BodyPart(queryResult, MediaType.TEXT_XML_TYPE)) //
            .bodyPart(new BodyPart(renderResult, MediaType.TEXT_XML_TYPE)); //
        
        return multiPart; //Response.ok(multiPart).type(MultiPartMediaTypes.MULTIPART_MIXED).build();
    }

    private MQLResultStream getQueryResult(MQLResult mqlResult, String resultFormat) throws ShemdrosException
    {
        if ("mql-xml".equalsIgnoreCase(resultFormat) || "xml".equalsIgnoreCase(resultFormat))
        {
            XmlMqlResultProducer producer = new XmlMqlResultProducer();
            producer.setIndent(2);
            return new MQLResultStream(mqlResult, producer);
        }
        else
        {
            throw new ShemdrosParameterException("Unknown result-format: '" + resultFormat + "'.");
        }
    }

    private MQLResultStream getRenderResult(MQLResult mqlResult, String renderer, String database, String jsonName, int contextLevel, String contextMark,
            int offsetFirst, int offsetLast) throws ShemdrosParameterException
    {
        ContextProducer contextProducer;
        if (renderer.equalsIgnoreCase(RENDER_AT_LEVEL))
        {
            LevelContextProducer producer = new LevelContextProducer(database, jsonName);
            producer.setContextLevel(contextLevel);
            producer.setOffsetFirst(offsetFirst);
            producer.setOffsetLast(offsetLast);
            contextProducer = producer;
        }
        else if (renderer.equalsIgnoreCase(RENDER_AT_MARK))
        {
            MarksContextProducer producer = new MarksContextProducer(database, jsonName);
            producer.setContextMark(contextMark);
            producer.setOffsetFirst(offsetFirst);
            producer.setOffsetLast(offsetLast);
            contextProducer = producer;
        }
        else
        {
            throw new ShemdrosParameterException("Unknown renderer: '" + renderer + "'.");
        }
        return new MQLResultStream(mqlResult, contextProducer);
    }

}
