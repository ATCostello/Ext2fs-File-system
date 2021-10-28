package OS_Coursework;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Essential information about the file.
 */
public class FileInfo {

    private int inodeNumber;
    private short length;
    private byte nameLength;
    private byte fileType;
    private String fileName;
    private Inode inode;

    /**
     * Sets file information
     * @param position Position of the information to read from the buffer.
     * @param inodeTablePointer The pointer to the inode table from the group descriptor block.
     * @param f The ext2 file.
     * @param buffer Buffer containing the bytes of the file.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    public FileInfo(int position, int inodeTablePointer, RandomAccessFile f, ByteBuffer buffer) throws IOException {
        inodeNumber = buffer.getInt(position);
        length = buffer.getShort(position + 4);
        nameLength = buffer.get(position + 6);
        fileType = buffer.get(position + 7);
        byte[] name = new byte[(int) nameLength];
        buffer.position(position + 8);
        buffer.get(name);
        fileName = new String(name);
        int block = inodeNumber/1712;
        inode = new Inode(inodeTablePointer, block, f, inodeNumber);
    }

    /**
     * Return the length of the file.
     * @return The file length. (in bytes)
     */
    public short getLength() {
        return length;
    }

    /**
     * Returns the name of the file.
     * @return Name of the file.
     */
    public String getFileName(){
        return fileName;
    }

    /**
     * Calculates and returns the size of the file in bytes.
     * @return Size of the file. (in bytes)
     */
    public int getFileSize(){
        int fileSize = Integer.parseInt(inode.getFileSize(), 2);
        fileSize = fileSize/2;
        return fileSize;
    }

    /**
     * Returns the file type.
     * @return File type.
     */
    public int getFileType(){
        return fileType;
    }

    /**
     * Returns the current Inode number.
     * @return Inode number.
     */
    public int getInodeNumber(){
        return inodeNumber;
    }

    /**
     * Returns the User ID of the current file.
     * @return User ID of the current file.
     */
    public int getUserID(){
        return inode.getUserID();
    }

    /**
     * Returns the Group ID of the current file.
     * @return Group ID of the current file.
     */
    public int getGroupID(){
        return inode.getGroupID();
    }

    /**
     * Returns the number of hard links of the current file.
     * @return Number of hard links of the current file.
     */
    public int getHardlinks(){
        return inode.getHardlinks();
    }

    /**
     * Returns the current inode.
     * @return Current inode.
     */
    public Inode getInode(){
        return inode;
    }

    /**
     * Calculates the file permissions of the current file.
     * @return 10 digit string of the permissions of the current file.
     */
    public String getFilePermissions(){
        //Default permissions to none
        String first = "-";
        String second = "-";
        String third = "-";
        String fourth = "-";
        String fifth = "-";
        String sixth = "-";
        String seventh = "-";
        String eighth = "-";
        String ninth = "-";
        String tenth = "-";

        //Inode file modes bits
        final int IRUSR = 0x0100;
        final int IWUSR = 0x0080;
        final int IXUSR = 0x0040;
        final int IRGRP = 0x0020;
        final int IWGRP = 0x0010;
        final int IXGRP = 0x0008;
        final int IROTH = 0x0004;
        final int IWOTH = 0x0002;
        final int IXOTH = 0x0001;

        //Get file mode of the inode
        int fileMode = inode.getFileMode();

        //If file type is a directory, set first byte of permissions to d
        if(fileType == 2)
            first = "d";

        //Or together the file mode bits and file mode
        if((fileMode | IRUSR) == fileMode)
            second = "r";
        if((fileMode | IWUSR) == fileMode)
            third = "w";
        if((fileMode | IXUSR) == fileMode)
            fourth = "x";
        if((fileMode | IRGRP) == fileMode)
            fifth = "r";
        if((fileMode | IWGRP) == fileMode)
            sixth = "w";
        if((fileMode | IXGRP) == fileMode)
            seventh = "x";
        if((fileMode | IROTH) == fileMode)
            eighth = "r";
        if((fileMode | IWOTH) == fileMode)
            ninth = "w";
        if((fileMode | IXOTH) == fileMode)
            tenth = "x";

        //Put together permissions bits into a string
        String perms = String.format("%s%s%s%s%s%s%s%s%s%s", first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth);
        return perms;
    }

    /**
     * Calculates and returns the last modified date of the file.
     * @return Last modified date of the file.
     */
    public String getDate(){
        long seconds = inode.getLastModifiedDate();
        long milliseconds = seconds * 1000;
        Date date = new Date(milliseconds);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm");
        String dateString = dateFormat.format(date);
        return dateString;
    }

}
