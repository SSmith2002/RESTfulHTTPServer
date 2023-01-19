import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MyHTTPServer {
    private int port;
    private ServerSocket serverSock;
    private Socket clientSock;
    private BufferedReader input;
    private OutputStream output;

    public MyHTTPServer(int port){
        this.port = port;
    }

    public void startServer(Object serverObject){
        try{
            serverSock = new ServerSocket(port);
            InetAddress localaddress = InetAddress.getLocalHost();
            System.out.println("Server starting on " + localaddress.getHostAddress() + ":" + port);

            while(true){
                clientSock = serverSock.accept();
                System.out.println("Connection from: " + clientSock.getInetAddress().getHostAddress());
                input = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
                 output = clientSock.getOutputStream();

                ArrayList<String> lines = new ArrayList<>();
                String line;
                while((line = input.readLine()) != null){
                    if(line.isEmpty()){
                        break;
                    }
                    lines.add(line);
                    System.out.println(line);
                }
                
                Thread handler = new Thread(new RequestHandler(serverObject,output,lines));
                handler.start();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            
            // serverSock.close();
        }
    }
}
