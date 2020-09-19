package ink.ptms.raphael

import com.google.common.collect.Maps
import com.google.gson.JsonObject
import ink.ptms.raphael.module.data.DatabaseMongo
import ink.ptms.raphael.module.data.DatabaseSQL
import ink.ptms.raphael.module.data.DatabaseYML
import ink.ptms.raphael.module.permission.PermissibleData
import io.izzel.taboolib.module.inject.PlayerContainer
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object RaphaelAPI {

    @PlayerContainer
    val attachment = Maps.newConcurrentMap<String, PermissibleData>()!!
    val permission by lazy {
        getService()!!
    }

    val database by lazy {
        if (Raphael.conf.getBoolean("DatabaseSQL.enable")) {
            try {
                return@lazy DatabaseSQL()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        if (Raphael.conf.getBoolean("DatabaseMongo.enable")) {
            try {
                return@lazy DatabaseMongo()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        return@lazy DatabaseYML()
    }

    fun getService(): RaphaelHook? {
        val registration = Bukkit.getServer().servicesManager.getRegistration(Permission::class.java)
        if (registration != null && registration.provider is RaphaelHook) {
            return registration.provider as RaphaelHook
        }
        return null
    }

    fun getGroupPlayers(group: String): List<Player> {
        return Bukkit.getOnlinePlayers().filter { permission.playerInGroup(it, group) }
    }

    fun updatePermission() {
        Bukkit.getOnlinePlayers().forEach { updatePermission(it) }
    }

    fun updatePermission(player: Player) {
        val data = attachment.computeIfAbsent(player.name) { PermissibleData(player.addAttachment(Raphael.getPlugin())) }
        val map = getPermissions(player).map { permission ->
            Pair(if (permission[0] == '-') permission.substring(1) else permission, !permission.startsWith("-"))
        }
        try {
            synchronized(data.permissionAttachment.permissible) {
                data.permissions.clear()
                data.permissions.putAll(map)
                data.permissionAttachment.permissible.recalculatePermissions()
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    fun cancelPermission() {
        Bukkit.getOnlinePlayers().forEach { cancelPermission(it) }
    }

    fun cancelPermission(player: Player) {
        (attachment[player.name] ?: return).permissionAttachment.remove()
    }

    fun getPermissions(player: Player): List<String> {
        return ArrayList<String>().run {
            permission.playerGroups(player).value.forEach {
                this.addAll(permission.groupPermissions(it.name))
            }
            this.addAll(permission.playerPermissions(player).value.map { it.name })
            this.addAll(permission.groupPermissions("default"))
            this
        }
    }

    fun writeLogs(json: JsonObject) {
        writeLogs { json }
    }

    fun writeLogs(container: () -> JsonObject) {
        if (!Raphael.conf.getBoolean("Logs.enable")) {
            return
        }
        Bukkit.getScheduler().runTaskAsynchronously(Raphael.plugin, Runnable {
            val json = container.invoke()
            val jsonCaller = json.get("caller")?.asString
            if (jsonCaller != "unknown" && Raphael.conf.getStringList("Logs.ignore").none { jsonCaller?.startsWith(it) == true }) {
                database.writeLogs(json.toString())
            }
        })
    }
}