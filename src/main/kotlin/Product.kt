package ch.frankel.blog.vclusterpipeline

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.LocalDateTime
import java.util.UUID

data class Product(
    val id: UUID,
    val name: String,
    val description: String,
    val price: Double,
    val createdAt: LocalDateTime
)

interface ProductRepository : CoroutineCrudRepository<Product, String>
