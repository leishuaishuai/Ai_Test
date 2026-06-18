
package com.language.learn.dto.response;

public class LoginResponse {

    private String token;

    private Long userId;

    private String username;

    private String nickname;

    private Integer role;

    private String avatar;

    public LoginResponse() {
    }

    public LoginResponse(String token, Long userId, String username, String nickname, Integer role, String avatar) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
        this.avatar = avatar;
    }

    public static LoginResponseBuilder builder() {
        return new LoginResponseBuilder();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public static class LoginResponseBuilder {
        private String token;
        private Long userId;
        private String username;
        private String nickname;
        private Integer role;
        private String avatar;

        public LoginResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public LoginResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public LoginResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public LoginResponseBuilder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public LoginResponseBuilder role(Integer role) {
            this.role = role;
            return this;
        }

        public LoginResponseBuilder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public LoginResponse build() {
            return new LoginResponse(token, userId, username, nickname, role, avatar);
        }
    }
}
