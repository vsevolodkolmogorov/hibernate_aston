package ru.astondevs.dto;


public class UserDto {
    private String name;
    private String email;
    private int age;
    private Long role_id;

    public UserDto(String name, String email, int age, Long role_id) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.role_id = role_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Long getRole_id() {
        return role_id;
    }

    public void setRole_id(Long role_id) {
        this.role_id = role_id;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", role_id=" + role_id +
                '}';
    }
}
