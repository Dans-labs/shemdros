package nl.knaw.dans.shemdros.pro;

import java.io.File;
import java.io.IOException;

public class LevelContextProducer3 extends ContextProducer
{
    
    private int contextLevel = 0;

    public LevelContextProducer3(Appendable out, File jsonFile, String focusElementPart)
    {
        super(jsonFile);
        FocusInterventionist focusProducer = new FocusInterventionist(out, focusElementPart);
        focusProducer.setFocusList(getFocusMonadSets());
        setOut(focusProducer);
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
        addAttribute("contex-level", Integer.toString(contextLevel));
    }

    @Override
    protected boolean isContextMatch()
    {
        return getStrawLevel() == contextLevel;
    }

}
