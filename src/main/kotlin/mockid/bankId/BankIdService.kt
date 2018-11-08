package mockid.bankId

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Prototype
import io.micronaut.spring.tx.annotation.Transactional
import mockid.ClientDTO
import org.slf4j.LoggerFactory
import java.util.*


@Prototype
open class BankIdService(private val context: ApplicationContext) {

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



        if (order != null) {

            val completionData = CompletionData(
                    user = UserData(
                            order.personalNumber,
                            givenName = order.firstName ?: "null",
                            surname = order.lastName ?: "null",
                            name = "${order.firstName} ${order.lastName}"),
                    device = DeviceData("127.0.0.1"),
                    cert = CertData(
                            "2018-01-01",
                            "2018-01-01"),
                    signature = "JLKAJSDLSAJ=",
                    ocspResponse = "KJLDSAJLADJ==")

            return CollectResponse(
                    orderRef = order.id,
                    status = order.status.toString(),
                    completionData = if (order.status == OrderState.complete) completionData else null,
                    hintCode = order.hintCode?.toString())
        } else {
            throw OrderNotFoundException("Order not found")
        }
    }

    @Transactional
    open fun sign(signRequest: SignRequest): SignResponse {
        val orderRepository = context.getBean(OrderRepository::class.java)

        val order = BankIdOrder(
                id = UUID.randomUUID().toString(),
                autoStartToken = UUID.randomUUID().toString(),
                type = OrderType.SIGN,
                personalNumber = signRequest.personalNumber ?: "19121212-1212",
                status = OrderState.pending,
                userVisibleData = signRequest.userVisibleData)

        orderRepository.save(order)

        return SignResponse(autoStartToken = order.autoStartToken, orderRef = order.id)
    }

    @Transactional
    open fun getOrderByAutostartToken(autostartToken: String): BankIdOrder? {
        val orderRepository = context.getBean(OrderRepository::class.java)

        val orderState = orderRepository.findByAutostartToken(autostartToken)

        return orderState
    }

    @Transactional
    open fun clientUpdate(request: ClientDTO) {
        val orderRepository = context.getBean(OrderRepository::class.java)

        val order = orderRepository.findByAutostartToken(request.autostarttoken)
        if (order != null) {
            when (request.action) {
                "complete" -> {
                    order.status = OrderState.complete
                    order.hintCode = null
                }
                else -> {
                    val hint = HintCodes.valueOf(request.action)
                    order.setStatusBasedOnHintCode(hint)
                }
            }

            order.firstName = request.firstName
            order.lastName = request.lastName

            orderRepository.save(order)
        } else {
            throw OrderNotFoundException("Order not found")
        }
    }

    @Transactional
    open fun getOrders(): List<BankIdOrder> {
        val orderRepository = context.getBean(OrderRepository::class.java)
        val orders = orderRepository.findActiveOrders()

        return orders
    }

    companion object {

        private val log = LoggerFactory.getLogger(BankIdService::class.java)
    }
}