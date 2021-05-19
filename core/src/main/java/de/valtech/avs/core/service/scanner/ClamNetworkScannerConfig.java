/*
 * Copyright 2021 Valtech GmbH
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

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Clam network scanner configuration.
 * 
 * @author Roland Gruber
 */
@ObjectClassDefinition(name = "AVS ClamAV network scanning configuration")
@ProviderType
public @interface ClamNetworkScannerConfig {

    /**
     * Returns the scan host.
     * 
     * @return host
     */
    @AttributeDefinition(name = "Host", description = "Host of remote Clam scanning server.", type = AttributeType.STRING)
    String host();

    /**
     * Returns the scan port.
     * 
     * @return port
     */
    @AttributeDefinition(name = "Port", description = "Port of remote Clam scanning server (e.g. 3310).",
            type = AttributeType.INTEGER)
    int port();

    /**
     * Returns the connection timeout.
     * 
     * @return port
     */
    @AttributeDefinition(name = "Connection timeout", description = "Connection timeout in seconds.",
            type = AttributeType.INTEGER)
    int timeout();

    /**
     * Returns the chunk size.
     * 
     * @return port
     */
    @AttributeDefinition(name = "Chunk size", description = "Chunk size that is acceptable for Clam.",
            type = AttributeType.INTEGER)
    int chunkSize();

}
