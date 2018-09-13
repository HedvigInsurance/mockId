package mockid.bankId

import io.micronaut.context.annotation.Prototype
import java.util.*
import javax.persistence.*



@Prototype
open class OrderRepository(@PersistenceContext private val entityManager: EntityManager) {

    open fun createOrder(personNumber: String, ipAddress: String): BankIdOrder {

        val order = BankIdOrder(autoStartToken = UUID.randomUUID().toString(), type = OrderType.AUTH, personalNumber = personNumber, ipAddress = ipAddress)

        return order
    }

    open fun find(id: String): BankIdOrder? {
        return entityManager.find(BankIdOrder::class.java, id)
    }

    open fun save(order:BankIdOrder) {
        entityManager.persist(order)
    }

}

enum class OrderType {
AUTH, SIGN
}

enum class OrderState {
    pending,
    complete,
    failed
}

enum class HintCodes {
    //pending
    outstandingTransaction,
    noClient,
    started,
    userSign,
    //failed
    expiredTransaction,
    certificateErr,
    userCancel,
    cancelled,
    startFailed
}


@Entity(name ="bankid_order")
data class BankIdOrder(
        @Id @GeneratedValue(generator = "uuid")
        val id: String? = null,
        val autoStartToken: String? = null,
        @Enumerated(EnumType.ORDINAL)
        val type: OrderType,
        val personalNumber: String,
        val ipAddress: String = "")