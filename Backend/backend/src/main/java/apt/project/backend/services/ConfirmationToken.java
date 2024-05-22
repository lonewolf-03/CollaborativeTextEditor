package apt.project.backend.services;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConfirmationToken {
    private final String token;
    private final LocalDateTime start;
    private final LocalDateTime expirationTime;
}
