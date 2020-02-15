package ink.ptms.raphael.module.data

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

abstract class Database {

    abstract fun getData(player: Player): FileConfiguration

    abstract fun saveData(player: Player)

    abstract fun writeLogs(data: String)

    abstract fun readLogs(time: Long): List<Log>
}