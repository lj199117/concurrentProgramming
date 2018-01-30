package chapter5.server.socket.pratice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		ServerSocket serverSocke = null;
		Socket clientSocket = null;

		BufferedReader is = null;
		PrintWriter os = null;
		try {
			serverSocke = new ServerSocket(65500);

			clientSocket = serverSocke.accept();
			System.out.println(clientSocket.getRemoteSocketAddress() + " conect");

			is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			os = new PrintWriter(clientSocket.getOutputStream(), true);

		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				// 从inputstream 中读取客户端发送的数据
				String inputString = null;
				long b = System.currentTimeMillis();
				while ((inputString = is.readLine()) != null) {
					System.out.println("收到客户端数据" + inputString);
					os.println(scan.nextLine());
				}
				long e = System.currentTimeMillis();
				System.out.println("spend:" + (e - b) + "ms");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
					}
					if (os != null) {
						os.close();
					}
					if (scan!=null){
		            	scan.close();
		            }
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

	}
}
