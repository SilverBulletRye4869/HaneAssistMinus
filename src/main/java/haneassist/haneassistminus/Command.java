package haneassist.haneassistminus;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

import static haneassist.haneassistminus.HaneAssistMinus.PREFIX;
import static haneassist.haneassistminus.HaneAssistMinus.getDb;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 2) {
            if(!sender.isOp())return true;
            if (!args[0].equals("test")) return true;
            switch (args[1]) {
                case "discord":
                    @NotNull FileConfiguration config = HaneAssistMinus.getInstance().getConfig();
                    String channelId = config.getString("pass_channel");
                    if (channelId == null) {
                        sender.sendMessage("チャンネルを設定してください");
                        return true;
                    }
                    TextChannel channel = HaneAssistMinus.getJDA().getTextChannelById(channelId);
                    if (channel == null) {
                        sender.sendMessage("チャンネルが見つかりません");
                        return true;
                    }
                    channel.sendMessage("This is HaneAuthTestMessage").queue();
                    sender.sendMessage("send success; Discord");
                    break;
                case "firebase":
                    CollectionReference collection = getDb().collection("discordID");
                    DocumentReference docRef = collection.document("test");

                    HashMap<String,Object> data = new HashMap<>();
                    data.put("Test","success");
                    docRef.set(data);

                    sender.sendMessage("send success; Firebase");
                    break;
            }
        } else {
            if (!(sender instanceof BlockCommandSender)) {
                sender.sendMessage(PREFIX + "§cこのコマンドはコマブロのみ実行可能です");
                return true;
            }
            BlockCommandSender cb = (BlockCommandSender) sender;
            Player p = PlayerSearch.getNearbyPlayer(cb.getBlock().getLocation());
            UUID u = p.getUniqueId();

            String pass = null;
            if (HaneAssistMinus.oneTimePassWordMemo.containsKey(u)){
                pass = HaneAssistMinus.oneTimePassWordMemo.get(u);
            }else {
                int cnt = 0;
                while (cnt < 100) { //失敗した場合は100回まで試行
                    String kari = String.valueOf((long) (Math.random() * Long.MAX_VALUE)); //仮で数値を生成
                    if (!HaneAssistMinus.oneTimePassWord.containsKey(kari)) { //既に使用されてなければbreak;
                        pass = kari;
                        break;
                    }
                    cnt++;
                }

                if (pass == null) {
                    p.sendMessage(PREFIX + "§c§lワンタイムパスワードの生成に失敗しました");
                }
                pass = "0000000000000000000" + pass;
                pass = pass.substring(pass.length() - 20);

                HaneAssistMinus.oneTimePassWord.put(pass, u);
                HaneAssistMinus.oneTimePassWordMemo.put(u, pass);
                String finalPass = pass;
                Bukkit.getScheduler().runTaskLater(HaneAssistMinus.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        HaneAssistMinus.oneTimePassWord.remove(finalPass); //1時間後に削除
                        HaneAssistMinus.oneTimePassWordMemo.remove(u);
                    }
                }, 20 * 3600);
            }
            Function.sendCopyMessage(p, "§aワンタイムパスワードは§d§l" + pass + "§a§lです。§r§e(クリックでコピー)", pass);
            p.sendMessage(PREFIX + "§c有効時間本番号の初回発行時より1時間です");

        }
        return true;
    }
}