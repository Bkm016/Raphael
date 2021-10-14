package ink.ptms.raphael.module.command

import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.Baffle
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

open class CommandHandle {

    val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

    fun notify(sender: CommandSender, value: String) {
        sender.sendMessage("§c[Raphael] §7${value.replace("&", "§")}")
        if (sender is Player && baffle.hasNext(sender.name)) {
            sender.playSound(sender.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 2f)
        }
    }

    companion object {

        val baffle = Baffle.of(50, TimeUnit.MILLISECONDS)

        @SubscribeEvent
        fun e(e: PlayerQuitEvent) {
            baffle.reset(e.player.name)
        }
    }
}