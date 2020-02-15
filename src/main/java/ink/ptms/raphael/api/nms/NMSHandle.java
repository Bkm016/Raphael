package ink.ptms.raphael.api.nms;

import io.izzel.taboolib.module.lite.SimpleReflection;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

/**
 * @Author sky
 * @Since 2020-02-03 14:32
 */
public class NMSHandle extends NMS {

    public NMSHandle() {
        SimpleReflection.checkAndSave(CraftHumanEntity.class);
    }

    @Override
    public void setPermissibleBase(HumanEntity player, PermissibleBase permissibleBase) {
        SimpleReflection.setFieldValue(CraftHumanEntity.class, player, "perm", permissibleBase);
    }

    @Override
    public PermissibleBase getPermissibleBase(HumanEntity player) {
        return (PermissibleBase) SimpleReflection.getFieldValue(CraftHumanEntity.class, player, "perm");
    }
}
