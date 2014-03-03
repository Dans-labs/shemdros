package nl.knaw.dans.shemdros.core;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.junit.BeforeClass;
import org.junit.Test;

public class CmdRenderObjectsTest
{
    
    private static CmdRenderObjects ro;
    
    @BeforeClass
    public static void beforeClass()
    {
        ro = new CmdRenderObjects(new File("src/test/resources/core/fetchinfo.json"));
    }
    
    @Test
    public void testVersion() throws Exception
    {
        String version = ro.getVersion();
        assertThat(version, startsWith("renderobjects from Emdros version"));
    }
    
    @Test
    public void testHelp() throws Exception
    {
        String help = ro.getHelp();
        //System.out.println(help);
        assertThat(help, containsString("OPTIONS:"));
    }
    
    @Test
    public void testContext() throws Exception
    {        
        String result = ro.getContextPart(1, 350);
        System.out.println(result);
    }
    
    @Test
    public void testStreaming() throws Exception
    {
        FileOutputStream fop = new FileOutputStream("target/test-streaming.xml");
        BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(fop));
        
        ro.getContextPart(430150, 1000000, buf);
        
        buf.close();
    }
    
    @Test
    public void testAppendable() throws Exception
    {
        ro.getContextPart(1, 300, new Appendable()
        {
            
            @Override
            public Appendable append(CharSequence csq, int start, int end) throws IOException
            {
                System.err.println("1 " + csq + " start=" + start + " end=" + end);
                return this;
            }
            
            @Override
            public Appendable append(char c) throws IOException
            {
                System.err.println("2 " + c);
                return this;
            }
            
            @Override
            public Appendable append(CharSequence csq) throws IOException
            {
                System.err.println("3 " + csq);
                return this;
            }
        });
    }

}
