package ink.ptms.raphael.module.command

import ink.ptms.raphael.RaphaelAPI
import ink.ptms.raphael.module.data.SerializedGroups
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.command.command
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common5.util.parseMillis
import taboolib.module.chat.TellrawJson

object CommandUser : CommandHandle() {

    @Awake(LifeCycle.ENABLE)
    fun init() {
        // RaphaelUserAddGroup [player] [group] <time> <reason>
        command(name = "RaphaelUserAddGroup", aliases = listOf("ruadd", "ruaddg", "manuadd", "manuaddg"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, playerName: String, group: String, time: String = "0", reason: String = "command by ${sender.name}") {
                val player = Bukkit.getPlayerExact(playerName)
                if (player == null) {
                    notify(sender, "Player \"&f${player}&7\" Not Found.")
                    return
                }
                if (!RaphaelAPI.permission.groups.contains(group)) {
                    notify(sender, "Group \"&f${group}&7\" Not Found.")
                    return
                }
                if (RaphaelAPI.permission.playerAddGroup(player, group, time.parseMillis(), reason)) {
                    notify(sender, "Add \"&f${group}&7\" to \"&f${player.name}&7\"'s Groups. &8(time: ${time}, reason: ${reason})")
                } else {
                    notify(sender, "Failed.")
                }
            }
            // player
            dynamic {
                // group
                dynamic {
                    // time
                    dynamic(optional = true) {
                        // reason
                        dynamic(optional = true) {
                            execute<CommandSender> { sender, context, reason ->
                                invoke(sender, context.argument(-3)!!, context.argument(-2)!!, context.argument(-1)!!, reason)
                            }
                        }
                        execute<CommandSender> { sender, context, time ->
                            invoke(sender, context.argument(-2)!!, context.argument(-1)!!, time)
                        }
                    }
                    execute<CommandSender> { sender, context, group ->
                        invoke(sender, context.argument(-1)!!, group)
                    }
                }
            }
        }
        // RaphaelUserDelGroup [player] [group] <reason>
        command(name = "RaphaelUserDelGroup", aliases = listOf("rudel", "rudelg", "manudel", "manudelg"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, playerName: String, group: String, reason: String = "command by ${sender.name}") {
                val player = Bukkit.getPlayerExact(playerName)
                if (player == null) {
                    notify(sender, "Player \"&f${player}&7\" Not Found.")
                    return
                }
                if (!RaphaelAPI.permission.groups.contains(group)) {
                    notify(sender, "Group \"&f${group}&7\" Not Found.")
                    return
                }
                if (RaphaelAPI.permission.playerRemoveGroup(player, group, reason)) {
                    notify(sender, "Remove \"&f${group}&7\" from \"&f${player.name}&7\"'s Groups. &8(reason: ${reason})")
                } else {
                    notify(sender, "Failed.")
                }
            }
            // player
            dynamic {
                // group
                dynamic {
                    // reason
                    dynamic(optional = true) {
                        execute<CommandSender> { sender, context, reason ->
                            invoke(sender, context.argument(-2)!!, context.argument(-1)!!, reason)
                        }
                    }
                    execute<CommandSender> { sender, context, group ->
                        invoke(sender, context.argument(-1)!!, group)
                    }
                }
            }
        }
        // RaphaelUserAddVariable [player] [key] [value(space=\s)] <time> <reason>
        command(name = "RaphaelUserAddVariable", aliases = listOf("ruaddv", "manuaddv"), permission = "raphael.command") {
            fun invoke(
                sender: CommandSender,
                playerName: String,
                key: String,
                value: String,
                time: String = "0",
                reason: String = "command by ${sender.name}",
            ) {
                val player = Bukkit.getPlayerExact(playerName)
                if (player == null) {
                    notify(sender, "Player \"&f${player}&7\" Not Found.")
                    return
                }
                if (RaphaelAPI.permission.playerAddVariable(player, key, value.replace("\\s", " "), time.parseMillis(), reason)) {
                    notify(
                        sender,
                        "Add \"&f${key} = ${value.replace("\\s", " ")}&7\" to \"&f${player.name}&7\"'s Variables. &8(time: ${time}, reason: ${reason})"
                    )
                } else {
                    notify(sender, "Failed.")
                }
            }
            // player
            dynamic {
                // key
                dynamic {
                    // value
                    dynamic {
                        // time
                        dynamic(optional = true) {
                            // reason
                            dynamic(optional = true) {
                                execute<CommandSender> { sender, context, reason ->
                                    invoke(sender, context.argument(-4)!!, context.argument(-3)!!, context.argument(-2)!!, context.argument(-1)!!, reason)
                                }
                            }
                            execute<CommandSender> { sender, context, time ->
                                invoke(sender, context.argument(-3)!!, context.argument(-2)!!, context.argument(-1)!!, time)
                            }
                        }
                        execute<CommandSender> { sender, context, value ->
                            invoke(sender, context.argument(-2)!!, context.argument(-1)!!, value)
                        }
                    }
                }
            }
        }
        // RaphaelUserDelVariable [player] [key] <reason>
        command(name = "RaphaelUserDelVariable", aliases = listOf("rudelv", "manudelv"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, playerName: String, key: String, reason: String = "command by ${sender.name}") {
                val player = Bukkit.getPlayerExact(playerName)
                if (player == null) {
                    notify(sender, "Player \"&f${player}&7\" Not Found.")
                    return
                }
                if (RaphaelAPI.permission.playerRemoveVariable(player, key, reason)) {
                    notify(sender, "Remove \"&f${key}&7\" from \"&f${player.name}&7\"'s Variables. &8(reason: ${reason})")
                } else {
                    notify(sender, "Failed.")
                }
            }
            // player
            dynamic {
                // key
                dynamic {
                    // reason
                    dynamic(optional = true) {
                        execute<CommandSender> { sender, context, reason ->
                            invoke(sender, context.argument(-2)!!, context.argument(-1)!!, reason)
                        }
                    }
                    execute<CommandSender> { sender, context, key ->
                        invoke(sender, context.argument(-1)!!, key)
                    }
                }
            }
        }
        // RaphaelUserAddPermission [player] [permission] <time> <reason>
        command(name = "RaphaelUserAddPermission", aliases = listOf("ruaddp", "manuaddp"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, playerName: String, permission: String, time: String = "0", reason: String = "command by ${sender.name}") {
                val player = Bukkit.getPlayerExact(playerName)
                if (player == null) {
                    notify(sender, "Player \"&f${player}&7\" Not Found.")
                    return
                }
                if (RaphaelAPI.permission.playerAdd(player, permission, time.parseMillis(), reason)) {
                    notify(sender, "Add \"&f${permission}&7\" to \"&f${player.name}&7\"'s Permissions. &8(time: ${time}, reason: ${reason})")
                } else {
                    notify(sender, "Failed.")
                }
            }
            // player
            dynamic {
                // permission
                dynamic {
                    // time
                    dynamic(optional = true) {
                        // reason    
                        dynamic(optional = true) {
                            execute<CommandSender> { sender, context, reason ->
                                invoke(sender, context.argument(-3)!!, context.argument(-2)!!, context.argument(-1)!!, reason)
                            }
                        }
                        execute<CommandSender> { sender, context, time ->
                            invoke(sender, context.argument(-2)!!, context.argument(-1)!!, time)
                        }
                    }
                    execute<CommandSender> { sender, context, permission ->
                        invoke(sender, context.argument(-1)!!, permission)
                    }
                }
            }
        }
        // RaphaelUserDelPermission [player] [permission] <reason>
        command(name = "RaphaelUserDelPermission", aliases = listOf("rudelp", "manudelp"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, playerName: String, permission: String, reason: String = "command by ${sender.name}") {
                val player = Bukkit.getPlayerExact(playerName)
                if (player == null) {
                    notify(sender, "Player \"&f${player}&7\" Not Found.")
                    return
                }
                if (RaphaelAPI.permission.playerRemove(player, permission, reason)) {
                    notify(sender, "Remove \"&f${permission}&7\" from \"&f${player.name}&7\"'s Permissions. &8(reason: ${reason})")
                } else {
                    notify(sender, "Failed.")
                }
            }
            // player
            dynamic {
                // permission
                dynamic {
                    // reason
                    dynamic(optional = true) {
                        execute<CommandSender> { sender, context, reason ->
                            invoke(sender, context.argument(-2)!!, context.argument(-1)!!, reason)
                        }
                    }
                    execute<CommandSender> { sender, context, permission ->
                        invoke(sender, context.argument(-1)!!, permission)
                    }
                }
            }
        }
        // RaphaelUserClearPermission [player] <reason>
        command(name = "RaphaelUserClearPermission", aliases = listOf("ruclearp", "manuclearp"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, playerName: String, reason: String = "command by ${sender.name}") {
                val player = Bukkit.getPlayerExact(playerName)
                if (player == null) {
                    notify(sender, "Player \"&f${player}&7\" Not Found.")
                    return
                }
                RaphaelAPI.permission.playerPermissions(player).validPermissions.forEach { permission ->
                    if (RaphaelAPI.permission.playerRemove(player, permission.name, reason)) {
                        notify(sender, "Remove \"&f${permission.name}&7\" from \"&f${player.name}&7\"'s Permissions. &8(reason: ${reason})")
                    }
                }
                notify(sender, "Done.")
            }
            // player
            dynamic {
                // reason
                dynamic(optional = true) {
                    execute<CommandSender> { sender, context, reason ->
                        invoke(sender, context.argument(-1)!!, reason)
                    }
                }
                execute<CommandSender> { sender, _, player ->
                    invoke(sender, player)
                }
            }
        }
        // RaphaelUserClearVariables [player] <reason>
        command(name = "RaphaelUserClearVariables", aliases = listOf(), permission = "raphael.command") {
            fun invoke(sender: CommandSender, playerName: String, reason: String = "command by ${sender.name}") {
                val player = Bukkit.getPlayerExact(playerName)
                if (player == null) {
                    notify(sender, "Player \"&f${player}&7\" Not Found.")
                    return
                }
                RaphaelAPI.permission.playerVariables(player).variables.forEach { v ->
                    if (RaphaelAPI.permission.playerRemoveVariable(player, v.name, reason)) {
                        notify(sender, "Remove \"&f${v.name}&7\" from \"&f${player.name}&7\"'s Variables. &8(reason: ${reason})")
                    }
                }
                notify(sender, "Done.")
            }
            // player
            dynamic {
                // reason
                dynamic(optional = true) {
                    execute<CommandSender> { sender, context, reason ->
                        invoke(sender, context.argument(-1)!!, reason)
                    }
                }
                execute<CommandSender> { sender, _, player ->
                    invoke(sender, player)
                }
            }
        }
        // RaphaelUserClearGroups [player] <reason>
        command(name = "RaphaelUserClearGroups", aliases = listOf("ruclearg", "manuclearg"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, playerName: String, reason: String = "command by ${sender.name}") {
                val player = Bukkit.getPlayerExact(playerName)
                if (player == null) {
                    notify(sender, "Player \"&f${player}&7\" Not Found.")
                    return
                }
                RaphaelAPI.permission.playerGroups(player).validGroups.forEach { v ->
                    if (RaphaelAPI.permission.playerRemoveGroup(player, v.name, reason)) {
                        notify(sender, "Remove \"&f${v.name}&7\" from \"&f${player.name}&7\"'s Groups. &8(reason: ${reason})")
                    }
                }
                notify(sender, "Done.")
            }
            // player
            dynamic {
                // reason
                dynamic(optional = true) {
                    execute<CommandSender> { sender, context, reason ->
                        invoke(sender, context.argument(-1)!!, reason)
                    }
                }
                execute<CommandSender> { sender, _, player ->
                    invoke(sender, player)
                }
            }
        }
        // RaphaelUserCheckPermission [player] [permission]
        command(name = "RaphaelUserCheckPermission", aliases = listOf("rucheckp", "manucheckp"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, playerName: String, permission: String, reason: String = "command by ${sender.name}") {
                val player = Bukkit.getPlayerExact(playerName)
                if (player == null) {
                    notify(sender, "Player \"&f${player}&7\" Not Found.")
                    return
                }
                if (player.hasPermission(permission)) {
                    notify(sender, "Check \"&f${permission}&7\" from \"&f${player.name}&7\"'s Permissions. &8(result: &aTRUE&8)")
                } else {
                    notify(sender, "Check \"&f${permission}&7\" from \"&f${player.name}&7\"'s Permissions. &8(result: &cFALSE&8)")
                }
            }
            // player
            dynamic {
                // permission
                dynamic {
                    execute<CommandSender> { sender, context, permission ->
                        invoke(sender, context.argument(-1)!!, permission)
                    }
                }
            }
        }
        // RaphaelUserListPermission [player]
        command(name = "RaphaelUserListPermission", aliases = listOf("rulistp", "manulistp"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, playerName: String) {
                val player = Bukkit.getPlayerExact(playerName)
                if (player == null) {
                    notify(sender, "Player \"&f${player}&7\" Not Found.")
                    return
                }
                notify(sender, "Player \"&f${player.name}&7\"'s Permissions:")
                val list = RaphaelAPI.permission.playerPermissions(player).validPermissions.toMutableList()
                if (list.isEmpty()) {
                    notify(sender, "- §fNULL")
                } else {
                    list.forEach { permission ->
                        TellrawJson()
                            .append("§c[Raphael] §7- §f")
                            .append(permission.name).hoverText("§nCOPY").suggestCommand(permission.name)
                            .run {
                                if (permission.expired == 0L) {
                                    append(" §8(expired: §f-§8)")
                                } else {
                                    append(" §8(expired: §f${format.format(permission.expired)}§8)")
                                }
                                sendTo(adaptCommandSender(sender))
                            }
                    }
                }
            }
            // player
            dynamic {
                execute<CommandSender> { sender, _, player ->
                    invoke(sender, player)
                }
            }
        }
        // RaphaelUserListVariables [player]
        command(name = "RaphaelUserListVariables", aliases = listOf("rulistv", "manulistv"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, playerName: String) {
                val player = Bukkit.getPlayerExact(playerName)
                if (player == null) {
                    notify(sender, "Player \"&f${player}&7\" Not Found.")
                    return
                }
                notify(sender, "Player \"&f${player.name}&7\"'s Variables:")
                val list = RaphaelAPI.permission.playerVariables(player).validVariables
                if (list.isEmpty()) {
                    notify(sender, "- §fNULL")
                } else {
                    list.forEach { v ->
                        TellrawJson()
                            .append("§c[Raphael] §7- §f")
                            .append(v.name).hoverText("§nCOPY").suggestCommand(v.name)
                            .append("§8: ")
                            .append(v.data).hoverText("§nCOPY").suggestCommand(v.data)
                            .run {
                                if (v.expired == 0L) {
                                    append(" §8(expired: §f-§8)")
                                } else {
                                    append(" §8(expired: §f${format.format(v.expired)}§8)")
                                }
                                sendTo(adaptCommandSender(sender))
                            }
                    }
                }
            }
            // player
            dynamic {
                execute<CommandSender> { sender, _, player ->
                    invoke(sender, player)
                }
            }
        }
        // RaphaelUserListGroups [player]
        command(name = "RaphaelUserListGroups", aliases = listOf("rulistg", "manulistg"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, playerName: String) {
                val player = Bukkit.getPlayerExact(playerName)
                if (player == null) {
                    notify(sender, "Player \"&f${player}&7\" Not Found.")
                    return
                }
                notify(sender, "Player \"&f${player.name}&7\"'s Groups:")
                val list = RaphaelAPI.permission.playerGroups(player).validGroups.toMutableList()
                if (RaphaelAPI.permission.groupPermissions("default").isNotEmpty()) {
                    list.add(SerializedGroups.Group("default"))
                }
                if (list.isEmpty()) {
                    notify(sender, "- §fNULL")
                } else {
                    list.forEach { group ->
                        TellrawJson()
                            .append("§c[Raphael] §7- §f")
                            .append(group.name).hoverText("§nCOPY").suggestCommand(group.name)
                            .run {
                                if (group.expired == 0L) {
                                    append(" §8(expired: §f-§8)")
                                } else {
                                    append(" §8(expired: §f${format.format(group.expired)}§8)")
                                }
                                sendTo(adaptCommandSender(sender))
                            }
                    }
                }
            }
            // player
            dynamic {
                execute<CommandSender> { sender, _, player ->
                    invoke(sender, player)
                }
            }
        }
        // RaphaelUserInfo [player]
        command(name = "RaphaelUserInfo", aliases = listOf("ruinfo", "ruwhois", "manuwhois"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, playerName: String) {
                val player = Bukkit.getPlayerExact(playerName)
                if (player == null) {
                    notify(sender, "Player \"&f${player}&7\" Not Found.")
                    return
                }
                notify(sender, "Information:")
                val adapt = adaptCommandSender(sender)
                adapt.performCommand("rulistp ${player.name}")
                adapt.performCommand("rulistv ${player.name}")
                adapt.performCommand("rulistg ${player.name}")
            }
            // player
            dynamic {
                execute<CommandSender> { sender, _, player ->
                    invoke(sender, player)
                }
            }
        }
    }
}
