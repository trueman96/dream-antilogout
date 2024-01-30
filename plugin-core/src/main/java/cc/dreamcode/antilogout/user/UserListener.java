package cc.dreamcode.antilogout.user;

import cc.dreamcode.antilogout.AntiLogoutConfig;
import cc.dreamcode.antilogout.event.AntilogoutPlayerDeathEvent;
import cc.dreamcode.antilogout.helper.AntiLogoutHelper;
import cc.dreamcode.utilities.TimeUtil;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.tasker.core.Tasker;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
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
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UserListener implements Listener {

    private final Tasker tasker;
    private final UserCache userCache;
    private final UserFactory userFactory;
    private final AntiLogoutConfig antiLogoutConfig;

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
                .acceptAsync(this.userCache::remove)
                .execute();
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        User victimUser = this.userCache.findUserByPlayer(victim);

        if ((killer == null || killer.equals(victim)) && victimUser.getLastAttackPlayer() != null && victimUser.isInCombat()) {
            killer = victimUser.getLastAttackPlayer();
        }

        if (killer == null) {
            victimUser.resetCombat();
            return;
        }

        User killerUser = this.userCache.findUserByPlayer(killer);
        User assistantUser = null;
        if (victimUser.getLastAssistPlayer() != null
                && victimUser.getLastAssistTime() > System.currentTimeMillis()
                && !victimUser.getLastAssistPlayer().getUniqueId().equals(killer.getUniqueId())) {

            Player assistant = victimUser.getLastAssistPlayer();
            assistantUser = this.userCache.findUserByPlayer(assistant);
        }

        victimUser.resetCombat();

        Bukkit.getPluginManager().callEvent(new AntilogoutPlayerDeathEvent(event, victimUser, killerUser, assistantUser));
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

        //todo: add combat time in cfg
        victimUser.setLastAttackTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(31));
        attackerUser.setLastAttackTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(31));

        if (victimUser.getLastAttackPlayer() != attacker) {
            victimUser.setLastAssistPlayer(victimUser.getLastAttackPlayer());
            victimUser.setLastAssistTime(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(60));
        }

        victimUser.setLastAttackPlayer(attacker);
        attackerUser.setLastAttackPlayer(victim);
    }

    @EventHandler
    public void playerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        //todo:
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        //todo: block interactions
    }

}