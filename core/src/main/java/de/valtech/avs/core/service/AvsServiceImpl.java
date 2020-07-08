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

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import de.valtech.avs.api.service.AvsException;
import de.valtech.avs.api.service.AvsService;
import de.valtech.avs.api.service.scanner.AvsScannerEnine;
import de.valtech.avs.api.service.scanner.ScanResult;

/**
 * AVS scan service.
 * 
 * @author Roland Gruber
 */
@Component(service = AvsService.class)
public class AvsServiceImpl implements AvsService {

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
    public ScanResult scanContent(String content) throws AvsException {
        return new ScanResult("out", true);
    }

}
