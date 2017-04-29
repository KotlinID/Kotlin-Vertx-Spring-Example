package org.jasoet.vertx.controller

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import org.jasoet.vertx.config.properties.AppProperties
import org.jasoet.vertx.extension.DataInconsistentException
import org.jasoet.vertx.extension.NotAllowedException
import org.jasoet.vertx.extension.NullObjectException
import org.jasoet.vertx.extension.OK
import org.jasoet.vertx.extension.RegistrationException
import org.jasoet.vertx.extension.endWithJson
import org.jasoet.vertx.extension.first
import org.jasoet.vertx.extension.header
import org.jasoet.vertx.extension.logger
import org.jasoet.vertx.extension.serveStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.FileNotFoundException

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */

@Component
class MainController @Autowired constructor(override val router: Router,
                                            val appProperties: AppProperties) : Controller({

    val log = logger(MainController::class)

    route("/static/*").serveStatic()
    route().last().handler { it.fail(404) }

    get("/").handler { context ->
        context.OK(message = "Hello World!")
    }

    route().first().failureHandler { errorContext ->
        val e: Throwable? = errorContext.failure()
        if (e != null) {
            log.error(e.message, e)
        }
        val code = when (e) {
            is FileNotFoundException -> HttpResponseStatus.NOT_FOUND.code()
            is NullObjectException -> HttpResponseStatus.NOT_FOUND.code()
            is DataInconsistentException -> HttpResponseStatus.INTERNAL_SERVER_ERROR.code()
            is NotAllowedException -> HttpResponseStatus.METHOD_NOT_ALLOWED.code()
            is SecurityException -> HttpResponseStatus.UNAUTHORIZED.code()
            is RegistrationException -> HttpResponseStatus.BAD_REQUEST.code()
            else ->
                if (errorContext.statusCode() > 0) {
                    errorContext.statusCode()
                } else {
                    500
                }
        }

        val acceptHeader = errorContext.header("Accept") ?: ""
        val contentTypeHeader = errorContext.header("Content-Type") ?: ""
        if (acceptHeader.matches(".*/json$".toRegex()) || contentTypeHeader.matches(".*/json$".toRegex())) {
            val result = mapOf(
                "success" to false,
                "message" to errorContext.failure().message
            )
            errorContext.response().setStatusCode(code).endWithJson(result)
        } else {
            errorContext
                .reroute(HttpMethod.GET, "/static/html/$code.html")
        }
    }
})