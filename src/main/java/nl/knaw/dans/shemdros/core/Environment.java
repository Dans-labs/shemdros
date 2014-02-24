package nl.knaw.dans.shemdros.core;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Environment
{
    private static final Logger logger = LoggerFactory.getLogger(Environment.class);

    private static final java.lang.reflect.Field LIBRARIES;

    static
    {
        try
        {
            LIBRARIES = ClassLoader.class.getDeclaredField("loadedLibraryNames");
            LIBRARIES.setAccessible(true);
        }
        catch (NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
        catch (SecurityException e)
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static String[] getLoadedLibraries(final ClassLoader loader) throws IllegalArgumentException, IllegalAccessException
    {
        final Vector<String> libraries = (Vector<String>) LIBRARIES.get(loader);
        return libraries.toArray(new String[] {});
    }
    
    /**
     * 
     * @param libjemdrosPath
     * @param libharvestPath not used. jemdros.RenderObjects unusable because of error in this library.
     */
    public Environment(String libjemdrosPath, String libharvestPath)
    {
        if (libjemdrosPath == null)
        {
            libjemdrosPath = Shemdros.DEFAULT_LIBJEMDROS_PATH;
        }
        if (libharvestPath == null)
        {
            libharvestPath = Shemdros.DEFAULT_LIBHARVEST_PATH;
        }
        loadLibrary(libjemdrosPath, libharvestPath);
    }

    private void loadLibrary(String libjemdrosPath, String libharvestPath)
    {
        try
        {
            boolean loaded = false;
            final String[] libraries = getLoadedLibraries(ClassLoader.getSystemClassLoader()); 
            for (int i = 0; i < libraries.length; i++)
            {
                if (libraries[i].contains("emdros"))
                {
                    logger.info("Emdros native library already loaded: '{}'.", libraries[i]);
                    loaded = true;
                }
            }
            if (!loaded)
            {
                System.load(libjemdrosPath);
                logger.info("Loaded dynamic library at path '{}'.", libjemdrosPath);
                System.load(libharvestPath);
                logger.info("Loaded dynamic library at path '{}'.", libharvestPath);
            }
            
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

}
