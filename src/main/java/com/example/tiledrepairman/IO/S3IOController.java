package com.example.tiledrepairman.IO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collection;

@Component
public class S3IOController {

    @Value("${s3.location}")
    private String s3Location;

    public Collection<File> getAllTMXFiles() {
        Collection<File> tmxFiles = FileUtils.listFiles(new File(s3Location), new RegexFileFilter("^(.*tmx)"), DirectoryFileFilter.DIRECTORY);

        return tmxFiles;
    }

}
