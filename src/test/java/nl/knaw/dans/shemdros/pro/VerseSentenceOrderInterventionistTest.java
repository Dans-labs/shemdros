package nl.knaw.dans.shemdros.pro;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class VerseSentenceOrderInterventionistTest
{

    @Test
    public void testAppendNormal() throws Exception
    {
        TestAppendable tap = new TestAppendable();
        VerseSentenceOrderInterventionist vsoi = new VerseSentenceOrderInterventionist(tap);

        String in1 = "<context_part>\n";
        vsoi.append(in1);
        // assertEquals(in1, tap.lastLine);

        String in2 = "<v d=\"Towrah\" b=\"Genesis\" c=\"1\" v=\"1\">\n";
        vsoi.append(in2);
        // assertEquals(in1, tap.lastLine);

        String in3 = "<s id_d=\"84383\">\n";
        vsoi.append(in3);
        // assertEquals(in3, tap.lastLine);

        String in4 = "<w fm=\"1\">בְּ</w>\n";
        vsoi.append(in4);
        // assertEquals(in4, tap.lastLine);

        String in5 = "</s>\n";
        vsoi.append(in5);
        // assertEquals(in4, tap.lastLine);

        String in6 = "</v>\n";
        vsoi.append(in6);
        // assertEquals(in6, tap.lastLine);

        // next verse
        String in7 = "<v d=\"Towrah\" b=\"Genesis\" c=\"1\" v=\"2\">\n";
        vsoi.append(in7);
        // assertEquals(in6, tap.lastLine);

        String in8 = "<s id_d=\"84384\">\n";
        vsoi.append(in8);
        // assertEquals(in8, tap.lastLine);

        String in9 = "</s>\n";
        vsoi.append(in9);
        // assertEquals(in8, tap.lastLine);

        String in10 = "</v>\n";
        vsoi.append(in10);
        // assertEquals(in10, tap.lastLine);
    }

    @Test
    public void testAppendStartWrong() throws Exception
    {
        TestAppendable tap = new TestAppendable();
        VerseSentenceOrderInterventionist vsoi = new VerseSentenceOrderInterventionist(tap);

        String in1 = "<context_part>\n";
        vsoi.append(in1);
        assertEquals(in1, tap.lastLine);

        String in3 = "<s id_d=\"84383\">\n";
        vsoi.append(in3);
        assertEquals(in1, tap.lastLine);

        String in2 = "<v d=\"Towrah\" b=\"Genesis\" c=\"1\" v=\"1\">\n";
        vsoi.append(in2);
        assertEquals(in3, tap.lastLine);

        String in4 = "<w fm=\"1\">בְּ</w>\n";
        vsoi.append(in4);
        assertEquals(in4, tap.lastLine);

        String in5 = "</s>\n";
        vsoi.append(in5);
        assertEquals(in4, tap.lastLine);

        String in6 = "</v>\n";
        vsoi.append(in6);
        assertEquals(in6, tap.lastLine);

        // next verse
        String in7 = "<v d=\"Towrah\" b=\"Genesis\" c=\"1\" v=\"2\">\n";
        vsoi.append(in7);
        assertEquals(in6, tap.lastLine);

        String in8 = "<s id_d=\"84384\">\n";
        vsoi.append(in8);
        assertEquals(in8, tap.lastLine);

        String in9 = "</s>\n";
        vsoi.append(in9);
        assertEquals(in8, tap.lastLine);

        String in10 = "</v>\n";
        vsoi.append(in10);
        assertEquals(in10, tap.lastLine);
    }

    @Test
    public void testAppendEndWrong() throws Exception
    {
        TestAppendable tap = new TestAppendable();
        VerseSentenceOrderInterventionist vsoi = new VerseSentenceOrderInterventionist(tap);

        String in1 = "<context_part>\n";
        vsoi.append(in1);
        assertEquals(in1, tap.lastLine);

        String in2 = "<v d=\"Towrah\" b=\"Genesis\" c=\"1\" v=\"1\">\n";
        vsoi.append(in2);
        assertEquals(in1, tap.lastLine);

        String in3 = "<s id_d=\"84383\">\n";
        vsoi.append(in3);
        assertEquals(in3, tap.lastLine);

        String in4 = "<w fm=\"1\">בְּ</w>\n";
        vsoi.append(in4);
        assertEquals(in4, tap.lastLine);

        String in6 = "</v>\n";
        vsoi.append(in6);
        assertEquals(in4, tap.lastLine);

        String in5 = "</s>\n";
        vsoi.append(in5);
        assertEquals(in6, tap.lastLine);

        // next verse
        String in7 = "<v d=\"Towrah\" b=\"Genesis\" c=\"1\" v=\"2\">\n";
        vsoi.append(in7);
        assertEquals(in6, tap.lastLine);

        String in8 = "<s id_d=\"84384\">\n";
        vsoi.append(in8);
        assertEquals(in8, tap.lastLine);

        String in9 = "</s>\n";
        vsoi.append(in9);
        assertEquals(in8, tap.lastLine);

        String in10 = "</v>\n";
        vsoi.append(in10);
        assertEquals(in10, tap.lastLine);
    }

    class TestAppendable implements Appendable
    {

        private String lastLine = null;

        @Override
        public Appendable append(CharSequence csq, int start, int end) throws IOException
        {
            System.err.println(start + " " + end + " m1 '" + csq + "'");
            fail("append(CharSequence csq, int start, int end) should not be called");
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
            System.err.print("m3 '" + csq + "'");
            if (!"\n".equals(csq.toString()))
                lastLine = csq.toString();
            return this;
        }
    }

}
