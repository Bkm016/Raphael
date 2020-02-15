package ink.ptms.raphael.module.command

import ink.ptms.raphael.RaphaelAPI
import ink.ptms.raphael.util.Utils
import io.izzel.taboolib.cronus.CronusUtils
import io.izzel.taboolib.module.command.lite.CommandBuilder
import io.izzel.taboolib.module.inject.TInject
import io.izzel.taboolib.module.tellraw.TellrawJson
import io.izzel.taboolib.util.Commands
import org.bukkit.Bukkit

object CommandGroup : CommandHandle() {

    @TInject
    val raphaelGroupAdd = CommandBuilder.create("RaphaelGroupAdd", null)
            .permission("raphael.command")
            .aliases("rgadd", "mangadd")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.isEmpty()) {
                    notify(sender, "Usage: /rgadd [group] <reason>")
                    return@execute
                }
                if (RaphaelAPI.permission.groups.contains(args[0])) {
                    notify(sender, "Group \"&f${args[0]}&7\" Exists.")
                    return@execute
                }
                if (RaphaelAPI.permission.groupCreate(args[0], args.getOrElse(1) { "command by ${sender.name}" })) {
                    notify(sender, "Group \"&f${args[0]}&7\" Created. &8(reason: ${args.getOrElse(1) { "command by ${sender.name}" }})")
                } else {
                    notify(sender, "Failed.")
                }
            }

    @TInject
    val raphaelGroupDel = CommandBuilder.create("raphaelGroupDel", null)
            .permission("raphael.command")
            .aliases("rgdel", "mangdel")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.isEmpty()) {
                    notify(sender, "Usage: /rgdel [group] <reason>")
                    return@execute
                }
                if (!RaphaelAPI.permission.groups.contains(args[0])) {
                    notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                if (RaphaelAPI.permission.groupDelete(args[0], args.getOrElse(1) { "command by ${sender.name}" })) {
                    notify(sender, "Group \"&f${args[0]}&7\" Deleted. &8(reason: ${args.getOrElse(1) { "command by ${sender.name}" }})")
                } else {
                    notify(sender, "Failed.")
                }
            }

    @TInject
    val raphaelGroupAddVariable = CommandBuilder.create("RaphaelGroupAddVariable", null)
            .permission("raphael.command")
            .aliases("rgaddv", "mangaddv")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.size < 3) {
                    notify(sender, "Usage: /rgaddv [group] [key] [value(space=\\s)] <reason>")
                    return@execute
                }
                if (!RaphaelAPI.permission.groups.contains(args[0])) {
                    notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                if (RaphaelAPI.permission.groupAddVariable(args[0], args[1], args[2].replace("\\s", " "), args.getOrElse(3) { "command by ${sender.name}" })) {
                    notify(sender, "Add \"&f${args[1]} = ${args[2].replace("\\s", " ")}&7\" to \"&f${args[0]}&7\"'s Variables. &8(reason: ${args.getOrElse(3) { "command by ${sender.name}" }})")
                } else {
                    notify(sender, "Failed.")
                }
            }

    @TInject
    val raphaelGroupDelVariable = CommandBuilder.create("RaphaelGroupDelVariable", null)
            .permission("raphael.command")
            .aliases("rgdelv", "mangdelv")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.size < 2) {
                    notify(sender, "Usage: /rgdelv [group] [key] <reason>")
                    return@execute
                }
                if (!RaphaelAPI.permission.groups.contains(args[0])) {
                    notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                if (RaphaelAPI.permission.groupRemoveVariable(args[0], args[1], args.getOrElse(2) { "command by ${sender.name}" })) {
                    notify(sender, "Remove \"&f${args[1]}&7\" from \"&f${args[0]}&7\"'s Variables. &8(reason: ${args.getOrElse(2) { "command by ${sender.name}" }})")
                } else {
                    notify(sender, "Failed.")
                }
            }

    @TInject
    val raphaelGroupAddPermission = CommandBuilder.create("RaphaelGroupAddPermission", null)
            .permission("raphael.command")
            .aliases("rgaddp", "mangaddp")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.size < 2) {
                    notify(sender, "Usage: /rgaddp [group] [permission] <reason>")
                    return@execute
                }
                if (!RaphaelAPI.permission.groups.contains(args[0])) {
                    notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                if (RaphaelAPI.permission.groupAddPermission(args[0], args[1], args.getOrElse(2) { "command by ${sender.name}" })) {
                    notify(sender, "Add \"&f${args[1]}&7\" to \"&f${args[0]}&7\"'s Permissions. &8(reason: ${args.getOrElse(2) { "command by ${sender.name}" }})")
                } else {
                    notify(sender, "Failed.")
                }
            }

    @TInject
    val raphaelGroupDelPermission = CommandBuilder.create("RaphaelGroupDelPermission", null)
            .permission("raphael.command")
            .aliases("rgdelp", "mangdelp")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.size < 2) {
                    notify(sender, "Usage: /rgdelp [group] [permission] <reason>")
                    return@execute
                }
                if (!RaphaelAPI.permission.groups.contains(args[0])) {
                    notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                if (RaphaelAPI.permission.groupRemovePermission(args[0], args[1], args.getOrElse(2) { "command by ${sender.name}" })) {
                    notify(sender, "Remove \"&f${args[1]}&7\" from \"&f${args[0]}&7\"'s Permissions. &8(reason: ${args.getOrElse(2) { "command by ${sender.name}" }})")
                } else {
                    notify(sender, "Failed.")
                }
            }

    @TInject
    val raphaelGroupClearPermission = CommandBuilder.create("RaphaelGroupClearPermission", null)
            .permission("raphael.command")
            .aliases("rgclearp", "mangclearp")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.isEmpty()) {
                    notify(sender, "Usage: /rgclearp [group] <reason>")
                    return@execute
                }
                if (!RaphaelAPI.permission.groups.contains(args[0])) {
                    notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                RaphaelAPI.permission.groupPermissions(args[0]).forEach { permission ->
                    if (RaphaelAPI.permission.groupRemovePermission(args[0], permission, args.getOrElse(1) { "command by ${sender.name}" })) {
                        notify(sender, "Remove \"&f$permission&7\" from \"&f${args[0]}&7\"'s Permissions. &8(reason: ${args.getOrElse(1) { "command by ${sender.name}" }})")
                    }
                }
                notify(sender, "Done.")
            }

    @TInject
    val raphaelGroupClearVariables = CommandBuilder.create("RaphaelGroupClearVariables", null)
            .permission("raphael.command")
            .aliases("rgclearv", "mangclearv")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.isEmpty()) {
                    notify(sender, "Usage: /rgclearv [group] <reason>")
                    return@execute
                }
                if (!RaphaelAPI.permission.groups.contains(args[0])) {
                    notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                RaphaelAPI.permission.groupVariables(args[0]).forEach { k, _ ->
                    if (RaphaelAPI.permission.groupRemoveVariable(args[0], k, args.getOrElse(1) { "command by ${sender.name}" })) {
                        notify(sender, "Remove \"&f$k&7\" from \"&f${args[0]}&7\"'s Variables. &8(reason: ${args.getOrElse(1) { "command by ${sender.name}" }})")
                    }
                }
                notify(sender, "Done.")
            }


    @TInject
    val raphaelGroupListPermission = CommandBuilder.create("RaphaelGroupListPermission", null)
            .permission("raphael.command")
            .aliases("rglistp", "manglistp")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.isEmpty()) {
                    notify(sender, "Usage: /rglistp [player]")
                    return@execute
                }
                if (!RaphaelAPI.permission.groups.contains(args[0])) {
                    notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                notify(sender, "Group \"&f${args[0]}&7\"'s Permissions:")
                val list = RaphaelAPI.permission.groupPermissions(args[0])
                if (list.isEmpty()) {
                    notify(sender, "- §fNULL")
                } else {
                    list.forEach { permission ->
                        TellrawJson.create()
                                .append("§c[Raphael] §7- §f")
                                .append(permission).hoverText("§nCOPY").clickSuggest(permission)
                                .send(sender)
                    }
                }
            }

    @TInject
    val raphaelGroupListVariables = CommandBuilder.create("RaphaelGroupListVariables", null)
            .permission("raphael.command")
            .aliases("rglistv", "manglistv")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.isEmpty()) {
                    notify(sender, "Usage: /rglistv [player]")
                    return@execute
                }
                if (!RaphaelAPI.permission.groups.contains(args[0])) {
                    notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                notify(sender, "Group \"&f${args[0]}&7\"'s Variables:")
                val list = RaphaelAPI.permission.groupVariables(args[0])
                if (list.isEmpty()) {
                    notify(sender, "- §fNULL")
                } else {
                    list.forEach { k, v ->
                        TellrawJson.create()
                                .append("§c[Raphael] §7- §f")
                                .append(k).hoverText("§nCOPY").clickSuggest(k)
                                .append("§8: ")
                                .append(v).hoverText("§nCOPY").clickSuggest(v)
                                .send(sender)
                    }
                }
            }

    @TInject
    val raphaelUserInfo = CommandBuilder.create("RaphaelGroupInfo", null)
            .permission("raphael.command")
            .aliases("rginfo", "rgwhois", "mangwhois")
            .tab(Utils.playerTab())
            .execute { sender, args ->
                if (args.isEmpty()) {
                    notify(sender, "Usage: /rginfo [player]")
                    return@execute
                }
                if (!RaphaelAPI.permission.groups.contains(args[0])) {
                    notify(sender, "Group \"&f${args[0]}&7\" Not Found.")
                    return@execute
                }
                notify(sender, "Information:")
                Commands.dispatchCommand(sender, "rglistp ${args[0]}")
                Commands.dispatchCommand(sender, "rglistv ${args[0]}")
            }
}