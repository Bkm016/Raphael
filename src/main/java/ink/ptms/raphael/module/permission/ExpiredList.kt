package ink.ptms.raphael.module.permission

/**
 * @Author sky
 * @Since 2020-02-01 15:39
 */
class ExpiredList(source: List<*>) {

    val value = source.filterIsInstance<Map<*, *>>().map { ExpiredValue(it) }.toMutableList()

    fun removeExpired(): ExpiredList {
        value.removeIf { it.isExpired() }
        return this
    }

    fun translate(): List<*> {
        return value.map { it.translate() }.toList()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ExpiredList) return false
        if (value != other.value) return false
        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "ExpiredList(value=$value)"
    }
}