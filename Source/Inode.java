/**
 * Created by Ko Fukushima and Jesse Luo on 7/9/2016.
 *
 * This class describes a file, and this inode is a
 * Simplified version of the UnixInode
 */
public class Inode
{
    private final static int iNodeSize = 32;        // fix to 32 bytes
    private final static int directSize = 11;       // # direct pointers

    public int length;                              // file size in bytes
    public short count;                             // # file-table entries pointing on this
    public short flag;                              // 0 = unused, 1 = used, ...
    public short direct[] = new short[directSize];  // direct pointers
    public short indirect;                          // a indirect pointer

    /**
     * Class constructor that initializes the fields that are length,
     * count, flag, direct, and indirect.
     */
    Inode()
    {
        length = 0;
        count = 0;
        flag = 1;
        for (int i = 0; i < directSize; i++)
        {
            direct[i] = -1;
        }
        indirect = -1;
    }

    /**
     * This method retrieves the inode from disk
     *
     * @param iNumber
     * * edited by Midori on 7/23/16
     * fixed some typos so it compiles 7/25/16
     * added function code from slides
     */
    Inode(short iNumber)
    {
        //retrieves existing inode from disk to memory
        //reads disk block, and locates the iNumber information in that block
        // Initialize a new inode with that info
        int blockNumber = 1 + iNumber / 16;
        byte[] data = new byte[Disk.blockSize];
        SysLib.rawread( blockNumber, data );
        int offset = ( iNumber % 16 ) * 32;

        length = SysLib.bytes2int ( data, offset );
        offset += 4;
        count = SysLib.bytes2short ( data, offset );
        offset += 2;
        flag = SysLib.bytes2short ( data, offset );
        offset += 2;

        for(int i = 0; i < directSize; i++)
        {
            direct[i] = SysLib.bytes2short( data, offset );
            offset += 2;
        }

        indirect = SysLib.bytes2short( data, offset );
    }

    /**
     * This method saves to disk as the i-th inode
     *
     * @param iNumber
     * @return
     * edited by Midori on 7/23/16
     * added function code from slides
     */
    int toDisk(short iNumber)
    {
        //Save to disk as the i-th inode
        int blockNumber = 1 + iNumber / 16;

        //set the block number, divisible by 16. Add 1 to keep out of superblock
        byte[] data = new byte[Disk.blockSize];
        SysLib.rawread(blockNumber, data);

        //Read in the buffer
        int offset = (iNumber % 16) * iNodeSize;

        //Calculate the offset
        SysLib.int2bytes(length, data, offset);

        //Prepare the length
        offset += 4;
        SysLib.short2bytes(count, data, offset);

        //Prepare the count
        offset += 2;
        SysLib.short2bytes(flag, data, offset);

        //Prepare the flag
        offset += 2;
        for (int i = 0; i < directSize; i++)
        {
            SysLib.short2bytes(direct[i], data, offset);
            offset += 2;
        }

        SysLib.short2bytes(indirect, data, offset);
        SysLib.rawwrite(blockNumber, data);
        return iNodeSize;
    }
}
