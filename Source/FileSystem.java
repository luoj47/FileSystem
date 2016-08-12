/**
 * Created by Ko Fukushima, Lu Ming Hsuan, Jesse Luo
 * CSS 430 A
 * Professor Mike Panitz
 * 11 August 2016
 * Final Project, FileSystem: FileSystem.java
 *
 * This class manages the structure of a FileSystem by holding the Superblock
 * Directory, and Filetable, as well as performing all the disk operations.
 * FileSystem acts as the interface in which a user may interact with to access
 * the functions of our FileSystem.
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
     * Constructs a new FileSystem and initializes superblock,
     * directory, and filetable.
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

    /**
     * Syncs the superblock ands keeps the file system synced with the disk.
     */
    public void sync()
    {
        FileTableEntry fte = this.open("/", "w");
        // write directory information to disk
        byte[] dirData = directory.directory2bytes();
        write(fte, dirData);
        close(fte);
        superblock.sync();
    }
    
    /**
     * This method formats the disk, erasing all data on the disk
     * (superblock, filetable, directory). The parameter, files,
     * the number of inodes to be created by the superblock after
     * it has been formatted.
     *
     * @param files the # files to be created
     * @return 0 on success (always)
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
        return true;
    }
    
    /**
     * This method opens the file corresponding to the file
     * name string in the given mode. A new FileTableEntry is
     * created with the given filename and mode. If the mode for
     * that FileTableEntry is write ("w"), then all blocks are
     * deallocated and writing starts from the beginning. The
     * FileTableEntry is returned after writing is successfull
     * or if the mode was not write.
     *
     * @param fileName
     * @param mode
     * @return FileTableEntry newfte
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
     * corresponding to the file descriptor.
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
     * file corresponding to the file descriptor. The write
     * starts at the seek pointer, and increases the seekptr
     * based on bytes, or the bytes written. The bytes written
     * is returned at the end, or -1 if an error occured.
     *
     * @param fd the file descriptor
     * @param buffer the buffer
     * @return bytes written (bytes)
     */
    public synchronized int write(FileTableEntry fd, byte[] buffer)
    {
        byte[] data = new byte[Disk.blockSize];
        int bytes = 0;
        int bufferLength = buffer.length;

        // Check if fd is not null and mode is correct.
        if (fd != null && fd.mode != "r")
        {
            while (bufferLength > 0)
            {
                int targetBlock = fd.inode.findTargetBlock(fd.seekPtr);
                // target block doesn't exist
                if (targetBlock == -1)
                {
                    targetBlock = superblock.getFreeBlock();
                    // attempt to get a block for the target block
                    if ((fd.inode.getBlockNumber(fd.seekPtr, (short) targetBlock)) == -1)
                    {
                        return -1;
                    }
                    else if ((fd.inode.getBlockNumber(fd.seekPtr, (short) targetBlock)) == -2)
                    {
                        // indirect < 0
                        if (!fd.inode.setBlockNumber((short) superblock.getFreeBlock()))
                        {
                            return -1;
                        }
                        // check for block pointer error
                        if (fd.inode.getBlockNumber(fd.seekPtr, (short) targetBlock) != 0)
                        {
                            return -1;
                        }
                    }
                }

                SysLib.rawread(targetBlock, data);
                int front = fd.seekPtr % Disk.blockSize;
                int end = Disk.blockSize - front;

                // update seek pointers
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

    /**
     * deallocAllBlocks
     * This method dellocates all blocks in inode by checking
     * the count of the inode block, if it is not 1, it means it is null
     * and therefore invalid. If count is 1 and valid
     * it traverses all direct pointer blocks with loops. 
     * Each block is check if valid and then returnBlock is
     * invoked to wipe the block clean
     * and the block will be set to invalid 
     * Finally, inodes are written back to disk
     * @param ftEnt
     * @return Returns if all direct pointers blocks are valid.
     */
    private boolean deallocAllBlocks(FileTableEntry ftEnt)
    {
        if(ftEnt.inode.count != 1){//checks count, if not 1, inode is empty
            SysLib.cerr("Null pointer, file table entry is empty");
            return false;//invalid
        }

        for(int bID = 0; bID < ftEnt.inode.getDirectSize(); bID++){//loops size of inode
            if(ftEnt.inode.direct[bID] != -1){//if not -1, valid
                superblock.returnBlock(bID);//return block
                ftEnt.inode.direct[bID] = -1;//and reset direct block to invalid
            }
        }

        byte[] data = new byte[Disk.blockSize];//create new byte[] data to store new block data
        SysLib.rawread(ftEnt.inode.indirect, data);
        ftEnt.inode.indirect = -1;
        
        //if data is not empty, write back to disk
        if(data != null){
            int bID;
            while((bID = SysLib.bytes2short(data, 0)) != -1){//block id valid
                superblock.returnBlock(bID);//return block
            }
        }
        ftEnt.inode.toDisk(ftEnt.iNumber);
        return true;
    }
}
