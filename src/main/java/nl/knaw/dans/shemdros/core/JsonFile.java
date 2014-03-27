package nl.knaw.dans.shemdros.core;

import java.io.File;

public class JsonFile
{

    public static final String DEFAULT = "default";

    private final String name;
    private final String path;

    private String focusElementPart;
    private String tagOrder;

    public JsonFile(String name, String path)
    {
        this.name = name;
        File file = new File(path);
        String absPath = file.getAbsolutePath();
        if (!file.exists())
        {
            throw new IllegalArgumentException("The file '" + absPath + "' does not exist.");
        }
        if (!file.canRead())
        {
            throw new IllegalArgumentException("The file '" + absPath + "' cannot be read.");
        }
        this.path = absPath;
    }

    public String getName()
    {
        return name;
    }

    public String getPath()
    {
        return path;
    }

    public String getFocusElementPart()
    {
        return focusElementPart;
    }

    public void setFocusElementPart(String focusElementPart)
    {
        this.focusElementPart = focusElementPart;
    }

    public String getTagOrder()
    {
        return tagOrder;
    }

    public void setTagOrder(String tagOrder)
    {
        this.tagOrder = tagOrder;
    }

    public JsonFile clone()
    {
        JsonFile clone = new JsonFile(name, path);
        clone.focusElementPart = focusElementPart;
        clone.tagOrder = tagOrder;
        return clone;
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append(this.getClass().getName()) //
                .append(" [").append("name=").append(name)//
                .append(", path=").append(path)//
                .append(", focusElementPart=").append(focusElementPart)//
                .append(", tagOrder=").append(tagOrder)//
                .append("]").toString();
    }

}
