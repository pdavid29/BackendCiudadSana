package com.ray.myfirtsproject

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MyFirstProjectApplication

fun main(args: Array<String>) {
    runApplication<MyFirstProjectApplication>(*args)
}