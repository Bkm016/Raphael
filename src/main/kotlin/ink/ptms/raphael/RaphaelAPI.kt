package ink.ptms.raphael

import com.google.gson.JsonObject
import ink.ptms.raphael.module.permission.PermissibleData
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.io.newFile
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.submit
import taboolib.platform.BukkitPlugin
import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap

object RaphaelAPI {

    private val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    private val formatDay = SimpleDateFormat("yyyy-MM-dd")

    val attachment = ConcurrentHashMap<String, PermissibleData>()

    val permission by lazy {
        getService()!!
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
        val data = attachment.computeIfAbsent(player.name) { PermissibleData(player.addAttachment(BukkitPlugin.getInstance())) }
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
            permission.playerGroups(player).validGroups.forEach {
                addAll(permission.groupPermissions(it.name))
            }
            addAll(permission.playerPermissions(player).validPermissions.map { it.name })
            addAll(permission.groupPermissions("default"))
            this
        }
    }

    fun writeLogs(container: JsonObject.() -> Unit) {
        if (Raphael.conf.getBoolean("Logs.enable")) {
            submit(async = true) {
                val json = JsonObject().also(container)
                val jsonCaller = json.get("caller")?.asString
                if (jsonCaller != "unknown" && Raphael.conf.getStringList("Logs.ignore").none { jsonCaller?.startsWith(it) == true }) {
                    val text = System.currentTimeMillis().toString() + "\t" + format.format(System.currentTimeMillis()) + "\t" + json.toString() + "\n"
                    newFile(getDataFolder(), "logs/${formatDay.format(System.currentTimeMillis())}.txt").appendText(text)
                }
            }
        }
    }

    @SubscribeEvent
    private fun onQuit(e: PlayerQuitEvent) {
        attachment.remove(e.player.name)
    }
}