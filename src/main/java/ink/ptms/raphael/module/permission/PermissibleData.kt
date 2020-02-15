package ink.ptms.raphael.module.permission

import io.izzel.taboolib.module.lite.SimpleReflection
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionAttachment

/**
 * @Author sky
 * @Since 2020-01-31 23:55
 */
@Suppress("UNCHECKED_CAST")
class PermissibleData(val permissionAttachment: PermissionAttachment) {

    var permissions: MutableMap<String, Boolean>

    init {
        SimpleReflection.checkAndSave(PermissionAttachment::class.java)
        permissions = SimpleReflection.getFieldValue(PermissionAttachment::class.java, permissionAttachment, "permissions") as MutableMap<String, Boolean>
    }
}
