package org.tursunkulov.authorization.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@Schema(name = "User", description = "Сущность пользователя")
@Entity
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Уникальный идентификатор пользователя")
    private int id;

    @NotNull
    @Schema(description = "Уникальное пользовательское имя")
    @Size(min = 1, max = 20)
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @NotNull
    @Schema(description = "Пароль")
    @Size(min = 1)
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank
    @Schema(description = "Электронная почта")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank
    @Schema(description = "Номер телефона")
    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;

    @ManyToMany(
            mappedBy = "user",
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<User> users = new ArrayList<>();

    public User(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public User() {
    }
}
