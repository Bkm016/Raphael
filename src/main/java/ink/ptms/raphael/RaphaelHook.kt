package ink.ptms.raphael

import ink.ptms.raphael.api.EventAction
import ink.ptms.raphael.api.EventType
import ink.ptms.raphael.api.RaphaelGroupEvent
import ink.ptms.raphael.api.RaphaelPlayerEvent
import ink.ptms.raphael.module.permission.ExpiredList
import ink.ptms.raphael.module.permission.ExpiredValue
import io.izzel.taboolib.module.db.local.Local
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import kotlin.collections.ArrayList

/**
 * @Author sky
 * @Since 2019-11-27 21:06
 */
class RaphaelHook : Permission() {

    fun data(): FileConfiguration = Local.get().get("data.yml")

    override fun getName(): String = "Raphael"

    override fun isEnabled(): Boolean = true

    override fun hasGroupSupport(): Boolean = true

    override fun hasSuperPermsCompat(): Boolean = true

    override fun getGroups(): Array<String> {
        return data().getConfigurationSection("Groups")?.getKeys(false)?.toTypedArray() ?: emptyArray()
    }

    /**
     * Groups:
     *   default:
     *     Variables:
     *       prefix: test_1
     *       suffix: test_2
     *     Permissions:
     *     - raphael.command
     */
    @Deprecated("", ReplaceWith("groupHas(group, permission)"))
    override fun groupHas(world: String, group: String, permission: String): Boolean {
        return groupHasPermission(group, permission)
    }

    @Deprecated("", ReplaceWith("groupAdd(group, permission)"))
    override fun groupAdd(world: String, group: String, permission: String): Boolean {
        return groupAddPermission(group, permission)
    }

    @Deprecated("", ReplaceWith("groupRemove(group, permission)"))
    override fun groupRemove(world: String, group: String, permission: String): Boolean {
        return groupRemovePermission(group, permission)
    }

    /**
     * Groups:
     * - name: default
     *   expired: 2020-2-1 13:34:04
     *
     * Permissions:
     * - name: raphael.command
     *   expired: 2020-2-1 13:34:04
     *
     * Variables:
     * - name: prefix
     *   data: test
     *   expired: 2020-2-1 13:34:04
     */
    override fun playerHas(world: String, player: String, permission: String): Boolean {
        val p = Bukkit.getPlayerExact(player) ?: return false
        return p.hasPermission(permission)
    }

    override fun playerAdd(world: String, player: String, permission: String): Boolean {
        val playerExact = Bukkit.getPlayerExact(player) ?: return false
        return this.playerAdd(playerExact, permission, 0, "")
    }

    override fun playerRemove(world: String, player: String, permission: String): Boolean {
        val playerExact = Bukkit.getPlayerExact(player) ?: return false
        return this.playerRemove(playerExact, permission, "")
    }

    override fun playerAddGroup(world: String, player: String, group: String): Boolean {
        val playerExact = Bukkit.getPlayerExact(player) ?: return false
        return this.playerAddGroup(playerExact, group, 0, "")
    }

    override fun playerRemoveGroup(world: String, player: String, group: String): Boolean {
        val playerExact = Bukkit.getPlayerExact(player) ?: return false
        return this.playerRemoveGroup(playerExact, group, "")
    }

    override fun getPrimaryGroup(world: String, player: String): String {
        return playerGroups(Bukkit.getPlayerExact(player) ?: return "null").value.firstOrNull()?.name ?: "null"
    }

    override fun getPlayerGroups(world: String, player: String): Array<String> {
        return playerGroups(Bukkit.getPlayerExact(player)
                ?: return emptyArray()).value.filterNot { it.isExpired() }.map { it.name }.toTypedArray()
    }

    override fun playerInGroup(world: String, player: String, group: String): Boolean {
        return playerInGroup(Bukkit.getPlayerExact(player) ?: return false, group)
    }

    /**
     * 获取玩家权限
     */
    fun playerPermissions(player: Player): ExpiredList {
        val data = RaphaelAPI.database.getData(player)
        return ExpiredList(data.getList("Permissions") ?: ArrayList<Any>()).removeExpired()
    }

    /**
     * 获取玩家权限组
     */
    fun playerGroups(player: Player): ExpiredList {
        val data = RaphaelAPI.database.getData(player)
        return ExpiredList(data.getList("Groups") ?: ArrayList<Any>()).removeExpired()
    }

    /**
     * 获取玩家变量
     */
    fun playerVariables(player: Player): ExpiredList {
        val data = RaphaelAPI.database.getData(player)
        return ExpiredList(data.getList("Variables") ?: ArrayList<Any>()).removeExpired()
    }

    /**
     * 玩家是否含有变量（检测键）
     */
    fun playerHasVariableKey(player: Player, variable: String): Boolean {
        return playerVariables(player).value.any { it.name == variable }
    }

    /**
     * 玩家是否含有变量（检测值）
     */
    fun playerHasVariableValue(player: Player, value: String): Boolean {
        return playerVariables(player).value.any { it.data == value }
    }

    /**
     * 玩家赋予变量
     */
    fun playerAddVariable(player: Player, variable: String, value: String, time: Long = 0L, reason: String = ""): Boolean {
        val data = RaphaelAPI.database.getData(player)
        val event = RaphaelPlayerEvent(player, EventType.VARIABLE, EventAction.ADD, variable, value, time, reason)
        if (event.call().isCancelled) {
            return false
        }
        data.set("Variables", playerVariables(player).run {
            val first = this.value.firstOrNull { it.name == event.asVariableKey() }
            if (first != null) {
                first.expired += event.time
            } else {
                this.value.add(ExpiredValue(event.asVariableKey(), event.asVariableValue().toString()).run {
                    if (event.time > 0) {
                        this.expired = System.currentTimeMillis() + event.time
                    }
                    this
                })
            }
            this.translate()
        })
        return true
    }

    /**
     * 玩家移除变量
     */
    fun playerRemoveVariable(player: Player, variable: String, reason: String = ""): Boolean {
        val data = RaphaelAPI.database.getData(player)
        val event = RaphaelPlayerEvent(player, EventType.VARIABLE, EventAction.REMOVE, variable, null, 0, reason)
        if (event.call().isCancelled) {
            return false
        }
        data.set("Variables", playerVariables(player).run {
            this.value.removeIf { it.name == event.asVariableKey() }
            this.translate()
        })
        return true
    }

    /**
     * 玩家赋予权限组
     */
    fun playerAddGroup(player: Player, group: String, time: Long = 0, reason: String = ""): Boolean {
        val data = RaphaelAPI.database.getData(player)
        val event = RaphaelPlayerEvent(player, EventType.GROUP, EventAction.ADD, group, null, time, reason)
        if (event.call().isCancelled) {
            return false
        }
        data.set("Groups", playerGroups(player).run {
            val first = this.value.firstOrNull { it.name == event.asGroup() }
            if (first != null) {
                first.expired += event.time
            } else {
                this.value.add(ExpiredValue(event.asGroup()).run {
                    if (event.time > 0) {
                        this.expired = System.currentTimeMillis() + event.time
                    }
                    this
                })
            }
            this.translate()
        })
        return true
    }

    /**
     * 玩家撤销权限组
     */
    fun playerRemoveGroup(player: Player, group: String, reason: String = ""): Boolean {
        val data = RaphaelAPI.database.getData(player)
        val event = RaphaelPlayerEvent(player, EventType.GROUP, EventAction.REMOVE, group, null, 0, reason)
        if (event.call().isCancelled) {
            return false
        }
        data.set("Groups", playerGroups(player).run {
            this.value.removeIf { it.name == event.asGroup() }
            this.translate()
        })
        return true
    }

    /**
     * 玩家赋予权限
     */
    fun playerAdd(player: Player, permission: String, time: Long = 0L, reason: String = ""): Boolean {
        val data = RaphaelAPI.database.getData(player)
        val event = RaphaelPlayerEvent(player, EventType.PERMISSION, EventAction.ADD, permission, null, time, reason)
        if (event.call().isCancelled) {
            return false
        }
        data.set("Permissions", playerGroups(player).run {
            val first = this.value.firstOrNull { it.name == event.asPermission() }
            if (first != null) {
                first.expired += event.time
            } else {
                this.value.add(ExpiredValue(event.asPermission()).run {
                    if (event.time > 0) {
                        this.expired = System.currentTimeMillis() + event.time
                    }
                    this
                })
            }
            this.translate()
        })
        return true
    }

    /**
     * 玩家移除权限
     */
    fun playerRemove(player: Player, permission: String, reason: String = ""): Boolean {
        val data = RaphaelAPI.database.getData(player)
        val event = RaphaelPlayerEvent(player, EventType.PERMISSION, EventAction.REMOVE, permission, null, 0, reason)
        if (event.call().isCancelled) {
            return false
        }
        data.set("Permissions", playerPermissions(player).run {
            this.value.removeIf { it.name == event.asPermission() }
            this.translate()
        })
        return true
    }

    /**
     * 获取权限组权限
     */
    fun groupPermissions(group: String): List<String> {
        return data().getStringList("Groups.$group.Permissions")
    }

    /**
     * 权限组持有权限
     */
    fun groupHasPermission(group: String, permission: String): Boolean {
        return data().getStringList("Groups.$group.Permissions").contains(permission)
    }

    /**
     * 权限组添加权限
     */
    fun groupAddPermission(group: String, permission: String, reason: String = ""): Boolean {
        val event = RaphaelGroupEvent(group, EventType.PERMISSION, EventAction.ADD, permission, null, reason)
        if (event.call().isCancelled) {
            return false
        }
        data().set("Groups.${event.group}.Permissions", data().getStringList("Groups.${event.group}.Permissions").toMutableSet().run {
            this.add(event.asPermission())
            this
        })
        return true
    }

    /**
     * 权限组移除权限
     */
    fun groupRemovePermission(group: String, permission: String, reason: String = ""): Boolean {
        val event = RaphaelGroupEvent(group, EventType.PERMISSION, EventAction.REMOVE, permission, null, reason)
        if (event.call().isCancelled) {
            return false
        }
        data().set("Groups.${event.group}.Permissions", data().getStringList("Groups.${event.group}.Permissions").toMutableSet().run {
            this.remove(event.asPermission())
            this
        })
        return true
    }

    /**
     * 获取权限组变量
     */
    fun groupVariables(group: String): Map<String, String> {
        return data().getConfigurationSection("Groups.$group.Variables")?.getValues(false)?.map { Pair(it.key, it.value.toString()) }?.toMap() ?: emptyMap()
    }

    /**
     * 权限组是否含有变量（检测键）
     */
    fun groupHasVariableKey(group: String, variable: String): Boolean {
        return data().contains("Groups.$group.Variable.$variable")
    }

    /**
     * 权限组是否含有变量（检测值）
     */
    fun groupHasVariableValue(group: String, value: String): Boolean {
        return data().getConfigurationSection("Groups.$group.Variable")?.getValues(false)?.any { it.value == value } == true
    }

    /**
     * 权限组赋予变量
     */
    fun groupAddVariable(group: String, variable: String, value: String, reason: String = ""): Boolean {
        val event = RaphaelGroupEvent(group, EventType.VARIABLE, EventAction.ADD, variable, value, reason)
        if (event.call().isCancelled) {
            return false
        }
        data().set("Groups.${event.group}.Variable.${event.asVariableKey()}", event.asVariableValue())
        return true
    }

    /**
     * 权限组移除变量
     */
    fun groupRemoveVariable(group: String, variable: String, reason: String = ""): Boolean {
        val event = RaphaelGroupEvent(group, EventType.VARIABLE, EventAction.REMOVE, variable, null, reason)
        if (event.call().isCancelled) {
            return false
        }
        data().set("Groups.${event.group}.Variable.${event.asVariableKey()}", null)
        return true
    }

    /**
     * 创建权限组
     */
    fun groupCreate(group: String, reason: String = ""): Boolean {
        val event = RaphaelGroupEvent(group, EventType.GROUP, EventAction.CREATE, group, null, reason)
        if (event.call().isCancelled) {
            return false
        }
        data().createSection("Groups.$group")
        return true
    }

    /**
     * 删除权限组
     */
    fun groupDelete(group: String, reason: String = ""): Boolean {
        val event = RaphaelGroupEvent(group, EventType.GROUP, EventAction.DELETE, group, null, reason)
        if (event.call().isCancelled) {
            return false
        }
        data().set("Groups.$group", null)
        return true;
    }
}