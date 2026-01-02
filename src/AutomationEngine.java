import java.util.List;

public class AutomationEngine {

    public static void checkRules(List<AutomationRule> rules) {
        if (rules == null || rules.isEmpty()) return;

        for (AutomationRule rule : rules) {
            // Skip inactive rules
            if (!rule.isActive()) continue;
            Device triggerDevice = findDevice(rule.getTriggerId());
            if (triggerDevice == null) continue; 

            // 2. Evaluate Condition
            boolean conditionMet = false;
            int sensorVal = triggerDevice.getValue();
            int threshold = rule.getThreshold();

            // Simple logic for comparison
            String op = rule.getOperator();
            if (op.equals(">")) {
                conditionMet = sensorVal > threshold;
            } else if (op.equals("<")) {
                conditionMet = sensorVal < threshold;
            } else if (op.equals("=")) {
                conditionMet = sensorVal == threshold;
            }

            // 3. Execute Action if Condition Met
            if (conditionMet) {
                Device targetDevice = findDevice(rule.getTargetId());
                if (targetDevice != null) {
                    // Only update if value is different (prevents infinite network loops)
                    if (targetDevice.getValue() != rule.getActionValue()) {
                        System.out.println("AUTOMATION EXECUTING: " + rule.getName());
                        
                        // Update Local Memory
                        targetDevice.setValue(rule.getActionValue());
                        
                        // Update Cloud
                        FirebaseHandler.updateDeviceValue(targetDevice.getId(), rule.getActionValue());
                    }
                }
            }
        }
    }
    
    // Helper to find device by ID
    private static Device findDevice(String id) {
        if (id == null) return null;
        for (Device d : DeviceManager.getAllDevices()) {
            if (d.getId().equals(id)) return d;
        }
        return null;
    }
}