package nl.knaw.dans.shemdros.web;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.glassfish.jersey.media.multipart.ContentDisposition;
import org.junit.Test;

public class FooTest
{
    
    @Test
    public void testName() throws Exception
    {
        ContentDisposition cd = new ContentDisposition("attachment; filename=\"fname.ext\"");
        System.err.println(cd.getFileName());
        System.err.println(cd.getType());
        System.err.println(cd.getSize());
        System.err.println(cd.getCreationDate());
        System.err.println(cd.getReadDate());
        
        System.err.println(cd);
    }

}
