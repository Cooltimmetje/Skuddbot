package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * When you ping, it pongs!
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.01-ALPHA-DEV
 * @since v0.3-ALPHA-DEV
 */
public class PingCommand {

    /**
     * PONG MOTHERFUCKER!
     *
     * @param message The message that pinged.
     */
    public static void run(IMessage message){
        MessagesUtils.sendSuccess((Constants.awesomePing.getOrDefault(message.getAuthor().getStringID(), "PONG!")), message.getChannel());
    }

}
