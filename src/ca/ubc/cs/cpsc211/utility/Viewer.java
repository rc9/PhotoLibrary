package ca.ubc.cs.cpsc211.utility;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * Simple viewer.
 * 
 * 
 * @author CPSC 211 Instructor and Riley Chang
 */

public class Viewer extends JPanel {

	// We use a panel to hold all of the thumbnails we want to view.
	private JPanel thumbnailPanel;

	/** 
	 * Constructor
	 * @pre name != null
	 * @post New instance of Viewer created
	 * @param name The name of the window 
	 */
	public Viewer(){

		setLayout(new GridLayout());

		// Create a panel for the thumbnails that we can make scrollable below 
		thumbnailPanel = new JPanel();
		thumbnailPanel.setLayout(new GridLayout(0, 5));
	}
	
	public Viewer(LayoutManager lm)
	{
	    super(lm);

	        setLayout(new GridLayout());

	        // Create a panel for the thumbnails that we can make scrollable below 
	        thumbnailPanel = new JPanel();
	        thumbnailPanel.setLayout(new GridLayout());
	}

	/**
	 * Add an image into the viewer
	 * 
	 * @pre image != null
	 * @post Image is ready to be displayed but not yet visible 
	 * @param image Image to display
	 */
	public void addImage(Image image) {
		thumbnailPanel.add(new JLabel(new ImageIcon(image)));
	}

	/**
	 * Display all added images
	 * 
	 * @pre true
	 * @post Window is displayed
	 */
	public void display() {
		JScrollPane scrollPane = new JScrollPane(thumbnailPanel);
		add(scrollPane);
		setVisible(true);
	}

}
