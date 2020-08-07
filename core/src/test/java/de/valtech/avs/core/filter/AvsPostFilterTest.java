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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Tests AvsPostFilter
 * 
 * @author Roland Gruber
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AvsPostFilterTest {

    @Mock
    private SlingHttpServletRequest request;

    @Mock
    private AvsPostFilterConfig config;

    @InjectMocks
    private AvsPostFilter filter;

    @Before
    public void setup() {
        when(request.getRequestURI()).thenReturn("/some/sample/request.html");
    }

    @Test
    public void isUrlToIgnore() {
        filter.activate(config);

        assertFalse(filter.isUrlToIgnore(request));
    }

    @Test
    public void isUrlToIgnore_excludeFilter_match() {
        when(config.excludePatterns()).thenReturn(new String[] {"/some/.*"});
        filter.activate(config);

        assertTrue(filter.isUrlToIgnore(request));
    }

    @Test
    public void isUrlToIgnore_excludeFilter_nomatch() {
        when(config.excludePatterns()).thenReturn(new String[] {"/bla"});
        filter.activate(config);

        assertFalse(filter.isUrlToIgnore(request));
    }

    @Test
    public void isUrlToIgnore_includeFilter_match() {
        when(config.includePatterns()).thenReturn(new String[] {"/some/.*"});
        filter.activate(config);

        assertFalse(filter.isUrlToIgnore(request));
    }

    @Test
    public void isUrlToIgnore_includeFilter_nomatch() {
        when(config.includePatterns()).thenReturn(new String[] {"/bla"});
        filter.activate(config);

        assertTrue(filter.isUrlToIgnore(request));
    }

}
