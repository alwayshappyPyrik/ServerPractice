package ru.netology;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Request {
    static String method;
    static String path;
    private final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

    public Request(String method, String path) {
        Request.method = method;
        Request.path = path;
    }

    public static Request parseHttpRequest(BufferedReader in) throws IOException {
        final String requestLine = in.readLine();
        final String[] parts = requestLine.split(" ");

        if (parts.length != 3) {
            throw new InvalidRequestLine("Невалидная строка запроса");
        }

        //Получаем метод у реквеста
        String pathMethod = parts[0];
        if (pathMethod.equals("GET") || pathMethod.equals("POST")) {
            method = pathMethod;
        } else {
            method = null;
        }

        //Получаем заголовки
        List<String> headers = new ArrayList<>();
        for (String line; (line = in.readLine()) != null; ) {
            if (line.isEmpty()) {
                break;
            }
            headers.add(line);
        }

        //Получаем тело, если есть
        if (method.equals("POST")) {
            List<String> body = new ArrayList<>();
            ByteArrayOutputStream resultBytes = new ByteArrayOutputStream();
            byte[] byteBody = new byte[50_000];
            for (int length; (length = in.read()) != -1; ) {
                resultBytes.write(byteBody, 0, length);
            }
            resultBytes.flush();
            resultBytes.close();
            body.add(resultBytes.toString(StandardCharsets.UTF_8));
        }

        String pathByResource = parts[1];
        if (pathByResource != null) {
            path = pathByResource;
        } else {
            path = null;
        }

        return new Request(method, path);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String validPaths() {
        return getPath();
    }
}
