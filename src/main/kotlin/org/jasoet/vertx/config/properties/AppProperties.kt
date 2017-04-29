package org.jasoet.vertx.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    var imageLocation: String = "",
    var baseUrl: String = "",
    var httpPort: Int = 8080
)

