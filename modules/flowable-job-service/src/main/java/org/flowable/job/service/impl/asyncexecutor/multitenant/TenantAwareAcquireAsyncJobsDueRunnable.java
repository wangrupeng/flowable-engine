/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.job.service.impl.asyncexecutor.multitenant;

import org.flowable.common.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.job.service.impl.asyncexecutor.AcquireAsyncJobsDueLifecycleListener;
import org.flowable.job.service.impl.asyncexecutor.AcquireAsyncJobsDueRunnable;
import org.flowable.job.service.impl.asyncexecutor.AcquireJobsRunnableConfiguration;
import org.flowable.job.service.impl.asyncexecutor.AsyncExecutor;

/**
 * Extends the default {@link AcquireAsyncJobsDueRunnable} by setting the 'tenant' context before executing.
 * 
 * @author Joram Barrez
 */
public class TenantAwareAcquireAsyncJobsDueRunnable extends AcquireAsyncJobsDueRunnable {

    protected TenantInfoHolder tenantInfoHolder;
    protected String tenantId;

    public TenantAwareAcquireAsyncJobsDueRunnable(AsyncExecutor asyncExecutor, TenantInfoHolder tenantInfoHolder, String tenantId) {
        this(asyncExecutor, tenantInfoHolder, tenantId, null, AcquireJobsRunnableConfiguration.DEFAULT);
    }

    public TenantAwareAcquireAsyncJobsDueRunnable(AsyncExecutor asyncExecutor, TenantInfoHolder tenantInfoHolder, String tenantId,
            AcquireAsyncJobsDueLifecycleListener lifecycleListener, AcquireJobsRunnableConfiguration configuration) {
        super("flowable-tenant-" + tenantId + "-" + asyncExecutor.getJobServiceConfiguration().getEngineName() + "-acquire-jobs",
                asyncExecutor, asyncExecutor.getJobServiceConfiguration().getJobEntityManager(), lifecycleListener, configuration);
        this.tenantInfoHolder = tenantInfoHolder;
        this.tenantId = tenantId;
    }

    protected ExecutorPerTenantAsyncExecutor getTenantAwareAsyncExecutor() {
        return (ExecutorPerTenantAsyncExecutor) asyncExecutor;
    }

    @Override
    public synchronized void run() {
        tenantInfoHolder.setCurrentTenantId(tenantId);
        super.run();
        tenantInfoHolder.clearCurrentTenantId();
    }

}
