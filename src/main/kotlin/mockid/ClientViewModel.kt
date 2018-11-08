package mockid

data class Model (
    val autostarttoken: String,
    val actions: List<Action>,
    val firstName: String,
    val lastName: String,
    val userVisibleData: String?
)

data class Action (
        val name:String,
        val selected:Boolean,
        val text: String)
