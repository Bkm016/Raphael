package ink.ptms.raphael.module.permission;

import ink.ptms.raphael.api.nms.NMS;
import io.izzel.taboolib.module.db.local.Local;
import io.izzel.taboolib.module.lite.SimpleReflection;
import io.izzel.taboolib.util.Reflection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.permissions.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @Author sky
 * @Since 2020-01-31 23:48
 */
public class PermissibleRaphael extends PermissibleBase {

    private static FileConfiguration data = Local.get().get("data.yml");
    private HumanEntity humanEntity;
    private PermissibleBase permissibleBase;

    public PermissibleRaphael() {
        super(null);
    }

    public void init(HumanEntity humanEntity) {
        this.humanEntity = humanEntity;
        this.permissibleBase = NMS.HANDLE.getPermissibleBase(humanEntity);
        NMS.HANDLE.setPermissibleBase(humanEntity, this);
    }

    public void cancel() {
        NMS.HANDLE.setPermissibleBase(humanEntity, this.permissibleBase);
    }

    @Override
    public boolean isPermissionSet(String s) {
        return this.permissibleBase.isPermissionSet(s);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return this.permissibleBase.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String s) {
        return this.permissibleBase.hasPermission(s);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return this.permissibleBase.hasPermission(permission);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
        return this.permissibleBase.addAttachment(plugin, s, b);
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.permissibleBase.addAttachment(plugin);
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
        return this.permissibleBase.addAttachment(plugin, s, b, i);
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {
        return this.permissibleBase.addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment) {
        this.permissibleBase.removeAttachment(permissionAttachment);
    }

    @Override
    public void recalculatePermissions() {
        this.permissibleBase.recalculatePermissions();
    }

    @NotNull
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.permissibleBase.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return this.permissibleBase.isOp();
    }

    @Override
    public void setOp(boolean b) {
        this.permissibleBase.setOp(b);
    }
}
