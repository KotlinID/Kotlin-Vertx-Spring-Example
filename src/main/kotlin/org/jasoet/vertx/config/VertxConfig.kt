package org.jasoet.vertx.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import com.hazelcast.config.Config
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.eventbus.EventBus
import io.vertx.core.file.FileSystem
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.ext.web.Router
import io.vertx.ext.web.templ.PebbleTemplateEngine
import io.vertx.ext.web.templ.TemplateEngine
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager
import org.jasoet.vertx.config.properties.AppProperties
import org.jasoet.vertx.extension.blockingSingle
import org.jasoet.vertx.extension.env
import org.jasoet.vertx.extension.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.InetAddress

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */

@Configuration
@EnableConfigurationProperties(value = *arrayOf(AppProperties::class))
class VertxConfig() {

    init {
        Json.mapper.apply {
            registerKotlinModule()
            registerModule(ParameterNamesModule())
            registerModule(JavaTimeModule())
        }

        Json.prettyMapper.apply {
            registerKotlinModule()
            registerModule(ParameterNamesModule())
            registerModule(JavaTimeModule())
        }
    }

    private val log = logger(VertxConfig::class)
    @Autowired
    lateinit var appProperties: AppProperties


    @Bean
    fun hazelClusterManager(): ClusterManager {
        val hazelcastConfig = Config()
        return HazelcastClusterManager(hazelcastConfig)
    }

    @Bean
    fun vertx(): Vertx {
        val vertxOption = VertxOptions().apply {
            this.clusterManager = hazelClusterManager()
            try {
                val address = InetAddress.getByName(env("HOSTNAME", "localhost")).hostAddress
                this.clusterHost = address
                log.info("Cluster set to use clusterHost ${this.clusterHost}")
            } catch (e: Exception) {
                log.info("Hostname not Found, perhaps you run this app locally!")
            }
        }

        return blockingSingle<Vertx> { Vertx.clusteredVertx(vertxOption, it) }.value()
    }

    @Bean
    fun config(): JsonObject {
        return json {
            obj("HTTP_PORT" to appProperties.httpPort)
        }
    }

    @Bean
    fun router(): Router {
        return Router.router(vertx())
    }

    @Bean
    fun eventBus(): EventBus {
        return vertx().eventBus()
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return Json.mapper
    }

    @Bean
    fun fileSystem(): FileSystem {
        return vertx().fileSystem()
    }

    @Bean
    fun pebbleTemplate(): TemplateEngine {
        return PebbleTemplateEngine.create(vertx())
    }

}