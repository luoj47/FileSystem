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
    private Vector<FileTableEntry> table;
    private Directory dir;

    /**
     * Class constructor that initializes the fields of table
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
     * This method allocates a new file table entry for this file name
     * and it also allocate/retrive and register the corresoponding inode
     * using dir increment this inode's count immediately write back this
     * inode this inode to the disk
     *
     * @param filename the file name
     * @param mode the mode such as "r", "w", "w+", or "a"
     * @return a reference to the file table entry
     */
    // iNode.flag: 0 = unused, 1 = used, 2 = read, 3 = write, 4 = delete
    /*
     falloc method by Lu Ming Hsuan 8/1/2016
     Jesse: made a few modifications since it didn't work
     This method allocates a new file table entry for the filename provided
     by assigning or retrieving and logging the correct iNode using dir
     and increments iNode's count
     The updated iNode will be written back to disk after
     and a reference to the file table entry is returned
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
                else if (!mode.equals("r")) // create new
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

        iNode.count++;//increment iNode count
        iNode.toDisk(iNumber);//write iNode back to disk
        fte = new FileTableEntry(iNode,iNumber, mode);//create new FileTableEntry for filename
        table.add(fte);//add FTE to table
        return fte;//return FTE reference
    }

    /**
     * This method receive a file table entry reference
     * and save the corresponding inode to the disk
     * and free this file table entry
     *
     * @param e the file table entry
     * @return True if this file entry found in the table,
     * false otherwise
     * edited on 7/23/16 by Midori
     */
    public synchronized boolean ffree(FileTableEntry e)
    {
        Inode temp = new Inode(e.iNumber);
        //try and remove FTE if it is in table, the remove will return true
        // return true if this file table entry found in my table
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
                // free this file table entry.
                notify();
                temp.flag = 1;
            }

            //decrease the count of users of that file
            if(temp.count != 0)
                temp.count--;
            // save the corresponding inode to the disk
            temp.toDisk(e.iNumber);
            return true;
        }

        return false;

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