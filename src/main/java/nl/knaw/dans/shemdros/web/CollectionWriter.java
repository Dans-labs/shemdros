package nl.knaw.dans.shemdros.web;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

public class CollectionWriter implements MessageBodyWriter<Collection<?>>
{

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return Collection.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(Collection<?> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        // deprecated by JAX-RS 2.0 and ignored by Jersey runtime
        return 0;
    }

    @Override
    public void writeTo(Collection<?> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
    {
        BufferedWriter buff = new BufferedWriter(new OutputStreamWriter(entityStream));
        for (Object o : t)
        {
            buff.append(o.toString());
            buff.append("\n");
        }
        buff.flush();
    }

}
