package dd.utils;

import java.io.File;
import java.util.*;

/**
 * Created by Sergey on 20.04.2017.
 */
public final class FolderReader {
    private FolderReader(){}

    public static Map<String, String> listFilesForFolder(final File folder) {
        Map<String, String> fileNames = new HashMap<>();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                fileNames.put(fileEntry.getName(), fileEntry.getAbsolutePath());
            }
        }
        return fileNames;
    }
}
