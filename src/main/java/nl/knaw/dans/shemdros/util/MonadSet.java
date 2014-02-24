package nl.knaw.dans.shemdros.util;

public class MonadSet
{

    private int first;
    private int last;
    
    public MonadSet(int first, int last)
    {
        this.first = first;
        this.last = last;
    }

    public int getFirst()
    {
        return first;
    }

    public void setFirst(int first)
    {
        this.first = first;
    }

    public int getLast()
    {
        return last;
    }

    public void setLast(int last)
    {
        this.last = last;
    }
    
    public int getSpan()
    {
        return last - first + 1;
    }

}
