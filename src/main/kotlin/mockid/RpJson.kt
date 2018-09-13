package mockid

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import org.slf4j.LoggerFactory


@Controller("/rp/v5")
open class RpJson(val bankIdService: BankIdService) {

    @Post("/auth")
    fun auth(@Body  body: AuthRequest) : AuthResponse {

        val order = bankIdService.auth(body.personalNumber, body.endUserIp)
        return AuthResponse(order.id.toString(), order.autoStartToken.toString())
    }

    @Post("/sign")
    fun sign(@Body body: SignRequest) : SignResponse {
        return bankIdService.sign(body)
    }


    @Post("/collect")
    fun collect(@Body  body: CollectRequest) : CollectResponse {
        return bankIdService.collect(body)
    }


    companion object {

        private val log = LoggerFactory.getLogger(RpJson::class.java)
    }
}

class SignResponse {

}

class SignRequest {

}

data class CollectRequest(val orderRef: String)

data class CollectResponse(val orderRef: String, val status: String, val completionData: CompletionData , val hintCode: String?)

data class CompletionData(val user: UserData, val device : DeviceData, val cert: CertData, val signature: String, val ocspResponse: String)
data class CertData(val notBefore: String, val notAfter: String)
data class DeviceData(val ipAddress:String)
data class UserData(val personalNumber: String, val name:String, val givenName: String, val surname:String )

data class AuthRequest(val personalNumber: String = "", val endUserIp: String = "")

data class AuthResponse(val autoStartToken: String, val orderRef: String)

