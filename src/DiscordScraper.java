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
    private final String DISCORD_API_BOT_KEY = "MTM3MzM2NjEzMDQ3NDg3NzAwOA.G1p2nv.hIVp5MHt-fAEFBKZLFZ9WItf2vNZkNktLu8kOY";


    // ------------------- // Local vars // ------------------- //
    private String channelUrl; // the entire url for the discord server+sub channel
    private String channelID; // the number for the sub channel with text you want to scrape


    // ------------------- // Constructor // ------------------- //
    public DiscordScraper(String channelUrl) {
        this.channelUrl = channelUrl;
        System.out.println(channelUrl);

        Pattern pattern = Pattern.compile("channels/\\d+/(\\d+)");
        Matcher matcher = pattern.matcher(channelUrl);
        if (matcher.find()) {
            channelID = matcher.group(1);
        } else {
            throw new IllegalArgumentException("Invalid Discord channel URL: " + channelUrl);
        }
        System.out.println(channelID);
    }


    // ------------------- // Public Methods // ------------------- //
    public void scrape() throws IOException {
        HashMap requestParams = new HashMap<String, String>();
        System.out.println(makeRequest(constructGetMessageUrlFromAPI(10, 0), "GET", requestParams));
    }

    public String constructGetMessageUrlFromAPI(int numberOfMessages, int afterTime) {
        return DISCORD_API_URL+"/channels/"+channelID+"/messages?limit="+numberOfMessages+"&after="+afterTime;
    }

    // ------------------- // Private Methods // ------------------- //
    //method is GET, POST, etc.
    private String makeRequest(String stringUrl, String method, Map<String,String> params) throws IOException {

        //connection settings
        URL url = new URL(stringUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(method);
        con.setRequestProperty("Authorization", "Bot "+DISCORD_API_BOT_KEY);
        con.setRequestProperty("User-Agent", "DiscordBot ($url, $versionNumber)");
        con.setRequestProperty("Content-Type", "application/json");

        con.setDoOutput(true);

//        // write data for params to the request
//        DataOutputStream out = new DataOutputStream(con.getOutputStream());
//        out.writeBytes(ParameterStringBuilder.getParamsString(params));
//        out.flush();
//        out.close();

        int status = con.getResponseCode(); // get http status code
        System.out.println("DEBUG: "+status);

        //if error print out the verbose error returned by request
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
            return resultString.length() > 0
                    ? resultString.substring(0, resultString.length() - 1)
                    : resultString;
        }
    }


}
