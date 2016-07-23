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

    /**
     * Constructs a new FileSystem.
     *
     * @param diskBlocks size of the superblock
     */
    public FileSystem(int diskBlocks)
    {
        // create new superblock, format disk with 64 inodes
        superblock = new Superblock (diskBlocks);

        // create new directory, register "/" in directory entry 0
        filetable = new FileTable(directory);

        // reconstruct the directory
        /*
        FileTableEntry dirEnt = open("/", "r");
        int dirSize = fsize(dirEnt);
        if(dirSize > 0)
        {
            byte[] dirData = new byte[dirSize];
            read(dirEnt, dirData);
            directory.bytes2directory(dirData);
        }
        close(dirEnt);*/
    }

    // the description of sync will be added more info later

    /**
     * This method formats the disk
     *
     * @param files the # files to be created
     * @return 0 on success, -1 otherwise
     */
    public int format(int files)
    {
        return 0; // it needs to be modified later
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
    public int open(String fileName, String mode)
    {
        return 0; // it needs to be modified later
    }

    /**
     * This method reads as many bytes as possible
     * or up to buffer.length the file
     * corresponding to the file descriptor
     *
     * @param fd the file descriptor
     * @param buffer the buffer
     * @return the # bytes read or -1 if there is an error
     */
    public int read(int fd, byte[] buffer)
    {
        return 0; // it needs to be modified later
    }

    /**
     * This method writes the contents of the buffer to the
     * file corresponding to the file descriptor.
     *
     * @param fd the file descriptor
     * @param buffer the buffer
     * @return
     */
    public int write(int fd,  byte[] buffer)
    {
        return 0; // it needs to be modified later
    }

    /**
     * This method updates the seek pointer corresponding to
     * the file descriptor.
     *
     * @param fd the file descriptor
     * @param offset the offset can be positive or negative
     * @param whence the whence represents SEEK_SET == 0,
     * SEEK_CUR == 1, and SEEK_END == 2
     * @return
     */
    public int seek(int fd, int offset, int whence)
    {
        return 0; // it needs to be modified later
    }

    /**
     * This method close the file corresponding to
     * the file descriptor
     *
     * @param fd file descriptor
     * @return 0 in success, -1 false
     */
    public int close(int fd)
    {
        return 0; // it needs to be modified later
    }

    /**
     * This method deletes the file that
     * is specified by file name only when the
     * file is closed
     *
     * @param fileName the file name
     * @return 0 if successful, -1 otherwise
     */
    public int delete(String fileName)
    {
        return 0; // it needs to be modified later
    }

    /**
     * This method returns the size in bytes
     * of the file indicated by file descriptor
     * and returns -1 when it detects an error
     *
     * @param fd the file descriptor
     * @return the file size
     */
    public int fsize(int fd)
    {
        return 0; // it needs to be modified later
    }
}
