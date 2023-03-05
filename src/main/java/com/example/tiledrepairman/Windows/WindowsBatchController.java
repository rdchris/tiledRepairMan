package com.example.tiledrepairman.Windows;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.*;

@Component
public class WindowsBatchController {

    @Value("${tiled.location}")
    private String tiledLocation;

    public void test() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        ProcessBuilder builder = new ProcessBuilder();
        //if (isWindows) {
        //builder.command("cmd.exe", "/c", "dir");
//        } else {
//            builder.command("sh", "-c", "ls");
//        }

        File dir;
        try {
            dir = new File(tiledLocation);
        }
        catch (Exception e) {
            System.out.println(e.toString());
            return;
        }


        //tiled.exe --export-map "C:\projects\s3bucketBackup\active\Hae-Catacombs\Hae-Catacombs00.tmx" "C:\projects\s3bucketBackup\active\Hae-Catacombs\Hae-catacombs00renamed.tmx"

       // builder.directory(dir);

        File mapsDirectory = new File("C:\\Projects\\s3bucketBackup\\active");
        Collection<File> tmxFiles = FileUtils.listFiles(
                mapsDirectory,
                new RegexFileFilter("^(.*tmx)"),
                DirectoryFileFilter.DIRECTORY
        );

        for (File file : tmxFiles) {
            String name = file.getAbsolutePath();
            name = name.replace(".tmx","-renamed.tmx");

            System.out.println("launching tiled on: "+file.getAbsolutePath());
            builder.command("C:\\Program Files\\Tiled\\tiled.exe","--export-map", file.getAbsolutePath(), name);

            Process process = builder.start();
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = process.waitFor();
            assert exitCode == 0;
            future.get(10, TimeUnit.SECONDS);

            //Process process = new ProcessBuilder("C:\\Program Files\\Tiled\\tiled.exe","--export-map",file.getAbsolutePath(),name).start();

            file.delete();

        }


        Collection<File> renamedTmxFiles = FileUtils.listFiles(
                mapsDirectory,
                new RegexFileFilter("^(.*tmx)"),
                DirectoryFileFilter.DIRECTORY
        );

        for (File file : renamedTmxFiles) {

            String name = file.getAbsolutePath();
            name = name.replace("-renamed.tmx",".tmx");
            File newFile = new File(name);

            System.out.println("renaming: "+file.getAbsolutePath()+" to: "+newFile.getAbsolutePath());
            file.renameTo(newFile);

        }


        System.out.println("All maps exported/resaved.");




    }
}
