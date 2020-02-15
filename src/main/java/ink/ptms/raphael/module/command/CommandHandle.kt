package ink.ptms.raphael.module.command

import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.util.lite.cooldown.Cooldown
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.text.SimpleDateFormat

open class CommandHandle {

    val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

    fun notify(sender: CommandSender, value: String) {
        sender.sendMessage("§c[Raphael] §7${value.replace("&", "§")}")
        if (sender is Player && !Global.cd.isCooldown(sender.name)) {
            sender.playSound(sender.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
        }
    }

    object Global {

        @TInject
        val cd = Cooldown("command.sound", 50)
    }
}