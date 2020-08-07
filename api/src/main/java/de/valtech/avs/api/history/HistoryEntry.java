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
package de.valtech.avs.api.history;

import java.util.Date;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Represents an entry in the AVS history.
 * 
 * @author Roland Gruber
 */
@ProviderType
public interface HistoryEntry {

    /**
     * Returns the scan time.
     * 
     * @return time
     */
    Date getTime();

    /**
     * Returns the scan output.
     * 
     * @return output
     */
    String getOutput();

    /**
     * Returns if the file was clean.
     * 
     * @return clean
     */
    boolean isClean();

    /**
     * Returns the scanned node path if available.
     * 
     * @return path
     */
    String getPath();

    /**
     * Path in repository.
     * 
     * @return path
     */
    String getRepositoryPath();

    /**
     * Returns the user name.
     * 
     * @return user name
     */
    String getUserId();

}
