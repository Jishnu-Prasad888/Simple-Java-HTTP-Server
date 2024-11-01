import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new HomeHandler());
        server.createContext("/post", new PostHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8000");
    }
    
    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if(exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                File file = new File("index.html");

                if (!file.exists()) {
                    String error = "File not found";
                    exchange.sendResponseHeaders(404, error.length());
                    exchange.getResponseBody().write(error.getBytes());
                    exchange.getResponseBody().close();
                    return;
                }

                byte[] bytes = Files.readAllBytes(file.toPath());
                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            }
        }
    }


static class PostHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            String body = new String(exchange.getRequestBody().readAllBytes());
            Map<String, String> params = parseQueryParams(body);
            String response = "Received POST data: " + params.toString();
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                params.put(kv[0], kv[1]);
            }
        }
        return params;
    }
  }

}
