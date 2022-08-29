package com.example.hellowebflux.functional

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

data class User(val id: Long, val email: String)

@Component
class UserHandler {

    val users = listOf(
        User(1, "test1@naver.com"),
        User(2, "test2@naver.com"),
        User(3, "test3@naver.com")
    )

    fun getUser(req: ServerRequest): Mono<ServerResponse> {
        val userId = req.pathVariable("id")

        return users.find {
            userId.toLong() == it.id
        }?.let {
            ServerResponse.ok().bodyValue(it)
        } ?: ServerResponse.notFound().build()
    }

    fun getAll(req: ServerRequest): Mono<ServerResponse> =
        ServerResponse.ok().bodyValue(users)

}
