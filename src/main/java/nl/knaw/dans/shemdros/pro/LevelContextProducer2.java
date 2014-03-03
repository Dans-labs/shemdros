package nl.knaw.dans.shemdros.pro;

import java.io.File;
import java.io.IOException;

import nl.knaw.dans.shemdros.util.MonadSet;

public class LevelContextProducer2 extends LevelContextProducer
{
    
    private final FocusInterventionist interventionist;

    public LevelContextProducer2(Appendable out, File jsonFile, String trigger)
    {
        super(new FocusInterventionist(out, trigger), jsonFile);
        interventionist = (FocusInterventionist) getOut();
        interventionist.setFocusList(getFocusMonadSets());
    }
    
    @Override
    protected void writeContext() throws IOException
    {
        for (MonadSet context : getContextMonadSets())
        {
            getRo().getContextPart(context.getFirst(), context.getLast(), interventionist);
        }
        clearMonadSets();
    }

}
