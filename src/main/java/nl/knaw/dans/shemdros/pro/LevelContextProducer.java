package nl.knaw.dans.shemdros.pro;

import java.io.IOException;

import nl.knaw.dans.shemdros.core.ShemdrosParameterException;

public class LevelContextProducer extends ContextProducer
{

    private int contextLevel = 0;

    public LevelContextProducer(String databaseName, String jsonName) throws ShemdrosParameterException
    {
        super(databaseName, jsonName);
    }

    public int getContextLevel()
    {
        return contextLevel;
    }

    public void setContextLevel(int contextLevel)
    {
        this.contextLevel = contextLevel;
    }

    @Override
    protected void addRootAttributes() throws IOException
    {
        addAttribute("contex-level", contextLevel);
    }

    @Override
    protected boolean isContextMatch()
    {
        return getStrawLevel() == contextLevel;
    }

}
