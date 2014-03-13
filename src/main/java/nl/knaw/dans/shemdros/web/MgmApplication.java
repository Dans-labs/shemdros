package nl.knaw.dans.shemdros.web;

import nl.knaw.dans.shemdros.core.ShemdrosException;
import nl.knaw.dans.shemdros.web.exc.ShemdrosExceptionMapper;

import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MgmApplication extends ResourceConfig
{

    private static final Logger logger = LoggerFactory.getLogger(MgmApplication.class);

    public MgmApplication() throws ShemdrosException
    {
        // ShemdrosExceptions get xml-formatted output.
        register(ShemdrosExceptionMapper.class);

        // register writer for jersey-media-multipart.
        register(MultiPartWriter.class);

        // register messagebodywriter for Collection.class.
        register(CollectionWriter.class);

        packages("nl.knaw.dans.shemdros.web");
        logger.info("Started " + this);
    }

}
