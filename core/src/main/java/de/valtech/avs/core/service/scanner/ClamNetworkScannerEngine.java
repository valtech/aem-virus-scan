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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
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
@Designate(ocd = ClamNetworkScannerConfig.class)
public class ClamNetworkScannerEngine implements AvsScannerEnine {

    private static final String INSTREAM_SIZE_LIMIT_EXCEEDED_ERROR = "INSTREAM size limit exceeded. ERROR";

    private static final int CHUNK_SIZE_FIELD_LENGTH = 4;

    private static final Logger LOG = LoggerFactory.getLogger(ClamScannerEngine.class);

    /** default to 1MB */
    private static final int DEFAULT_CHUNK_SIZE = 1024 * 1024;

    private static final int DEFAULT_TIMEOUT = 5;

    private ClamNetworkScannerConfig config;

    /**
     * Setup service
     * 
     * @param config configuration
     */
    @Activate
    public void activate(ClamNetworkScannerConfig config) {
        this.config = config;
    }

    @Override
    public ScanResult scan(InputStream content, String fileName) throws AvsException {
        try (Socket socket = connect();
                OutputStream out = new BufferedOutputStream(socket.getOutputStream());
                InputStream in = socket.getInputStream()) {
            socket.setSoTimeout(getConnectionTimeout() * 1000);
            // send scan command
            out.write("nINSTREAM\n".getBytes(StandardCharsets.US_ASCII));
            out.flush();
            // send data
            byte[] data = new byte[getChunkSize()];
            int contentRead = content.read(data);
            while (contentRead > 0) {
                byte[] lengthIndicator = ByteBuffer.allocate(CHUNK_SIZE_FIELD_LENGTH).putInt(contentRead).array();
                out.write(lengthIndicator);
                out.write(data, 0, contentRead);
                // check if there was any reply yet that would indicate an exceeded size limit
                if (in.available() > 0) {
                    String scanReply = IOUtils.toString(in, StandardCharsets.US_ASCII).trim();
                    if (scanReply.contains(INSTREAM_SIZE_LIMIT_EXCEEDED_ERROR)) {
                        throw new AvsException("File too large: " + scanReply);
                    }
                    throw new AvsException("Clam responded before all data was sent: " + scanReply);
                }
                contentRead = content.read(data);
            }
            // send empty chunk to end the scan
            out.write(new byte[] {0, 0, 0, 0});
            out.flush();
            // get reply
            String scanReply = IOUtils.toString(in, StandardCharsets.US_ASCII).trim();
            if (scanReply.contains("stream: OK")) {
                return new ScanResult(scanReply, true);
            } else if (scanReply.contains(INSTREAM_SIZE_LIMIT_EXCEEDED_ERROR)) {
                throw new AvsException("File too large: " + scanReply);
            } else if (scanReply.matches("stream: .+ FOUND")) {
                return new ScanResult(scanReply, false);
            }
            throw new AvsException("Unknown reply from clam: " + scanReply);
        } catch (IOException e) {
            LOG.error("Error during scanning", e);
            throw new AvsException("Error during scanning", e);
        }
    }

    /**
     * Connects to the remote server.
     * 
     * @return socket
     * @throws AvsException error during connect
     */
    private Socket connect() throws AvsException {
        try {
            return new Socket(config.host(), config.port());
        } catch (IOException e) {
            LOG.error("Unable to connect to network scanner", e);
            throw new AvsException("Unable to connect to clam network scanner", e);
        }
    }

    /**
     * Returns the chunk size.
     * 
     * @return size
     */
    private int getChunkSize() {
        if (config.chunkSize() > 0) {
            return config.chunkSize();
        }
        return DEFAULT_CHUNK_SIZE;
    }

    /**
     * Returns the connection timeout.
     * 
     * @return timeout in seconds
     */
    private int getConnectionTimeout() {
        if (config.timeout() > 0) {
            return config.timeout();
        }
        return DEFAULT_TIMEOUT;
    }

}
