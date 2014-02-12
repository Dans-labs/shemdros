package nl.knaw.dans.shemdros.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Props implements Serializable
{

    public static final String CONFIG_DIR = "conf/";
    public static final String PROP_FILE_EXTENSION = ".properties";

    private static final long serialVersionUID = -3291864914657242123L;
    private static final Logger logger = LoggerFactory.getLogger(Props.class);

    private final String configDir;
    private final String comment;

    private String location;
    private Properties properties;

    public Props()
    {
        configDir = CONFIG_DIR;
        comment = this.getClass().getName();
        logInstantiation();
    }

    public Props(String configDir)
    {
        this.configDir = configDir;
        comment = this.getClass().getName();
        logInstantiation();
    }

    private void logInstantiation()
    {
        logger.debug("Instantiated new properties for " + this.getClass().getName() + ". file location=" + getFileLocation().getAbsolutePath());
    }

    /**
     * Get the location of the properties file.
     * Default is "conf/{simpleclassname}.properties.
     * Subclasses can override.
     * 
     * @return the location of the properties file.
     */
    public String getLocation()
    {
        if (location == null)
        {
            location = configDir + this.getClass().getSimpleName().toLowerCase() + PROP_FILE_EXTENSION;
        }
        return location;
    }

    public String getComment()
    {
        return comment;
    }

    public File getFileLocation()
    {
        return new File(getLocation());
    }

    public String getString(String key)
    {
        return getString(key, null);
    }

    public String getString(String key, String defaultValue)
    {
        String value = getProperties().getProperty(key);
        if (value == null)
        {
            value = defaultValue;
            setString(key, value == null ? "" : value);
        }
        return value;
    }

    public void setString(String key, String value)
    {
        getProperties().setProperty(key, value);
    }

    public int getInt(String key)
    {
        return getInt(key, 0);
    }

    public int getInt(String key, int defaultValue)
    {
        int intValue = defaultValue;
        String stringValue = getProperties().getProperty(key);
        if (stringValue == null)
        {
            setInt(key, defaultValue);
        }
        else
        {
            try
            {
                intValue = Integer.parseInt(stringValue);
            }
            catch (NumberFormatException e)
            {
                logger.warn("Unable to parse int from " + stringValue, e);
            }
        }
        return intValue;
    }

    public void setInt(String key, int value)
    {
        getProperties().setProperty(key, Integer.toString(value));
    }

    public long getLong(String key)
    {
        return getLong(key, 0L);
    }

    public long getLong(String key, long defaultValue)
    {
        long longValue = defaultValue;
        String stringValue = getProperties().getProperty(key);
        if (stringValue == null)
        {
            setLong(key, defaultValue);
        }
        else
        {
            try
            {
                longValue = Long.parseLong(stringValue);
            }
            catch (NumberFormatException e)
            {
                logger.warn("Unable to parse long from " + stringValue, e);
            }
        }
        return longValue;
    }

    public void setLong(String key, long value)
    {
        getProperties().setProperty(key, Long.toString(value));
    }

    public float getFloat(String key)
    {
        return getFloat(key, 0.0F);
    }

    public float getFloat(String key, float defaultValue)
    {
        float floatValue = defaultValue;
        String stringValue = getProperties().getProperty(key);
        if (stringValue == null)
        {
            setFloat(key, defaultValue);
        }
        else
        {
            try
            {
                floatValue = Float.parseFloat(stringValue);
            }
            catch (NumberFormatException e)
            {
                logger.warn("Unable to parse float from " + stringValue, e);
            }
        }
        return floatValue;
    }

    public void setFloat(String key, float value)
    {
        getProperties().setProperty(key, Float.toString(value));
    }

    public double getDouble(String key)
    {
        return getDouble(key, 0.0D);
    }

    public double getDouble(String key, double defaultValue)
    {
        double doubleValue = defaultValue;
        String stringValue = getProperties().getProperty(key);
        if (stringValue == null)
        {
            setDouble(key, defaultValue);
        }
        else
        {
            try
            {
                doubleValue = Double.parseDouble(stringValue);
            }
            catch (NumberFormatException e)
            {
                logger.warn("Unable to parse double from " + stringValue, e);
            }
        }
        return doubleValue;
    }

    public void setDouble(String key, double value)
    {
        getProperties().setProperty(key, Double.toString(value));
    }

    public boolean getBoolean(String key)
    {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue)
    {
        boolean booleanValue = defaultValue;
        String stringValue = getProperties().getProperty(key);
        if (stringValue == null)
        {
            setBoolean(key, defaultValue);
        }
        else
        {
            booleanValue = Boolean.parseBoolean(stringValue);
        }
        return booleanValue;
    }

    public void setBoolean(String key, boolean value)
    {
        getProperties().setProperty(key, Boolean.toString(value));
    }

    public Properties getProperties()
    {
        if (properties == null)
        {
            try
            {
                logger.debug("Trying to load properties from " + getFileLocation().getAbsolutePath());
                properties = loadProperties(getLocation());
            }
            catch (IOException e)
            {
                String msg = "Could not load properties. property location=" + getLocation();
                logger.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }
        return properties;
    }

    public void saveProperties()
    {
        if (properties == null)
        {
            logger.warn("No properties loaded from " + getLocation());
        }
        else
        {
            try
            {
                storeProperties(properties, getLocation(), comment);
            }
            catch (IOException e)
            {
                String msg = "Could not save properties. property location=" + getLocation();
                logger.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }
    }

    private static Properties loadProperties(String location) throws IOException
    {
        Properties props = new Properties();
        createFileIfNotExists(location);
        InputStream inStream = null;
        try
        {
            inStream = new FileInputStream(location);
            props.load(inStream);
        }
        finally
        {
            if (inStream != null)
            {
                inStream.close();
            }
        }
        return props;
    }

    private static void storeProperties(Properties props, String location, String comments) throws IOException
    {
        createFileIfNotExists(location);
        OutputStream out = null;
        try
        {
            out = new FileOutputStream(location);
            props.store(out, comments);
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
        }
    }

    private static void createFileIfNotExists(String location) throws IOException
    {
        File file = new File(location);
        if (!file.exists())
        {
            logger.debug("Trying to create a new properties file at " + file.getAbsolutePath());
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }

}
