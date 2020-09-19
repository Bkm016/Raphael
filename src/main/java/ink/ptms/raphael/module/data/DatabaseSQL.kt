package ink.ptms.raphael.module.data

import ink.ptms.raphael.Raphael
import io.izzel.taboolib.module.db.source.DBSource
import io.izzel.taboolib.module.db.sql.*
import io.izzel.taboolib.module.db.sql.query.Where
import io.izzel.taboolib.module.inject.PlayerContainer
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource

/**
 * @Author sky
 * @Since 2019-12-21 15:28
 */
class DatabaseSQL : Database() {

    val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
    val host = SQLHost(Raphael.conf.getConfigurationSection("Database"), Raphael.getPlugin(), true)
    val table = SQLTable(Raphael.conf.getString("Database.table")).column(SQLColumn(SQLColumnType.VARCHAR, 64, "name").columnOptions(SQLColumnOption.PRIMARY_KEY)).column("text:data")!!
    val tableLogs = SQLTable(Raphael.conf.getString("Database.table") + "_logs").column("\$primary_key_id", "text:data", "bigint:time", "text:time_formatted")!!
    val dataSource: DataSource = DBSource.create(host)

    init {
        try {
            table.create(dataSource)
            tableLogs.create(dataSource)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    override fun getData(player: Player): FileConfiguration {
        player.setMetadata("raphael:save", FixedMetadataValue(Raphael.plugin, true))
        return when {
            dataMap.contains(player.name) -> dataMap[player.name]!!
            isExists(player) -> dataMap.computeIfAbsent(player.name) { get(player) }
            else -> dataMap.computeIfAbsent(player.name) { YamlConfiguration() }
        }
    }

    override fun saveData(player: Player) {
        val data = dataMap[player.name] ?: return
        if (data.getKeys(false).isEmpty()) {
            return
        }
        if (isExists(player)) {
            table.update(Where.`is`("name", player.name)).set("data", Base64.getEncoder().encodeToString(data.saveToString().toByteArray(StandardCharsets.UTF_8))).run(dataSource)
        } else {
            table.insert(player.name, Base64.getEncoder().encodeToString(data.saveToString().toByteArray(StandardCharsets.UTF_8))).run(dataSource)
        }
    }

    override fun writeLogs(data: String) {
        tableLogs.insert(null, data, System.currentTimeMillis(), format.format(System.currentTimeMillis())).run(dataSource)
    }

    override fun readLogs(time: Long): List<Log> {
        val logs = ArrayList<Log>()
        tableLogs.select().to(dataSource)
                .resultAutoNext {
                    if (time == 0L || time > System.currentTimeMillis() - it.getLong("time")) {
                        logs.add(Log(it.getString("data"), it.getLong("time"), it.getString("time_formatted")))
                    }
                }.run()
        return logs
    }

    fun isExists(player: Player): Boolean {
        return table.select(Where.`is`("name", player.name)).find(dataSource)
    }

    fun get(player: Player): FileConfiguration {
        val yaml = YamlConfiguration()
        val data = table.select(Where.`is`("name", player.name)).to(dataSource).result { it.getString("data") }.run("", "")
        yaml.loadFromString(Base64.getDecoder().decode(data).toString(StandardCharsets.UTF_8))
        return yaml
    }

    private companion object {

        @PlayerContainer
        val dataMap = ConcurrentHashMap<String, FileConfiguration>()
    }
}