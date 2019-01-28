package me.Cooltimmetje.Skuddbot.Minigames.FreeForAll;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Profiles.Server;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Utilities.Constants;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionRemoveEvent;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Managing of the different FFA handlers.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.61-ALPHA
 * @since v0.4.4-ALPHA
 */
public class FFAManager {

    public static void clearCooldowns(){
        for(Server server : ServerManager.servers.values()){
            server.getFfaHandler().cooldowns.clear();
        }
    }

    public static void run(IMessage message){
        String[] args = message.getContent().split(" ");
        if(args.length > 1){
            if(args[1].equalsIgnoreCase("-fix") && Constants.adminUser.contains(message.getAuthor().getStringID())){
                ServerManager.getServer(message.getGuild().getStringID()).setFfaHandler(new FFAHandler(message.getGuild().getStringID()));
                MessagesUtils.addReaction(message, "Created new FFA handler for this server.", EmojiEnum.WHITE_CHECK_MARK, false);
                return;
            }
        }
        ServerManager.getServer(message.getGuild().getStringID()).getFfaHandler().enter(message);
    }

    @EventSubscriber
    public void onReaction(ReactionAddEvent event){
        if(!event.getChannel().isPrivate()) {
            ServerManager.getServer(event.getMessage().getGuild().getStringID()).getFfaHandler().reactionAdd(event);
        }
    }

    @EventSubscriber
    public void onReactionRemove(ReactionRemoveEvent event){
        if(!event.getChannel().isPrivate()) {
            ServerManager.getServer(event.getMessage().getGuild().getStringID()).getFfaHandler().reactionRemove(event);
        }
    }

}
