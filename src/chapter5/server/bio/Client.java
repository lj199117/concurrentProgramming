package chapter5.server.bio;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket client=null;
        PrintWriter writer=null;
        BufferedReader reader=null;
        try {
            client=new Socket();
            client.connect(new InetSocketAddress("127.0.0.1",65500));
//            OutputStream os=client.getOutputStream();
//            String secondeString="这个是通过os传送得---";
//            os.write(secondeString.getBytes());
//            os.flush();
            //巨坑:如果你服务端口用readline来读取,客户端用print写入数据,那么客户端就一直不能关闭.不信你试一试
            
            writer=new PrintWriter(client.getOutputStream(),true);
            writer.println(System.in);
            writer.flush();

            reader=new BufferedReader(new InputStreamReader(client.getInputStream()));
            System.out.println("from server:"+reader.readLine());

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
        }
    }
}

