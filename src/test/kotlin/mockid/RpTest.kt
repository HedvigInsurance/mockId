package mockid

import io.micronaut.context.ApplicationContext
import io.micronaut.core.type.Argument
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.on
import org.junit.jupiter.api.Assertions

class RpTest: Spek({

    describe("HelloWorld"){

        var embeddedServer : EmbeddedServer = ApplicationContext.run(EmbeddedServer::class.java);
        var client : HttpClient = HttpClient.create(embeddedServer.url)
        on("test /rp/v5/auth responds with autostartToken and orderRef"){

            val request = HttpRequest.POST("/rp/v5/auth", emptyMap<String, String>())

            val rsp  = client.toBlocking().exchange(request, Argument.of(Map::class.java))
            Assertions.assertEquals("191212121212", rsp.body.get()["autoStartToken"])
            Assertions.assertEquals("1234", rsp.body.get()["orderRef"])
        }
        on("test /rp/v5/auth responds with autostartToken and orderRef"){

            val request = HttpRequest.POST("/rp/v5/auth", emptyMap<String, String>())

            val rsp  = client.toBlocking().exchange(request, Argument.of(Map::class.java))
            Assertions.assertEquals("191212121212", rsp.body.get()["autoStartToken"])
            Assertions.assertEquals("1234", rsp.body.get()["orderRef"])
        }
        afterGroup {
            client.close()
            embeddedServer.close()
        }
        afterGroup {
            client.close()
            embeddedServer.close()
        }
    }

})