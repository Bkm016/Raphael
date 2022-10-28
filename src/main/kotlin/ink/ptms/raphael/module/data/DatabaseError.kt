package ink.ptms.raphael.module.data

import org.bukkit.entity.Player

/**
 * Chemdah
 * ink.ptms.chemdah.database.DatabaseError
 *
 * @author sky
 * @since 2021/3/5 3:51 下午
 */
class DatabaseError(val cause: Throwable) : Database() {

    init {
        cause.printStackTrace()
    }

    override fun getPermissions(player: Player): SerializedPermissions {
        throw IllegalAccessError("Database initialization failed: ${cause.localizedMessage}")
    }

    override fun getVariables(player: Player): SerializedVariables {
        throw IllegalAccessError("Database initialization failed: ${cause.localizedMessage}")
    }

    override fun getGroups(player: Player): SerializedGroups {
        throw IllegalAccessError("Database initialization failed: ${cause.localizedMessage}")
    }

    override fun setPermission(player: Player, permission: SerializedPermissions.Permission, value: Boolean) {
        throw IllegalAccessError("Database initialization failed: ${cause.localizedMessage}")
    }

    override fun setVariable(player: Player, variable: SerializedVariables.Variable, value: Boolean) {
        throw IllegalAccessError("Database initialization failed: ${cause.localizedMessage}")
    }

    override fun setGroup(player: Player, group: SerializedGroups.Group, value: Boolean) {
        throw IllegalAccessError("Database initialization failed: ${cause.localizedMessage}")
    }
}