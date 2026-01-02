import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class FirebaseHandler {
    private static String DATABASE_URL;

    static {
        try (InputStream input = new FileInputStream("config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            DATABASE_URL = prop.getProperty("firebase.url");
        } catch (IOException ex) {
            System.err.println("Error: config.properties not found! Please create it.");
            DATABASE_URL = ""; 
        }
    }
    public static void saveDevice(Device d) {
        new Thread(() -> {
            try {
                String path = "devices/" + d.getId() + ".json";
                URL url = new URL(DATABASE_URL + path);
                
                String json = String.format(
                    "{\"name\": \"%s\", \"category\": \"%s\", \"type\": \"%s\", \"value\": %d, \"isPinned\": %b}",
                    d.getName(), d.getCategory(), d.getType(), d.getValue(), d.isPinned()
                );
                sendRequest(url, "PUT", json);
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    public static void updateDeviceValue(String deviceId, int newValue) {
        new Thread(() -> {
            try {
                String path = "devices/" + deviceId + "/value.json";
                URL url = new URL(DATABASE_URL + path);
                sendRequest(url, "PUT", String.valueOf(newValue));
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    public static String loadAllDevices() throws Exception {
        URL url = new URL(DATABASE_URL + "devices.json");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        
        if (conn.getResponseCode() == 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) response.append(line);
            br.close();
            return response.toString();
        }
        return null;
    }

    private static void sendRequest(URL url, String method, String jsonPayload) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        conn.getResponseCode();
    }

    public static void deleteDevice(String deviceId) {
        new Thread(() -> {
            try {
                String path = "devices/" + deviceId + ".json";
                URL url = new URL(DATABASE_URL + path);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.getResponseCode();
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    public static void updateDeviceName(String deviceId, String newName) {
        new Thread(() -> {
            try {
                String path = "devices/" + deviceId + "/name.json";
                URL url = new URL(DATABASE_URL + path);
                sendRequest(url, "PUT", "\"" + newName + "\"");
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }
}