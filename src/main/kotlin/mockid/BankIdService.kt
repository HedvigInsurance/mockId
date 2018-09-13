package mockid

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Prototype
import io.micronaut.spring.tx.annotation.Transactional
import mockid.bankId.BankIdOrder
import mockid.bankId.OrderRepository
import org.slf4j.LoggerFactory


@Prototype
open class BankIdService(private val context: ApplicationContext){

    @Transactional
    open fun auth(personalNumber: String, endUserIp: String): BankIdOrder {
        val orderRepository = context.getBean(OrderRepository::class.java)

        val order = orderRepository.createOrder(personalNumber, ipAddress = endUserIp)
        orderRepository.save(order)
        log.info("Session started for ssn:{} with endUserIp: {}", personalNumber, endUserIp)

        return order
    }

    @Transactional
    open fun collect(body: CollectRequest): CollectResponse {
        val orderService = context.getBean(OrderRepository::class.java)

        val order = orderService.find(body.orderRef)

        //return orderService.collect(body)
        val last = (Math.random() * 10000).toInt()


        return CollectResponse(
                orderRef = body.orderRef,
                status = order.,
                completionData = CompletionData(
                        user = UserData(
                                "19121212$last",
                                "Tolvan",
                                "Tolvan",
                                "Tolvansson"),
                        device = DeviceData("127.0.0.1"),
                        cert = CertData(
                                "2018-01-01",
                                "2018-01-01"),
                        signature = "JLKAJSDLSAJ=",
                        ocspResponse = "KJLDSAJLADJ=="),
                hintCode = null)


    }

    fun sign(signRequest: SignRequest): SignResponse {
        return SignResponse()
    }

    companion object {

        private val log = LoggerFactory.getLogger(BankIdService::class.java)
    }
}