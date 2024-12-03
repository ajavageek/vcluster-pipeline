package ch.frankel.blog.vclusterpipeline

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok

@SpringBootApplication
class VClusterPipelineApplication

fun beans() = beans {
    profile("local") { DatabaseInitialization() }
    bean { ProductHandler(ref()) }
    bean {
        coRouter {
            val handler = ref<ProductHandler>()
            GET("/products/{id}")(handler::getProduct)
            GET("/products")(handler::getProducts)
            POST("/products")(handler::saveProduct)
        }
    }
}

class ProductHandler(private val repository: ProductRepository) {
    suspend fun getProduct(req: ServerRequest): ServerResponse {
        val product: Product? = repository.findById(req.pathVariable("id"))
        return if (product != null) {
            ok().bodyValueAndAwait(product)
        } else {
            notFound().buildAndAwait()
        }
    }
    suspend fun getProducts(req: ServerRequest) = ok().bodyAndAwait(repository.findAll())
    suspend fun saveProduct(req: ServerRequest): ServerResponse {
        val product = req.awaitBody<Product>()
        val savedProduct = repository.save(product)
        return ok().bodyValueAndAwait(savedProduct)
    }
}

fun main(args: Array<String>) {
    runApplication<VClusterPipelineApplication>(*args) {
        addInitializers(beans())
    }
}
