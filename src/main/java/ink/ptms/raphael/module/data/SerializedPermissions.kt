package ink.ptms.raphael.module.data

/**
 * Raphael
 * ink.ptms.raphael.module.data.SerializedPermission
 *
 * @author sky
 * @since 2021/3/23 12:49 下午
 */
data class SerializedPermissions(val permissions: List<Permission>) {

    val validPermissions: List<Permission>
        get() = permissions.filter { !it.isExpired }

    data class Permission(val name: String, val expired: Long = -1L) {

        val isExpired: Boolean
            get() = expired > 0 && expired < System.currentTimeMillis()
    }
}