package ink.ptms.raphael.module.data

import ink.ptms.raphael.Raphael
import io.izzel.taboolib.cronus.bridge.CronusBridge
import io.izzel.taboolib.cronus.bridge.database.IndexType
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

/**
 * @Author sky
 * @Since 2020-09-19 12:37
 */
class DatabaseMongo : Database() {

    val perm = CronusBridge.get(Raphael.conf.getString("Database.client"), Raphael.conf.getString("Database.database"), "${Raphael.conf.getString("Database.collection")}_permission", IndexType.USERNAME)
    val logs = CronusBridge.get(Raphael.conf.getString("Database.client"), Raphael.conf.getString("Database.database"), "${Raphael.conf.getString("Database.collection")}_logs")

    override fun getData(player: Player): FileConfiguration {
        return perm.get(player.name)
    }

    override fun saveData(player: Player) {
        perm.update(player.name)
    }

    override fun writeLogs(data: String) {
        TODO("Not yet implemented")
    }

    override fun readLogs(time: Long): List<Log> {
        TODO("Not yet implemented")
    }
}