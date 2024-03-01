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
    Socket socketCLI, socketLogin, socketRegister, socketTablica, socketChat, socketPliki;
    BufferedReader inCLI, inLogin, inRegister, inTablica, inChat, inPliki;
    PrintStream outCLI, outLogin, outRegister, outTablica, outChat, outPliki;

    ClientThread(Socket socket) throws IOException {
        socketCLI = socket;
        socketLogin = new Socket("localhost", 8002);
        socketRegister = new Socket("localhost", 8003);
        socketTablica = new Socket("localhost", 8004);
        socketChat = new Socket("localhost", 8005);
        socketPliki = new Socket("localhost", 8006);

        inCLI = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outCLI = new PrintStream(socket.getOutputStream(), true);

        inLogin = new BufferedReader(new InputStreamReader(socketLogin.getInputStream()));
        outLogin = new PrintStream(socketLogin.getOutputStream(), true);

        inRegister = new BufferedReader(new InputStreamReader(socketRegister.getInputStream()));
        outRegister = new PrintStream(socketRegister.getOutputStream(), true);

        inTablica = new BufferedReader(new InputStreamReader(socketTablica.getInputStream()));
        outTablica = new PrintStream(socketTablica.getOutputStream(), true);

        inChat = new BufferedReader(new InputStreamReader(socketChat.getInputStream()));
        outChat = new PrintStream(socketChat.getOutputStream(), true);

        inPliki = new BufferedReader(new InputStreamReader(socketPliki.getInputStream()));
        outPliki = new PrintStream(socketPliki.getOutputStream(), true);

        start();
    }

    public void run(){
        try {
            String request = inCLI.readLine();
            String response = rozpoznaj(request);

            outCLI.println(response);
        } catch (IOException e){
            System.out.println("Błąd: " + e);
        } finally {
            try {
                socketCLI.close();
                socketLogin.close();
                socketRegister.close();
                socketTablica.close();
                socketChat.close();
                socketPliki.close();
            } catch (IOException e){
                System.out.println("Błąd: " + e);
            }
        }
    }

    private String rozpoznaj(String request) {
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

    private String login(String request) {
        String[] tab = request.split(";");
        String data = tab[2].substring(6) + ";" + tab[3].substring(6);
        String response;

        outLogin.println(data);
        outLogin.flush();

        try {
            response = inLogin.readLine();
        } catch (IOException e){
            response = "status:500";
        }

        return "typ:login;id:20;" + response;
    }

    private String register(String request){
        return "";
    }

    private String posty(String request){
        return "";
    }

    private String chat(String request){
        return "";
    }

    private String upload(String request){
        return "";
    }

    private String lista(String request){
        return "";
    }

    private String download(String request){
        return "";
    }
}