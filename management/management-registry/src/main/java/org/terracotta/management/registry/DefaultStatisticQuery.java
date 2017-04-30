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
package org.terracotta.management.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.management.model.context.Context;
import org.terracotta.management.model.stats.ContextualStatistics;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * @author Mathieu Carbou
 */
public class DefaultStatisticQuery implements StatisticQuery {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final CapabilityManagementSupport capabilityManagement;
  private final String capabilityName;
  private final Collection<String> statisticNames;
  private final Collection<Context> contexts;

  @SuppressWarnings("WeakerAccess")
  public DefaultStatisticQuery(CapabilityManagementSupport capabilityManagement, String capabilityName, Collection<String> statisticNames,
    Collection<Context> contexts) {

    if (contexts.isEmpty()) throw new IllegalArgumentException("You did not specify any context to extract the statistics from");

    this.capabilityManagement = capabilityManagement;
    this.capabilityName = capabilityName;
    this.statisticNames = Collections.unmodifiableSet(new LinkedHashSet<>(statisticNames));
    this.contexts = Collections.unmodifiableCollection(new ArrayList<>(contexts));
  }

  @Override
  public String getCapabilityName() {
    return capabilityName;
  }

  @Override
  public Collection<Context> getContexts() {
    return contexts;
  }

  @Override
  public Collection<String> getStatisticNames() {
    return statisticNames;
  }

  @Override
  public ResultSet<ContextualStatistics> execute() {
    Map<Context, ContextualStatistics> contextualStatistics = new LinkedHashMap<>(contexts.size());
    Collection<ManagementProvider<?>> managementProviders = capabilityManagement.getManagementProvidersByCapability(capabilityName);

    for (Context context : contexts) {
      Map<String, Number> statistics = getManagementProviderStatisticsByContext(managementProviders, context);
      contextualStatistics.put(context, new ContextualStatistics(capabilityName, context, statistics));
    }

    return new DefaultResultSet<>(contextualStatistics);
  }

  private Map<String, Number> getManagementProviderStatisticsByContext(Collection<ManagementProvider<?>> managementProviders, Context context)
  {
    Map<String, Number> statistics = new HashMap<>();

    for (ManagementProvider<?> managementProvider : managementProviders) {
      if (managementProvider.supports(context)) {
        for (Map.Entry<String, Number> entry : managementProvider.collectStatistics(context, statisticNames).entrySet()) {
          if (entry.getValue() != null && (entry.getValue().doubleValue() >= 0 || Double.isNaN(entry.getValue().doubleValue()))) {

            LOG.warn("entry.value is " + entry.getValue());

            statistics.put(entry.getKey(), entry.getValue());
          }
        }
      }
    }

    return statistics;
  }

//  private Map<String, Number> modifiedGetManagementProviderStatisticsByContext(Collection<ManagementProvider<?>> managementProviders, Context context)
//  {
//    Map<String, Number> statistics = new HashMap<>();
//
//    for (ManagementProvider<?> managementProvider : managementProviders) {
//      if (managementProvider.supports(context)) {
//        for (Map.Entry<String, Number> entry : managementProvider.collectStatistics(context, statisticNames).entrySet()) {
//          if (entry.getValue() != null && (entry.getValue().doubleValue() >= 0 || Double.isNaN(entry.getValue().doubleValue()))) {
//            statistics.put(entry.getKey(), entry.getValue());
//          }
//        }
//      }
//    }
//
//    final Stream<ManagementProvider<?>> managementProviderStream = managementProviders.stream()
//      .filter(p -> p.supports(context));
//
//
//
//
//    return statistics;
//  }
}
