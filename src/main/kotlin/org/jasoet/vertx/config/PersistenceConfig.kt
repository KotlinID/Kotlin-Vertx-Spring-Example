package org.jasoet.vertx.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */

@Configuration
@EnableJpaRepositories()
@EntityScan(
    basePackages = arrayOf(
        "org.jasoet.vertx.db"
    )
)
@EnableJpaAuditing
class PersistenceConfig