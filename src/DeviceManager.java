import java.util.ArrayList;
import java.util.List;

public class DeviceManager {
    
    private static List<Device> devices = new ArrayList<>();

    public static void addDevice(Device d) {
        devices.add(d);
    }

    public static List<Device> getAllDevices() {
        return devices;
    }

    public static boolean syncWithCloud() {
        try {
            String json = FirebaseHandler.loadAllDevices();
            if (json == null || json.equals("null") || json.length() < 5) return true; 

            devices.clear(); 
            json = json.substring(1, json.length() - 1);
            String[] entries = json.split("},");

            for (String entry : entries) {
                 if (!entry.endsWith("}")) entry += "}";
                 String id = entry.substring(1, entry.indexOf("\":")).replace("\"", "");
                 String name = extract(entry, "\"name\":\"", "\"");
                 String cat = extract(entry, "\"category\":\"", "\"");
                 String type = extract(entry, "\"type\":\"", "\"");
                 
                 // Parsing Pinned status
                 boolean pinned = entry.contains("\"isPinned\":true");
                 
                 String valStr = extract(entry, "\"value\":", "}");
                 if (valStr.contains(",")) valStr = valStr.substring(0, valStr.indexOf(","));
                 if (valStr.contains("}")) valStr = valStr.replace("}", "");
                 int val = 0;
                 try { val = Integer.parseInt(valStr.trim()); } catch (Exception e) {}
                 
                 Device d = new Device(id, name, cat, type);
                 d.setValue(val);
                 d.setPinned(pinned); 
                 devices.add(d);
            }
            return true; 
        } catch (Exception e) {
            return false; 
        }
    }

    private static String extract(String source, String startMarker, String endMarker) {
        int start = source.indexOf(startMarker);
        if (start == -1) return "Unknown";
        start += startMarker.length();
        int end = source.indexOf(endMarker, start);
        if (end == -1) return "Unknown";
        return source.substring(start, end);
    }

    public static void removeDevice(Device d) {
        devices.remove(d);
    }
}