/**
 * Created by Ko Fukushima and Jesse Luo on 7/9/2016.
 *
 * This class describes a file, and this inode is a
 * Simplified version of the UnixInode
 */
public class Inode
{
    private final static int iNodeSize = 32;        // fix to 32 bytes
    private final static int directSize = 11;       // # direct pointers

    public int length;                              // file size in bytes
    public short count;                             // # file-table entries pointing on this
    public short flag;                              // 0 = unused, 1 = used, ...
    public short direct[] = new short[directSize];  // direct pointers
    public short indirect;                          // a indirect pointer

    /**
     * Class constructor that initializes the fields that are length,
     * count, flag, direct, and indirect.
     */
    Inode()
    {
        length = 0;
        count = 0;
        flag = 1;
        for (int i = 0; i < directSize; i++)
        {
            direct[i] = -1;
        }
        indirect = -1;
    }

    /**
     * This method retrieves the inode from disk
     *
     * @param iNumber
     */
    Inode(short iNumber)
    {
        // Directory dir = new Directory(iNodeSize);
        // inode = (Inode)dir.directory2bytes;
        // direct[iNumber] = inode
    }

    /**
     * This method saves to disk as the i-th inode
     *
     * @param iNumber
     * @return
     */
    int toDisk(short iNumber)
    {
        // Directory dir = new Directory(iNodeSize);
        // dir.bytes2directory((byte[])direct[iNumber]);
        return 0; // It needs to be modified later
    }
}
