public class Device {
    private String id;
    private String name;
    private String category; 
    private String type;     
    private int value;       
    private boolean isPinned = false; // Added to track dashboard status

    public Device(String id, String name, String category, String type) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.type = type;
        this.value = 0; 
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getType() { return type; }
    public int getValue() { return value; }
    public boolean isPinned() { return isPinned; } // Added

    public void setName(String name) { this.name = name; }
    public void setValue(int value) { this.value = value; }
    public void setType(String type) { this.type = type; }
    public void setPinned(boolean pinned) { this.isPinned = pinned; } // Added
}