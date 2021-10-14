package ink.ptms.raphael.api

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

class RaphaelPlayerEvent(
    val player: Player,
    val eventType: EventType,
    val eventAction: EventAction,
    private var name: String,
    private var data: String?,
    var time: Long = 0,
    var reason: String = "",
) : BukkitProxyEvent() {

    fun asGroup(): String {
        return name
    }

    fun asPermission(): String {
        return name
    }

    fun asVariableKey(): String {
        return name
    }

    fun asVariableValue(): String? {
        return data
    }

    fun toGroup(group: String) {
        name = group
    }

    fun toPermission(permission: String) {
        name = permission
    }

    fun toVariableKey(key: String) {
        name = key
    }

    fun toVariableValue(value: String) {
        name = value
    }
}