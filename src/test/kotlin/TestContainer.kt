package ch.frankel.blog.vclusterpipeline

import ch.frankel.blog.vclusterpipeline.TestContainerConfig.Companion.name
import ch.frankel.blog.vclusterpipeline.TestContainerConfig.Companion.pass
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Profile
import org.testcontainers.containers.PostgreSQLContainer

@Profile("local")
class TestContainerConfig {

    companion object {

        val name = "test"
        val userName = "test"
        val pass = "test"

        val postgres = PostgreSQLContainer<Nothing>("postgres:17.2").apply {
            withDatabaseName(name)
            withUsername(userName)
            withPassword(pass)
            start()
        }
    }
}

class TestContainerInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        if (applicationContext.environment.activeProfiles.contains("local")) {
            TestPropertyValues.of(
                "spring.r2dbc.url=r2dbc:postgresql://${TestContainerConfig.postgres.host}:${TestContainerConfig.postgres.firstMappedPort}/$name",
                "spring.r2dbc.username=$name",
                "spring.r2dbc.password=$pass",
                "spring.flyway.url=jdbc:postgresql://${TestContainerConfig.postgres.host}:${TestContainerConfig.postgres.firstMappedPort}/$name",
            ).applyTo(applicationContext.environment)
        }
    }
}
