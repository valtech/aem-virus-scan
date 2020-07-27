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
package de.valtech.avs.core.maintenance;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobExecutionContext;
import org.apache.sling.event.jobs.consumer.JobExecutionResult;
import org.apache.sling.event.jobs.consumer.JobExecutor;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import com.adobe.granite.maintenance.MaintenanceConstants;

import de.valtech.avs.core.history.HistoryService;
import de.valtech.avs.core.serviceuser.ServiceResourceResolverService;

/**
 * Purges old entries from the history.
 *
 * @author Roland Gruber
 */
@Component(property = {MaintenanceConstants.PROPERTY_TASK_NAME + "=AVSPurgeHistory",
        MaintenanceConstants.PROPERTY_TASK_TITLE + "=AVS Purge History",
        JobExecutor.PROPERTY_TOPICS + "=" + MaintenanceConstants.TASK_TOPIC_PREFIX + "AVSPurgeHistory",})
@Designate(ocd = PurgeHistoryConfiguration.class)
public class PurgeHistoryTask implements JobExecutor {

    private PurgeHistoryConfiguration config;

    @Reference
    private ServiceResourceResolverService resolverService;

    @Reference
    private HistoryService historyService;

    /**
     * Activates the service.
     *
     * @param config configuration
     */
    @Activate
    public void activate(PurgeHistoryConfiguration config) {
        this.config = config;
    }

    @Override
    public JobExecutionResult process(Job job, JobExecutionContext context) {
        try (ResourceResolver resolver = resolverService.getServiceResourceResolver()) {
            historyService.purgeHistory(resolver, config.daysToKeep());
            resolver.commit();
            return context.result().message("Purged AVS history entries").succeeded();
        } catch (LoginException e) {
            return context.result().message("Service resolver failed with " + e.getMessage()).failed();
        } catch (PersistenceException e) {
            return context.result().message("Purge failed with " + e.getMessage()).failed();
        }
    }

}
