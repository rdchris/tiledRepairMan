package com.example.tiledrepairman;

import com.example.tiledrepairman.IO.S3IOController;
import com.example.tiledrepairman.IO.WindowsFileController;
import com.example.tiledrepairman.Windows.WindowsBatchController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.sql.Timestamp;
import java.util.Collection;

@SpringBootApplication
public class tiledRepairman implements CommandLineRunner {

    @Autowired
    S3IOController s3IOController;

    @Autowired
    WindowsBatchController windowsBatchController;

    @Autowired
    WindowsFileController windowsFileController;

    public static void main(String[] args) {
        SpringApplication.run(tiledRepairman.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Starting up TiledRepairman at " + new Timestamp(System.currentTimeMillis()));

        // get all TMX files
        Collection<File> oldTMXFiles = s3IOController.getAllTMXFiles();

        // create new TMX maps
        windowsBatchController.createNewTMXMapsUsingTiledCLI(oldTMXFiles);

        // delete all TMX maps
        WindowsFileController.deleteOldTmxFiles(oldTMXFiles);

        // rename new TMX maps to old TMX names
        windowsFileController.renameTmxFiles();

        System.out.println(new Timestamp(System.currentTimeMillis()) + "All maps exported/resaved.");

        // not sure why we need this
        System.exit(0);
    }
}
