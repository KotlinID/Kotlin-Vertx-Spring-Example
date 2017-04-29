package org.jasoet.vertx.extension

import org.springframework.beans.factory.BeanFactory
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo
 */


inline fun <reified T> BeanFactory.getBean(): T {
    return this.getBean(T::class.java)
}

inline fun <reified T> springBootStart(vararg args: String): ConfigurableApplicationContext {
    return SpringApplication.run(T::class.java, *args)
}
