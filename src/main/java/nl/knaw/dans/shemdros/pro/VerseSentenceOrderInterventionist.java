package nl.knaw.dans.shemdros.pro;

import java.io.Flushable;
import java.io.IOException;

public class VerseSentenceOrderInterventionist implements Appendable, Flushable
{

    private final Appendable wrapped;
    private CharSequence verseStart;
    private CharSequence sentenceStart;
    private CharSequence sentenceEnd;
    private CharSequence verseEnd;

    public VerseSentenceOrderInterventionist(Appendable wrapped)
    {
        this.wrapped = wrapped;
    }

    @Override
    public void flush() throws IOException
    {
        if (wrapped instanceof Flushable)
        {
            ((Flushable) wrapped).flush();
        }
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException
    {
        String xml = csq.toString();
        if (xml.startsWith("<v "))
        {
            verseStart = csq;
            writeStart();
            return this;
        }
        else if (xml.startsWith("<s "))
        {
            sentenceStart = csq;
            writeStart();
            return this;
        }
        else if (xml.startsWith("</v>"))
        {
            verseEnd = csq;
            writeEnd();
            return this;
        }
        else if (xml.startsWith("</s>"))
        {
            sentenceEnd = csq;
            writeEnd();
            return this;
        }
        else
        {
            wrapped.append(csq);
        }
        return this;
    }

    private void writeStart() throws IOException
    {
        if (verseStart != null && sentenceStart != null)
        {
            wrapped.append(verseStart);
            wrapped.append("\n").append(sentenceStart);
            verseStart = null;
            sentenceStart = null;
        }
    }

    private void writeEnd() throws IOException
    {
        if (sentenceEnd != null && verseEnd != null)
        {
            wrapped.append(sentenceEnd);
            wrapped.append("\n").append(verseEnd);
            sentenceEnd = null;
            verseEnd = null;
        }
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) throws IOException
    {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public Appendable append(char c) throws IOException
    {
        throw new UnsupportedOperationException("Method not implemented");
    }

}
