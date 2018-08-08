package me.Cooltimmetje.Skuddbot.Enums;

import lombok.Getter;

/**
 * This is to easily recall emoji's without going out and copying them.
 *
 * @author Tim (Cooltimmetje)
 * @version v0.4.31-ALPHA
 * @since v0.4-ALPHA-DEV
 */
@Getter
public enum EmojiEnum {

    WHITE_CHECK_MARK        ("✅", "white_check_mark"),
    ARROW_UP                ("⬆", "arrow_up"),
    WARNING                 ("⚠", "warning"),
    X                       ("❌", "x"),
    HOURGLASS_FLOWING_SAND  ("⏳", "hourglass_flowing_sand"),
    CROSSED_SWORDS          ("⚔", "crossed_swords"),
    EYES                    ("\uD83D\uDC40", "eyes"),
    MAILBOX_WITH_MAIL       ("\uD83D\uDCEC", "mailbox_with_mail");

    private String emoji;
    private String alias;

    EmojiEnum(String s, String s1){
        this.emoji = s;
        this.alias = s1;
    }

}
