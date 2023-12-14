package com.jjlee.identity

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class IdentityApplication

fun main(args: Array<String>) {
	runApplication<IdentityApplication>(*args)
}
