package com.example.hellowebflux.r2dbc

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface PersonRepository : ReactiveCrudRepository<Person, Long> {

    fun findByName(name: String) : Mono<Person>
}