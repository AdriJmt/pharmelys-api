package fr.pharmelys.api.bdpm;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
public class BdpmFileClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    /**
     * Télécharge un fichier BDPM et retourne ses lignes non vides,
     * décodées en ISO-8859-1 (latin1) — ne JAMAIS lire ces fichiers en UTF-8,
     * les caractères accentués seraient corrompus.
     */
    public List<String> downloadLines(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException("Echec telechargement " + url + " - statut HTTP " + response.statusCode());
        }

        String content = new String(response.body(), StandardCharsets.ISO_8859_1);
        return Arrays.stream(content.split("\\r?\\n"))
                .filter(line -> !line.isBlank())
                .toList();
    }
}