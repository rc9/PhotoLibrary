package ca.ubc.cs.cpsc211.photo;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;

public class SortedImageModel extends AbstractListModel
{
    private SortedSet set;

    public SortedImageModel()
    {
        set = new TreeSet<JLabel>(new Alphabetical());
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
    
    public void removeElement(Object o)
    {
        if(set.remove( o ))
            fireContentsChanged(this, 0, getSize());
        
    }
    

}
