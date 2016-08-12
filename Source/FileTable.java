/**
 * Created by Ko Fukushima, Lu Ming Hsuan, Jesse Luo
 * CSS 430 A
 * Professor Mike Panitz
 * 11 August 2016
 * Final Project, FileSystem: FileTable.java
 *
 *  This class manages file entry table by allocating for,
 *  freeing, emptying the entry the file table entry table.
 */

import java.util.Vector;

public class FileTable
{
    private Vector<FileTableEntry> table;
    private Directory dir;

    /**
     * Constructs a FileTable that initializes the fields of table
     * and dir.
     *
     * @param directory the directory
     */
    public FileTable(Directory directory)
    {
        table = new Vector<FileTableEntry>();
        dir = directory;
    }

    /**
     * This method allocates a new file table entry for the filename provided
     * by assigning or retrieving and logging the correct iNode using dir
     * and increments iNode's count
     * The updated iNode will be written back to disk after
     * and a reference to the file table entry is returned
     *
     * iNode.flag: 0 = unused, 1 = used, 2 = read, 3 = write, 4 = delete
     *
     * @param filename the file name
     * @param mode the mode such as "r", "w", "w+", or "a"
     * @return a reference to the file table entry
     */
    public synchronized FileTableEntry falloc(String filename, String mode)
    {
        short iNumber = -1;
        Inode iNode = null;
        FileTableEntry fte = null;

        //if mode is invalid, null request
        // if(!(m.equals("r") || m.equals("w") || m.equals("w+") || m.equals("a")) )
        if(!(mode.equals("r") || mode.equals("w") || mode.equals("w+") || mode.equals("a")) )
        {
            return fte;
        }
        else
        {
            while (true)
            {
                if (filename.equals("/"))
                {
                    iNumber = (short) 0;
                }
                else
                {
                    iNumber = dir.namei(filename);
                }

                if (iNumber >= 0)
                {
                    iNode = new Inode(iNumber);

                    if ((mode.equals("r")) && (iNode.flag == 0 // unused, used or read
                            || iNode.flag == 1
                            || iNode.flag == 2))
                    {
                        iNode.flag = 2;
                        break;
                    }
                    else if ((mode.equals("r")) && (iNode.flag == 3))
                    {
                        try
                        {
                            wait();
                        }
                        catch (InterruptedException e)
                        {
                        }
                    }
                    else if(iNode.flag == 0 // unused, used or write
                            || iNode.flag == 1
                            || iNode.flag == 3)
                    {
                        break;
                    }
                    else
                    {
                        try
                        {
                            wait();
                        }
                        catch(InterruptedException e){}
                    }
                }
                // if inode for file doesn't exist, create new inode with iNumber
                // obtained from ialloc
                else if (!mode.equals("r"))
                {
                    iNumber = dir.ialloc(filename);
                    iNode = new Inode(iNumber);
                    iNode.flag = 3;
                    break;
                }
                else
                {
                    return null;
                }
            }
        }

        iNode.count++;
        iNode.toDisk(iNumber);
        fte = new FileTableEntry(iNode,iNumber, mode);
        table.add(fte);
        return fte;
    }

    /**
     * This method receive a file table entry reference
     * and removes that file table entry. All threads are
     * woken up if the removed fte had writing status on the inode.
     * Only 1 other thread is woken up otherwise.
     *
     * @param e the file table entry
     * @return True if remove was successful, false if not
     */
    public synchronized boolean ffree(FileTableEntry e)
    {
        // holder for inode since so we can remove fte
        Inode temp = new Inode(e.iNumber);
        boolean removed = table.remove(e);
        if (removed)
        {
            if (temp.flag == 3)
            {
                temp.flag = 1;
                notifyAll();
            }
            else if ((temp.flag == 2) && (temp.count == 1))
            {
                notify();
                temp.flag = 1;
            }

            // if users exist, decrement users
            if(temp.count != 0)
                temp.count--;

            temp.toDisk(e.iNumber);
            return true;
        }

        return false;
    }

    /**
     * Returns whether or not the file table of file table entries is empty
     *
     * @return True if table is empty and false otherwise
     */
    public synchronized boolean fempty( )
    {
        return table.isEmpty();
    }
}