package ink.ptms.raphael.module.data

import io.izzel.taboolib.module.db.local.LocalPlayer
import io.izzel.taboolib.util.Coerce
import org.bukkit.entity.Player

/**
 * Raphael
 * ink.ptms.raphael.module.data.DatabaseLocal
 *
 * @author sky
 * @since 2021/3/23 3:04 下午
 */
class DatabaseLocal : Database() {

    override fun getPermissions(player: Player): SerializedPermissions {
        val mapList = LocalPlayer.get(player).getMapList("Raphael.permissions")
        return SerializedPermissions(mapList.map {
            SerializedPermissions.Permission(it["permission"].toString(), Coerce.toLong(it["expired"]))
        }.filter { !it.isExpired })
    }

    override fun getVariables(player: Player): SerializedVariables {
        val mapList = LocalPlayer.get(player).getMapList("Raphael.variables")
        return SerializedVariables(mapList.map {
            SerializedVariables.Variable(it["variable"].toString(), it["data"].toString(), Coerce.toLong(it["expired"]))
        }.filter { !it.isExpired })
    }

    override fun getGroups(player: Player): SerializedGroups {
        val mapList = LocalPlayer.get(player).getMapList("Raphael.groups")
        return SerializedGroups(mapList.map {
            SerializedGroups.Group(it["group"].toString(), Coerce.toLong(it["expired"]))
        }.filter { !it.isExpired })
    }

    override fun setPermission(player: Player, permission: SerializedPermissions.Permission, value: Boolean) {
        val data = LocalPlayer.get(player)
        val mapList = data.getMapList("Raphael.permissions")
        mapList.removeIf {
            it["permission"] == permission.name
        }
        if (value) {
            mapList.add(mapOf("permission" to permission.name, "expired" to permission.expired))
        }
        data.set("Raphael.permissions", mapList)
    }

    override fun setVariable(player: Player, variable: SerializedVariables.Variable, value: Boolean) {
        val data = LocalPlayer.get(player)
        val mapList = data.getMapList("Raphael.variables")
        mapList.removeIf {
            it["variable"] == variable.name
        }
        if (value) {
            mapList.add(mapOf("variable" to variable.name, "data" to variable.data, "expired" to variable.expired))
        }
        data.set("Raphael.variables", mapList)
    }

    override fun setGroup(player: Player, group: SerializedGroups.Group, value: Boolean) {
        val data = LocalPlayer.get(player)
        val mapList = data.getMapList("Raphael.groups")
        mapList.removeIf {
            it["group"] == group.name
        }
        if (value) {
            mapList.add(mapOf("group" to group.name, "expired" to group.expired))
        }
        data.set("Raphael.groups", mapList)
    }
}