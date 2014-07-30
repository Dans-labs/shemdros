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
            // int tryCount = 0;
            // while (StringUtils.isBlank(version) && tryCount < 5)
            // {
            // tryCount++;
            // version = getVersion();
            // }
            // if (StringUtils.isBlank(version))
            // {
            // version = "MQL version information could not be obtained this time.";
            // }
        }
        catch (IOException e)
        {
            version = "unknown version: " + e.getMessage();
        }
        return "MQL-version: " + version;
    }

    public String getVersion() throws IOException
    {
        // return executeCommand("--version");
        return process("--version");
    }

    protected String executeCommand(String cmd) throws IOException
    {
        StringWriter sout = new StringWriter();
        PrintWriter pout = new PrintWriter(sout);
        executeCommand(cmd, pout);
        pout.flush();
        sout.flush();
        String value = sout.toString();
        pout.close();
        return value;
    }

    protected void executeCommand(String cmd, Appendable out) throws IOException
    {
        StringWriter serr = new StringWriter();
        PrintWriter perr = new PrintWriter(serr);
        String command = "mql " + cmd;
        OS.execAndWait(command, out, perr);
        perr.flush();
        if (serr.toString().length() != 0)
        {
            throw new IOException("Executing command line returned error message: " + serr.toString());
        }
        perr.close();
    }

    protected String process(String cmd) throws IOException
    {
        return OS.process("mql", cmd);
    }

}
