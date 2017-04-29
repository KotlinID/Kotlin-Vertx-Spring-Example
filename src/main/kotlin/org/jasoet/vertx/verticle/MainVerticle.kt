package org.jasoet.vertx.verticle

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpServer
import org.jasoet.vertx.controller.MainController
import org.jasoet.vertx.extension.logger
import org.jasoet.vertx.extension.observable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */

@Component
class MainVerticle @Autowired constructor(val mainController: MainController) : AbstractVerticle() {
    private val log = logger(MainVerticle::class)
    private val config by lazy { config() }

    override fun start(startFuture: Future<Void>) {
        log.info("Initialize Main Verticle...")

        log.info("Initialize Router...")
        val router = mainController.create()

        log.info("Starting HttpServer...")
        observable<HttpServer> {
            vertx.createHttpServer()
                .requestHandler { router.accept(it) }
                .listen(config.getInteger("HTTP_PORT"), it)
        }.subscribe(
            {
                log.info("HttpServer started in port ${config.getInteger("HTTP_PORT")}")
                log.info("Main Verticle Deployed!")
                startFuture.complete()
            },
            {
                log.error("Failed to start HttpServer. [${it.message}]", it)
                log.error("Main Verticle Failed to Deploy!")
                startFuture.fail(it)
            }
        )
    }
}