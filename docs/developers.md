# AEM Server Setup

By default AEM is expected to listen on localhost on port 4502. This setting can be overridden by adding parameters:
* -Daem.port=4502
* -Daem.host=localhost
* -Daem.publish.port=4503
* -Daem.publish.host=localhost

You need AEM 6.4 with service pack 2 or AEM 6.5.

# Build and Deploy

To build and deploy run this in the base (aem-virus-scan) or ui.apps/examples folder:

```bash
mvn clean install -PautoInstallPackage
```

In case you want to deploy core only you can use this command in core folder:

```bash
mvn clean install -PautoInstallBundle
```

To build and deploy on publish instance run this in the base (aem-virus-scan) or ui.apps/examples folder:

```bash
mvn clean install -PautoInstallPackagePublish
```


# Code Formatting

Please use our standard code formatters for [Eclipse](formatter/eclipse-avs.xml)
and [IntelliJ](formatter/intellij-avs.xml).
