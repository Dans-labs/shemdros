package nl.knaw.dans.shemdros.pro;

import java.io.Flushable;
import java.io.IOException;
import java.util.List;

import nl.knaw.dans.shemdros.util.MonadSet;

public class FocusInterventionist implements Appendable, Flushable
{

    private final Appendable wrapped;
    private final String focusElementPart;
    private List<MonadSet> focusList;

    /**
     * Intervenes in RenderObjects stream and places the attribute focus="true" if monad is within range
     * of focusList monadSets.
     * 
     * @param wrapped
     *        the wrapped appendable.
     * @param focusElementPart
     *        the string that precedes the value of the monad given as attribute. example: "<w fm=".
     */
    public FocusInterventionist(Appendable wrapped, String focusElementPart)
    {
        this.wrapped = wrapped;
        this.focusElementPart = focusElementPart;
    }

    protected void setFocusList(List<MonadSet> focusList)
    {
        this.focusList = focusList;
    }

    @Override
    public Appendable append(CharSequence csq) throws IOException
    {
        String xml = csq.toString();
        if (xml.startsWith(focusElementPart))
        {
            int f = focusElementPart.length() + 1;
            int l = xml.substring(f).indexOf('"');

            int m = Integer.parseInt(xml.substring(f, f + l));
            boolean focus = false;
            for (MonadSet ms : focusList)
            {
                if (ms.isInSet(m))
                {
                    focus = true;
                    break;
                }
            }
            if (focus)
            {
                wrapped.append(xml.substring(0, f + (l + 1))).append(" focus=\"true\"").append(xml.substring(f + (l + 1)));
                return this;
            }
        }
        wrapped.append(csq);
        return this;
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

    @Override
    public void flush() throws IOException
    {
        if (wrapped instanceof Flushable)
        {
            ((Flushable) wrapped).flush();
        }

    }

}
