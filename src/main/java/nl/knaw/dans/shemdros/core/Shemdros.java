package nl.knaw.dans.shemdros.core;

public interface Shemdros
{

    String DEFAULT_CHARACTER_ENCODING = "UTF-8";
    String DEFAULT_CHARSET = ";charset=" + DEFAULT_CHARACTER_ENCODING;
    String XML_VERSION = "1.0";
    String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    String DEFAULT_LIBJEMDROS_PATH = "/usr/local/lib/emdros/libjemdros.so";
    String DEFAULT_LIBHARVEST_PATH = "/usr/local/lib/emdros/libharvest.so";

}
