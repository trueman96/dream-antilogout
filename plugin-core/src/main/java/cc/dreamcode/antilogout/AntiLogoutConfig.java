package cc.dreamcode.antilogout;

import cc.dreamcode.notice.minecraft.MinecraftNoticeType;
import cc.dreamcode.notice.minecraft.bukkit.BukkitNotice;
import cc.dreamcode.platform.bukkit.component.configuration.Configuration;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;
import lombok.Getter;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@SuppressWarnings("FieldMayBeFinal")
@Configuration(child = "config.yml")
@Header("## dream-antilogout (Main-Config) ##")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class AntiLogoutConfig extends OkaeriConfig {

    private PluginWrapper pluginWrapper = new PluginWrapper();
    private MessageWrapper messageWrapper = new MessageWrapper();

    @Getter
    @SuppressWarnings("FieldMayBeFinal")
    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class PluginWrapper extends OkaeriConfig {

        @Comment("Debug pokazuje dodatkowe informacje do konsoli. Lepiej wyłączyć. :P")
        private boolean debug = true;

        private List<String> disallowedCommands = Arrays.asList(
                "ec",
                "heal",
                "feed",
                "repair",
                "repair all"
        );

        private Duration combatTime = Duration.ofSeconds(21), protectionTime = Duration.ofMinutes(5);

        private Map<UUID, Long> protectionSaved = new ConcurrentHashMap<>();

    }

    @Getter
    @SuppressWarnings("FieldMayBeFinal")
    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class MessageWrapper extends OkaeriConfig {

        private BukkitNotice usage = BukkitNotice.of(MinecraftNoticeType.CHAT, "&7Poprawne uzycie: &5{usage}");
        private BukkitNotice noPermission = BukkitNotice.of(MinecraftNoticeType.CHAT, "&cNie posiadasz uprawnien.");
        private BukkitNotice notPlayer = BukkitNotice.of(MinecraftNoticeType.CHAT, "&cNie jestes aby to zrobic.");

        private BukkitNotice playerNotFound = BukkitNotice.of(MinecraftNoticeType.CHAT, "&cPodanego gracza nie znaleziono.");
        private BukkitNotice cannotDoAtMySelf = BukkitNotice.of(MinecraftNoticeType.CHAT, "&cNie mozesz tego zrobic na sobie.");
        private BukkitNotice numberIsNotValid = BukkitNotice.of(MinecraftNoticeType.CHAT, "&cPodana liczba nie jest cyfra.");

        private BukkitNotice youHaveProtection = BukkitNotice.of(MinecraftNoticeType.CHAT,
                "&cPosiadasz aktualnie ochrone startową! Zostało &4{time}&c! Jeśli chcesz ją wyłączyć użyj &4/wylaczochrone&c.");

        private BukkitNotice attackedPlayerHasProtection = BukkitNotice.of(MinecraftNoticeType.CHAT,
                "&cTen gracz posiada aktualnie ochrone startową! Zostało &4{time}&c!");

    }

}