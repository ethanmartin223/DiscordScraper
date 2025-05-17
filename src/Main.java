import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

//add bot url
//https://discord.com/api/oauth2/authorize?client_id=1373366130474877008&permissions=17179943936&scope=bot%20applications.commands

public class Main {
//    static {
//        ConsoleHandler handler = new ConsoleHandler();
//        handler.setLevel(Level.ALL);
//        Logger log = LogManager.getLogManager().getLogger("");
//        log.addHandler(handler);
//        log.setLevel(Level.ALL);
//        System.setProperty("javax.net.debug","all");
//    }

    public static void main(String[] args) throws IOException {
        String url = "https://discord.com/channels/1373341839054213171/1373342049763590214";

        DiscordScraper disc = new DiscordScraper(url);

        disc.scrape();
    }
}

