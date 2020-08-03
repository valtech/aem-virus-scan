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

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Configuration for mailer.
 * 
 * @author Roland Gruber
 */
@ObjectClassDefinition(name = "AVS Notification Mailer Configuration")
@ProviderType
public @interface AvsNotificationMailerConfig {

    /**
     * Returns the mail subject.
     * 
     * @return subject
     */
    @AttributeDefinition(name = "Email subject", description = "Subject for all virus notification emails",
            type = AttributeType.STRING)
    String subject() default "A virus was found in your file upload";

    /**
     * Returns the mail body.
     * 
     * @return body
     */
    @AttributeDefinition(name = "Email body",
            description = "Body for all virus notification emails. Wildcards are ${FILE_NAME} for uploaded file name and ${SCAN_OUTPUT} for scan details.",
            type = AttributeType.STRING)
    String body() default "Dear Sir or Madam,<br><br>a virus was detected in your AEM file upload.<br><br>File name: ${FILE_NAME}<br>Scan report: ${SCAN_OUTPUT}";

    /**
     * Returns if email is HTML or TEXT.
     * 
     * @return is HTML
     */
    @AttributeDefinition(name = "HTML format", description = "Specifies if email is sent as HTML or plain text.",
            type = AttributeType.BOOLEAN)
    boolean isHtml() default true;

    /**
     * Returns the FROM address.
     * 
     * @return FROM
     */
    @AttributeDefinition(name = "Email FROM address", description = "FROM address for notification emails.",
            type = AttributeType.STRING)
    String from() default "do-not-reply@example.com";

    /**
     * Returns list of additional email recipients when an alert is sent.
     * 
     * @return list
     */
    @AttributeDefinition(name = "Additional email recipients", description = "List of email addresses.",
            type = AttributeType.STRING)
    String[] additionalRecipients();

}
