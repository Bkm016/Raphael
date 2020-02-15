package ink.ptms.raphael.module.data

import ink.ptms.raphael.Raphael
import ink.ptms.raphael.RaphaelAPI
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.module.inject.TSchedule
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * @Author sky
 * @Since 2019-12-24 21:31
 */
@TListener(cancel = "cancel")
private class DatabaseHandle : Listener {

    @EventHandler
    fun e(e: PlayerJoinEvent) {
        Bukkit.getScheduler().runTaskAsynchronously(Raphael.getPlugin(), Runnable { RaphaelAPI.database.getData(e.player) })
    }

    @EventHandler
    fun e(e: PlayerQuitEvent) {
        Bukkit.getScheduler().runTaskAsynchronously(Raphael.getPlugin(), Runnable { RaphaelAPI.database.saveData(e.player) })
    }

    fun cancel() {
        saveTask()
    }

    companion object {

        @TSchedule(period = 100, async = true)
        fun saveTask() {
            Bukkit.getOnlinePlayers().forEach {
                if (it.hasMetadata("raphael:save")) {
                    it.removeMetadata("raphael:save", Raphael.getPlugin())
                    RaphaelAPI.database.saveData(it)
                }
            }
        }
    }
}