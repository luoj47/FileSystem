/**
 * Created by Ko Fukushima, Lu Ming Hsuan, Jesse Luo
 * CSS 430 A
 * Professor Mike Panitz
 * 11 August 2016
 * Final Project, FileSystem: FileTableEntry.java
 *
 * This class is shared among all user threads
 */
public class FileTableEntry
{
    public int seekPtr;
    public final Inode inode;
    public final short iNumber;
    public int count;
    public final String mode;

    /**
     * Class constructor that initializes the fields that are seekPtr,
     * inode, iNumbers, count, mode.
     *
     * @param inode the inode
     * @param iNumber the inode number
     * @param mode the mode such as "r", "w", "w+", or "a"
     */
    public FileTableEntry(Inode inode, short iNumber, String mode)
    {
        seekPtr = 0;                                    // a file seek pointer
        this.inode = inode;                             // a reference to its inode
        this.iNumber = iNumber;                         // am inode number
        count = 1;                                      // # threads sharing this entry
        this.mode = mode;                               // "r", "w", "w+", or "a"
        if (this.mode.compareTo("a") == 0)
        {
            seekPtr = this.inode.length;
        }
    }
}
