package krs.erp.dto;

/**
 * Data Transfer Object for IAM user information display
 * Used for returning user credentials and password hashes via API endpoints
 */
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String password; // Raw password for creation/updates
    private String userType;
    private String firstName;
    private String lastName;
    private String phone;
    private Boolean enabled;
    private Boolean isPrimaryUser;

    // Constructors
    public UserDTO() {
    }

    public UserDTO(Long id, String username, String email, String passwordHash,
            String userType, String firstName, String lastName, String phone, Boolean enabled, Boolean isPrimaryUser) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.password = null; // Don't expose raw password in response
        this.userType = userType;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.enabled = enabled;
        this.isPrimaryUser = isPrimaryUser;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getIsPrimaryUser() {
        return isPrimaryUser;
    }

    public void setIsPrimaryUser(Boolean isPrimaryUser) {
        this.isPrimaryUser = isPrimaryUser;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", userType='" + userType + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", enabled=" + enabled +
                ", isPrimaryUser=" + isPrimaryUser +
                '}';
    }
}
