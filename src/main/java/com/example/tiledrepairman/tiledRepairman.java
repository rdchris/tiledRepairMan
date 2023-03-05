package com.example.tiledrepairman;

import com.example.tiledrepairman.IO.S3IOController;
import com.example.tiledrepairman.Windows.WindowsBatchController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class tiledRepairman implements CommandLineRunner {

    @Autowired
    S3IOController s3IOController;

    @Autowired
    WindowsBatchController windowsBatchController;

    public static void main(String[] args) {
        SpringApplication.run(tiledRepairman.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //Collection<File> allTMXFiles = s3IOController.getAllTMXFiles();

        windowsBatchController.test();
        System.exit(0);
    }
}
