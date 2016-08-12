/**
 * Created by Ko Fukushima, Lu Ming Hsuan, Jesse Luo
 * CSS 430 A
 * Professor Mike Panitz
 * 11 August 2016
 * Final Project, FileSystem: TCB.java
 *
 * This class represents a Thread control block that
 * manages up to 32 open files
 *
 */

public class TCB
{
    private Thread thread = null;
    private int tid = 0;
    private int pid = 0;
    private boolean terminate = false;
    private final static int FILE_TABLE_ENTRY_SIZE = 32;

    // User file descriptor table:
    // each entry pointing to a file (structure) table entry
    public FileTableEntry[] ftEnt = null;

    /**
     *
     * Constructs a TCB that initializes the parameters: thread, tid, pid
     * , terminated, and FileTableEntry
     *
     * @param thread a thread
     * @param tid a thread id
     * @param pid a process id
     */
    public TCB(Thread thread, int tid, int pid)
    {
        this.thread = thread;
        this.tid = tid;
        this.pid = pid;
        terminate = false;

        // The following code is added for the file system
        ftEnt = new FileTableEntry[FILE_TABLE_ENTRY_SIZE];
    }

    /**
     * Returns the terminate
     *
     * @return terminate the terminte
     */
    public synchronized boolean getTerminated()
    {
        return terminate;
    }

    /**
     * Sets the terminate
     * @return the terminate, set to true
     */
    public synchronized boolean setTerminated()
    {
        return terminate = true;
    }

    /**
     * This method returns the thread
     *
     * @return thread the current thread
     */
    public synchronized Thread getThread()
    {
        return thread;
    }

    /**
     * This method returns the thread id
     *
     * @return tid the id for a thread
     */
    public synchronized int getTid()
    {
        return tid;
    }

    /**
     * This method returns the process id
     *
     * @return pid the id for a process
     */
    public synchronized int getPid()
    {
        return pid;
    }

    /**
     * This method returns a specified file table
     * entry corresponding to the fd. The fd should not
     * be 0, 1, and 2 since they're reserved by standard
     * input, output, and error.
     *
     * @param fd the file descriptor
     * @return fnEnt[fd] if the file entry exists
     * null if not
     */
    public synchronized FileTableEntry getFtEnt(int fd)
    {
        if (fd >= 3 && fd < FILE_TABLE_ENTRY_SIZE)
        {
            return ftEnt[fd];
        }

        return null;
    }

    /**
     * This method returns a specified file table
     * entry corresponding to the fd. The fd should not
     * be 0, 1, and 2 since they're reserved by standard
     * input, output, and error.
     *
     * @param fd the file descriptor
     * @return fnEnt[fd] if the file entry exists
     * null if not
     */
    public synchronized FileTableEntry returnFd(int fd)
    {
        if (fd >= 3 && fd < FILE_TABLE_ENTRY_SIZE)
        {
            FileTableEntry oldFtEtn = ftEnt[fd];
            ftEnt[fd] = null;
            return oldFtEtn;
        }

        return null;
    }

    /**
     * This method returns the file
     * descriptor.
     *
     * @param ftEnt file table Entry
     * @return file descriptor -1 if not found
     */
    public synchronized int getFd(FileTableEntry ftEnt)
    {
        int index = 3;

        // check if the FileTableEntry
        // is not null
        if (ftEnt != null)
        {
            // iterates through file table entries
            while (this.ftEnt[index++] != null && index < FILE_TABLE_ENTRY_SIZE) {}
        }
        else
        {
            return -1;
        }

        index--;
        if (this.ftEnt[index] == null)
        {
            this.ftEnt[index] = ftEnt;
            return index;
        }

        return -1;
    }
}