package me.Cooltimmetje.Skuddbot.Commands;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiManager;
import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Main;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.*;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.reaction.ReactionAddEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This allows people to challenge each other. Winner is picked at random.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.31-ALPHA
 * @since v0.4.3-ALPHA
 */

public class ChallengeCommand {

    public static int cooldown = 300; //cooldown in seconds
    public static int xpReward = 50;

    public static HashMap<String, Long> cooldowns = new HashMap<>();

    public static HashMap<String, String> senderMessage = new HashMap<>();
    public static HashMap<String, String> botMessage = new HashMap<>();
    public static HashMap<IMessage, IMessage> botIMessage = new HashMap<>();

    public static HashMap<IUser, IUser> outstandingChallenges = new HashMap<>();

    public static void run(IMessage message) {
        if(cooldowns.containsKey(message.getAuthor().getStringID())){
            if((System.currentTimeMillis() - cooldowns.get(message.getAuthor().getStringID())) < (cooldown * 1000)){
                MessagesUtils.addReaction(message, "Hold on there, **" + message.getAuthor().mention() + "**, you're still wounded from the last fight.", EmojiEnum.HOURGLASS_FLOWING_SAND);
                return;
            }
        }

        if(message.getMentions().isEmpty()){
            MessagesUtils.addReaction(message, "You didn't specify who you want to challenge.", EmojiEnum.X);
            return;
        }

        if(message.getMentions().get(0) == message.getAuthor()){
            MessagesUtils.addReaction(message, "You can't challenge yourself.", EmojiEnum.X);
            return;
        }

        if(message.getMentions().get(0) == Main.getInstance().getSkuddbot().getOurUser()){
            MessagesUtils.addReaction(message, "You can't challenge me!", EmojiEnum.X);
            return;
        }

        if(outstandingChallenges.get(message.getMentions().get(0)) == message.getAuthor()){ //Challenge was accepted
            fight(message.getMentions().get(0),message.getAuthor(), message, message.getChannel());
        } else { //Challenge was not accepted.
            if(senderMessage.containsKey(message.getAuthor().getStringID())) {
                message.getChannel().bulkDelete(new ArrayList<>(Arrays.asList(
                        Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(senderMessage.get(message.getAuthor().getStringID()))),
                        Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(botMessage.get(message.getAuthor().getStringID())))
                )));
            }
            outstandingChallenges.put(message.getAuthor(), message.getMentions().get(0));

            senderMessage.put(message.getAuthor().getStringID(), message.getStringID());

            IMessage messageBot = MessagesUtils.sendPlain(EmojiEnum.CROSSED_SWORDS.getEmoji() + " **" + message.getAuthor().getDisplayName(message.getGuild()) + "** has challenged **" + message.getMentions().get(0).getDisplayName(message.getGuild()) + "** to a fight! " +
                    "To accept type `!challenge @" + message.getAuthor().getName()  + "#" + message.getAuthor().getDiscriminator() + "` or click the reaction below!", message.getChannel(), false);
            messageBot.addReaction(EmojiManager.getForAlias(EmojiEnum.CROSSED_SWORDS.getAlias()));

            botMessage.put(message.getAuthor().getStringID(), messageBot.getStringID());
            botIMessage.put(messageBot, message);
        }
    }

    @EventSubscriber
    public void onReaction(ReactionAddEvent event){
        Logger.info("Reaction added.");
        IUser challengerOne = botIMessage.get(event.getReaction().getMessage()).getAuthor();
        if(botMessage.containsValue(event.getReaction().getMessage().getStringID())){
            Logger.info("Message recognized as challenge.");
            if(event.getReaction().getUserReacted(Main.getInstance().getSkuddbot().getOurUser())){
                Logger.info("We reacted");
                if(event.getReaction().getUserReacted(outstandingChallenges.get(challengerOne))){ //Accepted
                    Logger.info("User valid, running fight.");
                    IUser challengerTwo = outstandingChallenges.get(challengerOne);
                    fight(challengerOne, challengerTwo, null, event.getReaction().getMessage().getChannel());
                }
            }
        }
    }

    public static void fight(IUser challengerOne, IUser challengerTwo, IMessage message, IChannel channel){
        ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);

        exec.schedule(() -> {
            ArrayList<IMessage> toDelete = new ArrayList<>(Arrays.asList(
                    Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(senderMessage.get(challengerOne.getStringID()))),
                    Main.getInstance().getSkuddbot().getMessageByID(Long.parseLong(botMessage.get(challengerOne.getStringID())))));
            if(message != null){
                toDelete.add(message);
            }
            channel.bulkDelete(toDelete);

            senderMessage.remove(challengerOne.getStringID());
            botMessage.remove(challengerOne.getStringID());
            outstandingChallenges.remove(challengerOne);
        },10, TimeUnit.MILLISECONDS);

        cooldowns.put(challengerOne.getStringID(), System.currentTimeMillis());
        cooldowns.put(challengerTwo.getStringID(), System.currentTimeMillis());

        IUser preWinner = (MiscUtils.randomInt(1,2) == 1) ? challengerOne : challengerTwo;
        if(Constants.rigged.contains(challengerOne.getStringID())){
            preWinner = challengerOne;
            Constants.rigged.remove(challengerOne.getStringID());
        }
        if(Constants.rigged.contains(challengerTwo.getStringID())){
            preWinner = challengerTwo;
            Constants.rigged.remove(challengerTwo.getStringID());
        }
        final IUser winner = preWinner;

        IMessage messageBot = MessagesUtils.sendPlain(EmojiEnum.CROSSED_SWORDS.getEmoji() + " **" + challengerOne.getDisplayName(channel.getGuild()) + "** and **" +
                challengerTwo.getDisplayName(channel.getGuild()) + "** go head to head in the Rayscooter arena, who will win? 3... 2... 1... **FIGHT!**", channel, false);
        messageBot.getChannel().toggleTypingStatus();

        exec.schedule(() -> {
            try {
                IMessage messageResult = MessagesUtils.sendPlain(EmojiEnum.CROSSED_SWORDS.getEmoji() + " The crowd goes wild but suddenly a scream of victory sounds! **" + winner.getDisplayName(channel.getGuild()) + "** has won the fight!\n\n" +
                        winner.getDisplayName(channel.getGuild()) + ": *+" + xpReward + " " + EmojiHelper.getEmoji("xp_icon") + "*", channel, false);

                SkuddUser user = ProfileManager.getDiscord(winner.getStringID(), message.getGuild().getStringID(), true);

                user.setXp(user.getXp() + xpReward);
                user.calcXP(false, messageResult);
            } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
                e.printStackTrace();
            }
        }, 5, TimeUnit.SECONDS);

        if(senderMessage.isEmpty()){ //Just for memory sake
            botIMessage.clear();
        }
    }
}
