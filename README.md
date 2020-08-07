# AEM Virus Scan

AEM Virus Scan adds malware scanning to your AEM instance. It supports Clam AV out-of-the-box and can be extended to support more scan engines.

Features:

* Scanning of asset uploads
* API to run scan from custom code
* API to add custom scan engines
* Provided scan engines:
    * Clam AV
* Health checks

<a name="requirements"></a>

# Requirements

AVS requires Java 8 and AEM 6.4 or above.

| AEM Version   | AVS       |
| ------------- | --------- |
| 6.4           | 1.x       |
| 6.5           | 1.x       |

<a name="installation"></a>

# Installation

You can download the package from [Maven Central](http://repo1.maven.org/maven2/de/valtech/avs/avs.ui.apps/) or our [releases section](https://github.com/valtech/aem-virus-scan/releases). The avs.ui.apps package will install the AVS software.

```xml
        <dependency>
            <groupId>de.valtech.avs</groupId>
            <artifactId>avs.ui.apps</artifactId>
            <version>LATEST</version>
            <type>zip</type>
        </dependency>
```


## Uninstallation

The application can be removed by deleting the following paths:
* /apps/valtech/avs
* /var/avs

Afterwards, you can delete the "avs.ui.apps" package in package manager.

# Scan File Uploads

You can scan files in any POST request. E.g. this way you can scan files that are uploaded to DAM.

You should provide a [configuration](#conf_filter) of the URL patterns for the filter.

# Tools

AVS adds tools to the AEM menu.

<img src="docs/images/tools.png">


## Perform a Manual Scan

You can upload a file using AVS scan tool from the menu. This allows you to check if there is an issue with the file.

<img src="docs/images/manualScan.png">


## History

This shows the history of the last found infections. Scans that did not lead to an alert are not listed.

<img src="docs/images/history.png">

# Configuration

You can see an example for each configuration in [example package](/examples/src/main/content/jcr_root/apps/valtech/avs-examples/config).

<a name="conf_filter"></a>

## AVS Post Filter

This filter can scan e.g. asset uploads for viruses.

* includePatterns: List of regular expressions to match the URLs to check. If empty, all non-excluded URLs are scanned.
* excludePatterns: List of regular expressions to match the URLs to ignore. Has higher priority than include patterns.

PID: de.valtech.avs.core.filter.AvsPostFilter

File name: de.valtech.avs.core.filter.AvsPostFilter.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" jcr:primaryType="sling:OsgiConfig"
   includePatterns="[/content/dam/.*]"
   excludePatterns="[]"
/>
```

<a name="conf_mail"></a>

## AVS Notification Mailer

This filter can scan e.g. asset uploads for viruses.

* subject: mail subject
* body: body for all virus notification emails. Wildcards are ${FILE_NAME} for uploaded file name and ${SCAN_OUTPUT} for scan details.
* isHtml: specifies if mail format is HTML or TEXT
* from: FROM address for notification emails
* additionalRecipients: additional email recipients

PID: de.valtech.avs.core.mail.AvsNotificationMailer

File name: de.valtech.avs.core.mail.AvsNotificationMailer.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" jcr:primaryType="sling:OsgiConfig"
   subject="A virus was found"
   body="Dear Sir or Madam,&lt;br&gt;&lt;br&gt;a virus was detected in your file upload.&lt;br&gt;&lt;br&gt;File name: ${FILE_NAME}&lt;br&gt;Scan report: ${SCAN_OUTPUT}"
   isHtml="{Boolean}true"
   from="no-reply@example.com"
   additionalRecipients="[root@localhost]"
/>
```

<a name="conf_clam"></a>

## Clam Scanning Engine

You need to provide a configuration for Clam AV in case you want to use this scan engine. If no configuration is provided then it will not be activated.

* command: command to scan a single file. The file name will be added at the end of the command.

PID: de.valtech.avs.core.service.scanner.ClamScannerEngine

File name: de.valtech.avs.core.service.scanner.ClamScannerEngine.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" jcr:primaryType="sling:OsgiConfig"
   command="/usr/local/bin/clamdscan --infected --no-summary"
/>
```

<a name="conf_history"></a>

## Purge History

This configures how long to keep the scan history. Older entries will be purged with maintenance task.

PID: de.valtech.avs.core.maintenance.PurgeHistoryTask

File name: de.valtech.avs.core.maintenance.PurgeHistoryTask.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" jcr:primaryType="sling:OsgiConfig"
   daysToKeep="30"
/>
```

# Health Checks

Health checks show you the status of AVS. This includes its service user, scan engines and a test scan.
You can access them on the [status page](http://localhost:4502/libs/granite/operations/content/healthreports/healthreportlist.html/system/sling/monitoring/mbeans/org/apache/sling/healthcheck/HealthCheck/avsHealthCheckmBean).

<img src="docs/images/healthcheck.png">

<a name="api"></a>

# API Documentation

https://valtech.github.io/aem-virus-scan/

<a name="license"></a>

# License

The AVS tool is licensed under the [MIT LICENSE](LICENSE).

<a name="changelog"></a>

# Changelog

Please see our [history file](HISTORY).

<a name="developers"></a>

# Developers

See our [developer zone](docs/developers.md).
