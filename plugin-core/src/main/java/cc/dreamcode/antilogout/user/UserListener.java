package cc.dreamcode.antilogout.user;

import cc.dreamcode.antilogout.AntiLogoutConfig;
import cc.dreamcode.antilogout.helper.AntiLogoutHelper;
import cc.dreamcode.utilities.TimeUtil;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.tasker.core.Tasker;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;
import java.util.Collections;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserListener implements Listener {

    private final Tasker tasker;
    private final UserCache userCache;
    private final UserFactory userFactory;
    private final AntiLogoutConfig antiLogoutConfig;

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.tasker.newSharedChain(player.getUniqueId().toString())
                .supplyAsync(() -> this.userFactory.create(player.getUniqueId(), player.getName()))
                .acceptAsync(user -> {
                    if (this.antiLogoutConfig.getPluginWrapper().getProtectionSaved().containsKey(player.getUniqueId())) {
                        user.setProtection(System.currentTimeMillis() + this.antiLogoutConfig.getPluginWrapper().getProtectionSaved().get(player.getUniqueId()));
                        this.antiLogoutConfig.getPluginWrapper().getProtectionSaved().remove(player.getUniqueId());
                    } else {
                        user.setProtection(System.currentTimeMillis() + this.antiLogoutConfig.getPluginWrapper().getProtectionTime().toMillis());
                    }

                    this.userCache.add(user);
                })
                .execute();
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        this.tasker.newSharedChain(player.getUniqueId().toString())
                .supplyAsync(() -> this.userCache.findUserByUniqueId(player.getUniqueId()))
                .acceptAsync(user -> {
                    if (user.isInCombat()) {
                        player.setHealth(0);
                    }

                    if (user.hasProtection()) {
                        this.antiLogoutConfig.getPluginWrapper().getProtectionSaved().put(player.getUniqueId(), user.getProtection() - System.currentTimeMillis());
                    } else {
                        this.antiLogoutConfig.getPluginWrapper().getProtectionSaved().remove(player.getUniqueId());
                    }

                    this.userCache.remove(user);
                })
                .execute();
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player victim = event.getEntity();
        User victimUser = this.userCache.findUserByPlayer(victim);
        victimUser.resetCombat();
    }

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player attacker = AntiLogoutHelper.findAttackerByEvent(event);
        if (attacker == null) {
            return;
        }

        //todo: check if spawn

        Player victim = (Player) event.getEntity();
        if (victim.equals(attacker)) {
            return;
        }

        User victimUser = this.userCache.findUserByPlayer(victim);
        User attackerUser = this.userCache.findUserByPlayer(attacker);
        if (attackerUser.getProtection() > System.currentTimeMillis()) {
            this.antiLogoutConfig.getMessageWrapper().getYouHaveProtection().send(attacker, Collections.singletonMap("time",
                    TimeUtil.convertDurationMills(Duration.ofMillis(attackerUser.getProtection() - System.currentTimeMillis()))));
            event.setCancelled(true);
            return;
        }

        if (victimUser.getProtection() > System.currentTimeMillis()) {
            this.antiLogoutConfig.getMessageWrapper().getAttackedPlayerHasProtection().send(attacker, Collections.singletonMap("time",
                    TimeUtil.convertDurationMills(Duration.ofMillis(victimUser.getProtection() - System.currentTimeMillis()))));
            event.setCancelled(true);
            return;
        }

        long combatTime = this.antiLogoutConfig.getPluginWrapper().getCombatTime().toMillis();
        victimUser.setLastAttackTime(System.currentTimeMillis() + combatTime);
        attackerUser.setLastAttackTime(System.currentTimeMillis() + combatTime);
    }

    @EventHandler
    public void playerCommandPreprocess(PlayerCommandPreprocessEvent event) {

    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {

    }

}