package krs.erp.dto;

public class EmailStatistics {
    private Long totalEmails;
    private Long sentEmails;
    private Long deliveredEmails;
    private Long openedEmails;
    private Long failedEmails;
    private Long bouncedEmails;
    
    // Constructor
    public EmailStatistics() {}
    
    public EmailStatistics(Long totalEmails, Long sentEmails, Long deliveredEmails, 
                          Long openedEmails, Long failedEmails, Long bouncedEmails) {
        this.totalEmails = totalEmails;
        this.sentEmails = sentEmails;
        this.deliveredEmails = deliveredEmails;
        this.openedEmails = openedEmails;
        this.failedEmails = failedEmails;
        this.bouncedEmails = bouncedEmails;
    }
    
    // Getters and Setters
    public Long getTotalEmails() {
        return totalEmails;
    }
    
    public void setTotalEmails(Long totalEmails) {
        this.totalEmails = totalEmails;
    }
    
    public Long getSentEmails() {
        return sentEmails;
    }
    
    public void setSentEmails(Long sentEmails) {
        this.sentEmails = sentEmails;
    }
    
    public Long getDeliveredEmails() {
        return deliveredEmails;
    }
    
    public void setDeliveredEmails(Long deliveredEmails) {
        this.deliveredEmails = deliveredEmails;
    }
    
    public Long getOpenedEmails() {
        return openedEmails;
    }
    
    public void setOpenedEmails(Long openedEmails) {
        this.openedEmails = openedEmails;
    }
    
    public Long getFailedEmails() {
        return failedEmails;
    }
    
    public void setFailedEmails(Long failedEmails) {
        this.failedEmails = failedEmails;
    }
    
    public Long getBouncedEmails() {
        return bouncedEmails;
    }
    
    public void setBouncedEmails(Long bouncedEmails) {
        this.bouncedEmails = bouncedEmails;
    }
    
    // Helper methods
    public double getDeliveryRate() {
        if (totalEmails == null || totalEmails == 0) return 0.0;
        return ((double) (deliveredEmails + openedEmails)) / totalEmails * 100;
    }
    
    public double getOpenRate() {
        if (deliveredEmails == null || deliveredEmails == 0) return 0.0;
        return ((double) openedEmails) / deliveredEmails * 100;
    }
    
    public double getFailureRate() {
        if (totalEmails == null || totalEmails == 0) return 0.0;
        return ((double) (failedEmails + bouncedEmails)) / totalEmails * 100;
    }
}