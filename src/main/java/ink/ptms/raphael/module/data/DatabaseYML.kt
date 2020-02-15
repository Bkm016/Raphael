package ink.ptms.raphael.module.data

import ink.ptms.raphael.Raphael
import ink.ptms.raphael.RaphaelAPI
import ink.ptms.raphael.util.Writer
import io.izzel.taboolib.module.db.local.LocalPlayer
import io.izzel.taboolib.util.ArrayUtil
import io.izzel.taboolib.util.Files
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.util.NumberConversions
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.ArrayList

/**
 * @Author sky
 * @Since 2019-12-21 15:29
 */
class DatabaseYML : Database() {

    val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

    override fun getData(player: Player): FileConfiguration {
        return LocalPlayer.get(player)
    }

    override fun saveData(player: Player) {
    }

    override fun writeLogs(data: String) {
        Writer.writeAppend(Files.file(Raphael.getPlugin().dataFolder, "logs.txt")) { r ->
            r.write(System.currentTimeMillis().toString() + "    " + format.format(System.currentTimeMillis()) + "    " + data)
            r.newLine()
        }
    }

    override fun readLogs(time: Long): List<Log> {
        val logs = ArrayList<Log>()
        Files.read(Files.file(Raphael.getPlugin().dataFolder, "logs.txt")) { r ->
            r.lines().forEach { line ->
                val log = line.split("    ").run {
                    if (time == 0L || time > System.currentTimeMillis() - NumberConversions.toLong(this[0])) {
                        Log(ArrayUtil.arrayJoin(this.toTypedArray(), 2), NumberConversions.toLong(this[0]), this[1])
                    } else {
                        null
                    }
                }
                if (log != null) {
                    logs.add(log)
                }
            }
        }
        return logs
    }
}