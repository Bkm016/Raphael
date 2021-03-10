package ink.ptms.raphael.module.permission

import ink.ptms.raphael.RaphaelAPI
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration

object Refresher {

    @TSchedule(period = 20, async = true)
    fun e() {
        Bukkit.getOnlinePlayers().forEach { player ->
            val data = RaphaelAPI.database.getData(player)
            if (isExpired(data, "Groups") || isExpired(data, "Permissions")) {
                RaphaelAPI.updatePermission(player)
            }
        }
    }

    fun isExpired(data: FileConfiguration, id: String): Boolean {
        return ExpiredList(data.getList(id) ?: ArrayList<Any>()).value.any { it.isExpired() }
    }
}