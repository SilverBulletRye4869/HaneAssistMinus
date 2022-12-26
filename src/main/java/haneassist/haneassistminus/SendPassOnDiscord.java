package haneassist.haneassistminus;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

import java.util.UUID;

public class SendPassOnDiscord extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getAuthor().isBot())return;
        if(!e.getChannel().getId().equals(HaneAssistMinus.getPass_channnel()))return;
        TextChannel channel = HaneAssistMinus.getJDA().getTextChannelById(HaneAssistMinus.getPass_channnel());
        String msg = e.getMessage().getContentDisplay();
        if(HaneAssistMinus.oneTimePassWord.containsKey(msg)){
            UUID u = HaneAssistMinus.oneTimePassWord.get(msg);
            channel.sendMessage("あなたは、"+u.toString()+"("+ Bukkit.getOfflinePlayer(u).getName()+")ですね").queue();
            HaneAssistMinus.oneTimePassWord.remove(msg);
            HaneAssistMinus.oneTimePassWordMemo.remove(u);
        }else{
            channel.sendMessage("そのパスワードを持つユーザは見つかりません！").queue();
        }
    }
}
