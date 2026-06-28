package fr.pharmelys.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.pharmelys.api.dto.about.AboutResponseDTO;
import fr.pharmelys.api.service.AboutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/about")
@RequiredArgsConstructor
@Tag(name = "Mentions légales", description = "Source des données, statut non-dispositif-médical, licence")
public class AboutController {

    private final AboutService aboutService;

    @Operation(summary = "Mentions légales et source des données")
    @GetMapping
    public AboutResponseDTO getAbout() {
        return aboutService.getAbout();
    }
}