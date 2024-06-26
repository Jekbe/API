import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final List<ClientThread> list = new ArrayList<>();
    private static boolean run = true;

    public static void main(String[] args) {
        Thread konsola = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (run) {
                switch (scanner.nextLine()) {
                    case "exit" -> run = false;
                    case "lista" -> list.forEach(System.out::println);
                    default -> System.out.println("Nieznana komenda");
                }
            }
        });
        konsola.start();

        try (ServerSocket socketCLI = new ServerSocket(8001)){
            System.out.println("Serwer działa");
            while (run){
                System.out.println("Oczekiwanie na klienta");
                Socket socket = socketCLI.accept();
                System.out.println("Nowy klient");

                ClientThread thread = new ClientThread(socket);
                list.add(thread);
            }
        }catch (IOException e){
            System.out.println("Błąd: Odrzucono połączenie " + e);
        }
    }
}

class ClientThread extends Thread{
    Socket socketCLI, socketLogin, socketTablica, socketChat, socketPliki;
    BufferedReader inCLI, inLogin, inTablica, inChat, inPliki;
    PrintStream outCLI, outLogin, outTablica, outChat, outPliki;

    ClientThread(Socket socket) throws IOException {
        socketCLI = socket;
        socketLogin = new Socket("localhost", 8002);
        socketTablica = new Socket("localhost", 8003);
        socketChat = new Socket("localhost", 8004);
        socketPliki = new Socket("localhost", 8005);

        inCLI = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        outCLI = new PrintStream(socket.getOutputStream(), true);

        inLogin = new BufferedReader(new InputStreamReader(socketLogin.getInputStream()));
        outLogin = new PrintStream(socketLogin.getOutputStream(), true);

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
            System.out.println("API otrzymało ramkę: " + request);
            String response = rozpoznaj(request);
            System.out.println("API otrzymało odpowiedź: " + response);

            outCLI.println(response);
        } catch (IOException e){
            System.out.println("Błąd: " + e);
        } finally {
            try {
                socketCLI.close();
                socketLogin.close();
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
        return switch (ramka[1]) {
            case "id:20" -> login(request);
            case "id:10" -> register(request);
            case "id:30" -> posty(request);
            case "id:40" -> chat(request);
            case "id:50", "id:60" -> upload(request);
            case "id:70" -> lista(request);
            case "id:80" -> download(request);
            default -> "typ:nie_znany;" + ramka[1] + ";status:400";
        };
    }

    private String login(String request) {
        outLogin.println(request);
        outLogin.flush();
        System.out.println("Login przetwarza informację");

        try {
            return "typ:login;id:20;" + inLogin.readLine();
        } catch (IOException e){
            System.out.println("Błąd: login " + e);
            return "typ:login;id:20;status:500";
        }
    }

    private String register(String request){
        outLogin.println(request);
        outLogin.flush();
        System.out.println("Rejestracja przetwarza informację");

        try {
            return "typ:register;id:10;" + inLogin.readLine();
        } catch (IOException e){
            System.out.println("Błąd: rejestracja " + e);
            return "typ:register;id:10;status:500";
        }
    }

    private String posty(String request){
        outTablica.println(request);
        outTablica.flush();
        System.out.println("Posty przetwarza informację");

        try {
            return "typ:pobiez_posty;id:30;" + inTablica.readLine();
        } catch (IOException e){
            System.out.println("Błąd: posty " + e);
            return "typ:pobiez_posty;id:30;status:500";
        }
    }

    private String chat(String request){
        outChat.println(request);
        outChat.flush();
        System.out.println("Chat przetwarza informację");

        try {
            return "typ:nowa_wiadomosc;id:40;" + inChat.readLine();
        } catch (IOException e){
            System.out.println("Błąd: chat " + e);
            return "typ:nowa_wiadomosc;id:40;status:500";
        }
    }

    private String upload(String request){
        outPliki.println(request);
        outPliki.flush();
        System.out.println("Wysyłanie przetwarza informację");

        try {
            return "typ:wysylanie;id:50;" + inPliki.readLine();
        } catch (IOException e){
            System.out.println("Błąd: wysyłanie " + e);
            return "typ:wysylanie;id:50;status:500";
        }
    }

    private String lista(String request){
        outPliki.println(request);
        outPliki.flush();
        System.out.println("Lista przetwarza informację");

        try {
            return "typ:lista_plikow;id:70;" + inPliki.readLine();
        } catch (IOException e){
            System.out.println("Błąd: lista " + e);
            return "typ:lista_plikow;id:70;status:500";
        }
    }

    private String download(String request){
        outPliki.println(request);
        outPliki.flush();
        System.out.println("Pobieranie przetwarza informację");

        try {
            return "typ:pobierz_plik;id:80;" + inPliki.readLine();
        } catch (IOException e){
            System.out.println("Błąd: pobieranie " + e);
            return "typ:pobierz_plik;id:80;status:500";
        }
    }
}