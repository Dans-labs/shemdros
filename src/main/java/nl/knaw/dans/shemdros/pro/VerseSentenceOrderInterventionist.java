package nl.knaw.dans.shemdros.pro;

import java.io.Flushable;
import java.io.IOException;

@Deprecated
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
        String xml = csq.toString().trim();
        if (xml.startsWith("<v "))
        {
            verseStart = xml;
            writeStartVerse();
        }
        else if (xml.startsWith("<s "))
        {
            sentenceStart = xml;
            writeStartVerse();
        }
        else if (xml.startsWith("</v>"))
        {
            verseEnd = xml;
            writeEndVerse();
        }
        else if (xml.startsWith("</s>"))
        {
            sentenceEnd = xml;
            writeEndVerse();
        }
        else if (xml.length() < 3)
        {

        }
        else
        {
            writeStartSentence();
            wrapped.append(csq);
            writeEndSentence();
        }
        return this;
    }

    private void writeStartVerse() throws IOException
    {
        if (verseStart != null && sentenceStart != null)
        {
            wrapped.append(verseStart);
            wrapped.append("\n");
            wrapped.append(sentenceStart);
            wrapped.append("\n");
            verseStart = null;
            sentenceStart = null;
        }
    }

    private void writeEndVerse() throws IOException
    {
        if (sentenceEnd != null && verseEnd != null)
        {
            wrapped.append(sentenceEnd);
            wrapped.append("\n");
            wrapped.append(verseEnd);
            wrapped.append("\n");
            sentenceEnd = null;
            verseEnd = null;
        }
    }

    private void writeStartSentence() throws IOException
    {
        if (sentenceStart != null)
        {
            wrapped.append(sentenceStart);
            wrapped.append("\n");
            sentenceStart = null;
        }
    }

    private void writeEndSentence() throws IOException
    {
        if (sentenceEnd != null)
        {
            wrapped.append(sentenceEnd);
            // wrapped.append("\n");
            sentenceEnd = null;
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
