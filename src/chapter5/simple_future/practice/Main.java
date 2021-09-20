package chapter5.simple_future.practice;

public class Main {
	public static void main(String[] args) {
		Client client = new Client();
		Data result = client.queryByParam("name");
		System.out.println("凭证:"+result);
		System.out.println("结果:"+result.getResult());
	}
}
