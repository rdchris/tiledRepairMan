package com.example.tiledrepairman.IO;

import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

@Component
public class RepairmanStatusController {

    private Integer filesConfirmedCreated;
    int retryCount = 0;
    private static final int MAX_RETRY_COUNT = 500;

    public boolean areFilesReadyForDelete(Collection<File> oldTmxFiles) throws InterruptedException {
        int filesCountFromOldTmxFiles = oldTmxFiles.size();


        // DO NOT USE THE .toList() as it's immutable and will throw Null pointers on interator.remove()!
        LinkedList<File> copyOfOldTmxFiles = new LinkedList<>();

        oldTmxFiles.forEach((tmxFile) -> {
            copyOfOldTmxFiles.add(tmxFile);
        });

        filesConfirmedCreated = 0;
        filesConfirmedCreated = this.interateThroughAllFilesSeeingIfWeAreReadyToDelete(copyOfOldTmxFiles);

        if (filesConfirmedCreated == null) {
            System.out.println("Retry count surpassed " + MAX_RETRY_COUNT + " failing!");
            return false;
        }

        if (filesConfirmedCreated == filesCountFromOldTmxFiles) {
            return true;
        } else {
            return false;
        }

    }

    private Integer interateThroughAllFilesSeeingIfWeAreReadyToDelete(Collection<File> copyOfOldTmxFiles) throws InterruptedException {

        if (retryCount > MAX_RETRY_COUNT) {
            return null;
        }

        Iterator<File> iterator = copyOfOldTmxFiles.iterator();

        boolean needToRerun = false;

        while (iterator.hasNext()) {
            File fileNext = iterator.next();
            String name = fileNext.getAbsolutePath();
            name = name.replace(".tmx", "-renamed.tmx");
            if (Files.exists(Path.of(name))) {
                System.out.println("File " + name + " was found to have been created!");
                filesConfirmedCreated++;
                iterator.remove();
            } else {
                System.out.println(new Timestamp(System.currentTimeMillis()) + " File was not created yet .. " + name + " will recheck in 5 seconds");
                needToRerun = true;
            }
        }

        // This is extremely ugly.. I apologize...
        // The windows cmd process to fire the Tiled CLI is totally async, thus it might still have the file locked.
        // If this happens we need to just rerunning this method until they are all deleted.
        if (needToRerun) {
            System.out.println("Rerunning temp file check process, the retry count was " + retryCount);
            retryCount++;
            TimeUnit.SECONDS.sleep(5);
            interateThroughAllFilesSeeingIfWeAreReadyToDelete(copyOfOldTmxFiles);
        }

        return filesConfirmedCreated;
    }
}
