package ca.ubc.cs.cpsc211.photo;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

public class SortedListModel extends AbstractListModel
{
    private SortedSet set;

    public SortedListModel()
    {
        set = new TreeSet();
    }

    @Override
    public Object getElementAt( int index )
    {
        return set.toArray()[ index ];
    }

    @Override
    public int getSize()
    {
        return set.size();
    }
    
    public void clear()
    {
        set.clear();
    }
    
    public void addElement(Object o)
    {
        if(set.add( o ))
            fireContentsChanged(this, 0, getSize() );
    }
    

}
