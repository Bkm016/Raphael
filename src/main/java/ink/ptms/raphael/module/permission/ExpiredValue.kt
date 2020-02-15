package ink.ptms.raphael.module.permission

import org.bukkit.util.NumberConversions

/**
 * @Author sky
 * @Since 2020-02-01 15:47
 */
class ExpiredValue(source: Map<*, *>) {

    var name = source["name"].toString()
    var data = source["data"].toString()
    var expired = NumberConversions.toLong(source["expired"] ?: "0")

    constructor(name: String) : this(emptyMap<Any, Any>()) {
        this.name = name
    }

    constructor(name: String, data: String) : this(emptyMap<Any, Any>()) {
        this.name = name
        this.data = data
    }

    fun isExpired(): Boolean {
        return expired != 0L && System.currentTimeMillis() > expired
    }

    fun translate(): Map<*, *> {
        return mapOf(
                Pair("name", name),
                Pair("data", data),
                Pair("expired", expired)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ExpiredValue) return false
        if (name != other.name) return false
        if (data != other.data) return false
        if (expired != other.expired) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + expired.hashCode()
        return result
    }

    override fun toString(): String {
        return "ExpiredValue(name='$name', data='$data', expired=$expired)"
    }
}