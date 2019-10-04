package me.Cooltimmetje.Skuddbot.Commands;

import me.Cooltimmetje.Skuddbot.Profiles.MySqlManager;
import me.Cooltimmetje.Skuddbot.Profiles.ServerManager;
import me.Cooltimmetje.Skuddbot.Profiles.SkuddUser;
import me.Cooltimmetje.Skuddbot.Utilities.EmojiHelper;
import me.Cooltimmetje.Skuddbot.Utilities.MessagesUtils;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableArrayGenerator;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableDrawer;
import me.Cooltimmetje.Skuddbot.Utilities.TableUtilities.TableRow;
import sx.blah.discord.handle.obj.IMessage;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * Show the XP leaderboard of the server.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.5-ALPHA
 * @since v0.1-ALPHA
 */
public class LeaderboardCommand {

    /**
     * CMD: Show the XP leaderboard of the current server.
     *
     * @param message This is the message that triggered the command.
     */
    public static void run(IMessage message){
        message.getChannel().toggleTypingStatus();
        long startTime = System.currentTimeMillis();
        ServerManager.getServer(message.getGuild().getStringID()).save();
        HashMap<Integer,SkuddUser> discord = MySqlManager.getTopDiscord(message.getGuild().getStringID());
        HashMap<Integer,SkuddUser> twitch = MySqlManager.getTopTwitch(message.getGuild().getStringID());

        TreeMap<Integer,SkuddUser> top = new TreeMap<>();
        for(int i : discord.keySet()){
            top.put(i,discord.get(i));
        }
        for(int i : twitch.keySet()){
            top.put(i,twitch.get(i));
        }


        int i = 0;
        TableArrayGenerator tag = new TableArrayGenerator(new TableRow("Pos", "Name", "Level", "Progress", "Account Type"));
        for(int i2 : top.descendingKeySet()){
            SkuddUser su = top.get(i2);
            int[] levelInfo = su.calcXP(false, null);
            int progress = (int) (((double)levelInfo[0] / (double)levelInfo[2])*100);
            tag.addRow(new TableRow((i+1)+"", su.getFullName(), levelInfo[3]+"", progress + "%", su.getAccountType().getFullName()));

            if(i<9){
                i++;
            } else {
                break;
            }
        }

        String leaderboard = new TableDrawer(tag).drawTable();
        boolean displayLinkInfo = leaderboard.contains("(not linked)");

        MessagesUtils.sendPlain(EmojiHelper.getEmoji("xp_icon") + "** Leaderboard** | **" + message.getGuild().getName() + "**\n\n```\n" + leaderboard + "```\n" + (displayLinkInfo ? "**PRO-TIP:** You might have more XP if you are marked as \"not linked\", type `!twitch` to get started with linking your accounts! It's really easy to do, promise, and you'll get a nice tasty 1000xp free! Woo!\n" : "") + "Generated in `" + (System.currentTimeMillis() - startTime) + " ms`", message.getChannel(), false);
}

}
