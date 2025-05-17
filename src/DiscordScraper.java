// ------------------- // Import // ------------------- //
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// ------------------- // Static Imports // ------------------- //
import static jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle.parameters;


public class DiscordScraper {

    // ------------------- // Constants // ------------------- //
    public final String DISCORD_API_URL =
            "https://discord.com/api/v9";


    // ------------------- // Settings // ------------------- //
    //Application/bot settings
    //https://discord.com/developers/applications/1373366130474877008

    public final int SCRAPE_FREQUENCY = 1000;//ms
    private final String DISCORD_API_BOT_KEY = "";

    // ------------------- // Local vars // ------------------- //
    private String channelUrl; // the entire url for the discord server+sub channel
    private String channelID; // the number for the sub channel with text you want to scrape
    private long timeStartedScraping; // the time which the bot has started looking for messages
    private long timeToNextScrape; // time to scrape next
    private boolean continueScraping = false; // weather the bot should be scraping messages
    private String lastMessageSent; //used as a hash to check against to make sure there is a new message;

    // ------------------- // Constructor // ------------------- //
    public DiscordScraper(String channelUrl) {
        this.channelUrl = channelUrl;
        System.out.println(channelUrl);

        channelID = regex("channels/\\d+/(\\d+)", channelUrl);
        System.out.println(channelID);
    }


    // ------------------- // Public Methods // ------------------- //
    public void scrape() throws IOException {
        timeToNextScrape = System.currentTimeMillis();
        continueScraping = true;

        String messageScraped = "";

        while (continueScraping) {
            if (timeToNextScrape < System.currentTimeMillis()) {
                messageScraped = getMostRecentMessage();
                if (!messageScraped.equals(lastMessageSent)) {
                    lastMessageSent = messageScraped;
                    System.out.println(timeToNextScrape+": "+lastMessageSent);
                }
                timeToNextScrape = System.currentTimeMillis()+SCRAPE_FREQUENCY;
            }
        }
    }

    public String getMostRecentMessage() throws IOException {
        String mostRecentMessageJSON =
                makeRequest(constructGetMessageUrlFromAPI(1), "GET");
        return regex("\"content\"\\:\"(.*?)\"",mostRecentMessageJSON);
    }

    public String constructGetMessageUrlFromAPI(int numberOfMessages) {
        return DISCORD_API_URL+"/channels/"+channelID+"/messages?limit="+numberOfMessages;
    }

    // ------------------- // Private Methods // ------------------- //
    //method is GET, POST, etc.
    private String makeRequest(String stringUrl, String method) throws IOException {

        //connection settings
        URL url = new URL(stringUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("Authorization", "Bot "+DISCORD_API_BOT_KEY);
        con.setRequestProperty("User-Agent", "DiscordBot ($url, $versionNumber)");
        con.setRequestProperty("Content-Type", "application/json");

        con.setDoOutput(true);

        //if error print out the verbose error returned by request
        int status = con.getResponseCode(); // get http status code
        Reader streamReader = null;
        if (status > 299) {
            streamReader = new InputStreamReader(con.getErrorStream());
        } else {
            streamReader = new InputStreamReader(con.getInputStream());
        }

        //read the output from the request
        BufferedReader in = new BufferedReader(streamReader);
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        //return string
        return content.toString();
    }

    // ------------------- // Helper Classes // ------------------- //
    private static class ParameterStringBuilder {
        public static String getParamsString(Map<String, String> params)
                throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            }

            String resultString = result.toString();
            return !resultString.isEmpty()
                    ? resultString.substring(0, resultString.length() - 1)
                    : resultString;
        }
    }

    // ---------------- // Helper Methods // --------------- //
    private String regex(String pattern, String data) {
        Pattern pat = Pattern.compile(pattern);
        Matcher matcher = pat.matcher(data);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("Invalid Regex Data: " + data);
        }
    }

}
