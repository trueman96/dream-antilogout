package cc.dreamcode.antilogout;

import cc.dreamcode.notice.minecraft.MinecraftNoticeType;
import cc.dreamcode.notice.minecraft.bukkit.BukkitNotice;
import cc.dreamcode.platform.bukkit.component.configuration.Configuration;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration(child = "config.yml")
@Header("## dream-antilogout (Main-Config) ##")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class AntiLogoutConfig extends OkaeriConfig {

    private PluginWrapper pluginWrapper = new PluginWrapper();
    private MessageWrapper messageWrapper = new MessageWrapper();

    @Getter
    @Setter
    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class PluginWrapper extends OkaeriConfig {

        @Comment("Debug pokazuje dodatkowe informacje do konsoli. Lepiej wyłączyć. :P")
        private boolean debug = true;

    }

    @Getter
    @Setter
    @Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
    public static class MessageWrapper extends OkaeriConfig {

        private BukkitNotice usage = new BukkitNotice(MinecraftNoticeType.CHAT, "&7Poprawne uzycie: &5{usage}");
        private BukkitNotice noPermission = new BukkitNotice(MinecraftNoticeType.CHAT, "&cNie posiadasz uprawnien.");
        private BukkitNotice notPlayer = new BukkitNotice(MinecraftNoticeType.CHAT, "&cNie jestes aby to zrobic.");

        private BukkitNotice playerNotFound = new BukkitNotice(MinecraftNoticeType.CHAT, "&cPodanego gracza nie znaleziono.");
        private BukkitNotice cannotDoAtMySelf = new BukkitNotice(MinecraftNoticeType.CHAT, "&cNie mozesz tego zrobic na sobie.");
        private BukkitNotice numberIsNotValid = new BukkitNotice(MinecraftNoticeType.CHAT, "&cPodana liczba nie jest cyfra.");

    }

}