package ca.ubc.cs.cpsc211.photo;

import java.io.Serializable;
import java.util.Comparator;

public class OrderedTagComparator implements Comparator<Tag>, Serializable
{
    public int compare( Tag t1, Tag t2 )
    {
        return t1.getName().compareTo( t2.getName() );
    }

}
