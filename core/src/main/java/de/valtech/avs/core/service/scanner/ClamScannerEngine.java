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
package de.valtech.avs.core.service.scanner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.valtech.avs.api.service.AvsException;
import de.valtech.avs.api.service.scanner.AvsScannerEnine;
import de.valtech.avs.api.service.scanner.ScanResult;

/**
 * AVS scan engine using ClamAV.
 * 
 * @author Roland Gruber
 */
@Component(service = AvsScannerEnine.class, configurationPolicy = ConfigurationPolicy.REQUIRE, immediate = true)
@Designate(ocd = ClamScannerConfig.class)
public class ClamScannerEngine implements AvsScannerEnine {

    private static final Logger LOG = LoggerFactory.getLogger(ClamScannerEngine.class);

    private ClamScannerConfig config;

    /**
     * Setup service
     * 
     * @param config configuration
     */
    @Activate
    public void activate(ClamScannerConfig config) {
        this.config = config;
    }

    @Override
    public ScanResult scan(InputStream content, String fileName) throws AvsException {
        try {
            File tempFile = createTemporaryFile(content);
            Runtime runtime = Runtime.getRuntime();
            String command = config.command() + " " + tempFile.getPath();
            Process process = runtime.exec(command);
            InputStream in = process.getInputStream();
            InputStream err = process.getErrorStream();
            int returnCode = process.waitFor();
            String output = IOUtils.toString(in, Charset.forName(StandardCharsets.UTF_8.name()));
            String error = IOUtils.toString(err, Charset.forName(StandardCharsets.UTF_8.name()));
            in.close();
            err.close();
            if (StringUtils.isBlank(fileName)) {
                output = output.replace(tempFile.getPath(), "SCANNED_FILE");
            } else {
                output = output.replace(tempFile.getPath(), fileName);
            }
            Files.delete(Paths.get(tempFile.getPath()));
            if ((returnCode == 0) && StringUtils.isBlank(error)) {
                return new ScanResult(output, true);
            }
            return new ScanResult(output + "\n" + error, false);
        } catch (IOException | InterruptedException e) {
            LOG.error("Error during scanning", e);
            Thread.currentThread().interrupt();
            throw new AvsException("Error during scanning", e);
        }
    }

    /**
     * Creates a temporary file with the given content.
     * 
     * @param content content
     * @return file handle
     * @throws IOException error creating file
     */
    private File createTemporaryFile(InputStream content) throws IOException {
        File file = File.createTempFile("valtech-avs", ".tmp");
        Files.copy(content, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        content.close();
        return file;
    }

}
