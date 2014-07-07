package nl.knaw.dans.shemdros.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import nl.knaw.dans.shemdros.os.OS;

public class CmdMql
{

    public String getVersionString()
    {
        String version;
        try
        {
            version = getVersion();
        }
        catch (IOException e)
        {
            version = "unknown version: " + e.getMessage();
        }
        return "MQL-version: " + version;
    }

    public String getVersion() throws IOException
    {
        return executeCommand("--version");
    }

    protected String executeCommand(String cmd) throws IOException
    {
        StringWriter sout = new StringWriter();
        PrintWriter pout = new PrintWriter(sout);
        executeCommand(cmd, pout);
        return sout.toString();
    }

    protected void executeCommand(String cmd, Appendable out) throws IOException
    {
        StringWriter serr = new StringWriter();
        PrintWriter perr = new PrintWriter(serr);
        String command = "mql " + cmd;
        OS.execAndWait(command, out, perr);
        if (serr.toString().length() != 0)
        {
            throw new IOException("Executing command line returned error message: " + serr.toString());
        }
    }

}
