package ink.ptms.raphael.api

import io.izzel.taboolib.module.event.EventCancellable
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-02-01 17:51
 */
class RaphaelGroupEvent(
        var group: String,
        val eventType: EventType,
        val eventAction: EventAction,
        private var name: String,
        private var data: String?,
        var reason: String = ""
) : EventCancellable<RaphaelGroupEvent>() {

    init {
        async(!Bukkit.isPrimaryThread())
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