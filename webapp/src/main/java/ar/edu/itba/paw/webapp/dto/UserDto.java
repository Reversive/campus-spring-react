package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.User;

public class UserDto {

    private Long userId;
    private String name;
    private String surname;
    private String username;
    private String email;
    private Integer fileNumber;

    public static UserDto fromUser(User user) {
        if (user == null){
            return null;
        }

        UserDto dto = new UserDto();
        dto.userId = user.getUserId();
        dto.name = user.getName();
        dto.surname = user.getSurname();
        dto.username = user.getUsername();
        dto.email = user.getEmail();
        dto.fileNumber = user.getFileNumber();
        return dto;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    public Integer getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(Integer fileNumber) {
        this.fileNumber = fileNumber;
    }
}
