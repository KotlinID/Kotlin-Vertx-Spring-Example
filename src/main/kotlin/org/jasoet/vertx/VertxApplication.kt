package org.jasoet.vertx

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import org.jasoet.vertx.extension.getBean
import org.jasoet.vertx.extension.logger
import org.jasoet.vertx.extension.springBootStart
import org.jasoet.vertx.verticle.MainVerticle

@org.springframework.boot.autoconfigure.SpringBootApplication
class VertxApplication {
    companion object {
        val log = logger(VertxApplication::class)
        @JvmStatic
        fun main(args: Array<String>) {
            log.info("Initialize Spring Application Context!")
            val applicationContext = springBootStart<VertxApplication>(*args)

            log.info("Retrieve Vertx and MainVerticle")
            val vertx = applicationContext.getBean<Vertx>()
            val mainVerticle = applicationContext.getBean<MainVerticle>()
            val config = applicationContext.getBean<JsonObject>()

            log.info("Deploying Main Verticle")
            vertx.deployVerticle(mainVerticle, DeploymentOptions().apply {
                this.config = config
            })
        }
    }
}

