public class AutomationRule {
    private String name;
    private String triggerId;   
    private String operator;    
    private int threshold;      
    private String targetId;    
    private int actionValue;    
    
    
    private int delaySeconds;   
    private boolean isRepeat;  
    private int repeatSeconds;  
    
    private boolean isActive;

    public AutomationRule(String name, String triggerId, String operator, int threshold, 
                          String targetId, int actionValue, int delaySeconds, boolean isRepeat, int repeatSeconds) {
        this.name = name;
        this.triggerId = triggerId;
        this.operator = operator;
        this.threshold = threshold;
        this.targetId = targetId;
        this.actionValue = actionValue;
        this.delaySeconds = delaySeconds;
        this.isRepeat = isRepeat;
        this.repeatSeconds = repeatSeconds;
        this.isActive = true; 
    }

    // --- MISSING GETTERS ADDED BELOW ---
    public String getName() { return name; }
    public String getTriggerId() { return triggerId; }
    public String getOperator() { return operator; }
    public int getThreshold() { return threshold; }
    public String getTargetId() { return targetId; }
    public int getActionValue() { return actionValue; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }

    public String getDescription() {
        String action = "Set (" + targetId + ") to " + (actionValue==1?"ON":"OFF");
        
        if (isRepeat) {
            return "⏰ Every " + repeatSeconds + "s → " + action;
        }
        if (delaySeconds > 0 && (triggerId == null || triggerId.isEmpty())) {
            return "⏳ Wait " + delaySeconds + "s → " + action;
        }
        
        return "If (" + triggerId + ") " + operator + " " + threshold + " → " + action;
    }
}