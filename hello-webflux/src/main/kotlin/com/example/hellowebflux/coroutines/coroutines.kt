package com.example.hellowebflux.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

fun sum(a: Int, b: Int) = a + b

fun main() {
    // 1. runBlocking
//    runBlocking {
//        println("Hello")
//    } // coroutine을 생성하는 빌더 역학, 해당 block 내 에서는 스레드 blocking
//
//    println("World!")

    /*
    runBlocking<Unit> {
        launch { // 비동기로 수행
            delay(500) // 일시 중단 함수, Thread를 차단하지 않고 일시 중단 <-> Thread.sleep : Thread Blocking
            println("World!")
        }

        println("Hello")
    }
    */

    // 2. launch
//    runBlocking<Unit> {
//        val job1 = launch { // 비동기로 수행
//            val elapsedTime = measureTimeMillis { // 수행 시간 체크 함수
//                delay(150)
//            }
//            println("async task-1 $elapsedTime ms")
//        }
//        job1.cancel()
//
//        val job2 = launch(start = CoroutineStart.LAZY) { // start 함수 처리 시점에 동작
//            val elapsedTime = measureTimeMillis {
//                delay(100)
//            }
//            println("async task-2 $elapsedTime ms")
//        }
//
//        println("start task-2")
//        job2.start()ﬁ
//
//    }

    // 3. async
//    runBlocking<Unit> {
//        val result1 = async {
//            delay(150)
//            sum(1, 3)
//        }
//
//        println("result1 = ${result1.await()}")
//
//        val result2 = async {
//            delay(100)
//            sum(10, 3)
//        }
//
//        println("result2 = ${result2.await()}")
//    }

    // 4. suspend
//    runBlocking {
//        doSomething()
//    }

    // 5. Flow
    runBlocking {
        val flow = simple()
        flow.collect{ value -> println(value) }
    }
}

fun printHello() = print("Hello")

suspend fun doSomething() = coroutineScope {
    launch {
        delay(200)
        println("World!")
    }
    launch {
        printHello()
    }
}

fun simple() : Flow<Int> = flow {
    println("Flow started")

    for (i in 0..3) {
        delay(100)
        emit(i)
    }
}
