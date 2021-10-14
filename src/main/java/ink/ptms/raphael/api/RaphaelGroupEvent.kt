package ink.ptms.raphael.api

import taboolib.platform.type.BukkitProxyEvent

class RaphaelGroupEvent(
    var group: String,
    val eventType: EventType,
    val eventAction: EventAction,
    private var name: String,
    private var data: String?,
    var reason: String = "",
) : BukkitProxyEvent() {

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