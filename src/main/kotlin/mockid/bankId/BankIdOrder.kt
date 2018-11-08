package mockid.bankId

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity(name = "bankid_order")
data class BankIdOrder(
        @Id
        val id: String,
        val autoStartToken: String,
        @Enumerated(EnumType.STRING)
        val type: OrderType,
        var personalNumber: String,
        val ipAddress: String = "",
        @Enumerated(EnumType.STRING)
        var status: OrderState,
        @Enumerated(EnumType.STRING)
        var hintCode: HintCodes? = null,
        var firstName: String? = null,
        var lastName: String? = null,
        var userVisibleData: String? = null) {


    fun setStatusBasedOnHintCode(hint: HintCodes) {
        val failures = listOf(
                HintCodes.expiredTransaction,
                HintCodes.startFailed,
                HintCodes.userCancel,
                HintCodes.cancelled,
                HintCodes.certificateErr)

        hintCode = hint

        status = if(failures.contains(hint)) {
            OrderState.failed
        }else{
            OrderState.pending
        }
    }
}