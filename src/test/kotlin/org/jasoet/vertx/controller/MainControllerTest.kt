package org.jasoet.vertx.controller

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.jasoet.vertx.VertxApplication
import org.jasoet.vertx.extension.getBean
import org.jasoet.vertx.extension.logger
import org.jasoet.vertx.extension.springBootStart
import org.jasoet.vertx.verticle.MainVerticle
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.context.ConfigurableApplicationContext
import java.net.ServerSocket

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo
 */

@RunWith(VertxUnitRunner::class)
class MainControllerTest {
    val log = logger(MainControllerTest::class)
    lateinit var sharedVertx: Vertx
    lateinit var sharedContext: ConfigurableApplicationContext
    var port: Int = 0

    @Before
    fun setUp(context: TestContext) {
        val socket = ServerSocket(0)
        port = socket.localPort
        socket.close()
        log.info("Get Unused Port $port")

        log.info("Initialize Spring Application Context!")
        sharedContext = springBootStart<VertxApplication>()

        log.info("Retrieve Vertx and MainVerticle")
        sharedVertx = sharedContext.getBean<Vertx>()
        val mainVerticle = sharedContext.getBean<MainVerticle>()

        val config = sharedContext.getBean<JsonObject>()
        config.put("HTTP_PORT", port)

        log.info("Deploying Main Verticle")
        sharedVertx.deployVerticle(mainVerticle, DeploymentOptions().apply {
            this.config = config
        })
    }

    @Test
    fun testSimpleEndpoint(context: TestContext) {
        val async = context.async()
        log.info("Request Get to localhost:$port/")
        sharedVertx.createHttpClient().getNow(port, "localhost", "/") { response ->
            response.handler { body ->
                val bodyAsString = body.toString()
                log.info("Received Body [$bodyAsString]")
                context.assertTrue(bodyAsString.contains("Hello", ignoreCase = true))
                async.complete()
            }
        }
    }

    @After
    fun tearDown(context: TestContext) {
        sharedVertx.close(context.asyncAssertSuccess())
        sharedContext.close()
    }
}