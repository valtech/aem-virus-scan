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
package de.valtech.avs.core.jmx;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.management.NotCompliantMBeanException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;

import de.valtech.avs.api.service.AvsException;
import de.valtech.avs.api.service.AvsService;

/**
 * JMX service to check virus scan.
 *
 * @author Roland Gruber
 */
@Component(service = AntiVirusScannerMBean.class, immediate = true,
        property = {"jmx.objectname=de.valtech:type=AVS", "pattern=/.*"})
public class AntiVirusScannerMBeanImpl extends AnnotatedStandardMBean implements AntiVirusScannerMBean {

    @Reference
    private AvsService scanner;

    /**
     * Constructor
     * 
     * @throws NotCompliantMBeanException exception
     */
    public AntiVirusScannerMBeanImpl() throws NotCompliantMBeanException {
        super(AntiVirusScannerMBean.class);
    }

    @Override
    public String scanContent(String content) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8.name()));
            return scanner.scan(stream, "JMX").toString();
        } catch (AvsException | UnsupportedEncodingException e) {
            return "Error: " + e.getMessage();
        }
    }

}
