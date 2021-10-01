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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.security.user.UserProperties;

import de.valtech.avs.api.service.AvsException;
import de.valtech.avs.api.service.AvsService;
import de.valtech.avs.api.service.scanner.ScanResult;
import de.valtech.avs.core.history.HistoryService;
import de.valtech.avs.core.mail.AvsNotificationMailer;
import de.valtech.avs.core.serviceuser.ServiceResourceResolverService;

/**
 * Filter for POST requests. Checks included files.
 * 
 * @author Roland Gruber
 */
@Component(service = Filter.class, property = {"sling.filter.scope=REQUEST", Constants.SERVICE_RANKING + ":Integer=50000"})
@Designate(ocd = AvsPostFilterConfig.class)
public class AvsPostFilter implements Filter {

    private static final String REQUEST_PARTS = "request-parts-iterator";

    private static final Logger LOG = LoggerFactory.getLogger(AvsPostFilter.class);

    @Reference
    private AvsService avsService;

    @Reference
    private HistoryService historyService;

    @Reference
    private ServiceResourceResolverService serviceResolverService;

    @Reference
    private AvsNotificationMailer mailer;

    private List<Pattern> includePatterns = new ArrayList<>();
    private List<Pattern> excludePatterns = new ArrayList<>();

    /**
     * Setup service
     * 
     * @param config configuration
     */
    @Activate
    public void activate(AvsPostFilterConfig config) {
        excludePatterns = new ArrayList<>();
        if (config.excludePatterns() != null) {
            for (String patternString : config.excludePatterns()) {
                excludePatterns.add(Pattern.compile(patternString));
            }
        }
        includePatterns = new ArrayList<>();
        if (config.includePatterns() != null) {
            for (String patternString : config.includePatterns()) {
                includePatterns.add(Pattern.compile(patternString));
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        String contentType = slingRequest.getContentType();
        if (!"POST".equals(slingRequest.getMethod()) || !isMultipartRequest(contentType) || isUrlToIgnore(slingRequest)) {
            chain.doFilter(request, response);
            return;
        }
        Iterator<Part> parts = (Iterator<Part>) request.getAttribute(REQUEST_PARTS);
        if (parts == null) {
            chain.doFilter(request, response);
            return;
        }
        List<File> parameterFiles = new ArrayList<>();
        List<Part> newParts = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        while (parts.hasNext()) {
            Part part = parts.next();
            String partContentType = part.getContentType();
            if (StringUtils.isEmpty(partContentType)) {
                String partContent = IOUtils.toString(part.getInputStream(), StandardCharsets.UTF_8.name());
                newParts.add(new PartWrapper(part, partContent.getBytes(StandardCharsets.UTF_8.name())));
                continue;
            }
            if (StringUtils.isNotEmpty(part.getSubmittedFileName())) {
                fileNames.add(getFilePath(slingRequest, part));
            }
            InputStream partStream = part.getInputStream();
            File file = File.createTempFile("valtech-avs", ".tmp");
            Files.copy(partStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            partStream.close();
            parameterFiles.add(file);
            PartWrapper wrapper = new PartWrapper(part, file);
            newParts.add(wrapper);
        }
        request.setAttribute(REQUEST_PARTS, newParts.iterator());
        List<InputStream> streams = new ArrayList<>();
        for (File file : parameterFiles) {
            streams.add(new FileInputStream(file));
        }
        if (streams.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }
        SequenceInputStream combinedStream = new SequenceInputStream(Collections.enumeration(streams));
        try {
            Session session = slingRequest.getResourceResolver().adaptTo(Session.class);
            String userId = (session != null) ? session.getUserID() : StringUtils.EMPTY;
            ScanResult result = avsService.scan(combinedStream, userId, String.join(", ", fileNames));
            if (!result.isClean()) {
                for (File file : parameterFiles) {
                    if (!file.delete()) {
                        LOG.warn("Unable to remove temp file {}", file.getPath());
                    }
                }
                sendEmail(slingRequest, result, fileNames);
                throw new ServletException("Uploaded file contains a virus");
            }
        } catch (AvsException e) {
            LOG.error("Virus scan failed", e);
        }
        combinedStream.close();
        chain.doFilter(request, response);
    }

    /**
     * Returns path and name of uploaded file.
     * 
     * @param slingRequest request
     * @param part         file part
     * @return path
     */
    private String getFilePath(SlingHttpServletRequest slingRequest, Part part) {
        String fileName = part.getSubmittedFileName();
        String path = (StringUtils.isNotBlank(slingRequest.getRequestURI())) ? slingRequest.getRequestURI() + "/" : "";
        path = path.replace(".createasset.html", "");
        return path + fileName;
    }

    /**
     * Checks if the request is multipart form-data.
     * 
     * @param contentType content type
     * @return is multipart form-data
     */
    private boolean isMultipartRequest(String contentType) {
        if (StringUtils.isEmpty(contentType)) {
            return false;
        }
        return contentType.contains(FileUploadBase.MULTIPART_FORM_DATA);
    }

    /**
     * Checks if the request URL should be ignored from checking.
     * 
     * @param request request
     * @return ignore
     */
    protected boolean isUrlToIgnore(SlingHttpServletRequest request) {
        String url = request.getRequestURI();
        for (Pattern pattern : excludePatterns) {
            if (pattern.matcher(url).matches()) {
                return true;
            }
        }
        if (includePatterns.isEmpty()) {
            return false;
        }
        for (Pattern pattern : includePatterns) {
            if (pattern.matcher(url).matches()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sends out an email to notify the author about the virus upload.
     * 
     * @param slingRequest request
     * @param result       scan result
     * @param fileNames    file names
     */
    private void sendEmail(SlingHttpServletRequest slingRequest, ScanResult result, List<String> fileNames) {
        UserProperties properties = slingRequest.adaptTo(UserProperties.class);
        List<String> emails = new ArrayList<>();
        try {
            if ((properties != null) && StringUtils.isNotEmpty(properties.getProperty(UserProperties.EMAIL))) {
                emails.add(properties.getProperty(UserProperties.EMAIL));
            }
        } catch (RepositoryException e) {
            LOG.error("Cannot read email of user", e);
        }
        if (emails.isEmpty()) {
            return;
        }
        mailer.sendEmail(emails, String.join(", ", fileNames), result);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // no action
    }

    @Override
    public void destroy() {
        // no action
    }

}
