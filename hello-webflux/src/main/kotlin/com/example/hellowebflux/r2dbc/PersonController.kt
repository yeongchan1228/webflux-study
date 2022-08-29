package com.example.hellowebflux.r2dbc

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/r2dbc/persons")
class PersonController(
    private val personRepository: PersonRepository,
) {

    @GetMapping("/{name}")
    fun getByName(@PathVariable("name") bookName: String): Mono<Person> =
        personRepository.findByName(name = bookName)

    @PostMapping
    fun create(@RequestBody map: Map<String, Any>): Mono<Person> =
        personRepository.save(
            Person(
                name = map["name"].toString(),
                age = map["age"] as Int,
            )
        )
}