package nl.knaw.dans.shemdros.core;

public class ShemdrosParameterException extends ShemdrosException
{

    private static final long serialVersionUID = 1508664192712302351L;

    public ShemdrosParameterException()
    {
        super();
    }

    public ShemdrosParameterException(String message)
    {
        super(message);
    }

    public ShemdrosParameterException(Throwable cause)
    {
        super(cause);
    }

    public ShemdrosParameterException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
