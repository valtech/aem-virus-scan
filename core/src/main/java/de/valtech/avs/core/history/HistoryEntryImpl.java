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
package de.valtech.avs.core.history;

import java.util.Date;

import de.valtech.avs.api.history.HistoryEntry;

/**
 * Represents an entry in the AVS history.
 * 
 * @author Roland Gruber
 */
public class HistoryEntryImpl implements HistoryEntry {

    private Date time;

    private String output;

    private boolean clean;

    private String path;

    private String repositoryPath;

    private String userId;

    /**
     * Constructor
     * 
     * @param time           time
     * @param output         output text
     * @param clean          is clean
     * @param path           path that was scanned
     * @param repositoryPath path in crx
     * @param userId         user id
     */
    public HistoryEntryImpl(Date time, String output, boolean clean, String path, String repositoryPath, String userId) {
        super();
        this.time = time;
        this.output = output;
        this.clean = clean;
        this.path = path;
        this.repositoryPath = repositoryPath;
        this.userId = userId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.valtech.avs.api.history.HistoryEntry#getTime()
     */
    @Override
    public Date getTime() {
        return time;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.valtech.avs.api.history.HistoryEntry#getOutput()
     */
    @Override
    public String getOutput() {
        return output;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.valtech.avs.api.history.HistoryEntry#isClean()
     */
    @Override
    public boolean isClean() {
        return clean;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.valtech.avs.api.history.HistoryEntry#getPath()
     */
    @Override
    public String getPath() {
        return path;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.valtech.avs.api.history.HistoryEntry#getRepositoryPath()
     */
    @Override
    public String getRepositoryPath() {
        return repositoryPath;
    }

    @Override
    public String getUserId() {
        return userId;
    }

}
