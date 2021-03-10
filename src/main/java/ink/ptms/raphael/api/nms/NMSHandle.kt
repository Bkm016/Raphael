package ink.ptms.raphael.api.nms

import io.izzel.taboolib.kotlin.Reflex.Companion.reflex
import org.bukkit.entity.HumanEntity
import org.bukkit.permissions.PermissibleBase

/**
 * @Author sky
 * @Since 2020-02-03 14:32
 */
class NMSHandle : NMS() {

    override fun setPermissibleBase(player: HumanEntity, permissibleBase: PermissibleBase) {
        player.reflex("perm", permissibleBase)
    }

    override fun getPermissibleBase(player: HumanEntity): PermissibleBase {
        return player.reflex("perm")!!
    }
}