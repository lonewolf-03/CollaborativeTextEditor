package apt.project.backend.controllers;

import lombok.Data;
import lombok.Setter;


@Data
@Setter
public class RegistrationBody {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmedPassword;
}
