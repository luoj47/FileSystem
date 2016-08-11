/**
 *  Created by Ko Fukushima and Jesse Luo on 7/9/2016.
 *
 *  This class is used to describe the number of disk blocks
 *  , the number of inodes, and the block number of the
 *  head block of the free list.
 */
public class Superblock
{
    public final int defaultInodeBlocks = 64;
    public int totalBlocks; // the number of disk blocks
    public int totalInodes; // the number of inodes
    public int freeList;    // the block number of the free List's head

    /**
     * Class constructor that initializes the fields that are tatalBlocks,
     * totalInodes, and freeList
     *
     * @param diskSize the disk size
     */
    public Superblock(int diskSize)
    {
        byte[] superBlock = new byte[Disk.blockSize];
        SysLib.rawread(0, superBlock); // starts from disk0

        // Convert the data in the disk to integer
        totalBlocks = SysLib.bytes2int(superBlock, 0); // offset 0
        totalInodes = SysLib.bytes2int(superBlock, 4); // offset 4
        freeList = SysLib.bytes2int(superBlock, 8); // offset 8

        if (totalBlocks == diskSize && totalInodes > 0 && freeList >= 2)
        {
            return;
        }
        else
        {
            totalBlocks = diskSize;
            SysLib.cerr("Formatting\n");
            format(defaultInodeBlocks);
        }
    }

    /**
     * This sync writes the data back
     * to the disk
     */
    public void sync()
    {
        // create superblocks
        byte[] superBlock = new byte[Disk.blockSize];

        // Convert the data in the disk to integer
        SysLib.int2bytes(totalBlocks, superBlock, 0); // offset 0
        SysLib.int2bytes(totalInodes, superBlock, 4); // offset 4
        SysLib.int2bytes(freeList, superBlock, 8); // offset 8

        SysLib.rawwrite(0, superBlock);
    }

    /**
     * This formats the files
     *
     * @param numFiles
     */
    public void format(int numFiles)
    {
        // set the total Inodes
        totalInodes = numFiles;

        // get the starting block for the free list and set
        // the offset
        freeList = (totalInodes % 16 == 0) ? (totalInodes / 16 + 1) : (totalInodes / 16 + 2);

        // create Inodes
        for (int i = 0; i < numFiles; i++)
        {
            Inode inode = new Inode();
            inode.flag = 0;
            inode.toDisk((short)i);
        }

        // create superblocks
        byte[] superBlock = new byte[Disk.blockSize];

        // Convert the data in the disk to integer
        SysLib.int2bytes(totalBlocks, superBlock, 0); // offset 0
        SysLib.int2bytes(totalInodes, superBlock, 4); // offset 4
        SysLib.int2bytes(freeList, superBlock, 8); // offset 8

        SysLib.rawwrite(0, superBlock);

        for (int i = freeList; i < totalBlocks; i++)
        {
            byte[] data = new byte[Disk.blockSize];
            SysLib.int2bytes(i + 1, data, 0);
            SysLib.rawwrite(i, data);
        }
    }

    /**
     * This gets the free list
     *
     * @return freeList when totalBlocks > freeList && freeList > 0
     * and -1 otherwise
     */
    public int getFreeBlock()
    {
        if (totalBlocks > freeList && freeList > 0)
        {
            // this is to return the free list
            int freeList = this.freeList;

            byte[] data = new byte[Disk.blockSize];
            SysLib.rawread(freeList, data);

            // set the next free list
            this.freeList = SysLib.bytes2int(data, 0);

            return freeList;
        }

        return -1;
    }

    /**
     * This method  adds
     * back to the freelist.
     *
     * @param oldBlockNumber
     * @return True if success false otherwise
     */
    public boolean returnBlock(int oldBlockNumber)
    {

        if (oldBlockNumber > totalBlocks && oldBlockNumber > 0)
        {
            byte[] data = new byte[Disk.blockSize];

            SysLib.int2bytes(freeList, data, 0);
            SysLib.rawwrite(oldBlockNumber, data);

            freeList = oldBlockNumber;
            return true;
        }

        return false;
    }
}