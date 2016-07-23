/**
 * Created by Ko Fukushima and Jesse Luo on 7/9/2016.
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
    
    // User file descriptor table:
    // each entry pointing to a file (structure) table entry
    public FileTableEntry[] ftEnt = null;

    /**
     *
     * Class constructor that initializes the parameters: thread, tid, pid
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
        ftEnt = new FileTableEntry[32];
    }

    /**
     * This method returns a thread id
     *
     * @return tid the id for a thread
     */
    public int getTid()
    {
        return tid;
    }

    /**
     * This method returns a process id
     *
     * @return pid the id for a process
     */
    public int getPid()
    {
        return pid;
    }

}
