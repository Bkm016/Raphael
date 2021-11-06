package ink.ptms.raphael.module.data

import ink.ptms.raphael.Raphael
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
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

    fun updateUserTime(userId: Long) {
        tableUser.update(dataSource) {
            where("id" eq userId)
            set("time", Date())
        }
    }

    fun createUser(player: Player): CompletableFuture<Long> {
        val userId = CompletableFuture<Long>()
        CompletableFuture<Void>().also { future ->
            tableUser.insert(dataSource, "name", "uuid", "time") {
                value(player.name, player.uniqueId, Date())
                onFinally {
                    future.thenApply {
                        userId.complete(generatedKeys.getLong("id"))
                    }
                }
            }
        }.complete(null)
        return userId
    }

    fun getUserPermissions(user: Long): List<SerializedPermissions.Permission> {
        return tablePermission.select(dataSource) {
            where("user" eq user and ("value" eq true))
            rows("permission", "expired")
        }.map {
            SerializedPermissions.Permission(getString("permission"), getDate("expired").time)
        }
    }

    fun getUserVariables(user: Long): List<SerializedVariables.Variable> {
        return tableVariable.select(dataSource) {
            where("user" eq user and ("value" eq true))
            rows("variable", "data", "expired")
        }.map {
            SerializedVariables.Variable(getString("variable"), getString("data"), getDate("expired").time)
        }
    }

    fun getUserGroups(user: Long): List<SerializedGroups.Group> {
        return tablePermission.select(dataSource) {
            where("user" eq user and ("value" eq true))
            rows("group", "expired")
        }.map {
            SerializedGroups.Group(getString("group"), getDate("expired").time)
        }
    }

    override fun getPermissions(player: Player): SerializedPermissions {
        val user = getUserId(player)
        if (user == -1L) {
            return SerializedPermissions(emptyList())
        }
        submit(async = true) { updateUserTime(user) }
        return SerializedPermissions(getUserPermissions(user))
    }

    override fun getVariables(player: Player): SerializedVariables {
        val user = getUserId(player)
        if (user == -1L) {
            return SerializedVariables(emptyList())
        }
        submit(async = true) { updateUserTime(user) }
        return SerializedVariables(getUserVariables(user))
    }

    override fun getGroups(player: Player): SerializedGroups {
        val user = getUserId(player)
        if (user == -1L) {
            return SerializedGroups(emptyList())
        }
        submit(async = true) { updateUserTime(user) }
        return SerializedGroups(getUserGroups(user))
    }

    override fun setPermission(player: Player, permission: SerializedPermissions.Permission, value: Boolean) {
        val user = getUserId(player)
        if (user == -1L) {
            createUser(player).thenApply { userId ->
                tablePermission.insert(dataSource, "user", "permission", "value", "expired") {
                    value(userId, permission.name, value, permission.expired)
                }
            }
        } else {
            if (tablePermission.find(dataSource) { where("user" eq user and ("permission" eq permission.name)) }) {
                tablePermission.update(dataSource) {
                    where("user" eq user and ("permission" eq permission.name))
                    set("value", value)
                    if (value) {
                        set("expired", permission.expired)
                    }
                }
            } else {
                tablePermission.insert(dataSource, "user", "permission", "value", "expired") {
                    value(user, permission.name, value, permission.expired)
                }
            }
        }
    }

    override fun setVariable(player: Player, variable: SerializedVariables.Variable, value: Boolean) {
        val user = getUserId(player)
        if (user == -1L) {
            createUser(player).thenApply { userId ->
                tableVariable.insert(dataSource, "user", "variable", "value", "expired") {
                    value(userId, variable.name, variable.data, value, variable.expired)
                }
            }
        } else {
            if (tableVariable.find(dataSource) { where("user" eq user and ("variable" eq variable.name)) }) {
                tableVariable.update(dataSource) {
                    where("user" eq user and ("variable" eq variable.name))
                    set("value", value)
                    if (value) {
                        set("data", variable.data)
                        set("expired", variable.expired)
                    }
                }
            } else {
                tableVariable.insert(dataSource, "user", "variable", "value", "data", "expired") {
                    value(user, variable.name, value, variable.data, variable.expired)
                }
            }
        }
    }

    override fun setGroup(player: Player, group: SerializedGroups.Group, value: Boolean) {
        val user = getUserId(player)
        if (user == -1L) {
            createUser(player).thenApply { userId ->
                tableVariable.insert(dataSource, "user", "group", "value", "expired") {
                    value(userId, group.name, value, group.expired)
                }
            }
        } else {
            if (tableGroup.find(dataSource) { where("user" eq user and ("group" eq group.name)) }) {
                tableGroup.update(dataSource) {
                    where("user" eq user and ("group" eq group.name))
                    set("value", value)
                    if (value) {
                        set("expired", group.expired)
                    }
                }
            } else {
                tableGroup.insert(dataSource, "user", "group", "value", "expired") {
                    value(user, group.name, value, group.expired)
                }
            }
        }
    }

    companion object {

        private val cacheUserId = ConcurrentHashMap<String, Long>()

        @SubscribeEvent
        fun e(e: PlayerQuitEvent) {
            cacheUserId.remove(e.player.name)
        }
    }
}