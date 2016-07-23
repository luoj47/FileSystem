/**
 *  Created by Ko Fukushima and Jesse Luo on 7/9/2016.
 *
 *  This class manages file entry table by allocating for,
 *  freeing, emptying the entry in the table
 *
 */

import java.util.Vector;

public class FileTable
{
    private Vector table;
    private Directory dir;

    /**
     * Class constructor that initializes the fields of table
     * and dir.
     *
     * @param directory the directory
     */
    public FileTable(Directory directory)
    {
        table = new Vector();
        dir = directory;
    }

    /**
     * This method allocates a new file table entry for this file name
     * and it also allocate/retrive and register the corresoponding inode
     * using dir increment this inode's count immediately write back this
     * inode this inode to the disk
     *
     * @param filename the file name
     * @param mode the mode such as "r", "w", "w+", or "a"
     * @return a reference to the file table entry
     */
    public synchronized FileTableEntry falloc(String filename, String mode)
    {
        return null; // It needs to be modified later
    }

    /**
     * This method receive a file table entry reference
     * and save the corresponding inode to the disk
     * and free this file table entry
     *
     * @param e the file table entry
     * @return True if this file entry found in the table,
     * false otherwise
     */
    public synchronized boolean ffree(FileTableEntry e)
    {
        return false; // It needs to be modified later
    }

    /**
     * This method clear all file table entry in the table
     * and it should be called before starting a format
     *
     * @return True if table is empty and false otherwise
     */
    public synchronized boolean fempty( )
    {
        return table.isEmpty();
    }
}
