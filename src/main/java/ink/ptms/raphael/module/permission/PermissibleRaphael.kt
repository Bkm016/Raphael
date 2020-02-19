package ink.ptms.raphael.module.permission

import com.google.gson.JsonObject
import ink.ptms.raphael.Raphael
import ink.ptms.raphael.RaphaelAPI
import ink.ptms.raphael.api.nms.NMS
import io.izzel.taboolib.TabooLib
import io.izzel.taboolib.TabooLibAPI
import io.izzel.taboolib.Version
import io.izzel.taboolib.module.db.local.Local
import io.izzel.taboolib.util.Ref
import org.bukkit.entity.HumanEntity
import org.bukkit.permissions.*
import org.bukkit.plugin.Plugin

/**
 * @Author sky
 * @Since 2020-01-31 23:48
 */
class PermissibleRaphael : PermissibleBase(null) {

    val data = Local.get().get("data.yml")
    var humanEntity: HumanEntity? = null
    var permissibleBase: PermissibleBase? = null

    fun init(humanEntity: HumanEntity) {
        this.humanEntity = humanEntity
        this.permissibleBase = NMS.HANDLE.getPermissibleBase(humanEntity)
        NMS.HANDLE.setPermissibleBase(humanEntity, this)
    }

    fun cancel() {
        NMS.HANDLE.setPermissibleBase(humanEntity, this.permissibleBase)
    }

    override fun isPermissionSet(s: String): Boolean {
        return this.permissibleBase!!.isPermissionSet(s)
    }

    override fun isPermissionSet(permission: Permission): Boolean {
        return this.permissibleBase!!.isPermissionSet(permission)
    }

    override fun hasPermission(s: String): Boolean {
        val elements = Thread.currentThread().stackTrace
        RaphaelAPI.writeLogs {
            JsonObject().run {
                this.addProperty("event", "API")
                this.addProperty("eventType", "hasPermission")
                this.addProperty("id", humanEntity?.name)
                this.addProperty("name", s)
                this.addProperty("caller", getCaller(elements))
                this
            }
        }
        return this.permissibleBase!!.hasPermission(s)
    }

    override fun hasPermission(permission: Permission): Boolean {
        val elements = Thread.currentThread().stackTrace
        RaphaelAPI.writeLogs {
            JsonObject().run {
                this.addProperty("event", "API")
                this.addProperty("eventType", "hasPermission")
                this.addProperty("id", humanEntity?.name)
                this.addProperty("name", permission.name)
                this.addProperty("caller", getCaller(elements))
                this
            }
        }
        return this.permissibleBase!!.hasPermission(permission)
    }

    override fun addAttachment(plugin: Plugin, s: String, b: Boolean): PermissionAttachment {
        return this.permissibleBase!!.addAttachment(plugin, s, b)
    }

    override fun addAttachment(plugin: Plugin): PermissionAttachment {
        return this.permissibleBase!!.addAttachment(plugin)
    }

    override fun addAttachment(plugin: Plugin, s: String, b: Boolean, i: Int): PermissionAttachment? {
        return this.permissibleBase!!.addAttachment(plugin, s, b, i)
    }

    override fun addAttachment(plugin: Plugin, i: Int): PermissionAttachment? {
        return this.permissibleBase!!.addAttachment(plugin, i)
    }

    override fun removeAttachment(permissionAttachment: PermissionAttachment) {
        this.permissibleBase!!.removeAttachment(permissionAttachment)
    }

    override fun recalculatePermissions() {
        this.permissibleBase!!.recalculatePermissions()
    }

    override fun getEffectivePermissions(): Set<PermissionAttachmentInfo> {
        return this.permissibleBase!!.effectivePermissions
    }

    override fun isOp(): Boolean {
        val elements = Thread.currentThread().stackTrace
        RaphaelAPI.writeLogs {
            JsonObject().run {
                this.addProperty("event", "API")
                this.addProperty("eventType", "isOp")
                this.addProperty("id", humanEntity?.name)
                this.addProperty("caller", getCaller(elements))
                this
            }
        }
        return this.permissibleBase!!.isOp
    }

    override fun setOp(b: Boolean) {
        val elements = Thread.currentThread().stackTrace
        RaphaelAPI.writeLogs {
            JsonObject().run {
                this.addProperty("event", "API")
                this.addProperty("eventType", "setOp")
                this.addProperty("id", humanEntity?.name)
                this.addProperty("data", b)
                this.addProperty("caller", getCaller(elements))
                this
            }
        }
        this.permissibleBase!!.isOp = b
    }

    fun getCaller(elements: Array<StackTraceElement>): String {
        elements.filterNot { it.lineNumber == -1 || it.className.contains(Version.getCurrentVersion().name) }.forEach { element ->
            try {
                val plugin = Ref.getCallerPlugin(Class.forName(element.className))
                if (plugin != Raphael.getPlugin()) {
                    return "${element.className}(${element.methodName}:${element.lineNumber})"
                }
            } catch (t: Throwable) {
            }
        }
        return "unknown"
    }
}
