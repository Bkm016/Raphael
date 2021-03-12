package ink.ptms.raphael.module.permission

import io.izzel.taboolib.kotlin.Reflex.Companion.reflex
import org.bukkit.permissions.PermissionAttachment

/**
 * @Author sky
 * @Since 2020-01-31 23:55
 */
@Suppress("UNCHECKED_CAST")
class PermissibleData(val permissionAttachment: PermissionAttachment) {

    var permissions = permissionAttachment.reflex<MutableMap<String, Boolean>>("permissions")!!
}
