package ink.ptms.raphael.module.permission

import ink.ptms.raphael.RaphaelAPI
import org.bukkit.Bukkit
import org.bukkit.entity.HumanEntity
import org.bukkit.permissions.PermissibleBase
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.permissions.PermissionAttachmentInfo
import org.bukkit.plugin.Plugin
import taboolib.common.io.runningClasses
import taboolib.common.reflect.Reflex.Companion.getProperty
import taboolib.common.reflect.Reflex.Companion.setProperty
import taboolib.module.configuration.createLocal
import java.util.*

/**
 * @author sky
 * @since 2020-01-31 23:48
 */
class PermissibleRaphael : PermissibleBase(null) {

    val data = createLocal("data.yml")
    var humanEntity: HumanEntity? = null
    var permissibleBase: PermissibleBase? = null

    fun init(humanEntity: HumanEntity) {
        this.humanEntity = humanEntity
        this.permissibleBase = humanEntity.getProperty("perm")
        humanEntity.setProperty("perm", this)
    }

    fun cancel() {
        humanEntity?.setProperty("perm", permissibleBase)
    }

    override fun isPermissionSet(s: String): Boolean {
        return permissibleBase?.isPermissionSet(s) == true
    }

    override fun isPermissionSet(permission: Permission): Boolean {
        return permissibleBase?.isPermissionSet(permission) == true
    }

    override fun hasPermission(s: String): Boolean {
        if (humanEntity == null || permissibleBase == null) {
            return false
        }
        Thread.currentThread().stackTrace.also { elements ->
            RaphaelAPI.writeLogs {
                addProperty("event", "API")
                addProperty("eventType", "hasPermission")
                addProperty("id", humanEntity!!.name)
                addProperty("name", s)
                addProperty("caller", getCaller(elements))
            }
        }
        val manager = Bukkit.getServer().pluginManager
        val name = s.toLowerCase(Locale.ENGLISH)
        permissibleBase!!.effectivePermissions.forEach {
            if (it.permission == name) {
                return it.value
            }
            val bukkitPermission = manager.getPermission(it.permission)
            if (bukkitPermission != null && bukkitPermission.children[name] == true) {
                return it.value
            }
        }
        return manager.getPermission(name)?.default?.getValue(isOp) == true
    }

    override fun hasPermission(permission: Permission): Boolean {
        return hasPermission(permission.name)
    }

    override fun addAttachment(plugin: Plugin, s: String, b: Boolean): PermissionAttachment {
        return permissibleBase!!.addAttachment(plugin, s, b)
    }

    override fun addAttachment(plugin: Plugin): PermissionAttachment {
        return permissibleBase!!.addAttachment(plugin)
    }

    override fun addAttachment(plugin: Plugin, s: String, b: Boolean, i: Int): PermissionAttachment? {
        return permissibleBase?.addAttachment(plugin, s, b, i)
    }

    override fun addAttachment(plugin: Plugin, i: Int): PermissionAttachment? {
        return permissibleBase?.addAttachment(plugin, i)
    }

    override fun removeAttachment(permissionAttachment: PermissionAttachment) {
        permissibleBase?.removeAttachment(permissionAttachment)
    }

    override fun recalculatePermissions() {
        permissibleBase?.recalculatePermissions()
    }

    override fun getEffectivePermissions(): Set<PermissionAttachmentInfo> {
        return permissibleBase?.effectivePermissions ?: emptySet()
    }

    override fun isOp(): Boolean {
        Thread.currentThread().stackTrace.also { elements ->
            RaphaelAPI.writeLogs {
                addProperty("event", "API")
                addProperty("eventType", "isOp")
                addProperty("id", humanEntity?.name)
                addProperty("caller", getCaller(elements))
            }
        }
        return permissibleBase?.isOp == true
    }

    override fun setOp(b: Boolean) {
        Thread.currentThread().stackTrace.also { elements ->
            RaphaelAPI.writeLogs {
                addProperty("event", "API")
                addProperty("eventType", "setOp")
                addProperty("id", humanEntity?.name)
                addProperty("data", b)
                addProperty("caller", getCaller(elements))
            }
        }
        permissibleBase?.isOp = b
    }

    fun getCaller(elements: Array<StackTraceElement>): String {
        elements.filterNot { it.lineNumber == -1 || it.className.startsWith("net.minecraft.server") }.forEach { element ->
            try {
                if (Class.forName(element.className) !in runningClasses) {
                    return "${element.className}(${element.methodName}:${element.lineNumber})"
                }
            } catch (t: Throwable) {
            }
        }
        return "unknown"
    }
}
