package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class App {
   static int PORT = 8000;

   public static void main(String[] args) throws IOException {
      HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
      server.createContext("/location", new LocationEndpoint());
      server.createContext("/user", new UserEndPoint());
      server.createContext("/trip", new TripEndpoint());

      //Multi threading
      ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
      server.setExecutor(threadPoolExecutor);

      server.start();
      System.out.printf("Server started on port %d...\n", PORT);
   }
}
