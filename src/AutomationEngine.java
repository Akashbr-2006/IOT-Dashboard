import java.util.List;

public class AutomationEngine {

    public static void checkRules(List<AutomationRule> rules) {
        if (rules == null || rules.isEmpty()) return;

        for (AutomationRule rule : rules) {
            // Skip inactive rules
            if (!rule.isActive()) continue;
            Device triggerDevice = findDevice(rule.getTriggerId());
            if (triggerDevice == null) continue; 

 
            boolean conditionMet = false;
            int sensorVal = triggerDevice.getValue();
            int threshold = rule.getThreshold();

            String op = rule.getOperator();
            if (op.equals(">")) {
                conditionMet = sensorVal > threshold;
            } else if (op.equals("<")) {
                conditionMet = sensorVal < threshold;
            } else if (op.equals("=")) {
                conditionMet = sensorVal == threshold;
            }
            if (conditionMet) {
                Device targetDevice = findDevice(rule.getTargetId());
                if (targetDevice != null) {
                    if (targetDevice.getValue() != rule.getActionValue()) {
                        System.out.println("AUTOMATION EXECUTING: " + rule.getName());
                        targetDevice.setValue(rule.getActionValue());
                        FirebaseHandler.updateDeviceValue(targetDevice.getId(), rule.getActionValue());
                    }
                }
            }
        }
    }
    
    private static Device findDevice(String id) {
        if (id == null) return null;
        for (Device d : DeviceManager.getAllDevices()) {
            if (d.getId().equals(id)) return d;
        }
        return null;
    }
}