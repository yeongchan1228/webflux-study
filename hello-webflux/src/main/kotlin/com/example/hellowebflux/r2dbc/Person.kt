package com.example.hellowebflux.r2dbc

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table
data class Person (
    @Id
    val id: Long? = null,

    @Column
    val name: String,

    @Column
    val age: Int,
)
