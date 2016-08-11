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

    public void sync()
    {
        FileTableEntry fte = this.open("/", "w");
        byte[] dirData = directory.directory2bytes();
        write(fte, dirData);
        close(fte);
        superblock.sync();
    }
    
    /**
     * This method formats the disk
     *
     * @param files the # files to be created
     * @return 0 on success, -1 otherwise
     */
    public boolean format(int files)
    {
        // format/delete all files
        // Check if FileTable ad TCB are empty (isEmpty)
        // allocate "files" inodes
        // returns if format successful
        
        superblock.format(files);
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
        FileTableEntry newfte = filetable.falloc(fileName, mode);
        if (mode == "w")
        {
            if (!deallocAllBlocks(newfte))
            {
                return null;
            }
        }

        return newfte; // when there is no spot in the table
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
    public synchronized int read(FileTableEntry fd, byte[] buffer)
    {
        if (fd != null || fd.mode == "r" || buffer.length > 0)
        {
            synchronized (fd)
            {
                int reading = 0; // amount of data you have to read
                int havingRead = 0; // amound of the data you have read
                int sizeLeft = buffer.length; // size that you are reading
                boolean done = false;

                while(sizeLeft > 0 && fsize(fd) > fd.seekPtr)
                {
                    int targetBlockID = fd.seekPtr;
                    short targetBlock = fd.inode.findTargetBlock(targetBlockID);

                    if (targetBlock == (short) -1)
                    {
                        return 0;
                    }

                    byte[] data = new byte[Disk.blockSize];
                    SysLib.rawread(targetBlock, data);

                    int diskLeft = Disk.blockSize - reading;
                    int fileLeft = fd.inode.length - fd.seekPtr;
                    int offset = fd.seekPtr % Disk.blockSize;

                    reading = (diskLeft < fileLeft) ? diskLeft : fileLeft;

                    if (reading > sizeLeft)
                    {
                        reading = sizeLeft;
                    }

                    System.arraycopy(data, offset, buffer, havingRead, reading);

                    havingRead += reading;
                    sizeLeft -=  reading;
                    fd.seekPtr += reading;
                }
                return havingRead;
            }
        }

        // read byte[] buffer from tcb.ftEnt[fd]
        // return number of bytes read

        return -1; // it needs to be modified later
    }
    
    /**
     * This method writes the contents of the buffer to the
     * file corresponding to the file descriptor.
     *
     * @param fd the file descriptor
     * @param buffer the buffer
     * @return
     */
    public synchronized int write(FileTableEntry fd, byte[] buffer)
    {
        byte[] data = new byte[Disk.blockSize];
        int bytes = 0;
        int bufferLength = buffer.length;

        if (fd != null && fd.mode != "r")
        {
            while (bufferLength > 0)
            {
                int targetBlock = fd.inode.findTargetBlock(fd.seekPtr);

                if (targetBlock == -1)
                {
                    targetBlock = superblock.getFreeBlock();

                    if ((fd.inode.getBlockNumber(fd.seekPtr, (short) targetBlock)) == -1)
                    {
                        return -1;
                    }
                    else if ((fd.inode.getBlockNumber(fd.seekPtr, (short) targetBlock)) == -2)
                    {
                        if (!fd.inode.setBlockNumber((short) superblock.getFreeBlock()))
                        {
                            return -1;
                        }
                        if (fd.inode.getBlockNumber(fd.seekPtr, (short) targetBlock) != 0)
                        {
                            return -1;
                        }
                    }
                }

                SysLib.rawread(targetBlock, data);
                int front = fd.seekPtr % Disk.blockSize;
                int end = Disk.blockSize - front;

                System.arraycopy(buffer, bytes, data, front, Math.min(bufferLength, end));
                SysLib.rawwrite(targetBlock, data);
                fd.seekPtr += Math.min(bufferLength, end);
                bytes += Math.min(bufferLength, end);

                if (bufferLength < end)
                {
                    bufferLength = 0;
                }
                else
                {
                    bufferLength -= end;
                }
            }
        }
        else
        {
            return -1;
        }

        if (fd.inode.length < fd.seekPtr)
        {
            fd.inode.length = fd.seekPtr;
        }

        fd.inode.toDisk(fd.iNumber);
        return bytes;
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
    public synchronized int seek(FileTableEntry fd, int offset, int whence)
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
        
        return fd.seekPtr;
    }
    
    /**
     * This method close the file corresponding to
     * the file descriptor
     *
     * @param fd file descriptor
     * @return true in success, -1 false
     */
    public boolean close(FileTableEntry fd)
    {
        if (fd != null && filetable.ffree(fd))
        {
            return true;
        }

        return false; // when there is no corresponding file table entry
    }
    
    /**
     * This method deletes the file that
     * is specified by file name only when the
     * file is closed
     *
     * @param fileName the file name
     * @return True if successful, false otherwise
     */
    public boolean delete(String fileName)
    {
        // if (file == open){ mark for deletion (also can't receive new open request}
        // else { delete file}

        short iNumber = directory.namei(fileName); // get the iNumber
        if(directory.ifree(iNumber)) // free the iNode and file
        {
            return true;
        }

        return false;
    }
    
    /**
     * This method returns the size in bytes
     * of the file indicated by file descriptor
     * and returns -1 when it detects an error
     *
     * @param fd the file descriptor
     * @return the file size
     */
    public synchronized int fsize(FileTableEntry fd)
    {
        return fd.inode.length;
    }

    private boolean deallocAllBlocks(FileTableEntry ftEnt)
    {
        if(ftEnt.inode.count != 1){
            SysLib.cerr("Null pointer, file table entry is empty");
            return false;
        }

        for(int bID = 0; bID < ftEnt.inode.getDirectSize(); bID++){
            if(ftEnt.inode.direct[bID] != -1){
                superblock.returnBlock(bID);
                ftEnt.inode.direct[bID] = -1;
            }
        }

        byte[] data = new byte[Disk.blockSize];
        SysLib.rawread(ftEnt.inode.indirect, data);
        ftEnt.inode.indirect = -1;

        if(data != null){
            int bID;
            while((bID = SysLib.bytes2short(data, 0)) != -1){
                superblock.returnBlock(bID);
            }
        }
        ftEnt.inode.toDisk(ftEnt.iNumber);
        return true;
    }
}
