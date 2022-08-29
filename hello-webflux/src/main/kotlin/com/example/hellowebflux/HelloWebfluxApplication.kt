package com.example.hellowebflux

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HelloWebfluxApplication

fun main(args: Array<String>) {
	runApplication<HelloWebfluxApplication>(*args)
}
