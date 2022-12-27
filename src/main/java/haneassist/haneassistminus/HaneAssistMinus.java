package haneassist.haneassistminus;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.UUID;

public final class HaneAssistMinus extends JavaPlugin {
    private static JavaPlugin plugin = null;
    private static JDA jda = null;
    static HashMap<String,UUID> oneTimePassWord = new HashMap<>();
    static HashMap<UUID,String> oneTimePassWordMemo = new HashMap<>(); //JavaのMapは双方向じゃないので逆引き用
    public static final String PREFIX = "§e§l[HaneAssist]";
    private static String pass_channnel = null;
    private static Firestore db = null;
    @Override
    public void onEnable() {
        plugin = this;
        this.saveDefaultConfig();
        try {
            startBot();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        try {
            startDb();
        } catch (IOException e) {
            e.printStackTrace();
        }

        PluginCommand command = getCommand("haneauth");
        command.setExecutor(new Command());
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(jda!=null){
            try {
                jda.shutdownNow();
            }catch (NoClassDefFoundError e){
                System.err.println("[HaneAuth]Botのシャットダウンに失敗しました");
                System.err.println("[HaneAuth]reload時にはbotが多重起動する可能性があります。");
            }
        }
    }

    static JDA getJDA(){return jda;}
    static JavaPlugin getInstance(){return plugin;}
    static String getPass_channnel(){return pass_channnel;}
    static Firestore getDb(){return db;}

    private void startBot() throws LoginException {
        @NotNull FileConfiguration config = this.getConfig();
        String token = config.getString("token");
        pass_channnel = config.getString("pass_channel");
        if(token==null || pass_channnel == null){
            System.err.println("[HaneAuth]Botのtoken、チャンネルIDのいずれか又は両方が不定です");
            return;
        }
        try {
            jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES)
                    .setRawEventsEnabled(true)
                    .setActivity(Activity.playing("活動中"))
                    .addEventListeners(new SendPassOnDiscord())
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    private void startDb() throws IOException {
        @NotNull FileConfiguration config = this.getConfig();
        // Use a service account
        if(config.get("firebase_json") == null) {
            System.err.println("firebaseJsonのパスを指定してください");
            return;
        }
        InputStream serviceAccount = new FileInputStream(config.getString("firebase_json"));
        if(serviceAccount == null) {
            System.err.println("firebaseJsonをinputできませんでした");
            return;
        }
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp.initializeApp(options);

        db = FirestoreClient.getFirestore();
    }

}
