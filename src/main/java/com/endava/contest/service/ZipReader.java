package com.endava.contest.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.stereotype.Service;

import com.endava.contest.domain.File;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ZipReader {

    private static final int BUFFER_CAPACITY = 4096;

    public List<File> readZipArchive(InputStream zipArchive) {
        try {
            return tryReadZipArchive(zipArchive);
        } catch (IOException e) {
            log.error("Error while reading the zip archive: {}", e);
            throw new RuntimeException(e);
        }
    }

    private static List<File> tryReadZipArchive(InputStream zipArchiveStream) throws IOException {
        List<File> files = new ArrayList<>(1000);
        try (ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(zipArchiveStream))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                files.add(readFileFrom(zipInputStream, entry.getName()));
            }
            return files;
        }
    }

    private static File readFileFrom(ZipInputStream zipInputStream, String fileName) throws IOException {
        int bytesRead;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_CAPACITY];
        while ((bytesRead = zipInputStream.read(buffer)) > 0) {
            out.write(buffer, 0, bytesRead);
        }
        return new File(fileName, out.toByteArray());
    }
}