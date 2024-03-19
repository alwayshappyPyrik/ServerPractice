package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT_FOR_SERVER = 9999;
    private static final int COUNT_THREAD = 64;
    static Map<String, Map<String, Handler>> request = new ConcurrentHashMap<>();
    private static ExecutorService executorService;
    private final Handler errorMethodHttpRequest = ((request, out) -> {
        try {
            out.write((
                    "HTTP/1.1 400 Not Found\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    });

    private final Handler errorHttpRequest = ((request, out) -> {
        try {
            out.write((
                    "HTTP/1.1 404 Not Found\r\n" +
                            "Content-Length: 0\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    });


    public void startServer() {
        executorService = Executors.newFixedThreadPool(COUNT_THREAD);
        try (ServerSocket serverSocket = new ServerSocket(PORT_FOR_SERVER)) {
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.submit(() -> {
                    try {
                        connection(socket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void connection(Socket clientSocket) throws IOException {
        try (final var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             final var out = new BufferedOutputStream(clientSocket.getOutputStream())) {

            Request requestLine = Request.parseHttpRequest(in);
            if (!request.containsKey(Request.method)) {
                errorMethodHttpRequest.handle(requestLine, out);
            } else {
                Map<String, Handler> path = request.get(Request.method);
                Handler handler = path.get(Request.path);
                if (handler == null) {
                    errorHttpRequest.handle(requestLine, out);
                    return;
                }
                handler.handle(requestLine, out);
            }
        }
    }

    public void addHandler(String method, String path, Handler handler) {
        if (request.get(method) == null) {
            request.put(method, new ConcurrentHashMap<>());
        } else {
            request.get(method).put(path, handler);
        }
    }
}
