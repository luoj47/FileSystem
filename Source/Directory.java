/**
 * Created by Ko Fukushima and Jesse Luo on 7/9/2016.
 *
 * This class maintains each file in a different directory entry that
 * contains its file name and the corresponding inode number.
 */

public class Directory
{
    private static int maxChars = 30;   // max characters of each file name
    
    // Directory entries
    private int fsize[];
    private char fnames[][];

    /**
     * Class constructor that initializes the fields that are fsize,
     * fnames, root, fsize.
     *
     * @param maxInumber the max iNode number
     */
    public Directory(int maxInumber)
    {
        fsize = new int[maxInumber];
        fnames = new char[maxInumber][maxChars];
        String root = "/";
        fsize[0] = root.length();
        root.getChars(0, fsize[0], fnames[0], 0);
    }

    /**
     * This method initializes the Directory instance with this data[]
     *
     * @param data the data[] received directory information from disk
     * @return                                                  // It needs to be added later
     */
    public int bytes2directory(byte data[])
    {
        
        return 0; // It needs to be modified later
    }

    /**
     * This method converts and return Directory information into a plain
     * byte array that will be wrritten back to disk.
     *
     * @return the meaningfull Directory information
     */
    public byte[] directory2bytes()
    {
        return null; // It needs to be modified later
    }

    /**
     * This methods creates the one of a file, and
     * allocates a new inode number for it.
     *
     * @param filename the file name
     * @return a new inode number             // This might need to be modified later
     */
    public short ialloc(String filename)
    {
        return 0; // It needs to be modified later
    }

    /**
     * This method deallocate this inumber (inode number) and
     * also deallocate the corresponding file
     *
     * @param iNumber the inode number
     * @return True if find the inode number and deallocates
     * the file with that inumber, and False otherwise
     */
    public boolean ifree(short iNumber)
    {
        
        return false; // It needs to be modified later
    }

    /**
     * This method returns the inumber corresoponding to this filename
     *
     * @param filename the file name
     * @return the inode number corresponding to this file name
     */
    public short namei(String filename)
    {
        return 0; // It needs to be modified later
    }
}
