package ink.ptms.raphael.module.permission

import com.google.common.collect.Maps
import ink.ptms.raphael.RaphaelAPI
import ink.ptms.raphael.api.EventType
import ink.ptms.raphael.api.RaphaelGroupEvent
import ink.ptms.raphael.api.RaphaelPlayerEvent
import io.izzel.taboolib.kotlin.Tasks
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.util.Ref
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
            Tasks.task {
                RaphaelAPI.updatePermission()
            }
        }
        RaphaelAPI.writeLogs {
            addProperty("event", "PLAYER")
            addProperty("eventType", e.eventType.name)
            addProperty("eventAction", e.eventAction.name)
            addProperty("id", e.player.name)
            addProperty("name", e.asVariableKey())
            addProperty("data", e.asVariableValue())
            addProperty("time", e.time)
            addProperty("reason", e.reason)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: RaphaelGroupEvent) {
        if (e.eventType == EventType.PERMISSION) {
            Tasks.task {
                if (e.group == "default") {
                    Bukkit.getOnlinePlayers().forEach {
                        RaphaelAPI.updatePermission(it)
                    }
                } else {
                    RaphaelAPI.getGroupPlayers(e.group).forEach {
                        RaphaelAPI.updatePermission(it)
                    }
                }
            }
        }
        RaphaelAPI.writeLogs {
            addProperty("event", "GROUP")
            addProperty("eventType", e.eventType.name)
            addProperty("eventAction", e.eventAction.name)
            addProperty("id", e.group)
            addProperty("name", e.asVariableKey())
            addProperty("data", e.asVariableValue())
            addProperty("reason", e.reason)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun e(e: PlayerJoinEvent) {
        try {
            perm[e.player.name] = (Ref.getUnsafe().allocateInstance(PermissibleRaphael::class.java) as PermissibleRaphael).run {
                init(e.player)
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
                perm[player.name] = (Ref.getUnsafe().allocateInstance(PermissibleRaphael::class.java) as PermissibleRaphael).run {
                    init(player)
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