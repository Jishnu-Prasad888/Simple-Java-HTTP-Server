
# Simple Java HTTP Server

A lightweight HTTP server written in Java that handles basic GET and POST requests, serves static files, and parses query parameters. This project demonstrates how to create and handle HTTP connections in Java using `com.sun.net.httpserver.HttpServer`.

## Table of Contents
- [Requirements](#requirements)
- [Setup](#setup)
- [Running the Server](#running-the-server)
- [Project Structure](#project-structure)
- [Code Explanation](#code-explanation)
  - [Main Class](#main-class)
  - [HomeHandler](#homehandler)
  - [PostHandler](#posthandler)
- [Usage](#usage)

## Requirements

- Java Development Kit (JDK) 8 or higher
- Basic knowledge of Java and HTTP protocols

## Setup

1. Clone or download the repository.
2. Create an `index.html` file in the project directory. This will be the file served by the server on a GET request to `/`.

## Running the Server

Compile and run the server with:

javac Main.java
java Main


After starting the server, you’ll see `Server started on port 8000`. The server is now accessible at `http://localhost:8000`.

## Project Structure

- `Main.java` - The main server code, including the main class and handler classes.
- `index.html` - The HTML file served as a response to GET requests.

## Code Explanation

### Main Class

The `Main` class is the entry point for starting the server and defining its behavior.

```java
public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new HomeHandler());
        server.createContext("/post", new PostHandler());
        server.setExecutor(null);  // Creates a default executor
        server.start();
        System.out.println("Server started on port 8000");
    }
}
```

**Explanation**:
- `HttpServer.create()` creates an HTTP server listening on port 8000.
- `createContext("/", new HomeHandler())` binds the root (`/`) path to the `HomeHandler` class.
- `createContext("/post", new PostHandler())` binds the `/post` path to the `PostHandler` class for handling POST requests.
- `setExecutor(null)` assigns a default executor, handling incoming connections on separate threads.
- `server.start()` starts the server.

### HomeHandler

The `HomeHandler` class processes GET requests to the root path (`/`). It reads an HTML file (e.g., `index.html`) and sends it as the response.

```java
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
```

**Explanation**:
- Checks if the HTTP request is a GET request.
- Reads the `index.html` file from the file system and writes it as the response.
- If `index.html` is missing, it sends a 404 error with an appropriate message.

### PostHandler

The `PostHandler` class processes POST requests sent to the `/post` path. It reads the request body, parses it into key-value pairs, and returns a response with the parsed data.

```java
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
```

**Explanation**:
- Checks if the HTTP request is a POST request.
- Reads the request body as a string and parses it using `parseQueryParams` to convert URL-encoded parameters into a `Map`.
- Constructs a response containing the parsed parameters and sends it to the client.
  
### Usage

- **GET Request**: Open `http://localhost:8000` in a browser to see the `index.html` content served by `HomeHandler`.
- **POST Request**: Send a POST request to `http://localhost:8000/post` with URL-encoded parameters in the request body, and you’ll receive a response echoing the data.

This project demonstrates a minimal web server in Java, handling GET and POST requests, serving static files, and parsing parameters. You can expand it by adding more routes and functionalities, such as error handling and authentication.
