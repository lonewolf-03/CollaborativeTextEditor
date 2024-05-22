package apt.project.backend.controllers;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class AddDocumentBody {
    private String content;
    private String name;
}
