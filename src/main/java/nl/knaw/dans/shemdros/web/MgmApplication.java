package nl.knaw.dans.shemdros.web;

import nl.knaw.dans.shemdros.core.EnvPool;
import nl.knaw.dans.shemdros.core.ShemdrosException;
import nl.knaw.dans.shemdros.web.exc.ShemdrosExceptionMapper;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MgmApplication extends ResourceConfig
{

    private static final Logger logger = LoggerFactory.getLogger(MgmApplication.class);

    public MgmApplication() throws ShemdrosException
    {
        logger.info("Starting " + this);

        register(ShemdrosExceptionMapper.class);
        register(EnvPool.class);
        
        packages("nl.knaw.dans.shemdros.web");
        logger.info("Started " + this);
    }

}
