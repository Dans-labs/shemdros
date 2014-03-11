package nl.knaw.dans.shemdros.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import nl.knaw.dans.shemdros.os.OS;

public class CmdRenderObjects
{
    private final Database database;
    private final JsonFile jsonFile;
    private String stylesheetName = "base";

    protected CmdRenderObjects(Database database, JsonFile jsonFile)
    {
        this.database = database;
        this.jsonFile = jsonFile;
    }

    public String getVersion() throws IOException
    {
        return executeCommand("--version");
    }

    public String getHelp() throws IOException
    {
        return executeCommand("--help");
    }

    public String getContextPart(int fm, int lm) throws IOException
    {
        StringBuilder sb = buildContextPartCommand(fm, lm);
        return executeCommand(sb.toString());
    }

    public void getContextPart(int fm, int lm, Appendable out) throws IOException
    {
        StringBuilder sb = buildContextPartCommand(fm, lm);
        executeCommand(sb.toString(), out);
    }

    private StringBuilder buildContextPartCommand(int fm, int lm)
    {
        StringBuilder sb = new StringBuilder();
        appendDBInfo(sb);
        sb.append(" --fm ").append(fm).append(" --lm ").append(lm).append(" -s ").append(getStylesheetName()).append(" ").append(jsonFile.getPath())
                .append(" ").append(getDatabase().getInitialDB());
        return sb;
    }

    private void appendDBInfo(StringBuilder sb)
    {
        Database db = getDatabase();
        sb.append("-h ").append(db.getHostname()).append(" -u ").append(db.getUsername()).append(" -p ").append(db.getPassword()).append(" -b ")
                .append(db.getBackendName());
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
        String command = "renderobjects " + cmd;
        OS.execAndWait(command, out, perr);
        if (serr.toString().length() != 0)
        {
            throw new IOException("Executing command line returned error message: " + serr.toString());
        }
    }

    public Database getDatabase()
    {
        return database;
    }

    public String getStylesheetName()
    {
        return stylesheetName;
    }

    public void setStylesheetName(String stylesheetName)
    {
        this.stylesheetName = stylesheetName;
    }

    public JsonFile getJsonFile()
    {
        return jsonFile;
    }
}
