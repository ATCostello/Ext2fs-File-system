package OS_Coursework;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reads the files within the ext2 file and traverses the filesystem
 */
public class Ext2File {

    private RandomAccessFile f;
    private Volume vol;
    private ByteBuffer buffer;
    private List<String> files;
    private FileInfo currentFile;
    private List<ByteBuffer> buff;
    private int position;

    /**
     * Splits the filepath and traverses the filesystem
     * @param vol An instance of the Volume class to traverse through
     * @param filepath A file path to read
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    Ext2File(Volume vol, String filepath) throws IOException {
        this.vol = vol;
        filepath = filepath.substring(1);
        files = Arrays.asList(filepath.split("/"));
        FileInfo[] dirInfo = vol.getDirInfo();
        goThrough(dirInfo);
    }

    /**
     * Reads a buffer given a start byte and a length.
     * @param startByte The start byte to read from.
     * @param length The length of the bytes to read.
     * @return Byte array containing the bytes read.
     */
    public byte[] read(long startByte, long length){
        int x = 1;
        if(length>4096){
            x = (int)Math.ceil((double)length/1024);
        }
        int newLength = (int) length/x;
        byte[] bytes = new byte[newLength];

        buffer.position((int) startByte);
        buffer.get(bytes);
        return bytes;
    }

    /**
     * Reads a buffer given a length, starting from the current position in the file.
     * @param length The length of the bytes to read.
     * @return Byte array containing the bytes read.
     */
    public byte[] read(long length){
        byte[] bytes = new byte[(int)length];
        buffer.get(bytes);
        return bytes;
    }

    /**
     * Moves to a byte position in the file.
     * @param position The position to move to.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    public void seek(long position) throws IOException{
        f.seek((int) position);
    }

    /**
     * Returns the current position in the file.
     * @return Current position in the file
     */
    public long position(){
        return position;
    }

    /**
     * Returns the size of the file as specified in the filesystem.
     * @return The size of the file as specified in the filesystem.
     */
    public long size(){
        return currentFile.getFileSize();
    }

    /**
     * Traverse the filesystem of the given file.
     * @param dirInfo The file information of the main directory.
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    public void goThrough(FileInfo[] dirInfo) throws IOException{
        //For every file
        for(int j = 0; j<files.size();j++){
            //For each root directory folder
            for(int i = 0; i<dirInfo.length;i++){
                //If the root name is the same
                if(files.get(0).equalsIgnoreCase(dirInfo[i].getFileName())){
                    currentFile = dirInfo[i];
                    int inodeTablePointer;
                    int block = 0;
                    if(dirInfo[i].getInodeNumber() > vol.getInodesPerGroup()){
                        block = dirInfo[i].getInodeNumber()/vol.getInodesPerGroup();
                        inodeTablePointer = vol.getInodePointer(block);
                    }
                    else{
                        inodeTablePointer = vol.getInodePointer();
                    }
                    Inode inodeInfo = new Inode(inodeTablePointer, block, vol.getF(), dirInfo[i].getInodeNumber());
                    buff = new ArrayList<>();
                    for(int x = 0; x<inodeInfo.getBlockPointers().length; x++){
                        if(inodeInfo.getBlockPointer(x) != 0){
                            buff.add(vol.iNodePointer(inodeInfo.getBlockPointer(x)));
                        }
                    }
                    buffer = buff.get(0);
                }
            }

            //If file type is a directory, step through it.
            if(currentFile.getFileType() == 2){
                Directory dir = new Directory(vol.getF(),vol.getInodePointer());
                dirInfo = dir.getFileInfo(buffer);
                files = files.subList(1, files.size());
                goThrough(dirInfo);
            }
        }
    }

}