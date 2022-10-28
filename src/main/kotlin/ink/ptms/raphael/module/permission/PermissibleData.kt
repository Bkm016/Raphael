package ink.ptms.raphael.module.permission

import org.bukkit.permissions.PermissionAttachment
import taboolib.common.reflect.Reflex.Companion.getProperty

/**
 * @author sky
 * @since 2020-01-31 23:55
 */
@Suppress("UNCHECKED_CAST")
class PermissibleData(val permissionAttachment: PermissionAttachment) {

    var permissions = permissionAttachment.getProperty<MutableMap<String, Boolean>>("permissions")!!
}
