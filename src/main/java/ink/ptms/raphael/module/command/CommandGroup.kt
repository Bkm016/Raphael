package ink.ptms.raphael.module.command

import ink.ptms.raphael.RaphaelAPI
import org.bukkit.command.CommandSender
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.command.command
import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.chat.TellrawJson

object CommandGroup : CommandHandle() {

    @Awake(LifeCycle.ENABLE)
    fun init() {
        // RaphaelGroupAdd [Group]
        command("RaphaelGroupAdd", aliases = listOf("rgadd", "mangadd"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, group: String, reason: String = "command by ${sender.name}") {
                if (RaphaelAPI.permission.groups.contains(group)) {
                    notify(sender, "Group \"&f${group}&7\" Exists.")
                    return
                }
                if (RaphaelAPI.permission.groupCreate(group, reason)) {
                    notify(sender, "Group \"&f${group}&7\" Created. &8(reason: $reason)")
                } else {
                    notify(sender, "Failed.")
                }
            }
            // group
            dynamic {
                // reason
                dynamic(optional = true) {
                    execute<CommandSender> { sender, context, reason ->
                        invoke(sender, context.argument(-1), reason)
                    }
                }
                execute<CommandSender> { sender, context, group ->
                    invoke(sender, group)
                }
            }
        }
        // RaphaelGroupDel [group] <reason>
        command(name = "RaphaelGroupDel", aliases = listOf("rgdel", "mangdel"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, group: String, reason: String = "command by ${sender.name}") {
                if (!RaphaelAPI.permission.groups.contains(group)) {
                    notify(sender, "Group \"&f${group}&7\" Not Found.")
                    return
                }
                if (RaphaelAPI.permission.groupDelete(group, reason)) {
                    notify(sender, "Group \"&f${group}&7\" Deleted. &8(reason: $reason)")
                } else {
                    notify(sender, "Failed.")
                }
            }
            // group
            dynamic {
                suggestion<CommandSender> { _, _ -> RaphaelAPI.permission.groups.toList() }
                // reason
                dynamic(optional = true) {
                    execute<CommandSender> { sender, context, reason ->
                        invoke(sender, context.argument(-1), reason)
                    }
                }
                execute<CommandSender> { sender, context, group ->
                    invoke(sender, group)
                }
            }
        }
        // RaphaelGroupAddVariable [group] [key] [value(space=\s)] <reason>
        command(name = "RaphaelGroupAddVariable", aliases = listOf("rgaddv", "mangaddv"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, group: String, key: String, value: String, reason: String = "command by ${sender.name}") {
                if (!RaphaelAPI.permission.groups.contains(group)) {
                    notify(sender, "Group \"&f${group}&7\" Not Found.")
                    return
                }
                if (RaphaelAPI.permission.groupAddVariable(group, key, value.replace("\\s", " "), reason)) {
                    notify(sender, "Add \"&f${key} = ${value.replace("\\s", " ")}&7\" to \"&f${group}&7\"'s Variables. &8(reason: ${reason})")
                } else {
                    notify(sender, "Failed.")
                }
            }
            // group
            dynamic {
                suggestion<CommandSender> { _, _ -> RaphaelAPI.permission.groups.toList() }
                // key
                dynamic {
                    // value
                    dynamic {
                        // reason
                        dynamic(optional = true) {
                            execute<CommandSender> { sender, context, reason ->
                                invoke(sender, context.argument(-3), context.argument(-2), context.argument(-1), reason)
                            }
                        }
                        execute<CommandSender> { sender, context, value ->
                            invoke(sender, context.argument(-2), context.argument(-1), value)
                        }
                    }
                }
            }
        }
        // RaphaelGroupDelVariable [group] [key] <reason>
        command(name = "RaphaelGroupDelVariable", aliases = listOf("rgdelv", "mangdelv"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, group: String, key: String, reason: String = "command by ${sender.name}") {
                if (!RaphaelAPI.permission.groups.contains(group)) {
                    notify(sender, "Group \"&f${group}&7\" Not Found.")
                    return
                }
                if (RaphaelAPI.permission.groupRemoveVariable(group, key, reason)) {
                    notify(sender, "Remove \"&f${key}&7\" from \"&f${group}&7\"'s Variables. &8(reason: ${reason})")
                } else {
                    notify(sender, "Failed.")
                }
            }
            // group
            dynamic {
                suggestion<CommandSender> { _, _ -> RaphaelAPI.permission.groups.toList() }
                // key
                dynamic {
                    // reason
                    dynamic(optional = true) {
                        execute<CommandSender> { sender, context, reason ->
                            invoke(sender, context.argument(-2), context.argument(-1), reason)
                        }
                    }
                    execute<CommandSender> { sender, context, key ->
                        invoke(sender, context.argument(-1), key)
                    }
                }
            }
        }
        // RaphaelGroupAddPermission [group] [key] <reason>
        command(name = "RaphaelGroupAddPermission", aliases = listOf("rgaddp", "mangaddp"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, group: String, key: String, reason: String = "command by ${sender.name}") {
                if (!RaphaelAPI.permission.groups.contains(group)) {
                    notify(sender, "Group \"&f${group}&7\" Not Found.")
                    return
                }
                if (RaphaelAPI.permission.groupAddPermission(group, key, reason)) {
                    notify(sender, "Add \"&f${key}&7\" to \"&f${group}&7\"'s Permissions. &8(reason: ${reason})")
                } else {
                    notify(sender, "Failed.")
                }
            }
            // group
            dynamic {
                suggestion<CommandSender> { _, _ -> RaphaelAPI.permission.groups.toList() }
                // key
                dynamic {
                    // reason
                    dynamic(optional = true) {
                        execute<CommandSender> { sender, context, reason ->
                            invoke(sender, context.argument(-2), context.argument(-1), reason)
                        }
                    }
                    execute<CommandSender> { sender, context, key ->
                        invoke(sender, context.argument(-1), key)
                    }
                }
            }
        }
        // RaphaelGroupDelPermission [group] [key] <reason>
        command(name = "RaphaelGroupDelPermission", aliases = listOf("rgdelp", "mangdelp"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, group: String, key: String, reason: String = "command by ${sender.name}") {
                if (!RaphaelAPI.permission.groups.contains(group)) {
                    notify(sender, "Group \"&f${group}&7\" Not Found.")
                    return
                }
                if (RaphaelAPI.permission.groupRemovePermission(group, key, reason)) {
                    notify(sender, "Remove \"&f${key}&7\" from \"&f${group}&7\"'s Permissions. &8(reason: ${reason})")
                } else {
                    notify(sender, "Failed.")
                }
            }
            // group
            dynamic {
                suggestion<CommandSender> { _, _ -> RaphaelAPI.permission.groups.toList() }
                // key
                dynamic {
                    // reason
                    dynamic(optional = true) {
                        execute<CommandSender> { sender, context, reason ->
                            invoke(sender, context.argument(-2), context.argument(-1), reason)
                        }
                    }
                    execute<CommandSender> { sender, context, key ->
                        invoke(sender, context.argument(-1), key)
                    }
                }
            }
        }
        // RaphaelGroupClearPermission [group] <reason>
        command(name = "RaphaelGroupClearPermission", aliases = listOf("rgclearp", "mangclearp"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, group: String, reason: String = "command by ${sender.name}") {
                if (!RaphaelAPI.permission.groups.contains(group)) {
                    notify(sender, "Group \"&f${group}&7\" Not Found.")
                    return
                }
                RaphaelAPI.permission.groupPermissions(group).forEach { permission ->
                    if (RaphaelAPI.permission.groupRemovePermission(group, permission, reason)) {
                        notify(sender, "Remove \"&f$permission&7\" from \"&f${group}&7\"'s Permissions. &8(reason: ${reason})")
                    }
                }
                notify(sender, "Done.")
            }
            // group
            dynamic {
                // reason
                dynamic(optional = true) {
                    execute<CommandSender> { sender, context, reason ->
                        invoke(sender, context.argument(-1), reason)
                    }
                }
                execute<CommandSender> { sender, context, group ->
                    invoke(sender, group)
                }
            }
        }
        // RaphaelGroupClearVariables [group] <reason>
        command(name = "RaphaelGroupClearVariables", aliases = listOf(), permission = "raphael.command") {
            fun invoke(sender: CommandSender, group: String, reason: String = "command by ${sender.name}") {
                if (!RaphaelAPI.permission.groups.contains(group)) {
                    notify(sender, "Group \"&f${group}&7\" Not Found.")
                    return
                }
                RaphaelAPI.permission.groupVariables(group).forEach { (k, _) ->
                    if (RaphaelAPI.permission.groupRemoveVariable(group, k, reason)) {
                        notify(sender, "Remove \"&f$k&7\" from \"&f${group}&7\"'s Variables. &8(reason: ${reason})")
                    }
                }
                notify(sender, "Done.")
            }
            // group
            dynamic {
                suggestion<CommandSender> { _, _ -> RaphaelAPI.permission.groups.toList() }
                // reason
                dynamic(optional = true) {
                    execute<CommandSender> { sender, context, reason ->
                        invoke(sender, context.argument(-1), reason)
                    }
                }
                execute<CommandSender> { sender, context, group ->
                    invoke(sender, group)
                }
            }
        }
        // RaphaelGroupListPermission [group]
        command(name = "RaphaelGroupListPermission", aliases = listOf("rglistp", "manglistp"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, group: String) {
                if (!RaphaelAPI.permission.groups.contains(group)) {
                    notify(sender, "Group \"&f${group}&7\" Not Found.")
                    return
                }
                notify(sender, "Group \"&f${group}&7\"'s Permissions:")
                val list = RaphaelAPI.permission.groupPermissions(group)
                if (list.isEmpty()) {
                    notify(sender, "- §fNULL")
                } else {
                    list.forEach { permission ->
                        TellrawJson()
                            .append("§c[Raphael] §7- §f")
                            .append(permission).hoverText("§nCOPY").suggestCommand(permission)
                            .sendTo(adaptCommandSender(sender))
                    }
                }
            }
            // group
            dynamic {
                suggestion<CommandSender> { _, _ -> RaphaelAPI.permission.groups.toList() }
                execute<CommandSender> { sender, context, group ->
                    invoke(sender, group)
                }
            }
        }
        // RaphaelGroupListVariables [group]
        command(name = "RaphaelGroupListVariables", aliases = listOf("rglistv", "manglistv"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, group: String) {
                if (!RaphaelAPI.permission.groups.contains(group)) {
                    notify(sender, "Group \"&f${group}&7\" Not Found.")
                    return
                }
                notify(sender, "Group \"&f${group}&7\"'s Variables:")
                val list = RaphaelAPI.permission.groupVariables(group)
                if (list.isEmpty()) {
                    notify(sender, "- §fNULL")
                } else {
                    list.forEach { (k, v) ->
                        TellrawJson()
                            .append("§c[Raphael] §7- §f")
                            .append(k).hoverText("§nCOPY").suggestCommand(k)
                            .append("§8: ")
                            .append(v).hoverText("§nCOPY").suggestCommand(v)
                            .sendTo(adaptCommandSender(sender))
                    }
                }
            }
            // group
            dynamic {
                suggestion<CommandSender> { _, _ -> RaphaelAPI.permission.groups.toList() }
                execute<CommandSender> { sender, context, group ->
                    invoke(sender, group)
                }
            }
        }
        // RaphaelGroupInfo [group]
        command(name = "RaphaelGroupInfo", aliases = listOf("rginfo", "rgwhois", "mangwhois"), permission = "raphael.command") {
            fun invoke(sender: CommandSender, group: String) {
                if (!RaphaelAPI.permission.groups.contains(group)) {
                    notify(sender, "Group \"&f${group}&7\" Not Found.")
                    return
                }
                notify(sender, "Information:")
                adaptCommandSender(sender).performCommand("rglistp ${group}")
                adaptCommandSender(sender).performCommand("rglistv ${group}")
            }
            // group
            dynamic {
                suggestion<CommandSender> { _, _ -> RaphaelAPI.permission.groups.toList() }
                execute<CommandSender> { sender, context, group ->
                    invoke(sender, group)
                }
            }
        }
    }
}
