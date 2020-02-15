package ink.ptms.raphael.api.nms;

import io.izzel.taboolib.module.inject.TInject;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

/**
 * @Author sky
 * @Since 2020-02-03 14:31
 */
public abstract class NMS {

    @TInject(asm = "ink.ptms.raphael.api.nms.NMSHandle")
    public static final NMS HANDLE = null;

    abstract public void setPermissibleBase(HumanEntity player, PermissibleBase permissibleBase);

    abstract public PermissibleBase getPermissibleBase(HumanEntity player);

}
