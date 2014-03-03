package nl.knaw.dans.shemdros.pro;

import java.io.File;
import java.io.IOException;

import jemdros.EmdrosException;
import jemdros.MatchedObject;

public class MarksContextProducer extends ContextProducer
{

    private String contextMark = "context";
    private int markedContextLevel = -1;

    public MarksContextProducer(Appendable out, File jsonFile, String focusElementPart)
    {
        super(out, jsonFile);
        FocusInterventionist focusProducer = new FocusInterventionist(out, focusElementPart);
        focusProducer.setFocusList(getFocusMonadSets());
        setOut(focusProducer);
    }

    public String getContextMark()
    {
        return contextMark;
    }

    public void setContextMark(String contextMark)
    {
        this.contextMark = contextMark;
    }

    @Override
    protected void addRootAttributes() throws IOException
    {
        addAttribute("context-mark", contextMark);
    }
    
    @Override
    protected void renderMatchedObject(MatchedObject mo) throws EmdrosException, IOException
    {
        if (mo.getMarksString().contains(contextMark))
        {
            markedContextLevel = getStrawLevel();
        }
        super.renderMatchedObject(mo);
    }

    @Override
    protected boolean isContextMatch()
    {
        return markedContextLevel == getStrawLevel();
    }

}
