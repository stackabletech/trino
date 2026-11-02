/*
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
package io.trino.plugin.iceberg.catalog.rest;

import com.google.common.net.HostAndPort;
import io.trino.plugin.iceberg.TrinoMetricsReporter;
import io.trino.spi.NodeVersion;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.apache.iceberg.CatalogUtil.loadMetricsReporter;
import static org.assertj.core.api.Assertions.assertThat;

final class TestIcebergRestCatalogPropertiesProvider
{
    @Test
    void testMetricsReporter()
    {
        IcebergRestCatalogPropertiesProvider catalogPropertiesProvider = new IcebergRestCatalogPropertiesProvider(
                new IcebergRestCatalogConfig().setBaseUri("http://localhost"),
                new NoneSecurityProperties(),
                new NodeVersion("test"));

        assertThat(loadMetricsReporter(catalogPropertiesProvider.catalogProperties()))
                .isInstanceOf(TrinoMetricsReporter.class);
    }

    @Test
    void testHttpProxy()
    {
        IcebergRestCatalogPropertiesProvider withoutProxy = new IcebergRestCatalogPropertiesProvider(
                new IcebergRestCatalogConfig().setBaseUri("http://localhost"),
                new NoneSecurityProperties(),
                new NodeVersion("test"));
        assertThat(withoutProxy.catalogProperties().keySet())
                .noneMatch(key -> key.startsWith("rest.client.proxy."));

        IcebergRestCatalogPropertiesProvider withProxy = new IcebergRestCatalogPropertiesProvider(
                new IcebergRestCatalogConfig()
                        .setBaseUri("http://localhost")
                        .setHttpProxy(HostAndPort.fromString("proxy.example.com:8080"))
                        .setHttpProxyUsername("proxy-user")
                        .setHttpProxyPassword("proxy-password"),
                new NoneSecurityProperties(),
                new NodeVersion("test"));
        assertThat(withProxy.catalogProperties()).containsAllEntriesOf(Map.of(
                "rest.client.proxy.hostname", "proxy.example.com",
                "rest.client.proxy.port", "8080",
                "rest.client.proxy.username", "proxy-user",
                "rest.client.proxy.password", "proxy-password"));
    }
}
