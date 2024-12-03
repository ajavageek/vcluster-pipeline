package ch.frankel.blog.vclusterpipeline

import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertNotNull

@SpringBootTest
@ContextConfiguration(initializers = [TestContainerInitializer::class])
class VClusterPipelineTest @Autowired constructor(private val repository: ProductRepository) {

    @Test
    fun `When inserting a new Product, there should be one more Product in the database and the last inserted Product should be the one inserted`() {
        runBlocking {
            val initialCount = repository.count()
            val now = LocalDateTime.now()
            val product = Product(
                id = UUID.randomUUID(),
                name = "My awesome product",
                description = "Really awesome product",
                price = 100.0,
                createdAt = now
            )
            val insertedProduct = repository.save(product)
            assertNotNull(insertedProduct)
            val flow = repository.findAll()
            val allProducts = mutableListOf<Product>()
            flow.toCollection(allProducts)
            assertEquals(allProducts.size.toLong(), initialCount + 1)
            allProducts.sortBy(Product::createdAt)
            assertEquals(allProducts.last().name, product.name)
            assertEquals(allProducts.last().description, product.description)
            assertEquals(allProducts.last().price, product.price)
            assertEquals(now, product.createdAt)
        }
    }
}
