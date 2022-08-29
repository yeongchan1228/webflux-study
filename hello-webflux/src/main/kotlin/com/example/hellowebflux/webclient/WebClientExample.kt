package com.example.hellowebflux.webclient

import com.example.hellowebflux.controller.Book
import kotlinx.coroutines.flow.toList
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux

@RestController
class WebClientExample {

    val url = "http://localhost:8080/books"
    val log: Logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/books/block")
    fun getBooksBlockingWay(): List<Book> {
        log.info("Start RestTemplate")

        val restTemplate = RestTemplate()
        val response = restTemplate.exchange(url, HttpMethod.GET, null,
        object : ParameterizedTypeReference<List<Book>>() {})

        val result = response.body!!

        log.info("result = {}", result)
        log.info("Finish RestTemplate")

        return result
    }

    @GetMapping("/books/non-block")
    fun getBooksNonBlockingWay() : Flux<Book> {
        log.info("Start WebClient")

        val flux = WebClient.create()
            .get()
            .uri(url)
            .retrieve()
            .bodyToFlux(Book::class.java)
            .map {
                log.info("result = {}", it)
                it
            }

        log.info("Finish WebClient")
        return flux
    }
}