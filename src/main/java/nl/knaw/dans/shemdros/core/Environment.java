package nl.knaw.dans.shemdros.core;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Environment
{
    private static final Logger logger = LoggerFactory.getLogger(Environment.class);

    private static final java.lang.reflect.Field LIBRARIES;

    private static final int MAX_TRY_COUNT_LOAD_LIBRARIES = 5;

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
     * @param libjemdrosPath
     * @param libharvestPath
     *        not used. jemdros.RenderObjects unusable because of error in this library.
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

    /**
     * Loading native libraries in a container environment like Tomcat has some drawbacks. If libraries
     * are already loaded, this method tries to unload libraries by calling <code>System.gc()</code> a
     * maximized number of times.
     * 
     * @param libjemdrosPath
     *        absolute path to libjemdros.so
     * @param libharvestPath
     *        absolute path to libharvest.so
     */
    private void loadLibrary(String libjemdrosPath, String libharvestPath)
    {
        try
        {
            int tryCount = 0;
            while (isEmdrosLoaded() && tryCount < MAX_TRY_COUNT_LOAD_LIBRARIES)
            {
                tryCount++;
                System.gc();
                Thread.sleep(5000L);
                logger.debug("Tried unloading libraries {} times.", tryCount);
            }
            if (isEmdrosLoaded())
            {
                throw new RuntimeException("Could not load native libraries because already loaded in another classloader.");
            }
            Runtime.getRuntime().load(libjemdrosPath);
            logger.info("==> Loaded dynamic library at path '{}'.", libjemdrosPath);
            Runtime.getRuntime().load(libharvestPath);
            logger.info("==> Loaded dynamic library at path '{}'.", libharvestPath);
        }
        catch (IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (Throwable t)
        {
            logger.info("Unknown exception while loading native libraries.", t);
        }
    }

    private static boolean isEmdrosLoaded() throws IllegalAccessException
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
        return loaded;
    }

}
