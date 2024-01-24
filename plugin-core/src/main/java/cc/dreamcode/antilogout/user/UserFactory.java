package cc.dreamcode.antilogout.user;

import java.util.UUID;
import java.util.function.BiFunction;

public class UserFactory {

    private final BiFunction<UUID, String, User> createFunction;

    public UserFactory() {
        this.createFunction = User::new;
    }

    public User create(final UUID uniqueId, final String nickname) {
        return this.createFunction.apply(uniqueId, nickname);
    }

}