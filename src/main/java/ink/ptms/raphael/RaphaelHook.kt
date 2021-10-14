package ink.ptms.raphael

import ink.ptms.raphael.api.EventAction
import ink.ptms.raphael.api.EventType
import ink.ptms.raphael.api.RaphaelGroupEvent
import ink.ptms.raphael.api.RaphaelPlayerEvent
import ink.ptms.raphael.module.data.Database
import ink.ptms.raphael.module.data.SerializedGroups
import ink.ptms.raphael.module.data.SerializedPermissions
import ink.ptms.raphael.module.data.SerializedVariables
import net.milkbowl.vault.permission.Permission
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.configuration.createLocal
import java.util.concurrent.ConcurrentHashMap

/**
 * @Author sky
 * @Since 2019-11-27 21:06
 */
class RaphaelHook : Permission() {

    companion object {

        private val permissionsMap = ConcurrentHashMap<String, SerializedPermissions>()

        private val variablesMap = ConcurrentHashMap<String, SerializedVariables>()

        private val groupsMap = ConcurrentHashMap<String, SerializedGroups>()

        @SubscribeEvent
        fun e(e: PlayerQuitEvent) {
            permissionsMap.remove(e.player.name)
            variablesMap.remove(e.player.name)
            groupsMap.remove(e.player.name)
        }
    }

    fun data() = createLocal("data.yml")

    override fun getName(): String = "Raphael"

    override fun isEnabled(): Boolean = true

    override fun hasGroupSupport(): Boolean = true

    override fun hasSuperPermsCompat(): Boolean = true

    override fun getGroups(): Array<String> {
        return data().getConfigurationSection("Groups")?.getKeys(false)?.toTypedArray() ?: emptyArray()
    }

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

    @Deprecated("")
    override fun playerHas(world: String, player: String, permission: String): Boolean {
        return (Bukkit.getPlayerExact(player) ?: return false).hasPermission(permission)
    }

    @Deprecated("")
    override fun playerAdd(world: String, player: String, permission: String): Boolean {
        return playerAdd(Bukkit.getPlayerExact(player) ?: return false, permission, 0, "")
    }

    @Deprecated("")
    override fun playerRemove(world: String, player: String, permission: String): Boolean {
        return playerRemove(Bukkit.getPlayerExact(player) ?: return false, permission, "")
    }

    @Deprecated("")
    override fun playerAddGroup(world: String, player: String, group: String): Boolean {
        return playerAddGroup(Bukkit.getPlayerExact(player) ?: return false, group, 0, "")
    }

    @Deprecated("")
    override fun playerRemoveGroup(world: String, player: String, group: String): Boolean {
        return playerRemoveGroup(Bukkit.getPlayerExact(player) ?: return false, group, "")
    }

    @Deprecated("")
    override fun getPrimaryGroup(world: String, player: String): String {
        return playerGroups(Bukkit.getPlayerExact(player) ?: return "null").validGroups.firstOrNull()?.name ?: "null"
    }

    @Deprecated("")
    override fun getPlayerGroups(world: String, player: String): Array<String> {
        return playerGroups(Bukkit.getPlayerExact(player) ?: return emptyArray()).validGroups.map { it.name }.toTypedArray()
    }

    @Deprecated("", ReplaceWith("getPlayerGroups(world, player).contains(group)"))
    override fun playerInGroup(world: String, player: String, group: String): Boolean {
        return getPlayerGroups(world, player).contains(group)
    }

    /**
     * 获取玩家权限
     */
    fun playerPermissions(player: Player): SerializedPermissions {
        return permissionsMap.computeIfAbsent(player.name) {
            Database.INSTANCE.getPermissions(player)
        }
    }

    /**
     * 获取玩家变量
     */
    fun playerVariables(player: Player): SerializedVariables {
        return variablesMap.computeIfAbsent(player.name) {
            Database.INSTANCE.getVariables(player)
        }
    }

    /**
     * 获取玩家权限组
     */
    fun playerGroups(player: Player): SerializedGroups {
        return groupsMap.computeIfAbsent(player.name) {
            Database.INSTANCE.getGroups(player)
        }
    }

    /**
     * 玩家是否含有变量（检测键）
     */
    fun playerHasVariableKey(player: Player, variable: String): Boolean {
        return playerVariables(player).validVariables.any { it.name == variable }
    }

    /**
     * 玩家是否含有变量（检测值）
     */
    fun playerHasVariableValue(player: Player, value: String): Boolean {
        return playerVariables(player).validVariables.any { it.data == value }
    }

    /**
     * 玩家赋予变量
     */
    fun playerAddVariable(player: Player, variable: String, value: String, time: Long = 0L, reason: String = ""): Boolean {
        val event = RaphaelPlayerEvent(player, EventType.VARIABLE, EventAction.ADD, variable, value, time, reason)
        if (!event.call()) {
            return false
        }
        // 更新数据库
        Database.INSTANCE.setVariable(player, SerializedVariables.Variable(value, value, time), true)
        // 更新本地缓存
        variablesMap[player.name] = playerVariables(player).run {
            copy(variables = validVariables.toMutableList().also { list ->
                list.removeIf { v -> v.name == variable }
                list.add(SerializedVariables.Variable(value, value, time))
            })
        }
        return true
    }

    /**
     * 玩家移除变量
     */
    fun playerRemoveVariable(player: Player, variable: String, reason: String = ""): Boolean {
        val event = RaphaelPlayerEvent(player, EventType.VARIABLE, EventAction.REMOVE, variable, null, 0, reason)
        if (!event.call()) {
            return false
        }
        Database.INSTANCE.setVariable(player, SerializedVariables.Variable(variable), false)
        // 更新本地缓存
        variablesMap[player.name] = playerVariables(player).run {
            copy(variables = validVariables.toMutableList().also { list ->
                list.removeIf { v -> v.name == variable }
            })
        }
        return true
    }

    /**
     * 玩家赋予权限组
     */
    fun playerAddGroup(player: Player, group: String, time: Long = 0, reason: String = ""): Boolean {
        val event = RaphaelPlayerEvent(player, EventType.GROUP, EventAction.ADD, group, null, time, reason)
        if (!event.call()) {
            return false
        }
        Database.INSTANCE.setGroup(player, SerializedGroups.Group(group, time), true)
        // 更新本地缓存
        groupsMap[player.name] = playerGroups(player).run {
            copy(groups = validGroups.toMutableList().also { list ->
                list.removeIf { v -> v.name == group }
                list.add(SerializedGroups.Group(group, time))
            })
        }
        return true
    }

    /**
     * 玩家撤销权限组
     */
    fun playerRemoveGroup(player: Player, group: String, reason: String = ""): Boolean {
        val event = RaphaelPlayerEvent(player, EventType.GROUP, EventAction.REMOVE, group, null, 0, reason)
        if (!event.call()) {
            return false
        }
        Database.INSTANCE.setGroup(player, SerializedGroups.Group(group), false)
        // 更新本地缓存
        groupsMap[player.name] = playerGroups(player).run {
            copy(groups = validGroups.toMutableList().also { list ->
                list.removeIf { v -> v.name == group }
            })
        }
        return true
    }

    /**
     * 玩家赋予权限
     */
    fun playerAdd(player: Player, permission: String, time: Long = 0L, reason: String = ""): Boolean {
        val event = RaphaelPlayerEvent(player, EventType.PERMISSION, EventAction.ADD, permission, null, time, reason)
        if (!event.call()) {
            return false
        }
        Database.INSTANCE.setPermission(player, SerializedPermissions.Permission(permission, time), true)
        // 更新本地缓存
        permissionsMap[player.name] = playerPermissions(player).run {
            copy(permissions = validPermissions.toMutableList().also { list ->
                list.removeIf { v -> v.name == permission }
                list.add(SerializedPermissions.Permission(permission, time))
            })
        }
        return true
    }

    /**
     * 玩家移除权限
     */
    fun playerRemove(player: Player, permission: String, reason: String = ""): Boolean {
        val event = RaphaelPlayerEvent(player, EventType.PERMISSION, EventAction.REMOVE, permission, null, 0, reason)
        if (!event.call()) {
            return false
        }
        Database.INSTANCE.setPermission(player, SerializedPermissions.Permission(permission), false)
        // 更新本地缓存
        permissionsMap[player.name] = playerPermissions(player).run {
            copy(permissions = validPermissions.toMutableList().also { list ->
                list.removeIf { v -> v.name == permission }
            })
        }
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
        if (!event.call()) {
            return false
        }
        data().set("Groups.${event.group}.Permissions", data().getStringList("Groups.${event.group}.Permissions").toMutableSet().run {
            this.add(event.asPermission())
            this.toList()
        })
        return true
    }

    /**
     * 权限组移除权限
     */
    fun groupRemovePermission(group: String, permission: String, reason: String = ""): Boolean {
        val event = RaphaelGroupEvent(group, EventType.PERMISSION, EventAction.REMOVE, permission, null, reason)
        if (!event.call()) {
            return false
        }
        data().set("Groups.${event.group}.Permissions", data().getStringList("Groups.${event.group}.Permissions").toMutableSet().run {
            this.remove(event.asPermission())
            this.toList()
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
        if (!event.call()) {
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
        if (!event.call()) {
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
        if (!event.call()) {
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
        if (!event.call()) {
            return false
        }
        data().set("Groups.$group", null)
        return true;
    }
}