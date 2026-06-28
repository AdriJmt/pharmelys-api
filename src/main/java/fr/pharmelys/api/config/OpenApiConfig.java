package fr.pharmelys.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pharmelysOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pharmelys API")
                        .description("API de disponibilité des médicaments, alternatives génériques et "
                                + "filtrage par allergies. Source des données : Base de Données Publique des "
                                + "Médicaments (BDPM), ANSM/HAS/UNCAM, licence ouverte Etalab.")
                        .version("v0.1")
                        .license(new License()
                                .name("Données BDPM sous licence ouverte Etalab")
                                .url("https://base-donnees-publique.medicaments.gouv.fr/docs/telechargement/licence_bdpm.pdf")));
    }
}