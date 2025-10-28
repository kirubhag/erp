package krs.erp.dto;

/**
 * Data Transfer Object for updating menu item sequences.
 * Used to reorder menu items in the UI.
 */
public class SequenceUpdateDTO {
    
    private Long id;
    private Integer sequence;
    
    // Constructors
    public SequenceUpdateDTO() {
    }
    
    public SequenceUpdateDTO(Long id, Integer sequence) {
        this.id = id;
        this.sequence = sequence;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getSequence() {
        return sequence;
    }
    
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }
    
    @Override
    public String toString() {
        return "SequenceUpdateDTO{" +
                "id=" + id +
                ", sequence=" + sequence +
                '}';
    }
}
