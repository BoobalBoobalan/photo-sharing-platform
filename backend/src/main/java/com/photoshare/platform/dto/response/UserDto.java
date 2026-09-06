package com.photoshare.platform.dto.response;

import com.photoshare.platform.entity.Role;
import com.photoshare.platform.entity.User;

public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private Role role;

    public UserDto() {}

    public UserDto(Long id, String email, String fullName, Role role) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public static UserDto fromEntity(User user) {
        if (user == null) return null;
        return new UserDto(user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }

    public static UserDtoBuilder builder() { return new UserDtoBuilder(); }

    public static class UserDtoBuilder {
        private Long id;
        private String email;
        private String fullName;
        private Role role;

        public UserDtoBuilder id(Long id) { this.id = id; return this; }
        public UserDtoBuilder email(String email) { this.email = email; return this; }
        public UserDtoBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UserDtoBuilder role(Role role) { this.role = role; return this; }

        public UserDto build() {
            return new UserDto(id, email, fullName, role);
        }
    }
}
