package ink.ptms.raphael.module.data

/**
 * Raphael
 * ink.ptms.raphael.module.data.SerializedGroup
 *
 * @author sky
 * @since 2021/3/23 12:49 下午
 */
data class SerializedGroups(val groups: List<Group>) {

    val validGroups: List<Group>
        get() = groups.filter { !it.isExpired }

    data class Group(val name: String, val expired: Long = -1L) {

        val isExpired: Boolean
            get() = expired > 0 && expired < System.currentTimeMillis()
    }
}