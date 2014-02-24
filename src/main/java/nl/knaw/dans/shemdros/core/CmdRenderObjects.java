package nl.knaw.dans.shemdros.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.glassfish.hk2.runlevel.RunLevelException;

import nl.knaw.dans.shemdros.os.OS;

public class CmdRenderObjects
{
    private static Database database;
    
    private String jsonFilename;
    private String stylesheetName = "base";
    
    @SuppressWarnings("unused")
    private CmdRenderObjects()
    {
        // Constructor for spring-initialization
    }
    
    public CmdRenderObjects(File jsonFile)
    {
        setJsonFile(jsonFile);
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
        sb.append(" --fm ").append(fm)
            .append(" --lm ").append(lm)
            .append(" -s ").append(getStylesheetName())
            .append(" ").append(jsonFilename)
            .append(" ").append(getDatabase().getInitialDB());
        return sb;
    }
    
    private void appendDBInfo(StringBuilder sb)
    {
        Database db = getDatabase();
        sb.append("-h ").append(db.getHostname())
            .append(" -u ").append(db.getUsername())
            .append(" -p ").append(db.getPassword())
            .append(" -b ").append(db.getBackendName());
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
    
    public void setDatabase(Database database)
    {
        CmdRenderObjects.database = database;
    }
    
    public Database getDatabase()
    {
        if (database == null)
        {
            database = new Database();
        }
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

    public String getJsonFilename()
    {
        return jsonFilename;
    }

    public void setJsonFile(File jsonFile)
    {
        if (!jsonFile.exists())
        {
            throw new RuntimeException("Json file not found: " + jsonFile.getAbsolutePath());
        }
        if (!jsonFile.canRead())
        {
            throw new RunLevelException("Unable to read json file: " + jsonFile.getAbsolutePath());
        }
        jsonFilename = jsonFile.getAbsolutePath();
    }

}
