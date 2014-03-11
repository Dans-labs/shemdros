package nl.knaw.dans.shemdros.core;

public class ShemdrosCompileException extends ShemdrosParameterException
{

    private static final long serialVersionUID = 2131891383516858554L;

    public ShemdrosCompileException()
    {

    }

    public ShemdrosCompileException(String message)
    {
        super(message);
    }

    public ShemdrosCompileException(Throwable cause)
    {
        super(cause);
    }

    public ShemdrosCompileException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
