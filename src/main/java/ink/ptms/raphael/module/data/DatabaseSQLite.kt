package ink.ptms.raphael.module.data

import org.bukkit.entity.Player
import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.module.database.ColumnTypeSQLite
import taboolib.module.database.Table
import taboolib.module.database.getHost

/**
 * Chemdah
 * ink.ptms.chemdah.database.DatabaseSQL
 *
 * @author sky
 * @since 2021/3/5 3:51 下午
 */
class DatabaseSQLite : Database() {

    val host = newFile(getDataFolder(), "data.db").getHost()

    val tablePermission = Table("permission", host) {
        add("user") {
            type(ColumnTypeSQLite.TEXT, 36)
        }
        add("permission") {
            type(ColumnTypeSQLite.TEXT, 128)
        }
        add("value") {
            type(ColumnTypeSQLite.INTEGER)
        }
        add("expired") {
            type(ColumnTypeSQLite.INTEGER)
        }
    }

    val tableGroup = Table("group", host) {
        add("user") {
            type(ColumnTypeSQLite.TEXT, 36)
        }
        add("group") {
            type(ColumnTypeSQLite.TEXT, 128)
        }
        add("value") {
            type(ColumnTypeSQLite.INTEGER)
        }
        add("expired") {
            type(ColumnTypeSQLite.INTEGER)
        }
    }

    val tableVariable = Table("variable", host) {
        add("user") {
            type(ColumnTypeSQLite.TEXT, 36)
        }
        add("variable") {
            type(ColumnTypeSQLite.TEXT, 128)
        }
        add("data") {
            type(ColumnTypeSQLite.TEXT, 128)
        }
        add("value") {
            type(ColumnTypeSQLite.INTEGER)
        }
        add("expired") {
            type(ColumnTypeSQLite.INTEGER)
        }
    }

    val dataSource = host.createDataSource()

    init {
        tablePermission.createTable(dataSource)
        tableGroup.createTable(dataSource)
        tableVariable.createTable(dataSource)
    }

    fun getUserId(player: Player): String {
        return when (UserIndex.INSTANCE) {
            UserIndex.NAME -> player.name
            UserIndex.UUID -> player.uniqueId.toString()
        }
    }

    fun getUserPermissions(user: String): List<SerializedPermissions.Permission> {
        return tablePermission.select(dataSource) {
            where("user" eq user and ("value" eq 1))
            rows("permission", "expired")
        }.map {
            SerializedPermissions.Permission(getString("permission"), getLong("expired"))
        }
    }

    fun getUserVariables(user: String): List<SerializedVariables.Variable> {
        return tableVariable.select(dataSource) {
            where("user" eq user and ("value" eq 1))
            rows("variable", "data", "expired")
        }.map {
            SerializedVariables.Variable(getString("variable"), getString("data"), getLong("expired"))
        }
    }

    fun getUserGroups(user: String): List<SerializedGroups.Group> {
        return tablePermission.select(dataSource) {
            where("user" eq user and ("value" eq 1))
            rows("group", "expired")
        }.map {
            SerializedGroups.Group(getString("group"), getLong("expired"))
        }
    }

    override fun getPermissions(player: Player): SerializedPermissions {
        return SerializedPermissions(getUserPermissions(getUserId(player)))
    }

    override fun getVariables(player: Player): SerializedVariables {
        return SerializedVariables(getUserVariables(getUserId(player)))
    }

    override fun getGroups(player: Player): SerializedGroups {
        return SerializedGroups(getUserGroups(getUserId(player)))
    }

    override fun setPermission(player: Player, permission: SerializedPermissions.Permission, value: Boolean) {
        val user = getUserId(player)
        if (tablePermission.find(dataSource) { where("user" eq user and ("permission" eq permission.name)) }) {
            tablePermission.update(dataSource) {
                where("user" eq user and ("permission" eq permission.name))
                set("value", if (value) 1 else 0)
                if (value) {
                    set("expired", permission.expired)
                }
            }
        } else {
            tablePermission.insert(dataSource, "user", "permission", "value", "expired") {
                value(user, permission.name, if (value) 1 else 0, permission.expired)
            }
        }
    }

    override fun setVariable(player: Player, variable: SerializedVariables.Variable, value: Boolean) {
        val user = getUserId(player)
        if (tableVariable.find(dataSource) { where("user" eq user and ("variable" eq variable.name)) }) {
            tableVariable.update(dataSource) {
                where("user" eq user and ("variable" eq variable.name))
                set("value", if (value) 1 else 0)
                if (value) {
                    set("data", variable.data)
                    set("expired", variable.expired)
                }
            }
        } else {
            tableVariable.insert(dataSource, "user", "variable", "value", "data", "expired") {
                value(user, variable.name, if (value) 1 else 0, variable.data, variable.expired)
            }
        }
    }

    override fun setGroup(player: Player, group: SerializedGroups.Group, value: Boolean) {
        val user = getUserId(player)
        if (tableGroup.find(dataSource) { where("user" eq user and ("group" eq group.name)) }) {
            tableGroup.update(dataSource) {
                where("user" eq user and ("group" eq group.name))
                set("value", if (value) 1 else 0)
                if (value) {
                    set("expired", group.expired)
                }
            }
        } else {
            tableGroup.insert(dataSource, "user", "group", "value", "expired") {
                value(user, group.name, if (value) 1 else 0, group.expired)
            }
        }
    }
}