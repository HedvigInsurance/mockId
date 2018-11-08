package mockid.bankId

import io.micronaut.context.annotation.Prototype
import java.util.*
import javax.persistence.*


@Prototype
open class OrderRepository(@PersistenceContext private val entityManager: EntityManager) {

    open fun createOrder(personNumber: String, ipAddress: String): BankIdOrder {

        val order = BankIdOrder(id = UUID.randomUUID().toString(), autoStartToken = UUID.randomUUID().toString(), type = OrderType.AUTH, personalNumber = personNumber, ipAddress = ipAddress, status = OrderState.pending)

        return order
    }

    open fun find(id: String): BankIdOrder? {
        return entityManager.find(BankIdOrder::class.java, id)
    }

    open fun save(order: BankIdOrder) {
        entityManager.persist(order)
    }

    fun findByAutostartToken(autostartToken: String): BankIdOrder? {
        val query = entityManager.createQuery("from mockid.bankId.BankIdOrder where autostarttoken = :autostarttoken")
        query.setParameter("autostarttoken", autostartToken)
        val result = query.singleResult
        return result as? BankIdOrder
    }

    fun findActiveOrders(): List<BankIdOrder> {
        val query = entityManager.createQuery("from mockid.bankId.BankIdOrder where status = 'pending'", BankIdOrder::class.java)

        val result = query.resultList
        return result
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


