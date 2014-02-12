package nl.knaw.dans.shemdros.web;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.FileInputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;


public class MqlResourceTest
{
    
    private static WebTarget webTarget;
    
    @BeforeClass
    public static void beforeClass() throws Exception
    {
        Client client = ClientBuilder.newClient();
        webTarget = client.target("http://localhost:8080/shemdros/mql/");
    }
    
    @Test
    public void testingTest() throws Exception
    {
        Response response = webTarget.path("test").request().get();
        String answer = response.readEntity(String.class);
        assertThat(answer, is("OK Testing"));
    }
    
    @Test
    public void testQuery() throws Exception
    {
        String query = IOUtils.toString(new FileInputStream("src/test/resources/queries/bh_lq01.mql"));
        Response response = webTarget.path("query").request(MediaType.TEXT_XML)
                .post(Entity.entity(query, MediaType.TEXT_PLAIN));
        
        assertThat(response.getStatus(), is(200));        
        assertThat(response.getMediaType().toString(), is(MediaType.TEXT_XML));
        
        String mqlResult = response.readEntity(String.class);
        assertThat(mqlResult, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
    }
    
    @Test
    public void testInvallidQuery() throws Exception
    {
        Response response = webTarget.path("query").request(MediaType.TEXT_XML)
                .post(Entity.entity("SELECT foo", MediaType.TEXT_PLAIN));
        
        assertThat(response.getStatus(), is(200));        
        assertThat(response.getMediaType().toString(), is(MediaType.TEXT_XML));
        
        String mqlResult = response.readEntity(String.class);
        assertThat(mqlResult, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
    }

}
