package me.Cooltimmetje.Skuddbot.Commands.Useless;

import me.Cooltimmetje.Skuddbot.Enums.DataTypes;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashMap;

/**
 * This class is responsible for the handling of the puppy command (has some alliasses), it will spit out one random puppy picture from the database.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.34-ALPHA
 * @since v0.4.34-ALPHA
 */
public class PuppyCommand {

    public static void run(IMessage message){
        String pictureURL = MiscUtils.getRandomMessage(DataTypes.PUPPY);
        boolean allowed = MiscUtils.randomCheck(pictureURL);

        while (!allowed){
            pictureURL = MiscUtils.getRandomMessage(DataTypes.PUPPY);
            allowed = MiscUtils.randomCheck(pictureURL);
        }

        MessagesUtils.sendPlain(":dog: " + pictureURL , message.getChannel(), false);
    }

}