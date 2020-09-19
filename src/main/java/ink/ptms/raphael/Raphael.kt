package ink.ptms.raphael;

import io.izzel.taboolib.loader.Plugin;
import io.izzel.taboolib.module.config.TConfig;
import io.izzel.taboolib.module.inject.TInject;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

@Plugin.Version(5.15)
public final class Raphael extends Plugin {

    @TInject
    public static TConfig CONF = null;

    @Override
    public void onLoading() {
        Bukkit.getServicesManager().register(Permission.class, new RaphaelHook(), getPlugin(), ServicePriority.Normal);
    }

    @Override
    public void onStopping() {
        Bukkit.getServicesManager().unregister(RaphaelAPI.INSTANCE.getPermission());
    }
}
