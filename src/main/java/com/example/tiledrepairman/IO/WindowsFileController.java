package com.example.tiledrepairman.IO;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collection;

@Component
public class WindowsFileController {

    @Value("${s3.location}")
    private String s3Location;

    public static void deleteOldTmxFiles(Collection<File> oldTmxFiles) {
        for (File fileToDelete : oldTmxFiles) {
            System.out.println("Deleting file : " + fileToDelete.getAbsolutePath());
            fileToDelete.delete();
        }
    }

    public void renameTmxFiles() {
        Collection<File> renamedTmxFiles = FileUtils.listFiles(
                new File(s3Location),
                new RegexFileFilter("^(.*tmx)"),
                DirectoryFileFilter.DIRECTORY
        );

        for (File file : renamedTmxFiles) {

            String name = file.getAbsolutePath();
            name = name.replace("-renamed.tmx", ".tmx");
            File newFile = new File(name);

            System.out.println("renaming: " + file.getAbsolutePath() + " to: " + newFile.getAbsolutePath());
            file.renameTo(newFile);

        }
    }
}
