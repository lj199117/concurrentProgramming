package chapter5.server.socket.pratice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) throws IOException {
		Scanner scan = new Scanner(System.in);
		Socket client=null;
        PrintWriter writer=null;
        BufferedReader reader=null;
        try {
            client=new Socket();
            client.connect(new InetSocketAddress("127.0.0.1",65500));
            writer=new PrintWriter(client.getOutputStream(),true);
            while(true){
            	writer.println(scan.nextLine());
            	writer.flush();
            	
            	reader=new BufferedReader(new InputStreamReader(client.getInputStream()));
            	System.out.println("from server:"+reader.readLine());
            }

        }catch (UnknownHostException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (writer!=null){
                writer.close();
            }
            if (reader!=null){
                reader.close();
            }
            if (client!=null){
                client.close();
            }
            if (scan!=null){
            	scan.close();
            }
        }
    }
}
