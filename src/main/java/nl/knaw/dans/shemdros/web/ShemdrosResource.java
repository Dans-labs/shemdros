package nl.knaw.dans.shemdros.web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import nl.knaw.dans.shemdros.core.EmdrosFactory;
import nl.knaw.dans.shemdros.core.JsonFile;
import nl.knaw.dans.shemdros.core.Shemdros;
import nl.knaw.dans.shemdros.core.ShemdrosException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("")
public class ShemdrosResource
{

    private static final Logger logger = LoggerFactory.getLogger(ShemdrosResource.class);

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
    @Path("databases")
    @Produces(MediaType.TEXT_PLAIN + Shemdros.DEFAULT_CHARSET)
    public Response getDatabases()
    {
        logger.debug("recieved GET databases.");
        return Response.ok(EmdrosFactory.instance().getDatabases()) //
                .encoding(Shemdros.DEFAULT_CHARACTER_ENCODING) //
                .build();
    }

    @GET
    @Path("databases/list")
    @Produces(MediaType.TEXT_PLAIN + Shemdros.DEFAULT_CHARSET)
    public Response listDatabases()
    {
        logger.debug("recieved GET databases/list.");
        return getDatabases();
    }

    @GET
    @Path("jsonfiles")
    @Produces(MediaType.TEXT_PLAIN + Shemdros.DEFAULT_CHARSET)
    public Response getJsonFiles()
    {
        logger.debug("recieved GET jsonfiles.");
        return Response.ok(EmdrosFactory.instance().getJsonFiles()) //
                .encoding(Shemdros.DEFAULT_CHARACTER_ENCODING) //
                .build();
    }

    @GET
    @Path("jsonfiles/list")
    @Produces(MediaType.TEXT_PLAIN + Shemdros.DEFAULT_CHARSET)
    public Response listJsonFiles()
    {
        logger.debug("recieved GET jsonfiles/list.");
        return getJsonFiles();
    }

    @GET
    @Path("jsonfiles/{id}")
    @Produces(MediaType.APPLICATION_JSON + Shemdros.DEFAULT_CHARSET)
    public Response getJsonFile(@PathParam("id")
    String id) throws ShemdrosException
    {
        logger.debug("recieved GET jsonfiles/{}.", id);
        JsonFile jsonFile = EmdrosFactory.getJsonFile(id);
        FileInputStream fis = null;
        String content;
        try
        {
            fis = new FileInputStream(jsonFile.getPath());
            content = IOUtils.toString(fis);
        }
        catch (FileNotFoundException e)
        {
            throw new ShemdrosException(e);
        }
        catch (IOException e)
        {
            throw new ShemdrosException(e);
        }
        finally
        {
            IOUtils.closeQuietly(fis);
        }
        return Response.ok(content)//
                .encoding(Shemdros.DEFAULT_CHARACTER_ENCODING)//
                .build();
    }

    @GET
    @Path("envpools")
    @Produces(MediaType.TEXT_PLAIN + Shemdros.DEFAULT_CHARSET)
    public Response getEnvPools()
    {
        logger.debug("recieved GET envpools.");
        return Response.ok(EmdrosFactory.instance().getEnvPools()) //
                .encoding(Shemdros.DEFAULT_CHARACTER_ENCODING) //
                .build();
    }

}
