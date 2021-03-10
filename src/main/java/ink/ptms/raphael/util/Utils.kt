package ink.ptms.raphael.util

import io.izzel.taboolib.module.command.lite.CompleterTab
import org.bukkit.Bukkit

object Utils {

    fun playerTab(): CompleterTab {
        return CompleterTab { _, args ->
            Bukkit.getOnlinePlayers().map { it.name }.filter { it.toLowerCase().startsWith(args.last().toLowerCase()) }
        }
    }

    fun compare(permA: String, permB: String): Int {
        val ap = permA.startsWith("+")
        val bp = permB.startsWith("+")
        val am = permA.startsWith("-")
        val bm = permB.startsWith("-")
        if (ap && bp) {
            return 0
        }
        if (ap && !bp) {
            return -1
        }
        if (!ap && bp) {
            return 1
        }
        if (am && bm) {
            return 0
        }
        if (am && !bm) {
            return -1
        }
        if (!am && bm) {
            return 1
        }
        return permA.compareTo(permB, ignoreCase = true)
    }
}