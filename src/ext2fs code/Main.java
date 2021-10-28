package OS_Coursework;

import java.io.IOException;

/**
 * Used to provide the ext2 file name and provides an output buffer.
 * @author Alfred Costello
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Volume  vol = new Volume("ext2fs");
        Ext2File  f = new Ext2File (vol, "/big-dir");
        byte buf[] = f.read(0L, f.size());
        System.out.format("%s\n", new String(buf));
    }
    
}
