package com.andreformento.money.extensions

import org.springframework.test.web.reactive.server.FluxExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult

inline fun <reified T : Any> FluxExchangeResult<T>.expectHeaderLocation(matcher: (bodyData: T) -> String): FluxExchangeResult<T> {
    val body: T = this.responseBody.blockFirst()!!
    assert(matcher(body) == this.responseHeaders.location.toString())
    return this
}

inline fun <reified T : Any> WebTestClient.ResponseSpec.extractBody(): T =
    this.returnResult<T>().responseBody.blockFirst()!!

fun WebTestClient.ResponseSpec.printBody(): WebTestClient.ResponseSpec {
    this.expectBody().consumeWith(System.out::println)
    return this
}
