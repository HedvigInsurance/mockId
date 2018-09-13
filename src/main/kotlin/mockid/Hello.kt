package mockid

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces

@Controller("/h")
open class Hello {

    @Get("/hej")
    @Produces("application/json")
    fun hej(): String{
        return "{\"name\":\"Hej där!\"}"
    }
}