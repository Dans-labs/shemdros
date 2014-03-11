package nl.knaw.dans.shemdros.integration;

import nl.knaw.dans.shemdros.util.Props;

public class TestProps extends Props
{

    public static final String LOCATION = "/var/local/shebanq/test.properties";

    private static final long serialVersionUID = -8838198018566081774L;

    @Override
    public String getLocation()
    {
        return LOCATION;
    }

}
