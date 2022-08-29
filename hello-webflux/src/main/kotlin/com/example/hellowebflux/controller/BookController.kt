package com.example.hellowebflux.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class BookController(
    private val bookService: BookService,
) {
    @GetMapping("/books")
    @ResponseStatus(HttpStatus.OK)
    fun getAll(): Flux<Book> = bookService.getAll()

    @GetMapping("/books/{bookId}")
    fun get(@PathVariable("bookId") bookId: Int): Mono<Book> = bookService.getBookById(bookId)

    @PostMapping("/books")
    fun add(@RequestBody request: Map<String, Any>): Mono<Book> = bookService.save(request)

    @DeleteMapping("/books/{bookId}")
    fun delete (@PathVariable("bookId") bookId: Int): Mono<Void> = bookService.delete(bookId)
}