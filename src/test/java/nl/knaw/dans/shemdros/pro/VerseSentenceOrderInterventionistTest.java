package nl.knaw.dans.shemdros.pro;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class VerseSentenceOrderInterventionistTest
{
    
    @Test
    public void testAppend() throws Exception
    {
        VerseSentenceOrderInterventionist vsoi = new VerseSentenceOrderInterventionist(new Appendable()
        {
            
            @Override
            public Appendable append(CharSequence csq, int start, int end) throws IOException
            {
                System.err.println(start + " " + end + " m1 '" + csq + "'");
                return this;
            }
            
            @Override
            public Appendable append(char c) throws IOException
            {
                System.err.println("m2 '" + c + "'");
                fail("append(char c) should not be called");
                return this;
            }
            
            @Override
            public Appendable append(CharSequence csq) throws IOException
            {
                System.err.println("m3 '" + csq + "'");
                fail("append(CharSequence csq) should not be called");
                return this;
            }
        });
        
        
    }

}
