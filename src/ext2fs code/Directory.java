package OS_Coursework;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates a unix style directory.
 */
public class Directory {

    private int pointer;
    private RandomAccessFile f;
    private int size = 0;
    private int filesize = 1024;

    /**
     * Directory structure of the filesystem.
     * @param f ext2 file.
     * @param pointer Location of file in directory.
     */
    public Directory(RandomAccessFile f, int pointer) {
        this.pointer = pointer;
        this.f = f;
    }

    /**
     * Prints contents of a directory in a unix format.
     * @param buffer The buffer of bytes.
     * @return Array of file info.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    public FileInfo[] getFileInfo(ByteBuffer buffer) throws IOException {
        //Create list of info
        List<FileInfo> info = new ArrayList<>();

        //Add info to list
        for (int i = 0; size < filesize; i++) {
            info.add(new FileInfo(size, pointer, f, buffer));
            size += (info.get(i).getLength());
            if(i == 0) {
                filesize = info.get(i).getFileSize();
            }
        }

        //Iterate through info list and print file info
        for(FileInfo fi : info) {
            System.out.println(fi.getFilePermissions() + " " + fi.getHardlinks() + " " + fi.getUserID() + " " + fi.getGroupID() + " " + fi.getFileSize() + " " + fi.getDate() + " " + fi.getFileName());
        }
        System.out.println(" ");
        return info.toArray(new FileInfo[info.size()]);
    }

}
