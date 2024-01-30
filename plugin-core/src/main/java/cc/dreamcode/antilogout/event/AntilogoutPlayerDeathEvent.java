package cc.dreamcode.antilogout.event;

import cc.dreamcode.antilogout.user.User;
import lombok.Getter;
import org.bukkit.event.entity.PlayerDeathEvent;

@Getter
public class AntilogoutPlayerDeathEvent extends PlayerDeathEvent {

    private final User victim, killer;
    private final User assistant;

    public AntilogoutPlayerDeathEvent(PlayerDeathEvent event, User victim, User killer, User assistant) {
        super(event.getEntity(), event.getDrops(), event.getDroppedExp(), event.getNewExp(),
                event.getNewTotalExp(), event.getNewLevel(), event.getDeathMessage());
        this.victim = victim;
        this.killer = killer;
        this.assistant = assistant;
    }

}