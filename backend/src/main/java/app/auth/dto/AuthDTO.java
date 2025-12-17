package app.auth.dto;

/**
 * Response DTO for authentication result. Contains JWT token and user information.
 */
public class AuthDTO {
  private String token;
  private String role;
  private String email;
  private Long userId;
  private Long businessPersonId;

  public AuthDTO() {
  }

  public AuthDTO(String token, String role, String email, Long userId, Long businessPersonId) {
    this.token = token;
    this.role = role;
    this.email = email;
    this.userId = userId;
    this.businessPersonId = businessPersonId;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getBusinessPersonId() {
    return businessPersonId;
  }

  public void setBusinessPersonId(Long businessPersonId) {
    this.businessPersonId = businessPersonId;
  }
}
