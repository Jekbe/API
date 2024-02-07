import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static ServerSocket socketCLI;
    private static final List<ClientThread> list = new ArrayList<>();

    public static void main(String[] args) {
        try {
            socketCLI = new ServerSocket(8001);
            System.out.println("Serwer działa");
            while (true){
                Socket socket = socketCLI.accept();
                System.out.println("Nowy klient");

                ClientThread thread = new ClientThread(socket);
                list.add(thread);
            }
        }catch (IOException e){
            System.out.println("Błąd: " + e);
        } finally {
            try {
                socketCLI.close();
            } catch (IOException e){
                System.out.println("Błąd: " + e);
            }
        }
    }
}

class ClientThread extends Thread{
    Socket socketCLI;
    BufferedReader in;
    PrintStream out;

    ClientThread(Socket socket) throws IOException {
        socketCLI = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintStream(socket.getOutputStream(), true);
        start();
    }

    public void run(){
        try {
            String request = in.readLine();
            String response = rozpoznaj(request);

            out.println(response);
        } catch (IOException e){
            System.out.println("Błąd: " + e);
        } finally {
            try {
                socketCLI.close();
            } catch (IOException e){
                System.out.println("Błąd: " + e);
            }
        }
    }

    private static String rozpoznaj(String request) {
        String[] ramka = request.split(";");
        String response;
        switch (ramka[1]){
            case "id:20" -> response = login(request);
            case "id:10" -> response = register(request);
            case "id:30" -> response = posty(request);
            case "id:40" -> response = chat(request);
            case "id:50", "id:60" -> response = upload(request);
            case "id:70" -> response = lista(request);
            case "id:80" -> response = download(request);
            default -> response = "typ:nie_znany;" + ramka[1] + ";status:400";
        }
        return response;
    }

    private static String login(String request){
        return "";
    }

    private static String register(String request){
        return "";
    }

    private static String posty(String request){
        return "";
    }

    private static String chat(String request){
        return "";
    }

    private static String upload(String request){
        return "";
    }

    private static String lista(String request){
        return "";
    }

    private static String download(String request){
        return "";
    }
}