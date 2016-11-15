/*
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terracotta.management.service.monitoring.registry.provider;

import com.tc.classloader.CommonComponent;
import org.terracotta.management.registry.AbstractManagementProvider;
import org.terracotta.management.registry.ManagementProvider;
import org.terracotta.management.service.monitoring.MonitoringService;

import java.util.Objects;

/**
 * @author Mathieu Carbou
 */
@CommonComponent
public abstract class AbstractConsumerManagementProvider<T> extends AbstractManagementProvider<T> implements ManagementProvider<T>, MonitoringServiceAware {

  private MonitoringService monitoringService;

  public AbstractConsumerManagementProvider(Class<? extends T> managedType) {
    super(managedType);
  }

  @Override
  public void setMonitoringService(MonitoringService monitoringService) {
    this.monitoringService = Objects.requireNonNull(monitoringService);
  }

  protected MonitoringService getMonitoringService() {
    return Objects.requireNonNull(monitoringService);
  }

}
