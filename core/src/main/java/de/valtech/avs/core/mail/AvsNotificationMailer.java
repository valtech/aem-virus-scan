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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.mailer.MailService;

import de.valtech.avs.api.service.scanner.ScanResult;

/**
 * Sends out an email notification when a virus was found.
 * 
 * @author Roland Gruber
 */
@Component(service = AvsNotificationMailer.class)
@Designate(ocd = AvsNotificationMailerConfig.class)
public class AvsNotificationMailer {

    private static final Logger LOG = LoggerFactory.getLogger(AvsNotificationMailer.class);

    @Reference
    private MailService mailService;

    private AvsNotificationMailerConfig config;

    /**
     * Activation
     * 
     * @param config config
     */
    @Activate
    public void activate(AvsNotificationMailerConfig config) {
        this.config = config;
    }

    /**
     * Sends out the email to the recipients.
     * 
     * @param emails   email recipients
     * @param fileName file name that was scanned
     * @param result   scan result
     */
    public void sendEmail(List<String> emails, String fileName, ScanResult result) {
        VelocityEngine ve = new VelocityEngine();
        ve.init();
        VelocityContext context = new VelocityContext();
        StringWriter writer = new StringWriter();
        if (fileName != null) {
            context.put("FILE_NAME", fileName);
        }
        context.put("SCAN_OUTPUT", result.getOutput());
        ve.evaluate(context, writer, "AvsNotificationMailer", getBodyText());
        String body = writer.toString();
        String subject = getSubject();
        List<String> emailTO = new ArrayList<>(emails);
        if (config.additionalRecipients() != null) {
            emailTO.addAll(Arrays.asList(config.additionalRecipients()));
        }
        try {
            sendMail(emailTO, subject, body);
        } catch (MessagingException | EmailException e) {
            LOG.error("Unable to send virus notification", e);
        }
    }

    /**
     * Returns the raw body text incl. wildcards.
     * 
     * @return body
     */
    private String getBodyText() {
        return config.body();
    }

    /**
     * Returns the email subject.
     * 
     * @return subject
     */
    private String getSubject() {
        return config.subject();
    }

    /**
     * Sends out the email.
     * 
     * @param emails  email addresses
     * @param subject subject
     * @param body    body text
     * @throws MessagingException error sending mail
     * @throws EmailException     error configuring email
     */
    private void sendMail(List<String> emails, String subject, String body) throws MessagingException, EmailException {
        MultiPartEmail email = new MultiPartEmail();
        MimeBodyPart messagePart = new MimeBodyPart();
        if (config.isHtml()) {
            messagePart.setContent(body, "text/html; charset=utf-8");
        } else {
            messagePart.setContent(body, "text/plain; charset=utf-8");
        }
        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(messagePart);
        email.setSubject(subject);
        for (String toAddress : emails) {
            email.addTo(toAddress);
        }
        email.setFrom(config.from());
        email.setContent(multipart);
        mailService.send(email);
    }

}
