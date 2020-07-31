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

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * POST filter configuration.
 * 
 * @author Roland Gruber
 */
@ObjectClassDefinition(name = "AVS POST filter configuration")
@ProviderType
public @interface AvsPostFilterConfig {

    /**
     * Returns the URL include pattern list.
     * 
     * @return list
     */
    @AttributeDefinition(name = "URL include patterns", description = "List of regular expressions to match the URLs to check",
            type = AttributeType.STRING)
    String[] includePatterns();

    /**
     * Returns the URL exclude pattern list.
     * 
     * @return list
     */
    @AttributeDefinition(name = "URL exclude patterns",
            description = "List of regular expressions to match the URLs to ignore. Has higher priority than include patterns.",
            type = AttributeType.STRING)
    String[] excludePatterns();

}
