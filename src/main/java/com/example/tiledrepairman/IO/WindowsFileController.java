package com.example.tiledrepairman.IO;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;

@Component
public class WindowsFileController {

    @Value("${s3.location}")
    private String s3Location;

    public static void deleteOldTmxFiles(Collection<File> oldTmxFiles) {
        Iterator<File> iterator = oldTmxFiles.iterator();

        boolean needToRerun = false;

        while (iterator.hasNext()) {
            File fileNext = iterator.next();
            if (fileNext.delete()) {
                iterator.remove();
            } else {
                System.out.println(new Timestamp(System.currentTimeMillis()) + " File was not deleted .. " + fileNext.getAbsolutePath() + " get a better OS loser");
                needToRerun = true;
            }
        }

        if (needToRerun) {
            deleteOldTmxFiles(oldTmxFiles);
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

            System.out.println(new Timestamp(System.currentTimeMillis()) + " renaming: " + file.getAbsolutePath() + " to: " + newFile.getAbsolutePath());
            file.renameTo(newFile);

        }
    }
}
