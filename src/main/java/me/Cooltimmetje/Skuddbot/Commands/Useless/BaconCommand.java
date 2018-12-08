package me.Cooltimmetje.Skuddbot.Commands.Useless;

import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * mmmmmmmmmmmmmm..... Bacon....
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.41-ALPHA
 * @since v0.4.41-ALPHA
 */
public class BaconCommand {

    public static void run(IMessage message){
        String pictureURL;
        boolean allowed;

        do {
            pictureURL = MiscUtils.getRandomMessage(DataTypes.BACON);
            allowed = MiscUtils.randomCheck(pictureURL);
        } while (!allowed);

        MessagesUtils.sendPlain(":bacon: " + pictureURL , message.getChannel(), false);
    }

}
