package ink.ptms.raphael.module.permission

import com.google.common.collect.Maps
import com.google.gson.JsonObject
import ink.ptms.raphael.Raphael
import ink.ptms.raphael.RaphaelAPI
import ink.ptms.raphael.api.EventType
import ink.ptms.raphael.api.RaphaelGroupEvent
import ink.ptms.raphael.api.RaphaelPlayerEvent
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.util.UNSAFE
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * @Author sky
 * @Since 2020-02-01 17:40
 */
@TListener(register = "init", cancel = "cancel")
private class Events : Listener {

    val perm = Maps.newConcurrentMap<String, PermissibleRaphael>()!!

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: RaphaelPlayerEvent) {
        if (e.eventType != EventType.VARIABLE) {
            Bukkit.getScheduler().runTask(Raphael.getPlugin(), Runnable { RaphaelAPI.updatePermission() })
        }
        RaphaelAPI.writeLogs(JsonObject().run {
            this.addProperty("event", "PLAYER")
            this.addProperty("eventType", e.eventType.name)
            this.addProperty("eventAction", e.eventAction.name)
            this.addProperty("id", e.player.name)
            this.addProperty("name", e.asVariableKey())
            this.addProperty("data", e.asVariableValue())
            this.addProperty("time", e.time)
            this.addProperty("reason", e.reason)
            this
        })
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: RaphaelGroupEvent) {
        if (e.eventType == EventType.PERMISSION) {
            Bukkit.getScheduler().runTask(Raphael.getPlugin(), Runnable { RaphaelAPI.getGroupPlayers(e.group).forEach { RaphaelAPI.updatePermission(it) } })
        }
        RaphaelAPI.writeLogs(JsonObject().run {
            this.addProperty("event", "GROUP")
            this.addProperty("eventType", e.eventType.name)
            this.addProperty("eventAction", e.eventAction.name)
            this.addProperty("id", e.group)
            this.addProperty("name", e.asVariableKey())
            this.addProperty("data", e.asVariableValue())
            this.addProperty("reason", e.reason)
            this
        })
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun e(e: PlayerJoinEvent) {
        try {
            perm[e.player.name] = (UNSAFE.allocateInstance(PermissibleRaphael::class.java) as PermissibleRaphael).run {
                this.init(e.player)
                this
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        RaphaelAPI.cancelPermission(e.player)
        RaphaelAPI.updatePermission(e.player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun e(e: PlayerQuitEvent) {
        RaphaelAPI.cancelPermission(e.player)
        try {
            perm.remove(e.player.name)?.cancel()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun init() {
        Bukkit.getOnlinePlayers().forEach { player ->
            try {
                perm[player.name] = (UNSAFE.allocateInstance(PermissibleRaphael::class.java) as PermissibleRaphael).run {
                    this.init(player)
                    this
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        RaphaelAPI.cancelPermission()
        RaphaelAPI.updatePermission()
    }

    fun cancel() {
        Bukkit.getOnlinePlayers().forEach { player ->
            try {
                perm.remove(player.name)?.cancel()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        RaphaelAPI.cancelPermission()
    }
}