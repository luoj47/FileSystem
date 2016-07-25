import java.io.File;

/**
 * Created by Ko Fukushima and Jesse Luo on 7/9/2016.
 *
 * This class manages the structure of a FileSystem by holding
 * the superblock, directory, and filetable.
 *
 */
public class FileSystem
{
    private final int SEEK_SET = 0;
    private final int SEEK_CUR = 1;
    private final int SEEK_END = 2;

    private Superblock superblock;
    private Directory directory;
    private FileTable filetable;
    private TCB tcb;

    /**
     * Constructs a new FileSystem.
     *
     * @param diskBlocks size of the superblock
     */
    public FileSystem(int diskBlocks)
    {
        // create new superblock, format disk with 64 inodes
        superblock = new Superblock (diskBlocks);

        directory = new Directory(superblock.totalInodes);

        // create new directory, register "/" in directory entry 0
        filetable = new FileTable(directory);

        // reconstruct the directory

        FileTableEntry dirEnt = open("/", "r");
        int dirSize = fsize(dirEnt);
        if(dirSize > 0)
        {
            byte[] dirData = new byte[dirSize];
            read(dirEnt, dirData);
            directory.bytes2directory(dirData);
        }
        close(dirEnt);
    }

    // the description of sync will be added more info later

    /**
     * This method formats the disk
     *
     * @param files the # files to be created
     * @return true on success, false otherwise
     */
    public boolean format(int files)
    {
        // format/delete all files
        // Check if FileTable ad TCB are empty (isEmpty)
        // allocate "files" inodes
        // returns if format successful

        // format superblock here, superblock.format(files)?

        directory = new Directory(superblock.totalInodes);
        filetable = new FileTable(directory);
        return true; // it needs to be modified later
    }

    // the description of open will be added more info later

    /**
     * This method opens the file corresponding to the file
     * name in the given mode.
     *
     * @param fileName
     * @param mode
     * @return int fd
     * between 3 to 31
     */
    public FileTableEntry open(String fileName, String mode)
    {
        // FileTableEntry newfte = filetable.falloc(fileName, mode);
        // for tcb.ftENT's length
        // if a spot is null
        // insert newfte
        // return current index
        //...
        // return -1 if error
        return null; // it needs to be modified later
    }

    /**
     * This method reads as many bytes as possible
     * or up to buffer.length the file
     * corresponding to the file descriptor
     *
     * @param fd FileTableEntry
     * @param buffer the buffer
     * @return the # bytes read or -1 if there is an error
     */
    public int read(FileTableEntry fd, byte[] buffer)
    {
        // read byte[] buffer from tcb.ftEnt[fd]
        // return number of bytes read
        return 0; // it needs to be modified later
    }

    /**
     * This method writes the contents of the buffer to the
     * file corresponding to the file descriptor.
     *
     * @param fd FileTableEntry
     * @param buffer the buffer
     * @return number of bytes written, or negative for error
     */
    public int write(FileTableEntry fd,  byte[] buffer)
    {
        // write byte[] buffer to tcb.ftEnt[fd]
        // return number of bytes written
        return 0; // it needs to be modified later
    }

    /**
     * This method updates the seek pointer corresponding to
     * the file descriptor.
     * If whence = SEEK_SET (= 0),
     * file's seek pointer set to offset bytes from beginning of file
     * If whence = SEEK_CUR (= 1),
     * file's seek pointer set to its current value plus offset. Offset can be positive/negative.
     * If whence = SEEK_END (= 2),
     * file's seek pointer set to size of file plus offset. Offset can be positive/negative.
     *
     * If the user attempts to set the seek pointer to a negative number, seekPtr is set to zero.
     * If the user attempts to set the pointer to beyond the file size, seekPtr set to the end of the file.
     * In both cases, success is returned
     *
     * @param fd FileTableEntry
     * @param offset the offset can be positive or negative
     * @param whence the whence represents SEEK_SET == 0,
     * SEEK_CUR == 1, and SEEK_END == 2
     * @return 0 in success, -1 false
     */
    public int seek(FileTableEntry fd, int offset, int whence)
    {
        switch(whence)
        {
            case SEEK_SET:
                fd.seekPtr = offset;
                break;

            case SEEK_CUR:
                fd.seekPtr += offset;
                break;

            case SEEK_END:
                fd.seekPtr = this.fsize(fd) + offset;
                break;

            default:
                return -1;
        }

        if(fd.seekPtr < 0)
        {
            fd.seekPtr = 0;
        }
        if(fd.seekPtr > this.fsize(fd))
        {
            fd.seekPtr = this.fsize(fd);
        }

        // update seek pointer

        return fd.seekPtr; // it needs to be modified later
    }

    /**
     * This method close the file corresponding to
     * the file descriptor
     *
     * @param fd FileTableEntry
     * @return true in success, false otherwise
     */
    public boolean close(FileTableEntry fd)
    {
        // tcb.ftEnt[fd] = null;
        return false; // it needs to be modified later
    }

    /**
     * This method deletes the file that
     * is specified by file name only when the
     * file is closed
     *
     * @param fileName the file name
     * @return true if successful, false otherwise
     */
    public boolean delete(String fileName)
    {
        // if (file == open) { mark for deletion (also can't receive new open request}
        // else { delete file}
        return false; // it needs to be modified later
    }

    /**
     * This method returns the size in bytes
     * of the file indicated by file descriptor
     * and returns -1 when it detects an error
     *
     * @param fd FileTableEntry
     * @return the file size
     */
    public int fsize(FileTableEntry fd)
    {
        return fd.inode.length; 
    }

    // do later
    public void sync()
    {

    }
}
