package br.com.nawan.chat;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

public class ClientApplication extends Thread {

	private JFrame frame;
	private JTextField mensagemUsuario;
	private JTextField nomeUsuario;
	private JTextArea chat;

	private DatagramSocket socket;
	private InetAddress dest;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientApplication window = new ClientApplication();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ClientApplication() {
		initialize();
		new Thread(this).start();
	}
	
	private void initialize() {

		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel titulo = new JLabel("Bate-papo");
		titulo.setFont(new Font("Arial Cursiva", Font.PLAIN, 28));
		titulo.setHorizontalAlignment(SwingConstants.CENTER);

		chat = new JTextArea();
		chat.setBackground(Color.cyan);
		chat.setForeground(Color.DARK_GRAY);
		chat.setEditable(false);

		JButton botaoEnviar = new JButton("Enviar");
		botaoEnviar.setBackground(Color.CYAN);
		botaoEnviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enviarMensagem();
			}
		});

		mensagemUsuario = new JTextField();
		mensagemUsuario.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enviarMensagem();
			}
		});
		mensagemUsuario.setToolTipText("");
		mensagemUsuario.setColumns(10);
		mensagemUsuario.setBackground(Color.cyan);
		mensagemUsuario.setFont(new Font("Arial Cursiva", Font.PLAIN, 15));

		JLabel nome = new JLabel("Nome:");
		JLabel digite = new JLabel("Digite:");

		nomeUsuario = new JTextField();
		nomeUsuario.setText("Anônimo");
		nomeUsuario.setForeground(Color.DARK_GRAY);
		nomeUsuario.setColumns(10);
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(chat, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
								.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(digite).addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(mensagemUsuario, GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(botaoEnviar))
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addComponent(titulo, GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(nome)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(nomeUsuario, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(titulo, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
						.addComponent(nomeUsuario, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(nome))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chat, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE, false)
							.addComponent(digite)
						.addComponent(mensagemUsuario, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
						.addComponent(botaoEnviar, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(21))
		);
		frame.getContentPane().setLayout(groupLayout);
	}

	@Override
	public void run() {
		try {
			socket = new DatagramSocket();
			dest = InetAddress.getByName("localhost");
			String conexao = "Conexão iniciada";
			DatagramPacket iniciar = new DatagramPacket(conexao.getBytes(), conexao.length(), dest,
					4545);
			socket.send(iniciar);

			while (true) {
				DatagramPacket resposta = new DatagramPacket(new byte[512], 512);
				socket.receive(resposta);
				String resp = new String(resposta.getData());
				chat.append(resp + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void enviarMensagem() {
		String nome = (nomeUsuario.getText().trim().isEmpty()) ? "Anônimo" : nomeUsuario.getText().trim();
		String mensagem = mensagemUsuario.getText().trim();
		if (!mensagem.isEmpty() && mensagem != null) {
			try {
				String envio = String.format("%s disse: %s", nome, mensagem);
				DatagramPacket msg = new DatagramPacket(envio.getBytes(), envio.length(), dest, 4545);
				socket.send(msg);
				mensagemUsuario.setText("");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
