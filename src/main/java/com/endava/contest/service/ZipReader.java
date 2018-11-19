package com.endava.contest.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.endava.contest.domain.File;

public class ZipReader {

    private static final int BUFFER_CAPACITY = 4096;

    private Consumer<File> fileConsumer;

    public void readZipArchive(InputStream zipArchive) {
        try {
            tryReadZipArchive(zipArchive);
        } catch (IOException e) {
            System.out.println("Error while reading the zip archive: " + e);
            throw new RuntimeException(e);
        }
    }

    public void registerConsumer(final Consumer<File> fileConsumer) {
        this.fileConsumer = fileConsumer;
    }

    private void tryReadZipArchive(InputStream zipArchiveStream) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(zipArchiveStream))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                fileConsumer.accept(readFileFrom(zipInputStream, entry.getName()));
            }
            fileConsumer.accept(null);
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