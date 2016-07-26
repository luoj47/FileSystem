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
     * edited by Midori on 7/23/16
     * Jesse: edited so it will compile
     * code added from slides
     */
    public int bytes2directory(byte data[])
    {
        int offset = 0; //needed for SysLib call

        for( int i = 0; i < fsize.length; i++, offset += 4 )
            fsize[ i ] = SysLib.bytes2int( data, offset );

        for( int i = 0; i < fnames.length; i++, offset += maxChars * 2 )
        {
            String fname = new String(data, offset, maxChars * 2);
            fname.getChars(0, fsize[i], fnames[i], 0);
        }

        return 0; // it needs fixed since this function is incomplete
    }



    /**
     * This method converts and return Directory information into a plain
     * byte array that will be wrritten back to disk.
     *
     * @return the meaningfull Directory information
     */
    public byte[] directory2bytes()
    {
        // convert fnames/fisize to byte[]
        // return converted data
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
        // newINumber = new inode number for filename
        // fnames[filename][newINumber], insert in fnames
        // return newInumber
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
        // for the length of fnames
        // if the current inode number matches iNumber
        // for the length of ftEnt in TCB
        // if the current inode has inumber that matches iNumer
        // delete somehow
        // remove inumber and file from fnames
        // ...
        // return if dellocation is successful
        return false; // It needs to be modified later
    }

    /**
     * This method returns the inumber corresoponding to this filename
     *
     * @param filename the file name
     * @return the inode number corresponding to this file name, or -1 if not found
     */
    public short namei(String filename)
    {
        // for the length of fnames
        // if the current fname matches filename
        // return the inode number at this index
        // ...
        // return -1, because not found
        return 0; // It needs to be modified later
    }
}
