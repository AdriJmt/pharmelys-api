package fr.pharmelys.api.bdpm;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "pharmelys.bdpm")
public class BdpmProperties {

    private String baseUrl;
    private String specialitesFile;
    private String compositionsFile;
    private String groupesGeneriquesFile;
    private String rupturesFile;
    private boolean importOnStartup = true;

}