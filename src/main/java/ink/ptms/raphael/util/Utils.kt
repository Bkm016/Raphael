package ink.ptms.raphael.util

import io.izzel.taboolib.module.command.lite.CompleterTab
import org.bukkit.Bukkit

object Utils {

    fun playerTab() = CompleterTab { _, args ->
        Bukkit.getOnlinePlayers().map { it.name }.filter { it.toLowerCase().startsWith(args.last().toLowerCase()) }
    }
}