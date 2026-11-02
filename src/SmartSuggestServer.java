
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class SmartSuggestServer {

    private static final int PORT = Integer.parseInt(
            System.getenv().getOrDefault("PORT", "8081")
    );

    private static final SuggestionEngine engine =
            new SuggestionEngine();

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
                new InetSocketAddress("0.0.0.0", PORT), 0
        );

        // API: Get autocomplete suggestions
        server.createContext("/api/suggest", exchange -> {

            if (!exchange.getRequestMethod().equals("GET")) {
                sendJson(exchange, 405,
                        "{\"error\":\"Method not allowed\"}");
                return;
            }

            Map<String, String> params = getQueryParams(exchange);
            String prefix = params.getOrDefault("prefix", "");

            List<Suggestion> suggestions;

            synchronized (engine) {
                suggestions = engine.getSuggestions(prefix, 5);
            }

            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < suggestions.size(); i++) {

                Suggestion s = suggestions.get(i);

                if (i > 0) {
                    json.append(",");
                }

                json.append("{")
                    .append("\"word\":\"")
                    .append(escapeJson(s.getWord()))
                    .append("\",")
                    .append("\"frequency\":")
                    .append(s.getFrequency())
                    .append("}");
            }

            json.append("]");

            sendJson(exchange, 200, json.toString());
        });

        // API: Record a selected suggestion
        server.createContext("/api/select", exchange -> {

            if (!exchange.getRequestMethod().equals("POST")) {
                sendJson(exchange, 405,
                        "{\"error\":\"Method not allowed\"}");
                return;
            }

            Map<String, String> data = readForm(exchange);
            String word = data.get("word");

            if (word == null || word.isBlank()) {
                sendJson(exchange, 400,
                        "{\"error\":\"Word is required\"}");
                return;
            }

            synchronized (engine) {
                engine.recordSearch(word);
            }

            sendJson(exchange, 200,
                    "{\"message\":\"Frequency updated\"}");
        });

        // API: Add a new word
        server.createContext("/api/add", exchange -> {

            if (!exchange.getRequestMethod().equals("POST")) {
                sendJson(exchange, 405,
                        "{\"error\":\"Method not allowed\"}");
                return;
            }

            Map<String, String> data = readForm(exchange);
            String word = data.get("word");

            if (word == null || !word.trim().matches("[a-zA-Z]+")) {
                sendJson(exchange, 400,
                        "{\"error\":\"Enter a valid word using letters only\"}");
                return;
            }

            word = word.trim().toLowerCase();

            synchronized (engine) {
                engine.addWord(word, 0);
            }

            sendJson(exchange, 200,
                    "{\"message\":\"Word added successfully\"}");
        });

        // Serve website files
        server.createContext("/", exchange -> {

            String path = exchange.getRequestURI().getPath();

            if (path.equals("/")) {
                path = "/index.html";
            }

            Path publicDir = Paths.get("public")
                    .toAbsolutePath()
                    .normalize();

            Path file = publicDir.resolve(
                    path.substring(1)
            ).normalize();

            if (!file.startsWith(publicDir) ||
                    !Files.isRegularFile(file)) {

                sendText(exchange, 404, "File not found");
                return;
            }

            byte[] content = Files.readAllBytes(file);

            exchange.getResponseHeaders().set(
                    "Content-Type", getContentType(file)
            );

            exchange.sendResponseHeaders(200, content.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(content);
            }
        });

        server.setExecutor(null);
        server.start();

        System.out.println("==================================");
        System.out.println("       SMARTSUGGEST SERVER");
        System.out.println("==================================");
        System.out.println("Server running on port: " + PORT);
    }

    private static Map<String, String> getQueryParams(
            HttpExchange exchange) {

        String query = exchange.getRequestURI().getRawQuery();

        if (query == null) {
            return new HashMap<>();
        }

        return parseForm(query);
    }

    private static Map<String, String> readForm(
            HttpExchange exchange) throws IOException {

        String body;

        try (InputStream is = exchange.getRequestBody()) {
            body = new String(
                    is.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }

        return parseForm(body);
    }

    private static Map<String, String> parseForm(String data) {

        Map<String, String> result = new HashMap<>();

        if (data == null || data.isEmpty()) {
            return result;
        }

        for (String pair : data.split("&")) {

            String[] parts = pair.split("=", 2);

            String key = decode(parts[0]);

            String value = parts.length > 1
                    ? decode(parts[1]) : "";

            result.put(key, value);
        }

        return result;
    }

    private static String decode(String value) {

        return URLDecoder.decode(
                value, StandardCharsets.UTF_8
        );
    }

    private static String escapeJson(String value) {

        StringBuilder result = new StringBuilder();

        for (char c : value.toCharArray()) {

            switch (c) {
                case '"': result.append("\\\""); break;
                case '\\': result.append("\\\\"); break;
                case '\n': result.append("\\n"); break;
                case '\r': result.append("\\r"); break;
                case '\t': result.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        result.append(String.format("\\u%04x", (int) c));
                    } else {
                        result.append(c);
                    }
            }
        }

        return result.toString();
    }

    private static void sendJson(
            HttpExchange exchange,
            int status,
            String response) throws IOException {

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8"
        );

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendText(
            HttpExchange exchange,
            int status,
            String response) throws IOException {

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String getContentType(Path file) {

        String name = file.toString().toLowerCase();

        if (name.endsWith(".html")) {
            return "text/html; charset=UTF-8";
        }

        if (name.endsWith(".css")) {
            return "text/css; charset=UTF-8";
        }

        if (name.endsWith(".js")) {
            return "application/javascript; charset=UTF-8";
        }

        return "application/octet-stream";
    }
}
