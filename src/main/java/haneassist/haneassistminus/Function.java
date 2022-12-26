package haneassist.haneassistminus;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import static haneassist.haneassistminus.HaneAssistMinus.PREFIX;

public class Function {
    public static void sendCopyMessage(Player p, String text, String copyStr){
        TextComponent msg = new TextComponent(PREFIX + "§r"+text);
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, copyStr));
        p.spigot().sendMessage(msg);
    }
}
