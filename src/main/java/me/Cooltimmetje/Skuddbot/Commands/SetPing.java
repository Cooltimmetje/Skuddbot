package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.handle.obj.IMessage;

/**
 * This class will update the ping message for awesome users.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.3-ALPHA-DEV
 * @since v0.3-ALPHA-DEV
 */
public class SetPing {

    public static void run(IMessage message){
        if(Constants.awesomeUser.contains(message.getAuthor().getID())){ //Check if awesome
            String[] args = message.getContent().split(" "); //Split arguments
            if(args.length > 1){ //Check arguments
                StringBuilder sb = new StringBuilder();
                for(int i=1; i<args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
                String input = sb.toString().trim();
                String trimmed = input.substring(0, Math.min(input.length(), 512)); //Trim message
                MySqlManager.updateAwesome(message.getAuthor().getID(), trimmed); //Update database
                Constants.awesomePing.put(message.getAuthor().getID(), trimmed); //Save to memory
                if(input.length() > 512){
                    MessagesUtils.sendSuccess("Your ping message was updated to: `" + trimmed + "`!\n" +
                            ":warning: Your message exceeded the __512 character limit__, therefore we have trimmed it down to that limit.", message.getChannel());
                } else {
                    MessagesUtils.sendSuccess("Your ping message was updated to: `" + trimmed + "`!", message.getChannel());
                }
            } else {
                MessagesUtils.sendError("Not enough arguments: !setping <message>", message.getChannel());
            }
        }
    }

}
