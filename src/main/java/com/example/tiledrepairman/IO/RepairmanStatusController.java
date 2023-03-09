package com.example.tiledrepairman.IO;

import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Component
public class RepairmanStatusController {

    int filesConfirmedCreated;

    public boolean areFilesReadyForDelete(Collection<File> oldTmxFiles) throws InterruptedException {
        int filesCountFromOldTmxFiles = oldTmxFiles.size();

        Collection<File> copyOfOldTmxFiles = oldTmxFiles.stream().toList();

        filesConfirmedCreated = 0;
        filesConfirmedCreated = this.interateThroughAllFilesSeeingIfWeAreReadyToDelete(copyOfOldTmxFiles);

        if (filesConfirmedCreated == filesCountFromOldTmxFiles) {
            return true;
        } else {
            return false;
        }

    }

    private int interateThroughAllFilesSeeingIfWeAreReadyToDelete(Collection<File> copyOfOldTmxFiles) throws InterruptedException {
        Iterator<File> iterator = copyOfOldTmxFiles.iterator();

        boolean needToRerun = false;

        while (iterator.hasNext()) {
            File fileNext = iterator.next();
            String name = fileNext.getAbsolutePath();
            name.replace(".tmx", "-renamed.tmx");
            if (Files.exists(Path.of(name))) {
                System.out.println("File " + name + " was found to have been created!");
                filesConfirmedCreated++;
                iterator.remove();
            } else {
                System.out.println(new Timestamp(System.currentTimeMillis()) + " File was not created yet .. " + fileNext.getAbsolutePath() + " get a better OS loser, try again!");
                needToRerun = true;
            }
        }

        // This is extremely ugly.. I apologize...
        // The windows cmd process to fire the Tiled CLI is totally async, thus it might still have the file locked.
        // If this happens we need to just rerunning this method until they are all deleted.
        if (needToRerun) {
            TimeUnit.SECONDS.sleep(1);
            interateThroughAllFilesSeeingIfWeAreReadyToDelete(copyOfOldTmxFiles);
        }

        return filesConfirmedCreated;
    }
}
