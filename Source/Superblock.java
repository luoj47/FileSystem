/**
 *  Created by Ko Fukushima and Jesse Luo on 7/9/2016.
 *
 *  This class is used to describe the number of disk blocks
 *  , the number of inodes, and the block number of the
 *  head block of the free list.
 */
public class Superblock
{
    public int totalBlocks; // the number of disk blocks
    public int totalInodes; // the number of inodes
    public int freeList;    // the block number of the free List's head

    /**
     * Class constructor that initializes the fields that are tatalBlocks,
     * totalInodes, and freeList
     *
     * @param diskSize the disk size
     */
    public Superblock( int diskSize)
    {
    }
}
