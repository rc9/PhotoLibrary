package ca.ubc.cs.cpsc211.photo;

import java.io.*;

public class PhotoLibraryTest
{
    public static void main( String[] args ) throws IOException,
            ClassNotFoundException
    {
        PhotoManager pm = new PhotoManager();
        TagManager tm = new TagManager();

        PhotoLibraryGUI plg;

        File f = new File( "Library.dat" );
        if( f.exists() )
        {
            ObjectInputStream in = new ObjectInputStream( new FileInputStream(
                    f ) );
            pm = (PhotoManager) in.readObject();
            tm = (TagManager) in.readObject();

            in.close();
        }
        
            plg = new PhotoLibraryGUI( pm, tm );

            plg.showLibrary();
        
    }
}