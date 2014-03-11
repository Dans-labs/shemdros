package nl.knaw.dans.shemdros.core;

import java.io.OutputStream;

public interface StreamingMqlResultConsumer extends MqlResultConsumer<Void>
{

    public void setOutputStream(OutputStream output) throws ShemdrosException;

}
