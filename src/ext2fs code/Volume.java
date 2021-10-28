package OS_Coursework;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Opens the file and reads information from it,
 * such as the super block, group descriptor and the root directory information.
 * @author Alfred Costello
 */
public class Volume {

    private boolean helper = FALSE;
    private ByteBuffer buffer;
    private ByteBuffer GroupDescriptorBuffer;
    private int inodeTablePointer;
    private static final int blockSize = 1024;
    private Directory dir;
    private FileInfo[] dirInfo;
    private RandomAccessFile f;
    private int magicNumber;
    private int totalInodes;
    private int totalBlocks;
    private int blocksPerGroup;
    private int inodesPerGroup;
    private int sizeOfInode;

    /**
     * Reads the given ext2 file,
     * gets values within the super block, prints this information to the console
     * gets the group descriptor
     * reads the root directory information (inode 2)
     * @param filename Name of the ext2 file in the base directory
     */
    public Volume(String filename) {
        try {
            //Read file
            f = new RandomAccessFile(filename, "r");

            //open super block
            //Find and read superblock
            ByteBuffer superBlockBuffer = ByteBuffer.allocate(blockSize);
            byte[] superBlockBytes = new byte[blockSize];
            //go to block group 0, read and put into buffer
            f.seek(blockSize);
            f.read(superBlockBytes);
            superBlockBuffer.put(superBlockBytes);
            superBlockBuffer.order(ByteOrder.LITTLE_ENDIAN);
            //Superblock information
            magicNumber = superBlockBuffer.getInt(56);
            totalInodes = superBlockBuffer.getInt(0);
            totalBlocks = superBlockBuffer.getInt(4);
            blocksPerGroup = superBlockBuffer.getInt(32);
            inodesPerGroup = superBlockBuffer.getInt(40);
            sizeOfInode = superBlockBuffer.getInt(88);
            //Get name of block
            byte[] nameBytes = new byte[16];
            superBlockBuffer.position(120);
            superBlockBuffer.get(nameBytes);
            //Print superblock info
            System.out.println("Superblock information: ");
            System.out.println("Magic Number: " + magicNumber);
            System.out.println("Total iNodes: " + totalInodes);
            System.out.println("Total Blocks: " + totalBlocks);
            System.out.println("Blocks per group: " + blocksPerGroup);
            System.out.println("iNodes per group: " + inodesPerGroup);
            System.out.println("Size of iNode: " + sizeOfInode);
            System.out.println(" ");

            //Create GroupDescriptorBuffer with size blockSize
            GroupDescriptorBuffer = ByteBuffer.allocate(blockSize);
            //Go to Group Descriptor
            f.seek(blockSize*2);
            //Read content of Group Descriptor and add to buffer
            byte[] groupDescriptorBytes = new byte[blockSize];
            f.read(groupDescriptorBytes);
            GroupDescriptorBuffer.put(groupDescriptorBytes);
            GroupDescriptorBuffer.order(ByteOrder.LITTLE_ENDIAN);
            //Get inodeTablePointer from GroupDescriptorBuffer
            inodeTablePointer = GroupDescriptorBuffer.getInt(8);

            //Read inode 2 (root directory)
            Inode rootInode = new Inode(inodeTablePointer, 0, f, 2);
            buffer = ByteBuffer.allocate(blockSize);
            buffer = this.iNodePointer(rootInode.getBlockPointer(0));
            dir = new Directory(f, inodeTablePointer);
            dirInfo = dir.getFileInfo(buffer);
        } catch (Exception e) {
            System.out.println("Exception has been caught " + e);
        }
    }

    /**
     * Go to an inode in the file, given a pointer and create a file block.
     * @param pointer Pointer to the inode.
     * @return Buffer of the inode file block.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    public ByteBuffer iNodePointer(int pointer) throws IOException{
        buffer = ByteBuffer.allocate(blockSize*4);
        f.seek(blockSize*pointer);
        byte[] fileBlock = new byte[blockSize*4];
        f.read(fileBlock);
        buffer.put(fileBlock);
        if (helper == TRUE) {
            Helper h = new Helper();
            h.dumpHexBytes(fileBlock);
        }
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer;
    }

    /**
     * Return the pointer to the inode table of the given block.
     * @param block Block to get inode table from.
     * @return Pointer to the inode table.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    public int getInodePointer(int block) throws IOException{
        f.seek(blockSize*2);
        buffer.position(0);
        byte[] pointerBytes = new byte[blockSize];
        f.read(pointerBytes);
        buffer.put(pointerBytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        inodeTablePointer = buffer.getInt(32*block + 8);
        return inodeTablePointer;
    }

    /**
     * Return the pointer to the inode table.
     * @return Pointer to the inode table.
     */
    public int getInodePointer(){
        return inodeTablePointer;
    }

    /**
     * Returns the number of inodes per group.
     * @return Number of inodes per group.
     */
    public int getInodesPerGroup(){
        return inodesPerGroup;
    }

    /**
     * Returns the array of FileInfo.
     * @return Array of FileInfo.
     */
    public FileInfo[] getDirInfo() {
        return dirInfo;
    }

    /**
     * Returns the RandomAccessFile f.
     * @return RandomAccessFile f.
     */
    public RandomAccessFile getF(){
        return f;
    }
}
