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
     * code added from slides
     */
    public int bytes2directory(byte data[])
    {
        int offset = 0; //needed for SysLib call

        for ( int i = 0; i < fsize.length; i++, offset += 4 )
            fsize[ i ] = SysLib.bytes2int( data, offset );

        for ( int i = 0; i < fnames.length; i++, offset += maxChars * 2 )
        {
            String fname = new String(data, offset, maxChars * 2);
            fname.getChars(0, fsize[i], fnames[i], 0);
        }
        return 0;  // Find out what needs to be returned!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }

    /**
     * This method converts and return Directory information into a plain
     * byte array that will be wrritten back to disk.
     *
     * @return the meaningfull Directory information
     */
    public byte[] directory2bytes()
    {
        byte data[] = new byte[(fsize.length * maxChars * 2)];  // I'm not sure if this is the right size

        int offset = 0;

        for ( int i = 0; i < fsize.length; i++, offset += 4 )
            SysLib.int2bytes( fsize[i], data, offset );

        for ( int i = 0; i < fnames.length; i++, offset += maxChars * 2 )
        {
            String fname = new String(fnames[i], 0, fsize[i]);
            byte[] temp = fname.getBytes();
            System.arraycopy(temp, 0, data, offset, temp.length);
        }

        return data;
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
        // finds an empty spot and adds new filename and length
        for (int i = 0; i < fsize.length; i++)
        {
            if(fsize[i] == 0)
            {
                if (filename.length() > maxChars)
                {
                    fsize[i] = maxChars;
                }
                else
                {
                    fsize[i] = filename.length();
                }

                // Modify it later
                filename.getChars(0, fsize[i], fnames[i], 0);
                return (short) i;
            }
        }

        return -1;
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
        if(fsize[iNumber] < 0 || iNumber > maxChars)
        {
            return false;
        }

        fsize[iNumber] = 0;
        return true;
    }

    /**
     * This method returns the inumber corresoponding to this filename
     *
     * @param filename the file name
     * @return the inode number corresponding to this file name
     */
    public short namei(String filename)
    {
        String fname;

        for(int i = 0; i < fsize.length; i++)
        {
            fname = new String(fnames[i], 0, fsize[i]);
            if(filename.compareTo(fname) == 0)
            {
                return (short) i;
            }
        }
        return -1; // not found
    }
}
