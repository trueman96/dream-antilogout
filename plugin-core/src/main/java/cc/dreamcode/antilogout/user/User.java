package cc.dreamcode.antilogout.user;

import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import eu.okaeri.persistence.document.Document;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
public class User {

    private final UUID uniqueId;
    private String nickname;

    public User(UUID uniqueId, String nickname) {
        this.uniqueId = uniqueId;
        this.nickname = nickname;
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
}
