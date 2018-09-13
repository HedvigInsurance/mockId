package mockid

import io.micronaut.context.ApplicationContext
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions.assertEquals

class HelloTest: Spek({

    describe("HelloWorld"){
        var embeddedServer : EmbeddedServer = ApplicationContext.run(EmbeddedServer::class.java);
        var client : HttpClient = HttpClient.create(embeddedServer.url)
        on("test /hello responds Hello World"){
            var rsp : String = client.toBlocking().retrieve("/h/hej")
            assertEquals("{\"name\":\"Hej där!\"}", rsp)
        }
        afterGroup {
            client.close()
            embeddedServer.close()
        }
    }

})