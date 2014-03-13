package nl.knaw.dans.shemdros.web;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class CollectionWriterTest
{

    private CollectionWriter colw = new CollectionWriter();

    @Test
    public void testIsWritable() throws Exception
    {
        assertTrue(colw.isWriteable(List.class, null, null, null));
        assertTrue(colw.isWriteable(ArrayList.class, null, null, null));
        assertTrue(colw.isWriteable(Set.class, null, null, null));
    }

}
