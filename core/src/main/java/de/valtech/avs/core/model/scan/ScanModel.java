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
package de.valtech.avs.core.model.scan;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import de.valtech.avs.api.service.AvsException;
import de.valtech.avs.api.service.AvsService;
import de.valtech.avs.api.service.scanner.ScanResult;

/**
 * Sling model for scan tool.
 * 
 * @author Roland Gruber
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class ScanModel {

    private static final String FILE_PART = "scanfile";

    @SlingObject
    private SlingHttpServletRequest request;

    @OSGiService
    private AvsService avsService;

    private boolean scanDone = false;

    private String resultOutput;

    private boolean scanFailed = false;

    private boolean clean = true;

    @PostConstruct
    protected void init() {
        try {
            Part filePart = request.getPart(FILE_PART);
            if (filePart != null) {
                InputStream inputStream = filePart.getInputStream();
                if (inputStream != null) {
                    scanDone = true;
                    String userId = request.getResourceResolver().adaptTo(Session.class).getUserID();
                    ScanResult result = avsService.scan(inputStream, userId);
                    clean = result.isClean();
                    resultOutput = result.getOutput();
                }
            }
        } catch (IOException | ServletException | AvsException e) {
            scanFailed = true;
            resultOutput = e.getMessage();
        }
    }

    /**
     * Returns the scan result text.
     * 
     * @return result
     */
    public String getResult() {
        return resultOutput;
    }

    /**
     * Returns if a scan was performed.
     * 
     * @return scan done
     */
    public boolean isScanDone() {
        return scanDone;
    }

    /**
     * Returns if the scan failed.
     * 
     * @return failed
     */
    public boolean isScanFailed() {
        return scanFailed;
    }

    /**
     * Returns if the file was clean.
     * 
     * @return clean
     */
    public boolean isClean() {
        return clean;
    }

}
