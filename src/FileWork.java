import java.io.*;
import java.util.ArrayList;

public class FileWork
{
    static void writeToFile(File file, String keys)
    {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(keys + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static String readFileMessage(File file) {
        String line = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
}
