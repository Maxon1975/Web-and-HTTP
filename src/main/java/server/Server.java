package server;

import handler.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    public static final int PORT = 9999;
    private final ServerSocket serverSocket;
    private ConcurrentHashMap<String, Map<String, Handler>> handlers;
    public static List<String> validPaths = List.of("/index.html", "/spring.svg",
            "/spring.png", "/resources.html", "/styles.css", "/app.js",
            "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private ExecutorService executorService;

    public Server() throws IOException {
        serverSocket = new ServerSocket(PORT);
        executorService = Executors.newFixedThreadPool(64);
        handlers = new ConcurrentHashMap<>();
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        for (int i = 0; i < validPaths.size(); i++) {
            System.out.printf("Откройте в браузере http://localhost:%d%s\n", PORT, validPaths.get(i));
        }
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                executorService.execute(new MultiThreadedHandler (socket, handlers));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addHandler (String method, String msg, Handler handler){
        if (!handlers.containsKey(method))
            handlers.put(method, new ConcurrentHashMap<>());
        handlers.get(method).put(msg, handler);
    }
}
