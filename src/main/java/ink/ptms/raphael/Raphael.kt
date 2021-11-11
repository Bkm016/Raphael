package ink.ptms.raphael

import ink.ptms.raphael.RaphaelAPI.permission
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import taboolib.common.platform.Plugin
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile
import taboolib.platform.BukkitPlugin

object Raphael : Plugin() {

    @Config
    lateinit var conf: SecuredFile
        private set

    override fun onEnable() {
        Bukkit.getServicesManager().register(Permission::class.java, RaphaelHook(), BukkitPlugin.getInstance(), ServicePriority.Normal)
    }

    override fun onDisable() {
        Bukkit.getServicesManager().unregister(permission)
    }
}