package cc.dreamcode.antilogout.listener;

import cc.dreamcode.antilogout.user.UserCache;
import cc.dreamcode.antilogout.user.UserFactory;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.tasker.core.Tasker;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

@RequiredArgsConstructor(onConstructor_= @Inject)
public class PlayerDataListener implements Listener {

    private final Tasker tasker;
    private final UserCache userCache;
    private final UserFactory userFactory;

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.tasker.newSharedChain(player.getUniqueId().toString())
                .supplyAsync(() -> this.userFactory.create(player.getUniqueId(), player.getName()))
                .acceptAsync(this.userCache::add)
                .execute();
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        this.tasker.newSharedChain(player.getUniqueId().toString())
                .supplyAsync(player::getUniqueId)
                .transformAsync(this.userCache::findUserByUniqueId)
                .abortIfAsync(Objects::isNull)
                .acceptAsync(this.userCache::remove)
                .execute();
    }

}