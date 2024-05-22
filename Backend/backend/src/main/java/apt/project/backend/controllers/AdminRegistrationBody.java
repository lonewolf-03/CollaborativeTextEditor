package apt.project.backend.controllers;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class AdminRegistrationBody {
    private String email;
    private String password;
}
