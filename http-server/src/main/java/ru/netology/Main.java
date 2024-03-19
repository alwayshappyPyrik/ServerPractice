package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        final var server = new Server();
        server.addHandler("GET", "/index.html", (request, out) -> {
            try {
                Path filePath = Path.of(".", "http-server/public", request.validPaths());
                String mimeType = Files.probeContentType(filePath);
                long sizeFile = Files.size(filePath);
                outResponse(mimeType, sizeFile, out, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        server.addHandler("POST", "/messages", (request, out) -> {
            try {
                Path filePath = Path.of(".", "http-server/public", request.validPaths());
                String mimeType = Files.probeContentType(filePath);
                long sizeFile = Files.size(filePath);
                outResponse(mimeType, sizeFile, out, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        server.startServer();
    }

    public static void outResponse(String mimeType, long size, BufferedOutputStream out, Path path) throws IOException {
        out.write((
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + size + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        Files.copy(path, out);
        out.flush();
    }
}


