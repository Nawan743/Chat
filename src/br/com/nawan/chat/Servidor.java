package br.com.nawan.chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Servidor {

	private static List<InetAddress> enderecos = new ArrayList<>();
	private static List<Integer> portas = new ArrayList<>();

	public static void main(String[] args) throws Exception {

		try (DatagramSocket socket = new DatagramSocket(4545)) {
			System.out.println("Servidor iniciado com sucesso!");

			while (true) {
				DatagramPacket recebe = new DatagramPacket(new byte[512], 512);
				socket.receive(recebe);
				String recebido = new String(recebe.getData());

				if (enderecos.contains(recebe.getAddress())) {
					if (!portas.contains(recebe.getPort())) {
						enderecos.add(recebe.getAddress());
						portas.add(recebe.getPort());
					}
				} else {
					enderecos.add(recebe.getAddress());
					portas.add(recebe.getPort());
				}

				if (!recebido.trim().equalsIgnoreCase("Conexão iniciada")) {
					sendAll(socket, recebe);
					System.out.println(recebido);
				}
			}
		}
	}

	private static void sendAll(DatagramSocket socket, DatagramPacket recebido) throws IOException {
		for (int i = 0; i < enderecos.size(); i++) {
			DatagramPacket resp = new DatagramPacket(recebido.getData(), recebido.getLength(), enderecos.get(i),
					portas.get(i));
			socket.send(resp);
		}
	}
}
