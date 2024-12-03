package ch.frankel.blog.vclusterpipeline

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import java.util.*

class VClusterPipelineIT {

    val logger = LoggerFactory.getLogger(this::class.java)

    @Test
    fun `When inserting a new Product, there should be one more Product in the database and the last inserted Product should be the one inserted`() {

        val baseUrl = System.getenv("APP_BASE_URL") ?: "http://localhost:8080"

        logger.info("Using base URL: $baseUrl")

        val client = WebTestClient.bindToServer()
            .baseUrl(baseUrl)
            .build()

        val initialResponse: EntityExchangeResult<List<Product?>?> = client.get()
            .uri("/products")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(Product::class.java)
            .returnResult()

        val initialCount = initialResponse.responseBody?.size?.toLong()

        val now = LocalDateTime.now()
        val product = Product(
            id = UUID.randomUUID(),
            name = "My awesome product",
            description = "Really awesome product",
            price = 100.0,
            createdAt = now
        )

        client.post()
            .uri("/products")
            .bodyValue(product)
            .exchange()
            .expectStatus().isOk
            .expectBody(Product::class.java)

        client.get()
            .uri("/products")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(Product::class.java)
            .hasSize((initialCount!! + 1).toInt())
    }
}
