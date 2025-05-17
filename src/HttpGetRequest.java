import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpGetRequest {

    public static void main(String[] args) {
        try {
            String baseUrl = "https://example.com/";
            Map<String, String> parameters = new HashMap<>();
            parameters.put("param1", "value1");
            parameters.put("param2", "value2");

            String urlWithParams = buildUrlWithParameters(baseUrl, parameters);
            URL url = new URL(urlWithParams);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("Response Body: " + response.toString());
            } else {
                System.out.println("GET request failed");
            }
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String buildUrlWithParameters(String baseUrl, Map<String, String> parameters) throws IOException {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        if (!parameters.isEmpty()) {
            urlBuilder.append("?");
            boolean firstParam = true;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                if (!firstParam) {
                    urlBuilder.append("&");
                }
                urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                urlBuilder.append("=");
                urlBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
                firstParam = false;
            }
        }
        return urlBuilder.toString();
    }
}