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

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.management.model.context.Context;
import org.terracotta.management.model.context.ContextContainer;
import org.terracotta.management.model.stats.ContextualStatistics;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author glick
 */
public class CapabilityManagementTest
{
  @SuppressWarnings("unused")
  private static final transient Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @SuppressWarnings("FieldCanBeLocal")
  private String capabilityName = "cacheRestructuring";

  @SuppressWarnings("FieldCanBeLocal")
  private ContextContainer contextContainer;

  private Context context;
  private Collection<Context> contexts;
  private ManagementRegistry managementRegistry;

  @Before
  public void setUp() {

    context = Context.create("innerKey", "innerValue");

    contexts = Collections.singletonList(context);

    contextContainer = new ContextContainer("outerKey", "outerValue");

    managementRegistry = new DefaultManagementRegistry(contextContainer);
  }

  @Test
  public void testCapabilityManagement() {

    DefaultCapabilityManagement defaultCapabilityManagement = new DefaultCapabilityManagement(managementRegistry, capabilityName);

    StatisticQuery.Builder queryBuilder = defaultCapabilityManagement.queryStatistic("FFR");

    queryBuilder = queryBuilder.on(contexts);

    StatisticQuery query = queryBuilder.build();

    ResultSet resultSet = query.execute();

    ContextualStatistics contextualStatistics = (ContextualStatistics) resultSet.getResult(context);

    assertThat(contextualStatistics.getCapability()).isEqualTo("cacheRestructuring");
    assertThat(contextualStatistics.isEmpty()).isTrue();
    assertThat(contextualStatistics.size()).isEqualTo(0);
  }

  @Test
  @Ignore
  public void testManagmentProviders() {

  }
}
