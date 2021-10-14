package ink.ptms.raphael.module.permission

import com.google.common.collect.Maps
import ink.ptms.raphael.RaphaelAPI
import ink.ptms.raphael.api.EventType
import ink.ptms.raphael.api.RaphaelGroupEvent
import ink.ptms.raphael.api.RaphaelPlayerEvent
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.reflect.Reflex.Companion.unsafeInstance

/**
 * @Author sky
 * @Since 2020-02-01 17:40
 */
internal object Events {

    val perm = Maps.newConcurrentMap<String, PermissibleRaphael>()!!

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: RaphaelPlayerEvent) {
        if (e.eventType != EventType.VARIABLE) {
            submit {
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

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: RaphaelGroupEvent) {
        if (e.eventType == EventType.PERMISSION) {
            submit {
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun e(e: PlayerJoinEvent) {
        try {
            perm[e.player.name] = (PermissibleRaphael::class.java.unsafeInstance() as PermissibleRaphael).run {
                init(e.player)
                this
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        RaphaelAPI.cancelPermission(e.player)
        RaphaelAPI.updatePermission(e.player)
    }

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun e(e: PlayerQuitEvent) {
        RaphaelAPI.cancelPermission(e.player)
        try {
            perm.remove(e.player.name)?.cancel()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    @Awake(LifeCycle.ENABLE)
    fun init() {
        Bukkit.getOnlinePlayers().forEach { player ->
            try {
                perm[player.name] = (PermissibleRaphael::class.java.unsafeInstance() as PermissibleRaphael).run {
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

    @Awake(LifeCycle.DISABLE)
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