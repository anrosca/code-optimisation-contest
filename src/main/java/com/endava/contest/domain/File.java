package com.endava.contest.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class File {

    private String fileName;

    private byte[] content;

    public InputStream getContentStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public String toString() {
        return "File{" +
                "fileName='" + fileName + '\'' +
                ", size=" + content.length +
                '}';
    }
}