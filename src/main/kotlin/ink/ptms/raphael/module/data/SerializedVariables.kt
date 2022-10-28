package ink.ptms.raphael.module.data

/**
 * Raphael
 * ink.ptms.raphael.module.data.SerializedPermission
 *
 * @author sky
 * @since 2021/3/23 12:49 下午
 */
data class SerializedVariables(val variables: List<Variable>) {

    val validVariables: List<Variable>
        get() = variables.filter { !it.isExpired }

    data class Variable(val name: String, val data: String = "", val expired: Long = -1L) {

        val isExpired: Boolean
            get() = expired > 0 && expired < System.currentTimeMillis()
    }
}