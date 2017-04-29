package org.jasoet.vertx.config

import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

/**
 * Bean definition of RestTemplate with PoolingHttpConnection
 *
 * @author Deny Prasetyo.
 */

@Configuration
class RestTemplateConfig {
    companion object {
        fun defaultRestTemplate(): RestTemplate {
            return RestTemplateConfig().restTemplate()
        }
    }

    private val DEFAULT_MAX_TOTAL_CONNECTIONS = 100

    private val DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5

    private val DEFAULT_READ_TIMEOUT_MILLISECONDS = 60 * 1000

    @Bean
    fun httpClient(): HttpClient {
        val connectionManager = PoolingHttpClientConnectionManager()

        connectionManager.maxTotal = DEFAULT_MAX_TOTAL_CONNECTIONS
        connectionManager.defaultMaxPerRoute = DEFAULT_MAX_CONNECTIONS_PER_ROUTE
        val requestConfig = RequestConfig.custom().setConnectTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS).build()
        return HttpClientBuilder
            .create()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build()
    }

    @Bean
    fun httpRequestFactory(): ClientHttpRequestFactory {
        return HttpComponentsClientHttpRequestFactory(httpClient())
    }

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate(httpRequestFactory())
    }

}