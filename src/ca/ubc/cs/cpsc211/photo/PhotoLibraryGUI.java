package ca.ubc.cs.cpsc211.photo;

import ca.ubc.cs.cpsc211.utility.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Changed photo class to make a date.
 * 
 * @author Riley
 * 
 */
public class PhotoLibraryGUI implements Serializable
{

    private final Dimension PIC_AREA = new Dimension( 500, 500 );
    private final Dimension THUMBNAIL_AREA = new Dimension( 50, 100 );
    private final Dimension THUMBNAIL_SIZE = new Dimension( 80, 100 );
    private final Dimension TAB_AREA = new Dimension( 170, 100 );
    private final Dimension TEXT_AREA = new Dimension( 50, 100 );

    // Managers to provide functionality
    private final PhotoManager pm;
    private final TagManager tm;

    // Instance of the photo in the display
    private Photo photo;

    // The main frame for the GUI
    private JFrame frame;

    // The tab pane and its tabs
    private JTabbedPane tabs;
    private JComponent albumTab;
    private JComponent tagTab;
    private JComponent dateTab;

    // The labels
    private JLabel dateAdded;
    private JLabel photoAlbum;

    // The lists
    private JList tagList;
    private JList albumList;
    private JList thumbnailList;

    // The list models
    private SortedListModel tagListModel = new SortedListModel();
    private SortedListModel albumListModel = new SortedListModel();
    private SortedImageModel thumbnailListModel = new SortedImageModel();

    // The panels
    private JPanel details;
    private JPanel photoSection;
    private JPanel tabsPanel;
    private JPanel image;
    private JPanel specifics;

    // The menubar for the GUI
    private JMenuBar menuBar;

    // The buttons
    private JButton newAlbum = new JButton( "New Album" );
    private JButton deleteAlb = new JButton( "Remove Album" );
    private JButton movePhoto = new JButton( "Move to another Album" );
    private JButton removePhoto = new JButton( "Remove from Library" );
    private JButton addAnotherTag = new JButton( "Add Tag" );
    private JButton removeTag = new JButton( "Remove Tag" );
    private JButton detag = new JButton( "De-tag Photo" );

    // The text areas
    private JTextArea tagText;
    private JTextArea descripText;

    // The scroll areas
    private JScrollPane thumbnails;
    private JScrollPane imageScroll;
    private JScrollPane tagArea;
    private JScrollPane descripArea;

    public PhotoLibraryGUI( PhotoManager pm, TagManager tm )
    {
        this.pm = pm;
        this.tm = tm;
    }

    /**
     * Displays the frame.
     * 
     * @pre true
     * @post the window is shown.
     */
    public void showLibrary()
    {
        frame = new JFrame( "Photo Library" );
        frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        frame.addWindowListener( new WindowAdapter()
        {
            public void windowClosing( WindowEvent e )
            {
                closeProgram();
            }

        } );

        // Add everything!
        initializeButtons();
        initializeLists();
        initializeThumbnails();
        initializeScrollers();
        initializeTabs();
        initializePanels();
        initializeMenu();

        // Add panels to the frame.
        frame.getContentPane().add( tabsPanel, BorderLayout.WEST );
        frame.getContentPane().add( photoSection, BorderLayout.CENTER );

        // Sets the frame and shows it.
        frame.pack();
        frame.setVisible( true );

        restart();
        updateLists();
    }

    /**
     * Helper method to make the buttons.
     * 
     * @pre true
     * @post the buttons have been added to the frame.
     */
    private void initializeButtons()
    {
        // These buttons should be false initially since nothing should be in
        // the system yet.
        addAnotherTag.setEnabled( false );
        detag.setEnabled( false );
        removePhoto.setEnabled( false );
        movePhoto.setEnabled( false );
        if(albumListModel.getSize() == 0)
            deleteAlb.setEnabled( false );
        removeTag.setEnabled( false );

        // Adds a new albums to the system.
        newAlbum.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                String name = JOptionPane
                        .showInputDialog( "Please enter the name of the new album." );

                // Make sure the user entered something.
                if( name != null && !name.isEmpty() )

                    // Add the specified album to the system unless it already
                    // exists.
                    if( !pm.getAlbums().contains( pm.findAlbum( name ) ) )
                    {
                        pm.addAlbum( new Album( name ) );
                    }

                    else
                    {
                        JOptionPane.showMessageDialog( frame,
                                "That album already exists!", "Error",
                                JOptionPane.ERROR_MESSAGE );
                    }
                updateLists();
                updateButtons();
            }
        } );

        // Removes an album from the system.
        deleteAlb.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent arg )
            {

                Album name;
                try
                {
                    name = chooseAlbum();
                    pm.removeAlbum( name );
                    updateLists();
                    updateNull();
                    thumbnailListModel.clear();
                }
                catch( NothingChosenException e )
                {
                    // Do nothing.
                }
            }
        } );

        // Adds a tag to the photo.
        addAnotherTag.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                String name = JOptionPane
                        .showInputDialog( "Please enter the name of the tag.\n"
                                + "Note tags are case sensitive." );

                // Make sure user entered something.
                if( name != null && !name.isEmpty() )
                {
                    // Add the specified tag. If it does not exist create it.
                    try
                    {
                        Tag newTag = tm.createTag( name );
                        photo.addTag( newTag );
                    }
                    catch( DuplicateTagException exc )
                    {
                        photo.addTag( tm.findTag( name ) );
                    }
                }
                updateInfo();
            }

        } );

        removeTag.addActionListener( new ActionListener()
        {
            // Remove the tag from the system.
            public void actionPerformed( ActionEvent arg )
            {

                Object[] choices = tm.getTags().toArray();
                for( Object o : choices )
                {
                    Tag o2 = (Tag) o;
                    o = o2;
                }

                Tag name = (Tag) JOptionPane
                        .showInputDialog( frame,
                                "Please select a Tag from the list.",
                                "Choose the Tag", JOptionPane.DEFAULT_OPTION,
                                null, choices, choices[ 0 ] );

                // There is an error in the TagManager that does not allow for
                // removal of tag from multiple photos. This works around that
                // by finding all photo that have that tag, then remove that tag
                // from the photo.
                Iterator<Photo> itr = pm.getPhotos().iterator();
                while( itr.hasNext() )
                {
                    Photo p = itr.next();
                    if( p.getTags().contains( name ) )
                        p.removeTag( name );
                }

                // Must also remove the tag from the system.
                tm.removeTag( name.getName() );

                updateInfo();
            }
        } );

        // Remove tag from a specific photo.
        detag.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                // Cast the set to an array of tags.
                Object[] choices = photo.getTags().toArray();
                for( Object o : choices )
                {
                    Tag o2 = (Tag) o;
                    o = o2;
                }

                Tag name = (Tag) JOptionPane
                        .showInputDialog( frame,
                                "Please select a Tag from the list.",
                                "Choose the Tag", JOptionPane.DEFAULT_OPTION,
                                null, choices, choices[ 0 ] );

                // Make sure user didn't cancel.
                if( name != null )
                {
                    // Remove the tag.
                    photo.removeTag( name );

                    // if there are no more photos with this tag it is
                    // deleted.
                    if( name.getPhotos().isEmpty() )
                    {
                        tm.removeTag( name.getName() );
                    }

                    updateInfo();
                }
            }
        } );

        // Moves the photo to a different album.
        movePhoto.addActionListener( new MovingListener() );

        // Removes the photo from the system.
        removePhoto.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                // The choices for the option pane.
                String[] options = { "Yes, I'm Sure", "No!" };

                int n = JOptionPane.showOptionDialog( frame,
                        "Are you sure you want to delete this photo?",
                        "Remove Photo?", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options,
                        options[ 1 ] );

                // Make sure this is what the user wants.
                if( n == JOptionPane.YES_OPTION )
                {
                    try
                    {
                        // Create a temporary set to store the references to all
                        // the tags of the photo. This ensures all the tags in
                        // the set will be removed without changing the set
                        // being iterated over.
                        Set<Tag> temp = new HashSet<Tag>();
                        temp.addAll( photo.getTags() );
                        Iterator<Tag> itr = temp.iterator();

                        while( itr.hasNext() )
                        {
                            // Remove the tag from the photo.
                            Tag tag = itr.next();
                            photo.removeTag( tag );

                            // If the deleted photo was the only photo with the
                            // tag, delete the tag.
                            if( tag.getPhotos().isEmpty() )
                                tm.removeTag( tag.getName() );
                        }

                        // Remove the photo from the library.
                        pm.findAlbum( photo.getAlbum().getName() ).removePhoto(
                                photo );

                        // Refresh the displays and info.
                        // remove the thumbnail!!
                        updateLists();
                        updateNull();

                    }
                    catch( PhotoDoesNotExistException e1 )
                    {// This should never happen.
                    }
                }
            }
        } );
    }

    /**
     * Helper method to set up the panels. There is a lot of wrapping so the
     * format of the frame will look appealing.
     * 
     * @pre true
     * @post the panels have been added to the frame.
     */
    private void initializePanels()
    {
        // Some Labels to show the photo info.
        dateAdded = new JLabel();
        dateAdded.setText( "Added: " );
        photoAlbum = new JLabel();
        photoAlbum.setText( "Album: " );
        dateAdded.setAlignmentX( Component.CENTER_ALIGNMENT );
        photoAlbum.setAlignmentX( Component.CENTER_ALIGNMENT );

        // Panel that holds the buttons to move and delete a photo.
        JPanel labels = new JPanel();
        labels.setLayout( new BoxLayout( labels, 1 ) );
        movePhoto.setAlignmentX( Component.CENTER_ALIGNMENT );
        removePhoto.setAlignmentX( Component.CENTER_ALIGNMENT );
        labels.add( movePhoto );
        labels.add( removePhoto );

        // Panel that holds the buttons panel and the info about the photo.
        specifics = new JPanel();
        specifics.setLayout( new BoxLayout( specifics, 1 ) );
        specifics.setBorder( new TitledBorder( "Photo: " ) );
        specifics.add( dateAdded );
        specifics.add( photoAlbum );
        specifics.add( labels );

        // Panel that holds buttons to configure photo's tags.
        JPanel lowerButtons = new JPanel();
        lowerButtons
                .setLayout( new BoxLayout( lowerButtons, BoxLayout.X_AXIS ) );
        addAnotherTag.setAlignmentX( Component.CENTER_ALIGNMENT );
        detag.setAlignmentX( Component.CENTER_ALIGNMENT );
        lowerButtons.add( addAnotherTag );
        lowerButtons.add( detag );

        // Panel of the description and tag boxes. It also has the buttons to
        // change the photo's tags.
        JPanel editing = new JPanel();
        editing.setLayout( new BoxLayout( editing, BoxLayout.Y_AXIS ) );
        editing.add( descripArea );
        editing.add( tagArea );
        editing.add( lowerButtons );

        // Panel that holds all the above panels to be displayed on East side of
        // frame.
        details = new JPanel( new BorderLayout() );
        details.add( specifics, BorderLayout.NORTH );
        details.add( editing, BorderLayout.CENTER );

        // Panel on the West side of frame that holds the tabs.
        tabsPanel = new JPanel( new BorderLayout() );
        tabsPanel.add( tabs );

        // Panel in the center of frame where the image of the photo appears.
        // Puts this panel in a scrolling area.
        image = new JPanel( new GridLayout() );
        image.setBackground( new Color( 250, 250, 250 ) );
        imageScroll = new JScrollPane( image );
        imageScroll.setPreferredSize( PIC_AREA );

        // Panel in frame that includes all the other panels except the tabs.
        photoSection = new JPanel( new BorderLayout() );
        photoSection.add( thumbnails, BorderLayout.NORTH );
        photoSection.add( imageScroll, BorderLayout.CENTER );
        photoSection.add( details, BorderLayout.EAST );
    }

    /**
     * Helper method to set up scroll panes.
     * 
     * @pre true
     * @post the scroll areas of text have been added.
     */
    private void initializeScrollers()
    {
        // Put a text area into each scroll pane.
        // Give the scroll panes borders.
        descripText = new JTextArea();
        descripText.setLineWrap( true );
        descripText.setEditable( false );
        descripText.setPreferredSize( TEXT_AREA );

        descripArea = new JScrollPane( descripText );
        descripArea
                .setBorder( BorderFactory.createTitledBorder( "Description" ) );

        tagText = new JTextArea();
        tagText.setLineWrap( true );
        tagText.setEditable( false );
        tagArea = new JScrollPane( tagText );

        tagArea.setBorder( BorderFactory.createTitledBorder( "Tags" ) );
        tagArea.setPreferredSize( TEXT_AREA );
    }

    /**
     * Helper method to set up the lists.
     * 
     * @pre true
     * @post the lists have been added to the frame.
     */
    private void initializeLists()
    {
        // Making the lists.
        albumList = new JList( albumListModel );
        tagList = new JList( tagListModel );
        // photoTags = new TagList( new SortedListModel() );
        thumbnailList = new JList( thumbnailListModel );
        thumbnailList.setLayoutOrientation( JList.VERTICAL_WRAP );
        thumbnailList.setVisibleRowCount( 1 );
        thumbnailList.setBackground( new Color( 250, 250, 250 ) );
        thumbnailList.setCellRenderer( new ThumbnailRenderer() );

        // Sets their action listeners.
        albumList.addListSelectionListener( new AlbumListListener() );
        tagList.addListSelectionListener( new TagListListener() );
        thumbnailList.addListSelectionListener( new ImageListListener() );

        // Puts each in a scroll pane and wraps that in a panel.
        albumTab = new JPanel( new BorderLayout() );
        tagTab = new JPanel( new BorderLayout() );
        dateTab = new JPanel( new BorderLayout() );
        JScrollPane tagScroll = new JScrollPane( tagList );
        JScrollPane albumScroll = new JScrollPane( albumList );
        JPanel albumTabButtons = new JPanel( new BorderLayout() );
        albumTab.add( albumScroll, BorderLayout.CENTER );
        albumTab.add( albumTabButtons, BorderLayout.SOUTH );
        tagTab.add( tagScroll, BorderLayout.CENTER );

        // Adds some buttons to the panels.
        albumTabButtons.add( newAlbum, BorderLayout.NORTH );
        albumTabButtons.add( deleteAlb, BorderLayout.SOUTH );
        tagTab.add( removeTag, BorderLayout.SOUTH );
    }

    /**
     * Helper method that sets up the tab panel.
     * 
     * @pre true
     * @post the tabs have been added to the frame.
     */
    private void initializeTabs()
    {
        tabs = new JTabbedPane();

        tabs.add( "Albums", albumTab );
        tabs.setMnemonicAt( 0, KeyEvent.VK_A );

        tabs.add( "Tags", tagTab );
        tabs.setMnemonicAt( 1, KeyEvent.VK_T );

        tabs.setTabLayoutPolicy( JTabbedPane.TOP );
        tabs.setPreferredSize( TAB_AREA );
    }

    /**
     * Helper method to create the thumbnail panel and all its components.
     * 
     * @pre true
     * @post the thumbnail pane has been added to the frame.
     */
    private void initializeThumbnails()
    {
        // Makes a scroll pane with a panel with a list.
        thumbnails = new JScrollPane( thumbnailList );
        thumbnails.setPreferredSize( THUMBNAIL_AREA );
        thumbnails
                .setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER );
    }

    /**
     * Helper method to create the menu bar, menu items, and menu.
     * 
     * @pre true
     * @post the menu is now in the frame.
     */
    private void initializeMenu()
    {
        JMenu first, second, third;
        JMenuItem menuItem1, menuItem2, menuItem3, menuItem4, menuItem5, menuItem6;

        first = new JMenu( "File" );
        second = new JMenu( "Edit" );
        third = new JMenu( "View" );

        first.setMnemonic( KeyEvent.VK_F );
        second.setMnemonic( KeyEvent.VK_E );
        third.setMnemonic( KeyEvent.VK_V );

        menuItem1 = new JMenuItem( "Quit" );
        menuItem2 = new JMenuItem( "Load Photo" );
        menuItem3 = new JMenuItem( "Description" );
        menuItem4 = new JMenuItem( "Rename Album" );
        menuItem5 = new JMenuItem( "Tag Cloud" );
        menuItem6 = new JMenuItem( "Save" );

        menuItem1.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Q,
                ActionEvent.CTRL_MASK ) );
        menuItem2.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_L,
                ActionEvent.CTRL_MASK ) );
        menuItem3.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_D,
                ActionEvent.CTRL_MASK ) );
        menuItem4.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_R,
                ActionEvent.CTRL_MASK ) );
        menuItem5.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_T,
                ActionEvent.CTRL_MASK ) );
        menuItem6.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S,
                ActionEvent.CTRL_MASK ) );

        menuItem1.addActionListener( new ActionListener()
        {
            // When quit is selected, destroy the application
            public void actionPerformed( ActionEvent e )
            {
                closeProgram();
            }
        } );

        menuItem2.addActionListener( new ActionListener()
        {
            // Loading a photo into the system.
            public void actionPerformed( ActionEvent e )
            {
                String name = null;
                if( pm.getAlbums().size() == 0 )
                {
                    // Prompt for the name and make sure one is given.
                    name = JOptionPane
                            .showInputDialog( "You don't seem to have any albums yet.\n"
                                    + "Give me a name and I'll make you one!" );
                    if( name != null && !name.isEmpty() )
                    {
                        pm.addAlbum( new Album( name ) );
                        updateLists();
                    }
                }
                else
                {
                    try
                    {
                        name = chooseAlbum().getName();
                    }
                    catch( NothingChosenException e1 )
                    {
                        // Do Nothing
                    }
                }

                // Only prompt for a file if a name was given for the album.
                if( name != null && !name.isEmpty() )
                {
                    String photoName;

                    // User can find the file of the photo.
                    JFileChooser fc = new JFileChooser();
                    fc.changeToParentDirectory();
                    fc.setFileFilter( new FileNameExtensionFilter( "Jpeg",
                            "jpg" ) );
                    int result = fc.showOpenDialog( frame );

                    if( result == JFileChooser.APPROVE_OPTION )
                    {
                        // Get the file selected by the user and its name.
                        // Must remove extension from the name. The user can
                        // also enter their own name if they already know the
                        // file name.
                        File selectedFile = fc.getSelectedFile();
                        photoName = selectedFile.getName();

                        // In case user input custom name, we must check before
                        // we remove the extension.
                        if( photoName.contains( "." ) )
                        {
                            photoName = photoName.substring( 0, photoName
                                    .indexOf( "." ) );
                        }

                        // See if the photo is already in the system.
                        boolean present = false;
                        for( Photo p : pm.getPhotos() )
                            if( photoName.equals( p.getName() ) )
                                present = true;

                        if( present )
                        {
                            int answer = JOptionPane
                                    .showConfirmDialog(
                                            frame,
                                            "That photo already belongs to another album.\n"
                                                    + "Would you like to move it to this album?" );
                            if( answer == JOptionPane.YES_OPTION )
                                movePhoto( name );
                        }

                        else
                        {
                            try
                            {
                                // Load the photo into the system and put it
                                // into the specified album.
                                photo = new Photo( photoName );
                                photo.loadPhoto( selectedFile.getPath() );
                                pm.findAlbum( name ).addPhoto( photo );

                                // Clear the image display and add the new
                                // photo. Also update all the info.
                                image.removeAll();
                                image.add( createPhotoLabel() );
                                updateInfo();
                                updateThumbnails( name );
                            }

                            catch( PhotoDoesNotExistException exc )
                            {
                                // Say when the file cannot be found from the
                                // name.
                                JOptionPane.showMessageDialog( frame,
                                        "I can't find that photo.\n"
                                                + "Check the name.", "Error",
                                        JOptionPane.ERROR_MESSAGE );
                            }
                            catch( PhotoAlreadyInAlbumException e1 )
                            { // This error is never caught so I work around
                                // it above.
                            }
                        }
                    }
                    else
                    {
                        if( pm.getAlbums().size() == 1 )
                            pm.removeAlbum( pm.findAlbum( name ) );
                        updateLists();
                    }
                }
            }
        } );

        menuItem3.addActionListener( new ActionListener()
        {
            // To edit the description of a photo.
            public void actionPerformed( ActionEvent e )
            {
                String newDescription = JOptionPane
                        .showInputDialog( "Enter the new description of this photo" );
                if( newDescription != null )
                {
                    // Set description to the text area and the photo.
                    photo.setDescription( newDescription );
                    descripText.setText( newDescription );
                }
            }
        } );

        menuItem4.addActionListener( new ActionListener()
        {
            // To change an album's name.
            public void actionPerformed( ActionEvent e )
            {

                Album name = null;
                try
                {
                    name = chooseAlbum();
                }
                catch( NothingChosenException e1 )
                {
                    // Do Nothing.
                }
                if( name != null )
                {
                    String newName = JOptionPane
                            .showInputDialog( "Enter the new name of the Album." );

                    // Make sure user entered a name.
                    if( newName != null && !newName.isEmpty() )
                    {
                        // Check that there is not already an album with this
                        // new name.
                        if( pm.getAlbums().contains( pm.findAlbum( newName ) ) )
                        {
                            JOptionPane.showMessageDialog( frame,
                                    "Another album already has that name." );
                        }

                        // Rename the album and update the info and list.
                        name.setName( newName );
                        updateInfo();
                        updateLists();
                    }
                }
            }
        } );

        // To show the tag cloud.
        menuItem5.addActionListener( new CloudListener() );

        menuItem6.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent arg0 )
            {
                saveFile();
            }

        } );

        // Add all the options to the menu bar.
        first.add( menuItem2 );
        first.add( menuItem6 );
        first.addSeparator();
        first.add( menuItem1 );
        second.add( menuItem3 );
        second.addSeparator();
        second.add( menuItem4 );
        third.add( menuItem5 );

        // Create the menu bar and put it in the frame.
        menuBar = new JMenuBar();
        menuBar.add( first );
        menuBar.add( second );
        menuBar.add( third );
        frame.setJMenuBar( menuBar );
    }

    /**
     * Inner class for the function of moving a photo.
     * 
     * @author Riley
     * 
     */
    public class MovingListener implements ActionListener
    {
        public void actionPerformed( ActionEvent e )
        {
            Album album = null;
            try
            {
                album = chooseAlbum();
            }
            catch( NothingChosenException e1 )
            {
                // Do Nothing.
            }

            // Make sure user didn't cancel.
            if( album != null )
            {
                // Move the photo.
                String name = album.getName();
                movePhoto( name );
            }
        }
    }

    /**
     * Inner class for the function of the album list.
     * 
     * @author Riley
     * 
     */
    public class AlbumListListener implements ListSelectionListener
    {

        public void valueChanged( ListSelectionEvent e )
        {
            JList source = (JList) e.getSource();

            // Figure out which item was selected.
            int row = source.getSelectedIndex();
            if( row != -1 )
            {
                String s = (String) albumListModel.getElementAt( row );

                if( !pm.findAlbum( s ).getPhotos().isEmpty() )
                {
                    // Change the thumbnail panel to show only the selected
                    // album's images.
                    thumbnailListModel.clear();
                    for( Photo p : pm.findAlbum( s ).getPhotos() )
                    {

                        try
                        {
                            thumbnailListModel
                                    .addElement( createThumbnailLabel( p ) );
                        }
                        catch( ThumbnailDoesNotExistException e1 )
                        {// This should never happen.
                        }
                    }

                    // Get the first photo in the album.
                    JLabel img = (JLabel) thumbnailListModel.getElementAt( 0 );
                    for( Photo p : pm.getPhotos() )
                        if( img.getText().equals( p.getName() ) )
                            photo = p;

                    // Change the image panel to display the first photo in the
                    // album.
                    image.removeAll();
                    try
                    {
                        image.add( createPhotoLabel() );
                    }
                    catch( PhotoDoesNotExistException e2 )
                    {// This should not happen.
                    }

                    updateInfo();
                }

                // If the album has no photo, clear everything.
                else
                {
                    thumbnailListModel.clear();
                    image.removeAll();
                    updateNull();
                }
                // De-select the source.
                source.clearSelection();
            }

        }
    }

    /**
     * Inner class for the function of the tag list.
     * 
     * @author Riley
     * 
     */
    public class TagListListener implements ListSelectionListener
    {

        public void valueChanged( ListSelectionEvent e )
        {
            JList source = (JList) e.getSource();

            // Figure out which item was selected.
            int row = source.getSelectedIndex();
            if( row != -1 )
            {
                SortedListModel m = (SortedListModel) source.getModel();
                String s = (String) m.getElementAt( row );

                // Change the thumbnail panels to show only the
                // selected tag's images.
                if( !tm.findTag( s ).getPhotos().isEmpty() )
                {
                    SortedImageModel dfl = (SortedImageModel) thumbnailList
                            .getModel();
                    dfl.clear();
                    for( Photo p : tm.findTag( s ).getPhotos() )
                    {
                        try
                        {
                            dfl.addElement( createThumbnailLabel( p ) );
                        }
                        catch( ThumbnailDoesNotExistException e1 )
                        {// This should never happen.
                        }
                    }

                    // Get the first photo that has the tag.
                    JLabel img = (JLabel) thumbnailList.getModel()
                            .getElementAt( 0 );
                    for( Photo p : tm.findTag( s ).getPhotos() )
                        if( img.getText().equals( p.getName() ) )
                            photo = p;

                    // Empty the image pane and put the first photo in.
                    image.removeAll();
                    try
                    {
                        image.add( createPhotoLabel() );
                    }
                    catch( PhotoDoesNotExistException e2 )
                    {// This should never happen.
                    }

                    updateInfo();
                }
                // If the tag has no photo, clear everything.
                else
                {
                    thumbnailListModel.clear();
                    image.removeAll();
                    updateNull();
                }
            }
            // De-select the source.
            source.clearSelection();
        }
    }

    /**
     * Inner class for the function of the thumbnail list.
     * 
     * @author Riley
     * 
     */
    public class ImageListListener implements ListSelectionListener
    {

        public void valueChanged( ListSelectionEvent e )
        {
            JList source = (JList) e.getSource();

            // Figure out which item was selected.
            int position = source.getSelectedIndex();
            if( position != -1 )
            {
                SortedImageModel m = (SortedImageModel) source.getModel();
                JLabel img = (JLabel) m.getElementAt( position );

                // Find the photo that the thumbnail corresponds to.
                for( Photo p : pm.getPhotos() )
                {
                    if( img.getText().equals( p.getName() ) )
                    {
                        photo = p;
                    }
                }

                // Clear the image display and put the correct photo there.
                image.removeAll();
                try
                {
                    image.add( createPhotoLabel() );
                    updateInfo();
                }

                catch( PhotoDoesNotExistException e1 )
                {// The photo will always exist.
                }
            }

            // De-select the source.
            source.clearSelection();
        }
    }

    /**
     * Inner class to listen for when the user calls to view a tag cloud. Each
     * tag in the system will be displayed with a size dependent on the number
     * of photos attached to it.
     * 
     * @author Riley
     * 
     */
    private class CloudListener implements ActionListener
    {
        // Makes the TagCloud.
        public void actionPerformed( ActionEvent e )
        {
            JFrame tagCloud = new JFrame( "TagCloud" );

            // The display has a pre-defined horizontal component. If necessary
            // a vertical scrollbar will appear.
            JPanel cloud = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
            JScrollPane cloudScroll = new JScrollPane( cloud );
            cloudScroll
                    .setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
            cloud.setBackground( Color.white );
            cloud.setPreferredSize( new Dimension( 300, 300 ) );

            // Get all the tags in the system and set the size according to the
            // number of photos they have attached.
            for( Tag t : tm.getTags() )
            {
                JButton text = new JButton( t.getName() );
                text.setActionCommand( t.getName() );
                text.setBackground( Color.white );
                text.setBorderPainted( false );
                text.setFont( new Font( Font.SANS_SERIF, Font.PLAIN, t
                        .getPhotos().size() * 2 + 12 ) );
                text.addActionListener( new ActionListener()
                {
                    public void actionPerformed( ActionEvent arg0 )
                    {
                        String s = arg0.getActionCommand();
                        // Change the thumbnail panels to show only the
                        // selected tag's images.
                        if( !tm.findTag( s ).getPhotos().isEmpty() )
                        {
                            SortedImageModel dfl = (SortedImageModel) thumbnailList
                                    .getModel();
                            dfl.clear();
                            for( Photo p : tm.findTag( s ).getPhotos() )
                            {
                                try
                                {
                                    dfl.addElement( createThumbnailLabel( p ) );
                                }
                                catch( ThumbnailDoesNotExistException e1 )
                                {// This should never happen.
                                }
                            }

                            // Get the first photo that has the tag.
                            JLabel img = (JLabel) thumbnailList.getModel()
                                    .getElementAt( 0 );
                            for( Photo p : tm.findTag( s ).getPhotos() )
                                if( img.getText().equals( p.getName() ) )
                                    photo = p;

                            // Empty the image pane and put the first photo in.
                            image.removeAll();
                            try
                            {
                                image.add( createPhotoLabel() );
                            }
                            catch( PhotoDoesNotExistException e2 )
                            {// This should never happen.
                            }

                            updateInfo();
                        }
                    }

                } );
                cloud.add( text );
            }

            // Show the TagCloud in a new window.
            tagCloud.getContentPane().add( cloud );
            tagCloud.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
            tagCloud.pack();
            tagCloud.setVisible( true );
        }
    }

    /**
     * Helper method that creates a label that has the photo image.
     * 
     * @pre true
     * @post true
     * @return the label
     * @throws PhotoDoesNotExistException no such photo
     */
    private JLabel createPhotoLabel() throws PhotoDoesNotExistException
    {
        JLabel photoImage = new JLabel( new ImageIcon( photo.getImage() ) );
        photoImage.setToolTipText( photo.getName() );
        return photoImage;
    }

    /**
     * Helper method that creates a label that has the thumbnail image and its
     * name.
     * 
     * @pre true
     * @post true
     * @param p the photo to make the thumbnail
     * @return the label
     * @throws ThumbnailDoesNotExistException If there is no thumbnail
     */
    private JLabel createThumbnailLabel( Photo p )
            throws ThumbnailDoesNotExistException
    {
        JLabel label = new JLabel( p.getName(), new ImageIcon( p
                .getThumbnailImage() ), 0 );
        label.setVerticalTextPosition( JLabel.BOTTOM );
        label.setHorizontalTextPosition( JLabel.CENTER );
        label.setPreferredSize( THUMBNAIL_SIZE );
        label.setToolTipText( p.getName() );

        return label;
    }

    /**
     * Helper method to convert a set into a list of strings.
     * 
     * @pre set != null;
     * @post true
     * @param set the set to converts
     * @return the String list of the set.
     */
    private String stringSet( Set<Tag> set )
    {
        SortedSet<Tag> ordered = new TreeSet<Tag>( new OrderedTagComparator() );
        ordered.addAll( set );

        String s = "";
        if( set.size() != 0 )
            for( Tag t : ordered )
                s = s + t.getName() + "\n";
        return s;
    }

    /**
     * Helper method to move photo from one album to another.
     * 
     * @pre photo is not in the same album as the target
     * @post photo is now in target album
     */
    private void movePhoto( String name )
    {
        // Check if we'll need to update the frame.
        boolean needUpdate = true;
        try
        {

            // Remove photo from its current album and put it in the
            // indicated one.
            pm.findAlbum( name ).addPhoto( photo );
            photo.getAlbum().removePhoto( photo );
        }

        catch( PhotoAlreadyInAlbumException e1 )
        {
            // If this is the same album, tell the user. Ensure rest of
            // method doesn't finish.
            if( photo.getAlbum().getName().equals( name ) )
            {
                JOptionPane.showMessageDialog( frame,
                        "This photo is already in this album.", "Error",
                        JOptionPane.ERROR_MESSAGE );

                needUpdate = false;
            }

            // Otherwise, move the photo into the album specified.
            else
            {
                try
                {
                    photo.getAlbum().removePhoto( photo );
                    pm.findAlbum( name ).addPhoto( photo );
                }
                catch( PhotoDoesNotExistException ee1 )
                {// This will never be called here.
                }
                catch( PhotoAlreadyInAlbumException e3 )
                {// This will also never be called here.
                }
            }
        }

        // Let the user know if the photo isn't there.
        catch( PhotoDoesNotExistException e2 )
        {
            JOptionPane.showMessageDialog( frame, "This photo does not exist.",
                    "Error", JOptionPane.ERROR_MESSAGE );
        }

        if( needUpdate )
        {
            // Clear the thumbnail display.
            SortedImageModel dfl = (SortedImageModel) thumbnailList.getModel();
            dfl.clear();

            // Adds the thumbnail of each photo in the album to the
            // display.
            for( Photo p : pm.findAlbum( name ).getPhotos() )
            {
                try
                {
                    dfl.addElement( createThumbnailLabel( p ) );
                }
                catch( ThumbnailDoesNotExistException e1 )
                {// This should not happen.
                }
            }
            updateInfo();
        }
    }

    /**
     * Helper method that refreshes all the info relevant to the picture. It
     * empties all the info and thumbnails since there is no longer a photo in
     * the frame.
     * 
     * @pre true
     * @post everything is updated to be empty
     */
    private void updateNull()
    {
        photoAlbum.setText( "Album: " );
        descripText.setText( "" );
        tagText.setText( "" );
        dateAdded.setText( "Added: " );
        specifics.setBorder( BorderFactory.createTitledBorder( "Photo: " ) );
        image.removeAll();
        thumbnailListModel.clear();
        image.updateUI();
        thumbnails.updateUI();

        // These buttons should not be functional because there is no photo in
        // the display.
        addAnotherTag.setEnabled( false );
        detag.setEnabled( false );
        removePhoto.setEnabled( false );
        movePhoto.setEnabled( false );
    }

    /**
     *Helper method that refreshes all the info relevant to the picture.
     * 
     * @pre true
     * @post everything is updated
     */
    private void updateInfo()
    {
        photoAlbum.setText( "Album: "
                + shortenString( photo.getAlbum().getName() ) );
        descripText.setText( photo.getDescription() );
        tagText.setText( stringSet( photo.getTags() ) );

        // Formats the date.
        DateFormat df = DateFormat.getDateTimeInstance( DateFormat.MEDIUM,
                DateFormat.SHORT );
        dateAdded.setText( "Added: " + df.format( photo.getDateAdded() ) );

        specifics.setBorder( BorderFactory.createTitledBorder( "Photo: "
                + shortenString( photo.getName() ) ) );
        image.updateUI();
        updateLists();

        // These buttons should be functional.
        addAnotherTag.setEnabled( true );
        movePhoto.setEnabled( true );
        removePhoto.setEnabled( true );

        // Update the buttons.
        if( !photo.getTags().isEmpty() )
            detag.setEnabled( true );
        else
            detag.setEnabled( false );
        updateButtons();
    }

    /**
     * Helper method to update buttons.
     * 
     * @pre true
     * @post buttons have been updated
     */
    private void updateButtons()
    {
        if( !pm.getAlbums().isEmpty() )
            deleteAlb.setEnabled( true );
        else
            deleteAlb.setEnabled( false );
        if( !tm.getTags().isEmpty() )
            removeTag.setEnabled( true );
        else
            removeTag.setEnabled( false );
        if( photo == null )
        {
            addAnotherTag.setEnabled( false );
            removePhoto.setEnabled( false );
            movePhoto.setEnabled( false );
        }
        else
        {
            addAnotherTag.setEnabled( true );
            removePhoto.setEnabled( true );
            movePhoto.setEnabled( true );
        }
    }

    /**
     * Helper method to reset the lists' models.
     * 
     * @pre true
     * @post lists' contents have been updated
     */
    private void updateLists()
    {
        tagListModel.clear();
        for( Tag t : tm.getTags() )
            tagListModel.addElement( t.getName() );

        albumListModel.clear();
        for( Album a : pm.getAlbums() )
            albumListModel.addElement( a.getName() );

        tabs.updateUI();
    }

    /**
     * Helper method to make thumbnails. It takes a name and finds that album,
     * then puts the thumbnail of each of its photos in the thumbnail pane.
     * 
     * @pre s!= null
     * @post thumbnails have been added
     * @param s the name of the album.
     */
    private void updateThumbnails( String s )
    {
        thumbnailListModel.clear();
        for( Photo p : pm.findAlbum( s ).getPhotos() )
        {
            try
            {
                thumbnailListModel.addElement( createThumbnailLabel( p ) );
                thumbnailList.setModel( thumbnailListModel );
            }
            catch( ThumbnailDoesNotExistException e )
            {
                JOptionPane.showMessageDialog( frame, "There is no thumbnail!",
                        "Error", JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    /**
     * Helper method to decide which album the user wants. It displays a drop
     * down of options for the user to choose.
     * 
     * @pre true
     * @post true
     * @return the album chosen
     */
    private Album chooseAlbum() throws NothingChosenException
    {
        // Cast the set to an array of albums.
        Object[] choices = pm.getAlbums().toArray();
        for( Object o : choices )
        {
            Album o2 = (Album) o;
            o = o2;
        }

        // Check if no albums.
        if( choices.length == 0 )
            JOptionPane.showMessageDialog( frame, "You don't have any albums!",
                    "Error", JOptionPane.ERROR_MESSAGE );
        else
        {
            Album name = (Album) JOptionPane.showInputDialog( frame,
                    "Please select an album from the list.",
                    "Choose the Album", JOptionPane.DEFAULT_OPTION, null,
                    choices, choices[ 0 ] );

            // Make sure user didn't cancel.
            if( name != null )
                return name;
        }
        throw new NothingChosenException();
    }

    private String shortenString( String s )
    {
        String cut = s;
        if( s.length() > 20 )
        {
            cut = s.substring( 0, 19 ) + "...";
        }
        return cut;
    }

    private void saveFile()
    {
        ObjectOutputStream out = null;
        try
        {
            out = new ObjectOutputStream( new FileOutputStream( "Library.dat" ) );
            out.writeObject( pm );
            out.writeObject( tm );
            out.close();
        }
        catch( FileNotFoundException e1 )
        {
            System.out.println( "The file was not found" );
        }
        catch( IOException e1 )
        {
            System.out.println( "The object does not exist" );
            e1.printStackTrace();
        }

        System.out.println( "Saved data" );
    }

    private void closeProgram()
    {
        // Prompt the use to save the Library
        int confimation = JOptionPane.showConfirmDialog( frame,
                "Would you like to save your data?",
                "Are you sure you want to Exit?",
                JOptionPane.YES_NO_CANCEL_OPTION );
        if( confimation == JOptionPane.YES_OPTION )
        {
            saveFile();
            // Close the window.
            System.err.println( "Close window" );
            System.exit( 0 );
        }
        else if( confimation == JOptionPane.NO_OPTION )
        {// Close the window.
            System.err.println( "Close window" );
            System.exit( 0 );
        }
    }

    private void restart()
    {
        for( Photo p : pm.getPhotos() )
        {
            try
            {
                p.loadPhoto( p.getPath() );
            }
            catch( PhotoDoesNotExistException e )
            {
                System.out.println( "No Photo" );
            }
        }
    }

}
