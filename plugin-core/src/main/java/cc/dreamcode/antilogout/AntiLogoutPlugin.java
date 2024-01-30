package cc.dreamcode.antilogout;

import cc.dreamcode.antilogout.user.UserCache;
import cc.dreamcode.antilogout.user.UserFactory;
import cc.dreamcode.antilogout.user.UserListener;
import cc.dreamcode.command.bukkit.BukkitCommandProvider;
import cc.dreamcode.notice.minecraft.bukkit.serdes.BukkitNoticeSerdes;
import cc.dreamcode.platform.DreamVersion;
import cc.dreamcode.platform.bukkit.DreamBukkitConfig;
import cc.dreamcode.platform.bukkit.DreamBukkitPlatform;
import cc.dreamcode.platform.bukkit.component.CommandComponentResolver;
import cc.dreamcode.platform.bukkit.component.ConfigurationComponentResolver;
import cc.dreamcode.platform.bukkit.component.ListenerComponentResolver;
import cc.dreamcode.platform.bukkit.component.RunnableComponentResolver;
import cc.dreamcode.platform.component.ComponentManager;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.tasker.bukkit.BukkitTasker;
import lombok.Getter;
import lombok.NonNull;

public final class AntiLogoutPlugin extends DreamBukkitPlatform implements DreamBukkitConfig {

    @Getter
    private static AntiLogoutPlugin antiLogoutPlugin;

    @Override
    public void load(@NonNull ComponentManager componentManager) {
        antiLogoutPlugin = this;
    }

    @Override
    public void enable(@NonNull ComponentManager componentManager) {
        this.registerInjectable(BukkitTasker.newPool(this));
        this.registerInjectable(BukkitCommandProvider.create(this, this.getInjector()));

        componentManager.registerResolver(CommandComponentResolver.class);
        componentManager.registerResolver(ListenerComponentResolver.class);
        componentManager.registerResolver(RunnableComponentResolver.class);

        componentManager.registerResolver(ConfigurationComponentResolver.class);
        componentManager.registerComponent(AntiLogoutConfig.class, config -> {
            componentManager.setDebug(config.getPluginWrapper().isDebug());

            this.getInject(BukkitCommandProvider.class).ifPresent(bukkitCommandProvider -> {
                bukkitCommandProvider.setRequiredPermissionMessage(config.getMessageWrapper().getNoPermission().getText());
                bukkitCommandProvider.setRequiredPlayerMessage(config.getMessageWrapper().getNotPlayer().getText());
            });
        });

        componentManager.registerComponent(UserCache.class);
        componentManager.registerComponent(UserFactory.class);

        componentManager.registerComponent(UserListener.class);
    }

    @Override
    public void disable() {
    }

    @Override
    public @NonNull DreamVersion getDreamVersion() {
        return DreamVersion.create("dream-antilogout", "1.0-InDEV", "trueman96");
    }

    @Override
    public @NonNull OkaeriSerdesPack getConfigSerdesPack() {
        return registry -> {
            registry.register(new BukkitNoticeSerdes());
        };
    }

}