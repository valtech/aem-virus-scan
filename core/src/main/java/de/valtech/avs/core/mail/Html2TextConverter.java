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
package de.valtech.avs.core.mail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts HTML to plain text.
 * 
 * @author Roland Gruber
 */
public class Html2TextConverter extends HTMLEditorKit.ParserCallback {

    private static final Logger LOG = LoggerFactory.getLogger(Html2TextConverter.class);

    private StringBuilder text = new StringBuilder();

    /**
     * Constructor
     * 
     * @param input input text
     */
    public Html2TextConverter(String input) {
        if (input == null) {
            return;
        }
        ParserDelegator delegator = new ParserDelegator();
        Reader reader = new StringReader(input);
        try {
            delegator.parse(reader, this, true);
        } catch (IOException e) {
            LOG.error("Unable to convert data", e);
        }
    }

    @Override
    public void handleText(char[] data, int pos) {
        text.append(data);
    }

    /**
     * Returns the converted value.
     * 
     * @return text
     */
    public String getText() {
        return text.toString();
    }

}
