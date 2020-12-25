package top.nrcynet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class CommunicationLauncher {

	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("common:\txxx.jar xxx {ip address}");
			return;
		}

		String outputData = null;

		Socket connect = null;

		Scanner scanner = null;

		Scanner input = null;

		PrintWriter output = null;

		try {
			connect = new Socket();

			connect.connect(new InetSocketAddress(args[0], 8081));

			scanner = new Scanner(System.in);

			input = new Scanner(connect.getInputStream());

			output = new PrintWriter(new OutputStreamWriter(connect.getOutputStream()), true);

			CLReceiveModel receive = new CLReceiveModel(input, connect);

			receive.start();

			while (scanner.hasNext()) {
				outputData = scanner.nextLine();

				if (outputData.equals("exit")) {
					try {
						output.println(connect.getInetAddress() + "\tconnect session closed");
						connect.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (connect.isClosed()) {
					break;
				}

				output.println(connect.getInetAddress() + ":\n" + outputData);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}

			if (output != null) {
				output.close();
			}

			if (input != null) {
				input.close();
			}

			if (connect != null) {
				try {
					connect.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}

//接收模块 线程
class CLReceiveModel extends Thread {

	private String inputData;
	private Scanner input;
	private Socket connect;

	public CLReceiveModel(Scanner input, Socket connect) {
		super();
		this.input = input;
		this.connect = connect;
	}

	@Override
	public void run() {

		while (input.hasNext()) {
			inputData = input.nextLine();

			if (inputData.equals("exit")) {
				try {
					System.out.println(connect.getInetAddress() + "\tconnect session closed");
					connect.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (connect.isClosed()) {
				return;
			}

			System.out.println(connect.getInetAddress() + ":\n" + inputData);
		}

	}

}
