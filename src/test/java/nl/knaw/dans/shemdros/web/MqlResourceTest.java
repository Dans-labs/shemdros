package nl.knaw.dans.shemdros.web;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.io.FileInputStream;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import nl.knaw.dans.shemdros.integration.IntegrationTest;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.MultiPartMediaTypes;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class MqlResourceTest
{

    private static WebTarget webTarget;

    @BeforeClass
    public static void beforeClass() throws Exception
    {
        Client client = ClientBuilder.newClient().register(MultiPartFeature.class);
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
        Response response = webTarget.path("query") //
                .request(MediaType.TEXT_XML) //
                .post(Entity.entity(query, MediaType.TEXT_PLAIN));

        assertThat(response.getStatus(), is(200));
        assertThat(response.getMediaType().toString(), is(MediaType.TEXT_XML));

        String mqlResult = response.readEntity(String.class);
        assertThat(mqlResult, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<mql-results>"));
        // System.err.println(mqlResult);
    }

    @Test
    public void testInvallidQuery() throws Exception
    {
        Response response = webTarget.path("query").request(MediaType.TEXT_XML).post(Entity.entity("SELECT foo", MediaType.TEXT_PLAIN));

        assertThat(response.getStatus(), is(400));
        assertThat(response.getMediaType().toString(), is(MediaType.TEXT_XML));

        String mqlResult = response.readEntity(String.class);
        assertThat(mqlResult, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<error>"));
        // System.err.println(mqlResult);
    }

    @Test
    public void testInvalidResultFormat() throws Exception
    {
        String query = IOUtils.toString(new FileInputStream("src/test/resources/queries/bh_lq01.mql"));
        Response response = webTarget.path("query") //
                .queryParam("result-format", "foo-bar").request(MediaType.TEXT_XML) //
                .post(Entity.entity(query, MediaType.TEXT_PLAIN));

        assertThat(response.getStatus(), is(400));
        assertThat(response.getMediaType().toString(), is(MediaType.TEXT_XML));
        String mqlResult = response.readEntity(String.class);
        // System.err.println(mqlResult);
        assertThat(mqlResult, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<error>"));
    }

    @Test
    public void testInvalidDatabase() throws Exception
    {
        String query = IOUtils.toString(new FileInputStream("src/test/resources/queries/bh_lq01.mql"));
        Response response = webTarget.path("query") //
                .queryParam("database", "foo-bar").request(MediaType.TEXT_XML) //
                .post(Entity.entity(query, MediaType.TEXT_PLAIN));

        assertThat(response.getStatus(), is(400));
        assertThat(response.getMediaType().toString(), is(MediaType.TEXT_XML));
        String mqlResult = response.readEntity(String.class);
        assertThat(mqlResult, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<error>"));
        // System.err.println(mqlResult);
    }

    @Test
    public void testRenderDefault() throws Exception
    {
        String query = IOUtils.toString(new FileInputStream("src/test/resources/queries/bh_lq01.mql"));
        Response response = webTarget.path("render") //
                .request(MediaType.TEXT_XML) //
                .post(Entity.entity(query, MediaType.TEXT_PLAIN));

        assertThat(response.getStatus(), is(200));
        assertThat(response.getMediaType().toString(), is(MediaType.TEXT_XML));

        String mqlResult = response.readEntity(String.class);
        assertThat(
                mqlResult,
                startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<context_list producer=\"nl.knaw.dans.shemdros.pro.LevelContextProducer\" contex-level=\"0\""));
        // System.err.println(mqlResult);
    }

    @Test
    public void testRenderAtMark() throws Exception
    {
        String query = IOUtils.toString(new FileInputStream("src/test/resources/queries/bh_lq01.mql"));
        Response response = webTarget.path("render") //
                .queryParam("renderer", "mark").queryParam("context-mark", "context").request(MediaType.TEXT_XML) //
                .post(Entity.entity(query, MediaType.TEXT_PLAIN));

        assertThat(response.getStatus(), is(200));
        assertThat(response.getMediaType().toString(), is(MediaType.TEXT_XML));

        String mqlResult = response.readEntity(String.class);
        assertThat(
                mqlResult,
                startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<context_list producer=\"nl.knaw.dans.shemdros.pro.MarksContextProducer\" context-mark=\"context\""));
        // System.err.println(mqlResult);
    }

    @Test
    public void testExecute() throws Exception
    {
        String query = IOUtils.toString(new FileInputStream("src/test/resources/queries/bh_lq01.mql"));
        Response response = webTarget.path("execute") //
                .queryParam("renderer", "mark").queryParam("context-mark", "context").request(MultiPartMediaTypes.MULTIPART_MIXED) //
                .post(Entity.entity(query, MediaType.TEXT_PLAIN));
        
        

        assertThat(response.getStatus(), is(200));
        assertThat(response.getMediaType().toString(), startsWith(MultiPartMediaTypes.MULTIPART_MIXED));
        
        MultiPart multiPart = response.readEntity(MultiPart.class);
        List<BodyPart> parts = multiPart.getBodyParts();
        assertThat(parts.size(), is(2));
        BodyPart first = parts.get(0);
        String firstXml = first.getEntityAs(String.class);
        //System.err.println(firstXml);
        assertThat(firstXml, startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<mql-results>"));
        
        String secondXml = parts.get(1).getEntityAs(String.class);
        //System.err.println(secondXml);
        assertThat(
                secondXml,
                startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<context_list producer=\"nl.knaw.dans.shemdros.pro.MarksContextProducer\" context-mark=\"context\""));
        
//        String mqlResult = response.readEntity(String.class);
//        System.err.println(mqlResult);
    }

}
