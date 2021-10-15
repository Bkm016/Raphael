package ink.ptms.raphael.module.command

import ink.ptms.raphael.RaphaelAPI
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.command
import taboolib.module.chat.TellrawJson

@Awake(LifeCycle.ENABLE)
fun commandGroup() {
    command(name = "SpellCheckingInspection") {
        literal("RaphaelGroupAdd", "rgadd", "mangadd", permission = "raphael.command") {
            dynamic(optional = false) {
                suggestion<ProxyCommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                dynamic(optional = true) {
                    execute<ProxyCommandSender> { proxySender, context, argument ->
                        val sender = proxySender.cast<CommandSender>()
                        val args: MutableList<String> = mutableListOf(context.argument(-1)!!, argument)
                        if (RaphaelAPI.permission.groups.contains(args[0])) {
                            CommandHandle().notify(sender, "Group \"&f${args[0]}&7\" Exists.")
                            return@execute
                        }
                        if (RaphaelAPI.permission.groupCreate(
                                args[0],
                                args.getOrElse(1) { "command by ${sender.name}" })
                        ) {
                            CommandHandle().notify(
                                sender,
                                "Group \"&f${args[0]}&7\" Created. &8(reason: ${args.getOrElse(1) { "command by ${sender.name}" }})"
                            )
                        } else {
                            CommandHandle().notify(sender, "Failed.")
                        }
                    }
                }
            }

        }
        literal("RaphaelGroupDel", "rgdel", "mangdel", permission = "raphael.command") {
            dynamic(optional = false) {
                suggestion<ProxyCommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                dynamic(optional = true) {
                    execute<ProxyCommandSender> { proxySender, context, argument ->
                        val sender = proxySender.cast<CommandSender>()
                        val args: MutableList<String> = mutableListOf(context.argument(-1)!!, argument)
                        if (!RaphaelAPI.permission.groups.contains(args[0])) {
                            CommandHandle().notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                            return@execute
                        }
                        if (RaphaelAPI.permission.groupDelete(
                                args[0],
                                args.getOrElse(1) { "command by ${sender.name}" })
                        ) {
                            CommandHandle().notify(
                                sender,
                                "Group \"&f${args[0]}&7\" Deleted. &8(reason: ${args.getOrElse(1) { "command by ${sender.name}" }})"
                            )
                        } else {
                            CommandHandle().notify(sender, "Failed.")
                        }
                    }
                }
            }
        }
        literal("RaphaelGroupAddVariable", "rgaddv", "mangaddv", permission = "raphael.command") {
            dynamic(optional = false) {
                suggestion<ProxyCommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                dynamic(optional = false) {
                    dynamic(optional = false) {
                        dynamic(optional = true) {
                            execute<ProxyCommandSender> { proxySender, context, argument ->
                                val sender = proxySender.cast<CommandSender>()
                                val args: MutableList<String> =
                                    mutableListOf(context.argument(-2)!!, context.argument(-1)!!, argument)
                                if (!RaphaelAPI.permission.groups.contains(args[0])) {
                                    CommandHandle().notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                                    return@execute
                                }
                                if (RaphaelAPI.permission.groupAddVariable(
                                        args[0],
                                        args[1],
                                        args[2].replace("\\s", " "),
                                        args.getOrElse(3) { "command by ${sender.name}" })
                                ) {
                                    CommandHandle().notify(
                                        sender,
                                        "Add \"&f${args[1]} = ${
                                            args[2].replace(
                                                "\\s",
                                                " "
                                            )
                                        }&7\" to \"&f${args[0]}&7\"'s Variables. &8(reason: ${args.getOrElse(3) { "command by ${sender.name}" }})"
                                    )
                                } else {
                                    CommandHandle().notify(sender, "Failed.")
                                }
                            }
                        }
                    }
                }
            }

        }
        literal("RaphaelGroupDelVariable", "rgdelv", "mangdelv", permission = "raphael.command") {
            dynamic(optional = false) {
                suggestion<ProxyCommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                dynamic(optional = false) {
                    execute<ProxyCommandSender> { proxySender, context, argument ->
                        val sender = proxySender.cast<CommandSender>()
                        val args: MutableList<String> =
                            mutableListOf(context.argument(-2)!!, context.argument(-1)!!, argument)
                        if (!RaphaelAPI.permission.groups.contains(args[0])) {
                            CommandHandle().notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                            return@execute
                        }
                        if (RaphaelAPI.permission.groupRemoveVariable(
                                args[0],
                                args[1],
                                args.getOrElse(2) { "command by ${sender.name}" })
                        ) {
                            CommandHandle().notify(
                                sender,
                                "Remove \"&f${args[1]}&7\" from \"&f${args[0]}&7\"'s Variables. &8(reason: ${
                                    args.getOrElse(2) { "command by ${sender.name}" }
                                })"
                            )
                        } else {
                            CommandHandle().notify(sender, "Failed.")
                        }
                    }
                }
            }

        }
        literal("RaphaelGroupAddPermission", "rgaddp", "mangaddp", permission = "raphael.command") {
            dynamic(optional = false) {
                suggestion<ProxyCommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                dynamic(optional = false) {
                    dynamic(optional = true) {
                        execute<ProxyCommandSender> { proxySender, context, argument ->
                            val sender = proxySender.cast<CommandSender>()
                            val args: MutableList<String> =
                                mutableListOf(context.argument(-2)!!, context.argument(-1)!!, argument)
                            if (!RaphaelAPI.permission.groups.contains(args[0])) {
                                CommandHandle().notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                                return@execute
                            }
                            if (RaphaelAPI.permission.groupAddPermission(
                                    args[0],
                                    args[1],
                                    args.getOrElse(2) { "command by ${sender.name}" })
                            ) {
                                CommandHandle().notify(
                                    sender,
                                    "Add \"&f${args[1]}&7\" to \"&f${args[0]}&7\"'s Permissions. &8(reason: ${
                                        args.getOrElse(2) { "command by ${sender.name}" }
                                    })"
                                )
                            } else {
                                CommandHandle().notify(sender, "Failed.")
                            }
                        }
                    }

                }
            }

        }
        literal("RaphaelGroupDelPermission", "rgdelp", "mangdelp", permission = "raphael.command") {
            dynamic(optional = false) {
                suggestion<ProxyCommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                dynamic(optional = false) {
                    execute<ProxyCommandSender> { proxySender, context, argument ->
                        val sender = proxySender.cast<CommandSender>()
                        val args: MutableList<String> =
                            mutableListOf(context.argument(-1)!!, argument)
                        if (!RaphaelAPI.permission.groups.contains(args[0])) {
                            CommandHandle().notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                            return@execute
                        }
                        if (RaphaelAPI.permission.groupRemovePermission(
                                args[0],
                                args[1],
                                args.getOrElse(2) { "command by ${sender.name}" })
                        ) {
                            CommandHandle().notify(
                                sender,
                                "Remove \"&f${args[1]}&7\" from \"&f${args[0]}&7\"'s Permissions. &8(reason: ${
                                    args.getOrElse(2) { "command by ${sender.name}" }
                                })"
                            )
                        } else {
                            CommandHandle().notify(sender, "Failed.")
                        }
                    }
                }
            }

        }
        literal("RaphaelGroupClearPermission", "rgclearp", "mangclearp", permission = "raphael.command") {
            dynamic(optional = false) {
                suggestion<ProxyCommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                dynamic(optional = true) {
                    execute<ProxyCommandSender> { proxySender, context, argument ->
                        val sender = proxySender.cast<CommandSender>()
                        val args: MutableList<String> =
                            mutableListOf(context.argument(-1)!!, argument)
                        if (!RaphaelAPI.permission.groups.contains(args[0])) {
                            CommandHandle().notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                            return@execute
                        }
                        RaphaelAPI.permission.groupPermissions(args[0]).forEach { permission ->
                            if (RaphaelAPI.permission.groupRemovePermission(
                                    args[0],
                                    permission,
                                    args.getOrElse(1) { "command by ${sender.name}" })
                            ) {
                                CommandHandle().notify(
                                    sender,
                                    "Remove \"&f$permission&7\" from \"&f${args[0]}&7\"'s Permissions. &8(reason: ${
                                        args.getOrElse(1) { "command by ${sender.name}" }
                                    })"
                                )
                            }
                        }
                        CommandHandle().notify(sender, "Done.")
                    }
                }
            }

        }
        literal("RaphaelGroupClearVariables", "rgclearv", "mangclearv", permission = "raphael.command") {
            dynamic(optional = false) {
                suggestion<ProxyCommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                dynamic(optional = true) {
                    execute<ProxyCommandSender> { proxySender, context, argument ->
                        val sender = proxySender.cast<CommandSender>()
                        val args: MutableList<String> =
                            mutableListOf(context.argument(-1)!!, argument)
                        if (!RaphaelAPI.permission.groups.contains(args[0])) {
                            CommandHandle().notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                            return@execute
                        }
                        RaphaelAPI.permission.groupVariables(args[0]).forEach { (k, _) ->
                            if (RaphaelAPI.permission.groupRemoveVariable(
                                    args[0],
                                    k,
                                    args.getOrElse(1) { "command by ${sender.name}" })
                            ) {
                                CommandHandle().notify(
                                    sender,
                                    "Remove \"&f$k&7\" from \"&f${args[0]}&7\"'s Variables. &8(reason: ${
                                        args.getOrElse(1) { "command by ${sender.name}" }
                                    })"
                                )
                            }
                        }
                        CommandHandle().notify(sender, "Done.")
                    }
                }
            }

        }
        literal("RaphaelGroupListPermission", "rglistp", "manglistp", permission = "raphael.command") {
            dynamic(optional = false) {
                suggestion<ProxyCommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                dynamic(optional = false) {
                    execute<ProxyCommandSender> { proxySender, _, argument ->
                        val sender = proxySender.cast<CommandSender>()
                        if (!RaphaelAPI.permission.groups.contains(argument)) {
                            CommandHandle().notify(sender, "Group \"&f${argument}&7\" Not Found.")
                            return@execute
                        }
                        CommandHandle().notify(sender, "Group \"&f${argument}&7\"'s Permissions:")
                        val list = RaphaelAPI.permission.groupPermissions(argument)
                        if (list.isEmpty()) {
                            CommandHandle().notify(sender, "- §fNULL")
                        } else {
                            list.forEach { permission ->
                                TellrawJson.create()
                                    .append("§c[Raphael] §7- §f")
                                    .append(permission).hoverText("§nCOPY").clickSuggest(permission)
                                    .send(sender)
                            }
                        }
                    }
                }
            }

        }
        literal("RaphaelGroupListVariables", "rglistv", "manglistv", permission = "raphael.command") {
            dynamic(optional = false) {
                suggestion<ProxyCommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                dynamic(optional = false) {
                    execute<ProxyCommandSender> { proxySender, _, argument ->
                        val sender = proxySender.cast<CommandSender>()
                        if (!RaphaelAPI.permission.groups.contains(argument)) {
                            CommandHandle().notify(sender, "Group \"&f${argument}&7\" Not Found.")
                            return@execute
                        }
                        CommandHandle().notify(sender, "Group \"&f${argument}&7\"'s Variables:")
                        val list = RaphaelAPI.permission.groupVariables(argument)
                        if (list.isEmpty()) {
                            CommandHandle().notify(sender, "- §fNULL")
                        } else {
                            list.forEach { (k, v) ->
                                TellrawJson.create()
                                    .append("§c[Raphael] §7- §f")
                                    .append(k).hoverText("§nCOPY").clickSuggest(k)
                                    .append("§8: ")
                                    .append(v).hoverText("§nCOPY").clickSuggest(v)
                                    .send(sender)
                            }
                        }
                    }
                }
            }

        }
        literal("RaphaelGroupInfo", "rginfo", "rgwhois", "mangwhois", permission = "raphael.command") {
            dynamic(optional = false) {
                suggestion<ProxyCommandSender> { _, _ ->
                    Bukkit.getOnlinePlayers().map { it.name }
                }
                dynamic(optional = false) {
                    execute<ProxyCommandSender> { proxySender, _, argument ->
                        val sender = proxySender.cast<CommandSender>()
                        if (!RaphaelAPI.permission.groups.contains(argument)) {
                            CommandHandle().notify(sender, "Group \"&f${argument}&7\" Not Found.")
                            return@execute
                        }
                        CommandHandle().notify(sender, "Information:")
                        Commands.dispatchCommand(sender, "rglistp $argument")
                        Commands.dispatchCommand(sender, "rglistv $argument")
                    }
                }
            }

        }
    }
}
