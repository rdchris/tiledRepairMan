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

    private CompletableFuture<String> future;

    public void createNewTMXMapsUsingTiledCLI(Collection<File> allTMXFiles) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        ProcessBuilder builder = new ProcessBuilder();

        allTMXFiles.forEach((tmxFile -> {
            future = CompletableFuture.supplyAsync(() -> {
                String name = tmxFile.getAbsolutePath();
                System.out.println(new Timestamp(System.currentTimeMillis()) + " creating new version of " + name);
                name = name.replace(".tmx", "-renamed.tmx");

                System.out.println("launching tiled on: " + tmxFile.getAbsolutePath());
                builder.command("C:\\Program Files\\Tiled\\tiled.exe", "--export-map", "--new-instance", tmxFile.getAbsolutePath(), name);

                Process process = null;
                try {
                    process = builder.start();
                    StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
                    Future<?> future = Executors.newSingleThreadExecutor().submit(streamGobbler);
                    int exitCode = process.waitFor();
                    assert exitCode == 0;
                    future.get(10, TimeUnit.SECONDS);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
                return null;
            });

        }));

        future.get();

    }
}
