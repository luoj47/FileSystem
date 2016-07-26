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
    public Superblock(int diskSize) {
        byte[] superBlock = new byte[diskSize];
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
            format();
        }
    }
    
    public void sync()
    {
        
    }
    
    public void format()
    {
        int files = totalInodes * 16;
        SysLib.format(files); // # files is # inodes
    }
    
    public void format(int numBlocks)
    {
        
    }
    
    public int getFreeBlock()
    {
        return 0;
    }
    
    public boolean returnBlock(int oldBlockNumber)
    {
        return false;
    }
}
