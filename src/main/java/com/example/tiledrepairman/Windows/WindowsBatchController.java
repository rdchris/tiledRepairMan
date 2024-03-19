package com.example.tiledrepairman.Windows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
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

        allTMXFiles.forEach((tmxFile -> {

                String name = tmxFile.getAbsolutePath();
                System.out.println(new Timestamp(System.currentTimeMillis()) + " creating new version of " + name);
                name = name.replace(".tmx", "-renamed.tmx");

                System.out.println("launching tiled on: " + tmxFile.getAbsolutePath());
                builder.command("/usr/bin/flatpak","run","--branch=stable","--arch=x86_64","--command=tiled","--file-forwarding","org.mapeditor.Tiled","--export-map", tmxFile.getAbsolutePath(), name);

                Process process = null;
                try {
                    process = builder.start();
                    StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
                    Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
                    int exitCode = process.waitFor();
                    assert exitCode == 0;
                    future.get(60, TimeUnit.SECONDS);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }

        }));
    }
}
