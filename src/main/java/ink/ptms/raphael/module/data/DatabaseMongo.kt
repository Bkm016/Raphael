package ink.ptms.raphael.module.data

import com.mongodb.client.model.Filters
import ink.ptms.raphael.Raphael
import io.izzel.taboolib.cronus.bridge.CronusBridge
import io.izzel.taboolib.cronus.bridge.database.IndexType
import org.bson.Document
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.text.SimpleDateFormat

/**
 * @Author sky
 * @Since 2020-09-19 12:37
 */
class DatabaseMongo : Database() {

    val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    val perm = CronusBridge.get(Raphael.conf.getString("Database.client"), Raphael.conf.getString("Database.database"), "${Raphael.conf.getString("Database.collection")}_permission", IndexType.USERNAME)
    val logs = CronusBridge.get(Raphael.conf.getString("Database.client"), Raphael.conf.getString("Database.database"), "${Raphael.conf.getString("Database.collection")}_logs")

    override fun getData(player: Player): FileConfiguration {
        return perm.get(player.name)
    }

    override fun saveData(player: Player) {
        perm.update(player.name)
    }

    override fun writeLogs(data: String) {
        logs.mongoCollection.insertOne(Document("data", data).append("date", System.currentTimeMillis()))
    }

    override fun readLogs(time: Long): List<Log> {
        return logs.mongoCollection.find(Filters.gte("date", System.currentTimeMillis() - time)).map { Log(it.getString("data"), it.getLong("date"), format.format(it.getLong("date"))) }.toList()
    }
}