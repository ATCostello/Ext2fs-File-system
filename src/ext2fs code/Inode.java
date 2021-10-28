package OS_Coursework;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Contents of the inode information.
 */
public class Inode {

    private short fileMode;
    private short userID;
    private int filesizeLower;
    private int lastAccessTime;
    private int creationTime;
    private int lastModifiedTime;
    private int deletedTime;
    private short groupID;
    private short numberOfHardLinks;
    private int[] blockPointers;
    private int indirectPointer;
    private int doublePointer;
    private int triplePointer;
    private int filesizeUpper;

    /**
     * Stores the inode information of a given block and inodenumber.
     * @param inodeTablePointer The pointer to the inode table, found within the group descriptor.
     * @param block The block to read the inode from.
     * @param f The ext2 file.
     * @param inodeNumber The inode number to gather information of.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    public Inode(int inodeTablePointer, int block, RandomAccessFile f, int inodeNumber) throws IOException{
        //Decrement inodenumber
        int inodesInABlock = 1712*block;
        inodeNumber = inodeNumber - inodesInABlock;

        //Create iNodebuffer and allocate 1024 bytes
        ByteBuffer inodeBuffer = ByteBuffer.allocate(1024);
        f.seek(1024*inodeTablePointer + 128*(inodeNumber-1));

        //Create byte array for the inode block and add to inodeBuffer
        byte[] inodeBlock = new byte[1024];
        f.read(inodeBlock);
        inodeBuffer.put(inodeBlock);

        //Order inodeBuffer in little endian format
        inodeBuffer.order(ByteOrder.LITTLE_ENDIAN);

        //Get important information from the iNode
        fileMode = inodeBuffer.getShort(0);
        userID =  inodeBuffer.getShort(2);
        filesizeLower = inodeBuffer.getInt(4);
        lastAccessTime = inodeBuffer.getInt(8);
        creationTime = inodeBuffer.getInt(12);
        lastModifiedTime = inodeBuffer.getInt(16);
        deletedTime = inodeBuffer.getInt(20);
        groupID = inodeBuffer.getShort(24);
        numberOfHardLinks = inodeBuffer.getShort(26);
        //Pointers to first 12 data blocks (12*4)
        blockPointers = new int[12];
        for(int i = 0; i<12;i++){
            blockPointers[i]=inodeBuffer.getInt(40+(i*4));
        }
        filesizeUpper = inodeBuffer.getInt(108);
        indirectPointer = inodeBuffer.getInt(88);
        doublePointer = inodeBuffer.getInt(92);
        triplePointer = inodeBuffer.getInt(96);
    }

    /**
     * Returns the inode block pointer information given an index
     * @param index Index number to inode block pointer.
     * @return Inode block pointer information of the given index.
     */
    public int getBlockPointer(int index){
        return blockPointers[index];
    }

    /**
     * Returns the inode block pointers information in an integer array.
     * @return Inode block pointer information in an integer array.
     */
    public int[] getBlockPointers(){
        return blockPointers;
    }

    /**
     * Returns the last modified date of the file.
     * @return The last modified date of the file.
     */
    public int getLastModifiedDate(){
        return lastModifiedTime;
    }

    /**
     * Returns the number of hardlinks the file has.
     * @return Number of hardlinks the file has.
     */
    public short getHardlinks(){
        return numberOfHardLinks;
    }

    /**
     * Returns the user ID of the file.
     * @return User ID of the file.
     */
    public short getUserID(){
        return userID;
    }

    /**
     * Returns the group ID of the file.
     * @return Group ID of the file.
     */
    public short getGroupID(){
        return groupID;
    }

    /**
     * Calculates the file size of the file and returns it.
     * @return Filesize of the file. (in bytes)
     */
    public String getFileSize(){
        String upper = Integer.toBinaryString(filesizeUpper);
        String lower = Integer.toBinaryString(filesizeLower);
        String fileSize =  lower.concat(upper);
        return fileSize;
    }

    /**
     * Returns the file mode of the file.
     * @return File mode of the file.
     */
    public short getFileMode() {
        return fileMode;
    }
}
