package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Enums.EmojiEnum;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStats;
import me.Cooltimmetje.Skuddbot.Enums.UserStats.UserStatsCats;
import me.Cooltimmetje.Skuddbot.Profiles.ProfileManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.MiscUtils;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;

import java.text.MessageFormat;

/**
 * Command that will generate a stats overview and print it out.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.61-ALPHA
 * @since v0.4.42-ALPHA
 */
public class StatsCommand {

    public static void run(IMessage message) { //!stats <user> <stat> <add/remove/set> <amount>
        String[] args = message.getContent().split(" ");
        if(args.length <= 2) {
            printStats(message);
        } else if(args.length > 4) {
            changeStat(message);
        } else {
            MessagesUtils.addReaction(message, "Incorrect arguments. Usage: !stats <user> [stat] [add/remove/set] [amount]", EmojiEnum.X, false);
        }
    }

    public static void changeStat(IMessage message){
        if(!ProfileManager.getDiscord(message.getAuthor(), message.getGuild(), true).hasElevatedPermissions()){
            MessagesUtils.addReaction(message, "You do not have permission to do that.", EmojiEnum.X, false);
            return;
        }

        String[] args = message.getContent().split(" ");
        if(message.getMentions().isEmpty()){
            MessagesUtils.addReaction(message, "No user specified.", EmojiEnum.X, false);
        }
        IUser user = message.getMentions().get(0);
        IGuild guild = message.getGuild();
        SkuddUser su = ProfileManager.getDiscord(user, guild, true);
        UserStats stat = null;
        try {
            stat = UserStats.valueOf(args[2].toUpperCase().replace("-", "_"));
        } catch (IllegalArgumentException e){
            MessagesUtils.addReaction(message, "This stat does not exist.", EmojiEnum.X, false);
            return;
        }
        String operation = args[3].toLowerCase();
        if(!operation.equals("add") && !operation.equals("remove") && !operation.equals("set")){
            MessagesUtils.addReaction(message, "The operation specified is not valid.", EmojiEnum.X, false);
            return;
        }
        if(!MiscUtils.isInt(args[4])){
            MessagesUtils.addReaction(message, "The amount specified is not a number.", EmojiEnum.X, false);
            return;
        }
        int amount = Integer.parseInt(args[4]);

        switch (operation){
            case "add":
                int initialAmount = Integer.parseInt(su.getStat(stat));
                int newAmount = initialAmount + amount;
                su.setStat(stat, newAmount+"");
                MessagesUtils.addReaction(message, MessageFormat.format("`{0}` has been added to stat `{1}` for user `{2}`.", amount, stat.toString(), user.getDisplayName(guild)), EmojiEnum.WHITE_CHECK_MARK, false);
                break;
            case "remove":
                initialAmount = Integer.parseInt(su.getStat(stat));
                newAmount = initialAmount - amount;
                su.setStat(stat, newAmount+"");
                MessagesUtils.addReaction(message, MessageFormat.format("`{0}` has been removed from stat `{1}` for user `{2}`.", amount, stat.toString(), user.getDisplayName(guild)), EmojiEnum.WHITE_CHECK_MARK, false);
                break;
            case "set":
                su.setStat(stat, amount+"");
                MessagesUtils.addReaction(message, MessageFormat.format("Stat `{1}` has been set to `{0}` for user `{2}`.", amount, stat.toString(), user.getDisplayName(guild)), EmojiEnum.WHITE_CHECK_MARK, false);
                break;
            default:
                break;
        }
    }

    public static void printStats(IMessage message){
        IUser user = message.getAuthor();
        IGuild guild = message.getGuild();
        SkuddUser su = ProfileManager.getDiscord(user.getStringID(), guild.getStringID(), false);
        boolean otherUser = message.getMentions().size() >= 1;

        if (otherUser) {
            user = message.getMentions().get(0);
            su = ProfileManager.getDiscord(user.getStringID(), guild.getStringID(), false);
        }

        if (su == null) {
            String debug = otherUser ? "**" + user.getDisplayName(guild) + "** has no stats yet." : "You have no stats yet.";
            MessagesUtils.addReaction(message, debug, EmojiEnum.X, false);
            return;
        }

        if (su.isStatsPrivate() && otherUser) {
            MessagesUtils.addReaction(message, "**" + user.getDisplayName(guild) + "** has made their stats private.", EmojiEnum.X, false);
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();

        eb.withAuthorIcon(user.getAvatarURL()).withAuthorName("Stats for: " + user.getDisplayName(guild));
        eb.withColor(MiscUtils.randomInt(0, 255), MiscUtils.randomInt(0, 255), MiscUtils.randomInt(0, 255));
        eb.withDesc("__Server:__ " + guild.getName());

        for (UserStatsCats category : UserStatsCats.values()){
            if(category.isShow()) {
                eb.appendField("\u200B", "__" + category.getName() + ":__",  false);

                for (UserStats stat : UserStats.values()) {
                    if (stat.isShow() && stat.getCategory() == category) {
                        if (stat == UserStats.TD_FAV_TEAMMATE) {
                            eb.appendField("__" + stat.getDescription() + ":__", su.getFavouriteTeammates(), true);
                        } else {
                            eb.appendField("__" + stat.getDescription() + ":__", su.getStat(stat) + " " + stat.getStatSuffix(), true);
                        }
                    }
                }
            }
        }


        message.getChannel().sendMessage("" , eb.build());
    }

}
