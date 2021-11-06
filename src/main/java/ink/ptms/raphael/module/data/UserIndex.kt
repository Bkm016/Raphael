package ink.ptms.raphael.module.data

import ink.ptms.raphael.Raphael

/**
 * Chemdah
 * ink.ptms.chemdah.core.database.UserIndex
 *
 * @author sky
 * @since 2021/9/18 10:56 下午
 */
enum class UserIndex {

    NAME, UUID;

    companion object {

        val INSTANCE: UserIndex by lazy {
            try {
                valueOf(Raphael.conf.getString("Database.user-index", "")!!.uppercase())
            } catch (ignored: Throwable) {
                UUID
            }
        }
    }
}