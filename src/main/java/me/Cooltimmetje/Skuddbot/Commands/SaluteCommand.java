package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IEmoji;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashMap;
import java.util.List;

/**
 * o7
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.2-ALPHA
 * @since v0.4.01-ALPHA-DEV
 */
public class SaluteCommand {

    static HashMap<String,Long> cooldown = new HashMap<>();

    public static void run(IMessage message) {
        IGuild guild = message.getGuild();
        List<IEmoji> emojis = guild.getEmojis();

        if(message.getContent().endsWith("-tc") && Constants.adminUser.contains(message.getAuthor().getStringID())){
            boolean newState = !Boolean.parseBoolean(Constants.config.get("salute_cooldown"));
            Constants.config.put("salute_cooldown", newState+"");
            MessagesUtils.addReaction(message, "Cooldown toggled: " + newState, EmojiEnum.WHITE_CHECK_MARK);
            MySqlManager.saveGlobal("salute_cooldown", newState+"");
        } else {
            if(cooldown.containsKey(message.getAuthor().getStringID())){
                if(System.currentTimeMillis() - cooldown.get(message.getAuthor().getStringID()) < 30000){
                    if(Boolean.parseBoolean(Constants.config.get("salute_cooldown"))) {
                        return;
                    }
                }
            }

            if (message.getAuthor().getLongID() == 91949596737011712L) {
                MessagesUtils.sendPlain(Main.getInstance().getSkuddbot().getGuildByID(233573247021481995L).getEmojiByID(292331476362723328L).toString() + "7", message.getChannel(), false);
            } else {
                int chance = MiscUtils.randomInt(0, 100);
                if (chance <= 5) {
                    MessagesUtils.sendPlain(MiscUtils.randomStringWithChars(MiscUtils.randomInt(10, 200)), message.getChannel(), false);
                } else {
                    MessagesUtils.sendPlain(((emojis.size() == 0) ? ("o") : (emojis.get(MiscUtils.randomInt(0, emojis.size())).toString())) + "7", message.getChannel(), false);
                }
            }

            cooldown.put(message.getAuthor().getStringID(), System.currentTimeMillis());
        }
    }

}

