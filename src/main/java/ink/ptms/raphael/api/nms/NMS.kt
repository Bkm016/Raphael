package ink.ptms.raphael.api.nms

import io.izzel.taboolib.module.inject.TInject
import org.bukkit.entity.HumanEntity
import org.bukkit.permissions.PermissibleBase

/**
 * @Author sky
 * @Since 2020-02-03 14:31
 */
abstract class NMS {

    abstract fun setPermissibleBase(player: HumanEntity, permissibleBase: PermissibleBase)

    abstract fun getPermissibleBase(player: HumanEntity): PermissibleBase

    companion object {

        @TInject(asm = "ink.ptms.raphael.api.nms.NMSHandle")
        lateinit var HANDLE: NMS
            private set
    }
}