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
package de.valtech.avs.core.model.history;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import de.valtech.avs.api.history.HistoryEntry;

/**
 * Model class for a single history item.
 *
 * @author Roland Gruber
 */
@Model(adaptables = Resource.class)
public class HistoryDataItem {

    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @SlingObject
    private Resource resource;

    protected HistoryEntry history = null;

    @PostConstruct
    public void setup() {
        history = resource.getValueMap().get(HistoryDataSource.ATTR_HISTORY, HistoryEntry.class);
    }

    /**
     * Returns the date of the scan.
     *
     * @return date
     */
    public String getDate() {
        return format.format(history.getTime());
    }

    /**
     * Returns the output of the scan.
     *
     * @return output
     */
    public String getOutput() {
        return history.getOutput();
    }

    /**
     * Returns the path of the scan.
     *
     * @return path
     */
    public String getPath() {
        return history.getPath();
    }

    /**
     * Returns the user id that caused the scan.
     *
     * @return path
     */
    public String getUserId() {
        return history.getUserId();
    }

}
