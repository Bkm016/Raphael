package ink.ptms.raphael

import ink.ptms.raphael.RaphaelAPI.permission
import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.module.config.TConfig
import io.izzel.taboolib.module.inject.TInject
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority

object Raphael : Plugin() {

    @TInject
    lateinit var conf: TConfig
        private set

    override fun onLoad() {
        Bukkit.getServicesManager().register(Permission::class.java, RaphaelHook(), plugin, ServicePriority.Normal)
    }

    override fun onDisable() {
        Bukkit.getServicesManager().unregister(permission)
    }
}