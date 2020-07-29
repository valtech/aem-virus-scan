/*
 * Copyright 2020 Valtech GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package de.valtech.avs.core.filter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

import javax.servlet.http.Part;

/**
 * Wrapper to replace input stream from part.
 * 
 * @author Roland Gruber
 */
public class PartWrapper implements Part {

    private Part part;
    private File file;
    private byte[] content;

    /**
     * Constructor
     * 
     * @param part original part
     * @param file file
     */
    public PartWrapper(Part part, File file) {
        this.part = part;
        this.file = file;
    }

    /**
     * Constructor
     * 
     * @param part    original part
     * @param content content
     */
    public PartWrapper(Part part, byte[] content) {
        this.part = part;
        this.content = content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (file == null) {
            return new ByteArrayInputStream(content);
        }
        return Files.newInputStream(Paths.get(file.getPath()), StandardOpenOption.DELETE_ON_CLOSE);
    }

    @Override
    public String getContentType() {
        return part.getContentType();
    }

    @Override
    public String getName() {
        return part.getName();
    }

    @Override
    public long getSize() {
        return part.getSize();
    }

    @Override
    public void write(String fileName) throws IOException {
        part.write(fileName);
    }

    @Override
    public void delete() throws IOException {
        part.delete();
    }

    @Override
    public String getHeader(String name) {
        return part.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return part.getHeaders(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return part.getHeaderNames();
    }

    @Override
    public String getSubmittedFileName() {
        return part.getSubmittedFileName();
    }

}
