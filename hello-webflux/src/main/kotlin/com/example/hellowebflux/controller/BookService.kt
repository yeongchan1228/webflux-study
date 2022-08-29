package com.example.hellowebflux.controller

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.atomic.AtomicInteger

data class Book(val id: Int, val name: String, val price: Int)

@Service
class BookService {

    private final val nextId = AtomicInteger(0)

    val books = mutableListOf(
        Book(id = nextId.incrementAndGet(), name = "test1", price = 1000),
        Book(id = nextId.incrementAndGet(), name = "test2", price = 2000),
        Book(id = nextId.incrementAndGet(), name = "test3", price = 3000),
        Book(id = nextId.incrementAndGet(), name = "test4", price = 4000)
    )

    fun getAll(): Flux<Book> =
        Flux.fromIterable(books)

    fun getBookById(bookId: Int): Mono<Book> =
        books.find { it.id == bookId }.toMono()

    fun save(request: Map<String, Any>): Mono<Book> =
        Mono.just(request)
        .map {
            val book =
                Book(
                    id = nextId.incrementAndGet(),
                    name = it["name"].toString(),
                    price = it["price"] as Int
                )
            books.add(book)
            book
        }

    fun delete(bookId: Int): Mono<Void> =
        Mono.justOrEmpty(books.find { it.id == bookId }).map {books.remove(it)}.then()

}