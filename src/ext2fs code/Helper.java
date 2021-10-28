package OS_Coursework;

/**
 * Outputs an array of bytes in a readable hexadecimal format.
 */
public class Helper {

    /**
     * Outputs an array of bytes in a readable hexadecimal format.
     * @param bytes Bytes to transform into hex.
     */
    public void dumpHexBytes(byte[] bytes){
        StringBuilder hex = new StringBuilder();
        int i = 0;
        for (byte b : bytes) {
            if(i % 8 == 0 && i != 0){
                hex.append("| ");
            }
            if(i % 32 == 0 && i != 0){
                hex.append("\n");
            }
            hex.append(String.format("%02X ", b));
            i++;
        }
        System.out.println(hex.toString());
        System.out.println(" ");

    }
}
