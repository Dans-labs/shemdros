package nl.knaw.dans.shemdros.util;

public class MonadSet
{

    private int first;
    private int last;

    public MonadSet(int first, int last)
    {
        setFirst(first);
        setLast(last);
    }

    public MonadSet(int first, int last, int offsetFirst, int offsetLast)
    {
        setFirst(first - offsetFirst);
        setLast(last + offsetLast);
    }

    public int getFirst()
    {
        return first;
    }

    public void setFirst(int first)
    {
        this.first = first;
        if (this.first < 1)
        {
            this.first = 1;
        }
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

    public boolean isInSet(int monad)
    {
        return monad >= first && monad <= last;
    }

}
