package apt.project.backend.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class ConfirmationBody {
    private String email;
    private String token;
}
