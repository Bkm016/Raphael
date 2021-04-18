package ink.ptms.raphael.module.data

import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.locale.TLocale
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent

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

    @TListener
    companion object : Listener {

        val INSTANCE = try {
            when (Type.INSTANCE) {
                Type.SQL -> DatabaseSQL()
                Type.LOCAL -> DatabaseLocal()
                Type.MONGODB -> DatabaseMongoDB()
            }
        } catch (e: Throwable) {
            DatabaseError(e)
        }

        @EventHandler
        fun e(e: PlayerLoginEvent) {
            if (INSTANCE is DatabaseError) {
                e.result = PlayerLoginEvent.Result.KICK_OTHER
                e.kickMessage = TLocale.Translate.setColored("&4&loERROR! &r&oThe &4&lRaphael&r&o database failed to initialize.")
            }
        }
    }
}