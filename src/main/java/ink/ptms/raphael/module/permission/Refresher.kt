package ink.ptms.raphael.module.permission

import ink.ptms.raphael.RaphaelAPI
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.Bukkit

object Refresher {

    @TSchedule(period = 20, async = true)
    fun e() {
        Bukkit.getOnlinePlayers().forEach { player ->
            var changed = false
            RaphaelAPI.permission.playerPermissions(player).permissions.filter { it.isExpired }.forEach {
                RaphaelAPI.permission.playerRemove(player, it.name)
                changed = true
            }
            RaphaelAPI.permission.playerGroups(player).groups.filter { it.isExpired }.forEach {
                RaphaelAPI.permission.playerRemoveGroup(player, it.name)
                changed = true
            }
            if (changed) {
                RaphaelAPI.updatePermission(player)
            }
        }
    }
}