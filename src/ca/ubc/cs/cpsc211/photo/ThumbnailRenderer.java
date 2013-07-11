package ca.ubc.cs.cpsc211.photo;

import java.awt.Component;
import java.io.Serializable;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Class that creates a new Renderer for the thumbnail list to display a JLabel
 * with an image and text.
 * 
 * @author Riley
 * 
 */
public class ThumbnailRenderer implements ListCellRenderer, Serializable
{
    public Component getListCellRendererComponent( JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus )
    {
        Component component = (Component) value;
        return component;
    }
}
