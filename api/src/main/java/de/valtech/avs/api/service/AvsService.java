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
package de.valtech.avs.api.service;

import java.io.InputStream;

import org.osgi.annotation.versioning.ProviderType;

import de.valtech.avs.api.service.scanner.ScanResult;

/**
 * Scanner service interface. Use this to scan for viruses.
 * 
 * @author Roland Gruber
 */
@ProviderType
public interface AvsService {

    /**
     * Scans the given content for viruses.
     * 
     * @param content content
     * @param userId  user name
     * @return scan result
     * @throws AvsException error during scan
     */
    public ScanResult scan(InputStream content, String userId) throws AvsException;

    /**
     * Scans the given content for viruses.
     * 
     * @param content content
     * @param userId  user name
     * @param path    node path to add in history
     * @return scan result
     * @throws AvsException error during scan
     */
    public ScanResult scan(InputStream content, String userId, String path) throws AvsException;

    /**
     * Returns if there is at least one active scan engine.
     * 
     * @return scan engines available
     */
    public boolean hasActiveScanEngines();

}
