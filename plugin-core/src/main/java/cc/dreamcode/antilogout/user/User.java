package cc.dreamcode.antilogout.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
public class User {

    private final UUID uniqueId;
    private String nickname;

    private long lastAttackTime, protection;

    public User(UUID uniqueId, String nickname) {
        this.uniqueId = uniqueId;
        this.nickname = nickname;
    }

    public boolean isInCombat() {
        return this.lastAttackTime > System.currentTimeMillis();
    }

    public void resetCombat() {
        this.lastAttackTime = 0L;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.uniqueId);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof User)) {
            return false;
        }

        User user = (User) object;
        return this.uniqueId.equals(user.uniqueId);
    }

    public boolean hasProtection() {
        return this.protection > System.currentTimeMillis();
    }
}
