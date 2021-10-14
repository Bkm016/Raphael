package ink.ptms.raphael.module.data

import ink.ptms.raphael.Raphael
import io.izzel.taboolib.kotlin.Tasks
import io.izzel.taboolib.module.db.sql.*
import io.izzel.taboolib.module.db.sql.query.Where
import io.izzel.taboolib.module.inject.PlayerContainer
import org.bukkit.entity.Player
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.Table
import taboolib.module.database.getHost
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * Chemdah
 * ink.ptms.chemdah.database.DatabaseSQL
 *
 * @author sky
 * @since 2021/3/5 3:51 下午
 */
class DatabaseSQL : Database() {

    val host = Raphael.conf.getHost("Database.source.SQL")

    val name: String
        get() = Raphael.conf.getString("Database.source.SQL.table", "raphael")!!

    val tableUser = Table("${name}_user", host) {
        add { id() }
        add("name") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.UNIQUE_KEY)
            }
        }
        add("uuid") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.UNIQUE_KEY)
            }
        }
        add("time") {
            type(ColumnTypeSQL.DATE)
        }
    }

    val tablePermission = Table("${name}_permission", host) {
        add { id() }
        add("user") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("permission") {
            type(ColumnTypeSQL.VARCHAR, 128) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("value") {
            type(ColumnTypeSQL.BOOL)
        }
        add("expired") {
            type(ColumnTypeSQL.DATE)
        }
    }

    val tableGroup = Table("${name}_group", host) {
        add { id() }
        add("user") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("group") {
            type(ColumnTypeSQL.VARCHAR, 128) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("value") {
            type(ColumnTypeSQL.BOOL)
        }
        add("expired") {
            type(ColumnTypeSQL.DATE)
        }
    }

    val tableVariable = Table("${name}_variable", host) {
        add { id() }
        add("user") {
            type(ColumnTypeSQL.INT, 16) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("variable") {
            type(ColumnTypeSQL.VARCHAR, 128) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("data") {
            type(ColumnTypeSQL.VARCHAR, 128)
        }
        add("value") {
            type(ColumnTypeSQL.BOOL)
        }
        add("expired") {
            type(ColumnTypeSQL.DATE)
        }
    }

    val dataSource = host.createDataSource()

    init {
        tableUser.createTable(dataSource)
        tablePermission.createTable(dataSource)
        tableGroup.createTable(dataSource)
        tableVariable.createTable(dataSource)
    }

    fun getUserId(player: Player): Long {
        if (cacheUserId.containsKey(player.name)) {
            return cacheUserId[player.name]!!
        }
        val userId = tableUser.select(dataSource) {
            where("uuid" eq player.uniqueId.toString())
            limit(1)
            rows("id")
        }.firstOrNull { getLong("id") } ?: return -1L
        cacheUserId[player.name] = userId
        return userId
    }

    fun updateUserTime(userId: Long) = tableUser.update(Where.equals("id", userId))
        .set("time", Date())
        .run(dataSource)

    fun createUser(player: Player): CompletableFuture<Long> {
        val userId = CompletableFuture<Long>()
        CompletableFuture<Void>().also { future ->
            tableUser.insert(null, player.name, player.uniqueId, Date())
                .to(dataSource)
                .statement { stmt ->
                    future.thenApply {
                        userId.complete(stmt.generatedKeys.getLong("id"))
                    }
                }.run()
        }.complete(null)
        return userId
    }

    fun getUserPermissions(user: Long): List<SerializedPermissions.Permission> {
        return tablePermission.select(Where.equals("user", user), Where.equals("value", true), Where.more("expired", System.currentTimeMillis()))
            .row("permission", "expired")
            .to(dataSource)
            .map {
                SerializedPermissions.Permission(it.getString("permission"), it.getDate("expired").time)
            }
    }

    fun getUserVariables(user: Long): List<SerializedVariables.Variable> {
        return tableVariable.select(Where.equals("user", user), Where.equals("value", true), Where.more("expired", System.currentTimeMillis()))
            .row("variable", "data", "expired")
            .to(dataSource)
            .map {
                SerializedVariables.Variable(it.getString("variable"), it.getString("data"), it.getDate("expired").time)
            }
    }

    fun getUserGroups(user: Long): List<SerializedGroups.Group> {
        return tablePermission.select(Where.equals("user", user), Where.equals("value", true), Where.more("expired", System.currentTimeMillis()))
            .row("group", "expired")
            .to(dataSource)
            .map {
                SerializedGroups.Group(it.getString("group"), it.getDate("expired").time)
            }
    }

    override fun getPermissions(player: Player): SerializedPermissions {
        val user = getUserId(player)
        if (user == -1L) {
            return SerializedPermissions(emptyList())
        }
        Tasks.task(true) {
            updateUserTime(user)
        }
        return SerializedPermissions(getUserPermissions(user))
    }

    override fun getVariables(player: Player): SerializedVariables {
        val user = getUserId(player)
        if (user == -1L) {
            return SerializedVariables(emptyList())
        }
        Tasks.task(true) {
            updateUserTime(user)
        }
        return SerializedVariables(getUserVariables(user))
    }

    override fun getGroups(player: Player): SerializedGroups {
        val user = getUserId(player)
        if (user == -1L) {
            return SerializedGroups(emptyList())
        }
        Tasks.task(true) {
            updateUserTime(user)
        }
        return SerializedGroups(getUserGroups(user))
    }

    override fun setPermission(player: Player, permission: SerializedPermissions.Permission, value: Boolean) {
        val user = getUserId(player)
        if (user == -1L) {
            createUser(player).thenApply { userId ->
                tablePermission.insert(null, userId, permission.name, value, permission.expired).run(dataSource)
            }
        } else {
            tablePermission.update(Where.equals("user", user), Where.equals("permission", permission.name))
                .insertIfAbsent(null, user, permission.name, value, permission.expired)
                .set("value", value)
                .also {
                    if (value) {
                        it.set("expired", permission.expired)
                    }
                }.run(dataSource)
        }
    }

    override fun setVariable(player: Player, variable: SerializedVariables.Variable, value: Boolean) {
        val user = getUserId(player)
        if (user == -1L) {
            createUser(player).thenApply { userId ->
                tableVariable.insert(null, userId, variable.name, variable.data, value, variable.expired).run(dataSource)
            }
        } else {
            tableVariable.update(Where.equals("user", user), Where.equals("variable", variable.name))
                .insertIfAbsent(null, user, variable.name, variable.data, value, variable.expired)
                .set("value", value)
                .also {
                    if (value) {
                        it.set("data", variable.data)
                        it.set("expired", variable.expired)
                    }
                }.run(dataSource)
        }
    }

    override fun setGroup(player: Player, group: SerializedGroups.Group, value: Boolean) {
        val user = getUserId(player)
        if (user == -1L) {
            createUser(player).thenApply { userId ->
                tableGroup.insert(null, userId, group.name, value, group.expired).run(dataSource)
            }
        } else {
            tableGroup.update(Where.equals("user", user), Where.equals("group", group.name))
                .insertIfAbsent(null, user, group.name, value, group.expired)
                .set("value", value)
                .also {
                    if (value) {
                        it.set("expired", group.expired)
                    }
                }.run(dataSource)
        }
    }

    companion object {

        @PlayerContainer
        private val cacheUserId = ConcurrentHashMap<String, Long>()
    }
}