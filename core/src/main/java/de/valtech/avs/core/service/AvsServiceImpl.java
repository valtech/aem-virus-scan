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
package de.valtech.avs.core.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import de.valtech.avs.api.service.AvsException;
import de.valtech.avs.api.service.AvsService;
import de.valtech.avs.api.service.scanner.AvsScannerEnine;
import de.valtech.avs.api.service.scanner.ScanResult;
import de.valtech.avs.core.history.HistoryService;
import de.valtech.avs.core.serviceuser.ServiceResourceResolverService;

/**
 * AVS scan service.
 * 
 * @author Roland Gruber
 */
@Component(service = AvsService.class)
public class AvsServiceImpl implements AvsService {

    @Reference
    private HistoryService historyService;

    @Reference
    private ServiceResourceResolverService serviceResourceResolverService;

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, bind = "bindEngine",
            unbind = "unbindEngine")
    private List<AvsScannerEnine> engines = new ArrayList<>();

    /**
     * Adds a scanner engine.
     * 
     * @param engine engine
     */
    protected synchronized void bindEngine(AvsScannerEnine engine) {
        engines.add(engine);
    }

    /**
     * Removes a scanner engine.
     * 
     * @param engine engine
     */
    protected synchronized void unbindEngine(AvsScannerEnine engine) {
        engines.remove(engine);
    }

    @Override
    public ScanResult scan(InputStream content, String userId) throws AvsException {
        return scan(content, userId, StringUtils.EMPTY);
    }

    @Override
    public ScanResult scan(InputStream content, String userId, String path) throws AvsException {
        if (engines.isEmpty()) {
            throw new AvsException("No scanning engines available");
        }
        if (content == null) {
            // skip empty content
            return new ScanResult(null, true);
        }
        ScanResult result = null;
        try (ResourceResolver resolver = serviceResourceResolverService.getServiceResourceResolver()) {
            for (AvsScannerEnine engine : engines) {
                result = engine.scan(content, path);
                result.setPath(path);
                result.setUserId(userId);
                if (!result.isClean()) {
                    historyService.createHistoryEntry(resolver, result);
                    break;
                }
            }
        } catch (LoginException e) {
            throw new AvsException("Unable to access service resolver", e);
        }
        return result;
    }

    @Override
    public boolean hasActiveScanEngines() {
        return !engines.isEmpty();
    }

}
