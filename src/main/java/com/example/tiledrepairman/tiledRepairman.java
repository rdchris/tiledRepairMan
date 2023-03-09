package com.example.tiledrepairman;

import com.example.tiledrepairman.IO.RepairmanStatusController;
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

    @Autowired
    RepairmanStatusController repairmanStatusController;

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

        boolean areFilesReadyForDelete = repairmanStatusController.areFilesReadyForDelete(oldTMXFiles);

        // Not all files were prepared properly, lets fail and delete the temp files
        if (!areFilesReadyForDelete) {
            windowsFileController.deleteAllTempFiles();
            this.printFailedMessage();
            return;
        }

        // delete all TMX maps
        WindowsFileController.deleteOldTmxFiles(oldTMXFiles);

        // rename new TMX maps to old TMX names
        windowsFileController.renameTmxFiles();

        System.out.println(new Timestamp(System.currentTimeMillis()) + "All maps exported/resaved.");

        // not sure why we need this
        System.exit(0);
    }

    private void printFailedMessage() {
        System.out.println("===================================================");
        System.out.println("TiledRepairman bug found!       |     |");
        System.out.println("                                \\_V_//");
        System.out.println("                                \\/=|=\\/");
        System.out.println("                                 [=v=]");
        System.out.println("                               __\\___/_____");
        System.out.println("                              /..[  _____  ]");
        System.out.println("                             /_  [ [  M /] ]");
        System.out.println("                            /../.[ [ M /@] ]");
        System.out.println("                           <-->[_[ [M /@/] ]");
        System.out.println("                          /../ [.[ [ /@/ ] ]");
        System.out.println("     _________________]\\ /__/  [_[ [/@/ C] ]");
        System.out.println("    <_________________>>0---]  [=\\ \\@/ C / /");
        System.out.println("       ___      ___   ]/000o   /__\\ \\ C / /");
        System.out.println("          \\    /              /....\\ \\_/ /");
        System.out.println("       ....\\||/....           [___/=\\___/");
        System.out.println("      .    .  .    .          [...] [...]");
        System.out.println("     .      ..      .         [___/ \\___]");
        System.out.println("     .    0 .. 0    .         <---> <--->");
        System.out.println("  /\\/\\.    .  .    ./\\/\\      [..]   [..]");
        System.out.println(" / / / .../|  |\\... \\ \\ \\    _[__]   [__]_");
        System.out.println("/ / /       \\/       \\ \\ \\  [____>   <____]");
        System.out.println("===================================================");
        System.out.println(" Matt you will need to rerun this ");
        System.out.println("===================================================");


    }
}
