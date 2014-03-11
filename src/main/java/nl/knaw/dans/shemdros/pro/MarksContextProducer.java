package nl.knaw.dans.shemdros.pro;

import java.io.IOException;

import nl.knaw.dans.shemdros.core.ShemdrosParameterException;
import jemdros.EmdrosException;
import jemdros.MatchedObject;

public class MarksContextProducer extends ContextProducer
{

    private String contextMark = "context";
    private int markedContextLevel = -1;

    public MarksContextProducer(String databaseName, String jsonName) throws ShemdrosParameterException
    {
        super(databaseName, jsonName);
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
