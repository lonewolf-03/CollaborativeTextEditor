package apt.project.backend.controllers;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class UpdateDocBody {
    private String newContent;
    @Nullable
    private String name;
}
