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
        offset += 2;
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
        byte[] temp = new byte[iNodeSize];

        //Read in the buffer
        int offset = 0; // (iNumber % 16) * iNodeSize;

        //Calculate the offset
        SysLib.int2bytes(length, temp, offset);

        //Prepare the length
        offset += 4;
        SysLib.short2bytes(count, temp, offset);

        //Prepare the count
        offset += 2;
        SysLib.short2bytes(flag, temp, offset);

        //Prepare the flag
        offset += 2;

        for (int i = 0; i < directSize; i++)
        {
            SysLib.short2bytes(direct[i], temp, offset);
            offset += 2;
        }

        SysLib.short2bytes(indirect, temp, offset);
        offset += 2;


        byte[] data = new byte[Disk.blockSize];
        offset = (iNumber % 16) * iNodeSize;
        SysLib.rawread(blockNumber, data);

        System.arraycopy(temp, 0, data, offset, iNodeSize);
        SysLib.rawwrite(blockNumber, data);

        return 0;
    }

    // find target block
    public short findTargetBlock(int offset)
    {
        if ((offset / Disk.blockSize) < directSize)
        {
            return direct[offset / Disk.blockSize];
        }
        if(indirect < 0)
        {
            return -1;
        }


        byte[] data = new byte[Disk.blockSize];
        SysLib.rawread(indirect, data);

        return SysLib.bytes2short(data, (((offset / Disk.blockSize) - directSize) * 2));
    }

    /** get Block Number
     *
     * @param seek
     * @param offset
     * @return
     */
    int getBlockNumber(int seek, short offset)
    {
        if ((seek / Disk.blockSize) < directSize)
        {
            if(((direct[(seek / Disk.blockSize)] >= 0))
                || (((seek / Disk.blockSize) > 0 )
                &&(direct[(seek / Disk.blockSize) - 1 ] == -1)))
            {
                return -1;
            }
            else
            {
                direct[(seek / Disk.blockSize)] = offset;
                return 0;
            }
        }
        else if (indirect < 0)
        {
            return -2;
        }
        else
        {
            byte[] data = new byte[Disk.blockSize];
            SysLib.rawread(indirect, data);

            if ( SysLib.bytes2short(data, (((seek / Disk.blockSize) - directSize) * 2)) > 0)
            {
                return -1;
            }
            else
            {
                SysLib.short2bytes(offset, data, (((seek / Disk.blockSize) - directSize) * 2));
                SysLib.rawwrite(indirect, data);
            }
        }
        return 0;
    }

    /** set block number
     *
     * @param newIndirect
     * @return
     */
    boolean setBlockNumber(short newIndirect)
    {
        if (indirect != -1)
        {
            return false;
        }
        else
        {
            for (int i = 0; i < directSize; i++)
            {
                if (direct[i] == -1)
                {
                    return false;
                }
            }
        }

        indirect = newIndirect;
        byte[] data = new byte[Disk.blockSize];
        for(int i = 0; i < (Disk.blockSize / 2); i++)
        {
            SysLib.short2bytes(((short) -1), data, (i * 2));
        }

        SysLib.rawwrite(newIndirect, data);

        return true;
    }

    public static int getDirectSize(){
        return directSize;
    }
}