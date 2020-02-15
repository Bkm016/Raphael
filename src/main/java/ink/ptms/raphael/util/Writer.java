package ink.ptms.raphael.util;

import io.izzel.taboolib.util.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @Author sky
 * @Since 2020-02-03 18:42
 */
public class Writer {


    public static void writeAppend(File file, Files.WriteHandle writeHandle) {
        try (FileWriter fileWriter = new FileWriter(file, true); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            writeHandle.write(bufferedWriter);
            bufferedWriter.flush();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public interface WriteHandle {

        void write(BufferedWriter writer) throws IOException;
    }
}
