package ink.ptms.raphael.module.command

import ink.ptms.raphael.Raphael
import ink.ptms.raphael.RaphaelAPI
import ink.ptms.raphael.module.permission.ExpiredValue
import ink.ptms.raphael.util.Utils
import io.izzel.taboolib.cronus.CronusUtils
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.module.tellraw.TellrawJson
import io.izzel.taboolib.util.Commands
import org.bukkit.Bukkit

@Suppress("SpellCheckingInspection")
object CommandUser : CommandHandle() {

    @TInject
    val raphaelUserAddGroup: CommandBuilder = CommandBuilder.create("RaphaelUserAddGroup", Raphael.plugin)
            .permission("raphael.command")
            .aliases("ruadd", "ruaddg", "manuadd", "manuaddg")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.size < 2) {
                    notify(sender, "Usage: /ruadd [player] [group] <time> <reason>")
                    return@execute
                }
                val player = Bukkit.getPlayerExact(args[0])
                if (player == Raphael.plugin) {
                    notify(sender, "Player \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                if (!RaphaelAPI.permission.groups.contains(args[1])) {
                    notify(sender, "Group \"&f${args[1]}&7\" Not Found.")
                    return@execute
                }
                if (RaphaelAPI.permission.playerAddGroup(player!!, args[1], CronusUtils.toMillis(args.getOrElse(2) { "0" }), args.getOrElse(3) { "command by ${sender.name}" })) {
                    notify(sender, "Add \"&f${args[1]}&7\" to \"&f${player.name}&7\"'s Groups. &8(time: ${args.getOrElse(2) { "0" }}, reason: ${args.getOrElse(3) { "command by ${sender.name}" }})")
                } else {
                    notify(sender, "Failed.")
                }
            }

    @TInject
    val raphaelUserDelGroup: CommandBuilder = CommandBuilder.create("RaphaelUserDelGroup", Raphael.plugin)
            .permission("raphael.command")
            .aliases("rudel", "rudelg", "manudel", "manudelg")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.size < 2) {
                    notify(sender, "Usage: /rudel [player] [group] <reason>")
                    return@execute
                }
                val player = Bukkit.getPlayerExact(args[0])
                if (player == Raphael.plugin) {
                    notify(sender, "Player \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                if (!RaphaelAPI.permission.groups.contains(args[1])) {
                    notify(sender, "Group \"&f${args[1]}&7\" Not Found.")
                    return@execute
                }
                if (RaphaelAPI.permission.playerRemoveGroup(player!!, args[1], args.getOrElse(2) { "command by ${sender.name}" })) {
                    notify(sender, "Remove \"&f${args[1]}&7\" from \"&f${player.name}&7\"'s Groups. &8(reason: ${args.getOrElse(2) { "command by ${sender.name}" }})")
                } else {
                    notify(sender, "Failed.")
                }
            }

    @TInject
    val raphaelUserAddVariable: CommandBuilder = CommandBuilder.create("RaphaelUserAddVariable", Raphael.plugin)
            .permission("raphael.command")
            .aliases("ruaddv", "manuaddv")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.size < 3) {
                    notify(sender, "Usage: /ruaddv [player] [key] [value(space=\\s)] <time> <reason>")
                    return@execute
                }
                val player = Bukkit.getPlayerExact(args[0])
                if (player == Raphael.plugin) {
                    notify(sender, "Player \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                if (RaphaelAPI.permission.playerAddVariable(player!!, args[1], args[2].replace("\\s", " "), CronusUtils.toMillis(args.getOrElse(3) { "0" }), args.getOrElse(4) { "command by ${sender.name}" })) {
                    notify(sender, "Add \"&f${args[1]} = ${args[2].replace("\\s", " ")}&7\" to \"&f${player.name}&7\"'s Variables. &8(time: ${args.getOrElse(3) { "0" }}, reason: ${args.getOrElse(4) { "command by ${sender.name}" }})")
                } else {
                    notify(sender, "Failed.")
                }
            }

    @TInject
    val raphaelUserDelVariable: CommandBuilder = CommandBuilder.create("RaphaelUserDelVariable", Raphael.plugin)
            .permission("raphael.command")
            .aliases("rudelv", "manudelv")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.size < 2) {
                    notify(sender, "Usage: /rudelv [player] [key] <reason>")
                    return@execute
                }
                val player = Bukkit.getPlayerExact(args[0])
                if (player == Raphael.plugin) {
                    notify(sender, "Player \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                if (RaphaelAPI.permission.playerRemoveVariable(player!!, args[1], args.getOrElse(2) { "command by ${sender.name}" })) {
                    notify(sender, "Remove \"&f${args[1]}&7\" from \"&f${player.name}&7\"'s Variables. &8(reason: ${args.getOrElse(2) { "command by ${sender.name}" }})")
                } else {
                    notify(sender, "Failed.")
                }
            }

    @TInject
    val raphaelUserAddPermission: CommandBuilder = CommandBuilder.create("RaphaelUserAddPermission", Raphael.plugin)
            .permission("raphael.command")
            .aliases("ruaddp", "manuaddp")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.size < 2) {
                    notify(sender, "Usage: /ruaddp [player] [permission] <time> <reason>")
                    return@execute
                }
                val player = Bukkit.getPlayerExact(args[0])
                if (player == Raphael.plugin) {
                    notify(sender, "Player \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                if (RaphaelAPI.permission.playerAdd(player!!, args[1], CronusUtils.toMillis(args.getOrElse(2) { "0" }), args.getOrElse(3) { "command by ${sender.name}" })) {
                    notify(sender, "Add \"&f${args[1]}&7\" to \"&f${player.name}&7\"'s Permissions. &8(time: ${args.getOrElse(2) { "0" }}, reason: ${args.getOrElse(3) { "command by ${sender.name}" }})")
                } else {
                    notify(sender, "Failed.")
                }
            }

    @TInject
    val raphaelUserDelPermission: CommandBuilder = CommandBuilder.create("RaphaelUserDelPermission", Raphael.plugin)
            .permission("raphael.command")
            .aliases("rudelp", "manudelp")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.size < 2) {
                    notify(sender, "Usage: /rudelp [player] [permission] <reason>")
                    return@execute
                }
                val player = Bukkit.getPlayerExact(args[0])
                if (player == Raphael.plugin) {
                    notify(sender, "Player \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                if (RaphaelAPI.permission.playerRemove(player!!, args[1], args.getOrElse(2) { "command by ${sender.name}" })) {
                    notify(sender, "Remove \"&f${args[1]}&7\" from \"&f${player.name}&7\"'s Permissions. &8(reason: ${args.getOrElse(2) { "command by ${sender.name}" }})")
                } else {
                    notify(sender, "Failed.")
                }
            }

    @TInject
    val raphaelUserClearPermission: CommandBuilder = CommandBuilder.create("RaphaelUserClearPermission", Raphael.plugin)
            .permission("raphael.command")
            .aliases("ruclearp", "manuclearp")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.isEmpty()) {
                    notify(sender, "Usage: /ruclearp [player] <reason>")
                    return@execute
                }
                val player = Bukkit.getPlayerExact(args[0])
                if (player == Raphael.plugin) {
                    notify(sender, "Player \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                RaphaelAPI.permission.playerPermissions(player!!).value.forEach { permission ->
                    if (RaphaelAPI.permission.playerRemove(player, permission.name, args.getOrElse(1) { "command by ${sender.name}" })) {
                        notify(sender, "Remove \"&f${permission.name}&7\" from \"&f${player.name}&7\"'s Permissions. &8(reason: ${args.getOrElse(1) { "command by ${sender.name}" }})")
                    }
                }
                notify(sender, "Done.")
            }

    @TInject
    val raphaelUserClearVariables: CommandBuilder = CommandBuilder.create("RaphaelUserClearVariables", Raphael.plugin)
            .permission("raphael.command")
            .aliases("ruclearv", "manuclearv")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.isEmpty()) {
                    notify(sender, "Usage: /ruclearv [player] <reason>")
                    return@execute
                }
                val player = Bukkit.getPlayerExact(args[0])
                if (player == Raphael.plugin) {
                    notify(sender, "Player \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                RaphaelAPI.permission.playerVariables(player!!).value.forEach { v ->
                    if (RaphaelAPI.permission.playerRemoveVariable(player, v.name, args.getOrElse(1) { "command by ${sender.name}" })) {
                        notify(sender, "Remove \"&f${v.name}&7\" from \"&f${player.name}&7\"'s Variables. &8(reason: ${args.getOrElse(1) { "command by ${sender.name}" }})")
                    }
                }
                notify(sender, "Done.")
            }

    @TInject
    val raphaelUserClearGroups: CommandBuilder = CommandBuilder.create("RaphaelUserClearGroups", Raphael.plugin)
            .permission("raphael.command")
            .aliases("ruclearg", "manuclearg")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.isEmpty()) {
                    notify(sender, "Usage: /ruclearg [player] <reason>")
                    return@execute
                }
                val player = Bukkit.getPlayerExact(args[0])
                if (player == Raphael.plugin) {
                    notify(sender, "Player \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                RaphaelAPI.permission.playerGroups(player!!).value.forEach { v ->
                    if (RaphaelAPI.permission.playerRemoveGroup(player, v.name, args.getOrElse(1) { "command by ${sender.name}" })) {
                        notify(sender, "Remove \"&f${v.name}&7\" from \"&f${player.name}&7\"'s Groups. &8(reason: ${args.getOrElse(1) { "command by ${sender.name}" }})")
                    }
                }
                notify(sender, "Done.")
            }

    @TInject
    val raphaelUserCheckPermission: CommandBuilder = CommandBuilder.create("RaphaelUserCheckPermission", Raphael.plugin)
            .permission("raphael.command")
            .aliases("rucheckp", "manucheckp")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.size < 2) {
                    notify(sender, "Usage: /rucheckp [player] [permission]")
                    return@execute
                }
                val player = Bukkit.getPlayerExact(args[0])
                if (player == Raphael.plugin) {
                    notify(sender, "Player \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                if (player!!.hasPermission(args[1])) {
                    notify(sender, "Check \"&f${args[1]}&7\" from \"&f${player.name}&7\"'s Permissions. &8(result: &aTRUE&8)")
                } else {
                    notify(sender, "Check \"&f${args[1]}&7\" from \"&f${player.name}&7\"'s Permissions. &8(result: &cFALSE&8)")
                }
            }

    @TInject
    val raphaelUserListPermission: CommandBuilder = CommandBuilder.create("RaphaelUserListPermission", Raphael.plugin)
            .permission("raphael.command")
            .aliases("rulistp", "manulistp")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.isEmpty()) {
                    notify(sender, "Usage: /rulistp [player]")
                    return@execute
                }
                val player = Bukkit.getPlayerExact(args[0])
                if (player == Raphael.plugin) {
                    notify(sender, "Player \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                notify(sender, "Player \"&f${player!!.name}&7\"'s Permissions:")
                val list = RaphaelAPI.permission.playerPermissions(player).value.toMutableList()
                if (list.isEmpty()) {
                    notify(sender, "- §fNULL")
                } else {
                    list.forEach { permission ->
                        TellrawJson.create()
                                .append("§c[Raphael] §7- §f")
                                .append(permission.name).hoverText("§nCOPY").clickSuggest(permission.name)
                                .run {
                                    if (permission.expired == 0L) {
                                        append(" §8(expired: §f-§8)")
                                    } else {
                                        append(" §8(expired: §f${format.format(permission.expired)}§8)")
                                    }
                                    send(sender)
                                }
                    }
                }
            }

    @TInject
    val raphaelUserListVariables: CommandBuilder = CommandBuilder.create("RaphaelUserListVariables", Raphael.plugin)
            .permission("raphael.command")
            .aliases("rulistv", "manulistv")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.isEmpty()) {
                    notify(sender, "Usage: /rulistv [player]")
                    return@execute
                }
                val player = Bukkit.getPlayerExact(args[0])
                if (player == Raphael.plugin) {
                    notify(sender, "Player \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                notify(sender, "Player \"&f${player!!.name}&7\"'s Variables:")
                val list = RaphaelAPI.permission.playerVariables(player)
                if (list.value.isEmpty()) {
                    notify(sender, "- §fNULL")
                } else {
                    list.value.forEach { v ->
                        TellrawJson.create()
                                .append("§c[Raphael] §7- §f")
                                .append(v.name).hoverText("§nCOPY").clickSuggest(v.name)
                                .append("§8: ")
                                .append(v.data).hoverText("§nCOPY").clickSuggest(v.data)
                                .run {
                                    if (v.expired == 0L) {
                                        this.append(" §8(expired: §f-§8)")
                                    } else {
                                        this.append(" §8(expired: §f${format.format(v.expired)}§8)")
                                    }
                                    this.send(sender)
                                }
                    }
                }
            }

    @TInject
    val raphaelUserListGroups: CommandBuilder = CommandBuilder.create("RaphaelUserListGroups", Raphael.plugin)
            .permission("raphael.command")
            .aliases("rulistg", "manulistg")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.isEmpty()) {
                    notify(sender, "Usage: /rulistg [player]")
                    return@execute
                }
                val player = Bukkit.getPlayerExact(args[0])
                if (player == Raphael.plugin) {
                    notify(sender, "Player \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                notify(sender, "Player \"&f${player!!.name}&7\"'s Groups:")
                val list = RaphaelAPI.permission.playerGroups(player).value.toMutableList()
                if (RaphaelAPI.permission.groupPermissions("default").isNotEmpty()) {
                    list.add(ExpiredValue("default"))
                }
                if (list.isEmpty()) {
                    notify(sender, "- §fNULL")
                } else {
                    list.forEach { group ->
                        TellrawJson.create()
                                .append("§c[Raphael] §7- §f")
                                .append(group.name).hoverText("§nCOPY").clickSuggest(group.name)
                                .run {
                                    if (group.expired == 0L) {
                                        append(" §8(expired: §f-§8)")
                                    } else {
                                        append(" §8(expired: §f${format.format(group.expired)}§8)")
                                    }
                                    send(sender)
                                }
                    }
                }
            }

    @TInject
    val raphaelUserInfo: CommandBuilder = CommandBuilder.create("RaphaelUserInfo", Raphael.plugin)
            .permission("raphael.command")
            .aliases("ruinfo", "ruwhois", "manuwhois")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.isEmpty()) {
                    notify(sender, "Usage: /ruinfo [player]")
                    return@execute
                }
                val player = Bukkit.getPlayerExact(args[0])
                if (player == Raphael.plugin) {
                    notify(sender, "Player \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                notify(sender, "Information:")
                Commands.dispatchCommand(sender, "rulistp ${player!!.name}")
                Commands.dispatchCommand(sender, "rulistv ${player.name}")
                Commands.dispatchCommand(sender, "rulistg ${player.name}")
            }
}