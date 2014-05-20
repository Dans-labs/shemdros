package nl.knaw.dans.shemdros.web;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import jemdros.MQLResult;
import nl.knaw.dans.shemdros.core.Database;
import nl.knaw.dans.shemdros.core.DefaultMQLResultConsumer;
import nl.knaw.dans.shemdros.core.EmdrosClient;
import nl.knaw.dans.shemdros.core.JsonFile;
import nl.knaw.dans.shemdros.core.Shemdros;
import nl.knaw.dans.shemdros.core.ShemdrosException;
import nl.knaw.dans.shemdros.core.ShemdrosParameterException;
import nl.knaw.dans.shemdros.pro.ContextProducer;
import nl.knaw.dans.shemdros.pro.LevelContextProducer;
import nl.knaw.dans.shemdros.pro.MarksContextProducer;
import nl.knaw.dans.shemdros.pro.CsvMonadSetProducer;
import nl.knaw.dans.shemdros.pro.XmlMonadSetProducer;
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

    public static final String RESULT_FORMAT_MQL_XML = "mql-xml";
    public static final String RESULT_FORMAT_MONADSET_XML = "monadset-xml";
    public static final String RESULT_FORMAT_MONADSET_CSV = "monadset-csv";
    public static final String RENDER_AT_LEVEL = "level";
    public static final String RENDER_AT_MARK = "mark";
    private static final Logger logger = LoggerFactory.getLogger(MqlResource.class);

    public static List<String> getResultFormats()
    {
        return Arrays.asList(new String[] {//
                RESULT_FORMAT_MQL_XML, //
                        RESULT_FORMAT_MONADSET_XML, //
                        RESULT_FORMAT_MONADSET_CSV, //
                });
    }

    public static List<String> getRenderers()
    {
        return Arrays.asList(new String[] {//
                RENDER_AT_LEVEL,//
                        RENDER_AT_MARK, //
                });
    }

    private EmdrosClient emdrosClient = new EmdrosClient();

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(MediaType.TEXT_HTML + Shemdros.DEFAULT_CHARSET)
    public Response noPath()
    {
        logger.debug("recieved GET \"\" (no path). {}", uriInfo.getAbsolutePath().toString());
        String href = uriInfo.getBaseUri().toString() + "application.wadl";
        return Response.ok("Methods for querying an Emdros database. " //
                + "See <a href=\"" + href + "\">application.wadl</a> for details") //
                .encoding(Shemdros.DEFAULT_CHARACTER_ENCODING)//
                .build();
    }

    @GET
    @Path("result-formats")
    @Produces(MediaType.TEXT_PLAIN + Shemdros.DEFAULT_CHARSET)
    public Response listResultFormats()
    {
        logger.debug("recieved GET result-formats");
        return Response.ok(getResultFormats()) //
                .encoding(Shemdros.DEFAULT_CHARACTER_ENCODING) //
                .build();
    }

    @GET
    @Path("renderers")
    @Produces(MediaType.TEXT_PLAIN + Shemdros.DEFAULT_CHARSET)
    public Response listRenderers()
    {
        logger.debug("recieved GET renderers");
        return Response.ok(getRenderers()) //
                .encoding(Shemdros.DEFAULT_CHARACTER_ENCODING) //
                .build();
    }

    @POST
    @Path("query")
    @Produces(MediaType.TEXT_XML + Shemdros.DEFAULT_CHARSET)
    public Response query(final String query, //
            @QueryParam("database")
            @DefaultValue(Database.DEFAULT_DATABASE_NAME)
            String database, //
            @QueryParam("result-format")
            @DefaultValue(RESULT_FORMAT_MQL_XML)
            String resultFormat) throws ShemdrosException
    {
        logger.debug("recieved POST query, result-format={}", resultFormat);
        // logger.debug("query is '{}'", query);
        MQLResult mqlResult = emdrosClient.execute(database, query, new DefaultMQLResultConsumer());
        return Response.ok(getQueryResult(mqlResult, resultFormat))//
                .type(MediaType.TEXT_XML_TYPE)//
                .encoding(Shemdros.DEFAULT_CHARACTER_ENCODING).build();
    }

    @POST
    @Path("render")
    @Produces(MediaType.TEXT_XML + Shemdros.DEFAULT_CHARSET)
    public Response render(final String query, //
            @QueryParam("database")
            @DefaultValue(Database.DEFAULT_DATABASE_NAME)
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
        return Response.ok(resultStream) //
                .type(MediaType.TEXT_XML_TYPE)//
                .encoding(Shemdros.DEFAULT_CHARACTER_ENCODING) //
                .build();
    }

    @POST
    @Path("execute")
    @Produces(MultiPartMediaTypes.MULTIPART_MIXED)
    public MultiPart queryAndRender(final String query, //
            @QueryParam("database")
            @DefaultValue(Database.DEFAULT_DATABASE_NAME)
            String database, //
            @QueryParam("result-format")
            @DefaultValue(RESULT_FORMAT_MQL_XML)
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

        BodyPart queryPart = new BodyPart(queryResult, MediaType.TEXT_XML_TYPE);
        BodyPart resultPart = new BodyPart(renderResult, MediaType.TEXT_XML_TYPE);
        try
        {
            queryPart.setContentDisposition(new ContentDisposition("attachment; filename=\"mql-result.xml\""));
            resultPart.setContentDisposition(new ContentDisposition("attachment; filename=\"mql-context.xml\""));
        }
        catch (ParseException e)
        {
            throw new ShemdrosException(e);
        }

        multiPart //
                .bodyPart(queryPart) //
                .bodyPart(resultPart); //

        return multiPart; // Response.ok(multiPart).type(MultiPartMediaTypes.MULTIPART_MIXED).build();
    }

    private MQLResultStream getQueryResult(MQLResult mqlResult, String resultFormat) throws ShemdrosException
    {
        if (RESULT_FORMAT_MQL_XML.equalsIgnoreCase(resultFormat) || "xml".equalsIgnoreCase(resultFormat))
        {
            XmlMqlResultProducer producer = new XmlMqlResultProducer();
            producer.setIndent(2);
            return new MQLResultStream(mqlResult, producer);
        }
        else if (RESULT_FORMAT_MONADSET_XML.equalsIgnoreCase(resultFormat))
        {
            XmlMonadSetProducer producer = new XmlMonadSetProducer();
            producer.setIndent(2);
            return new MQLResultStream(mqlResult, producer);
        }
        else if (RESULT_FORMAT_MONADSET_CSV.equalsIgnoreCase(resultFormat))
        {
            CsvMonadSetProducer producer = new CsvMonadSetProducer();
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
