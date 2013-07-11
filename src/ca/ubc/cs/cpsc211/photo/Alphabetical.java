package ca.ubc.cs.cpsc211.photo;

import java.io.Serializable;
import java.util.Comparator;

import javax.swing.JLabel;


public class Alphabetical implements Comparator<JLabel>, Serializable
{

 
    public int compare( JLabel o1, JLabel o2 )
    {
        return o1.getText().compareTo( o2.getText() );
    }

}
