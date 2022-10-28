package ink.ptms.raphael.module.data

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerLoginEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.chat.colored

/**
 * Chemdah
 * ink.ptms.chemdah.core.database.Database
 *
 * @author sky
 * @since 2021/3/3 4:39 下午
 */
abstract class Database {

    abstract fun getPermissions(player: Player): SerializedPermissions

    abstract fun getVariables(player: Player): SerializedVariables

    abstract fun getGroups(player: Player): SerializedGroups

    abstract fun setPermission(player: Player, permission: SerializedPermissions.Permission, value: Boolean)

    abstract fun setVariable(player: Player, variable: SerializedVariables.Variable, value: Boolean)

    abstract fun setGroup(player: Player, group: SerializedGroups.Group, value: Boolean)

    companion object {

        val INSTANCE by lazy {
            try {
                when (Type.INSTANCE) {
                    Type.SQL -> DatabaseSQL()
                    Type.LOCAL -> DatabaseSQLite()
                }
            } catch (e: Throwable) {
                DatabaseError(e)
            }
        }

        @SubscribeEvent
        private fun onLogin(e: PlayerLoginEvent) {
            if (INSTANCE is DatabaseError) {
                e.result = PlayerLoginEvent.Result.KICK_OTHER
                e.kickMessage = "&4&loERROR! &r&oThe &4&lRaphael&r&o database failed to initialize.".colored()
            }
        }
    }
}