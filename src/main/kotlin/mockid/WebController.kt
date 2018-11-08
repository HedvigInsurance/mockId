package mockid

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponse.*
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.views.View
import mockid.bankId.BankIdService


@Controller("/h")
open class WebController(private val bankIdService: BankIdService) {

    @View("index")
    @Get("/")
    fun index() : HttpResponse<Any> {
        val orders = bankIdService.getOrders()
        return ok(mapOf("orders" to orders))
    }

    @View("client")
    @Get("/client")
    fun client(request: HttpRequest<Any>): HttpResponse<Any> {
        val autostartToken = request.parameters["autostarttoken"]
        if(autostartToken != null) {
            val order = bankIdService.getOrderByAutostartToken(autostartToken)
            if(order != null){
                return HttpResponse.ok(Model(
                        autostartToken,
                        actions(),
                        order.firstName ?: "Firstname",
                        order.lastName ?: "Lastname",
                        order.userVisibleData))
            }

        }
        return HttpResponse.notFound()
    }

    @View("complete")
    @Get("{autostarttoken}/complete")
    fun complete(autostarttoken:String) : HttpResponse<Any> {
        val order = bankIdService.getOrderByAutostartToken(autostarttoken)
        if(order != null) {
            return ok(order)
        }

        return notFound()
    }

    @View("client")
    @Post("/client", consumes = [MediaType.APPLICATION_FORM_URLENCODED])
    fun postClient(@Body body: ClientDTO, request: HttpRequest<Any>): HttpResponse<Any> {

        val action = actions().findLast { x -> x.name == body.action } ?: return badRequest()
        bankIdService.clientUpdate(body)
        return redirect(request.uri.resolve("${body.autostarttoken}/complete"))

    }

    fun selectOption(action: String): List<Action> {
        val actions = actions()

        return actions.map { a -> Action(a.name, a.name==action, a.text) }
    }

    private fun actions(): List<Action> {
        return listOf(
                Action("complete", false, "Complete"),
                Action("userCancel", false, text = "Cancel"),
                Action("expiredTransaction", false, "Expired transaction"),
                Action("certificateErr", false, "Certificate error"),
                Action("cancelled", false, "Cancelled"),
                Action("startFailed", false, "Start failed"))
    }
}