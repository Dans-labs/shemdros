package nl.knaw.dans.shemdros.core;

import nl.knaw.dans.shemdros.util.Props;

public class Build extends Props
{

    private static final long serialVersionUID = -8762171048015827119L;

    public Build()
    {
        super("");
    }

    public String getVersion()
    {
        return getString("version", "unknown");
    }

    public String getBuildDate()
    {
        return getString("build.date", "unknown");
    }

    public String toString()
    {
        return "Shemdros-version: " + getVersion() + "\nShemdros-build: " + getBuildDate();
    }

}
