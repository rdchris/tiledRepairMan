package com.example.tiledrepairman.Windows;

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

    @Value("${s3.location}")
    private String s3Location;

    public void createNewTMXMapsUsingTiledCLI(Collection<File> allTMXFiles) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        ProcessBuilder builder = new ProcessBuilder();

        for (File file : allTMXFiles) {
            String name = file.getAbsolutePath();
            name = name.replace(".tmx", "-renamed.tmx");

            System.out.println("launching tiled on: " + file.getAbsolutePath());
            builder.command("C:\\Program Files\\Tiled\\tiled.exe", "--export-map", file.getAbsolutePath(), name);

            Process process = builder.start();
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
            Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = process.waitFor();
            assert exitCode == 0;
            future.get(10, TimeUnit.SECONDS);

        }

    }
}
