package lin.message;

import java.io.*;
import java.net.*;

public class Messager {

	public int port;
	public int error;
	public int lost;
	public DatagramSocket socket;
	public String addRess;
	private int sequence;
	private boolean sequence_flag;

	private int MAX = 100;// 约定ack最大值为6，所以队列上限为10，此时队列永远不会满
	private int[] ack_1;// 记录对方返回的确认帧
	private int[] ack_2;// 记录自己要返回给对方的确认帧
	private int tail;// 队尾
	private int head;// 队首

	synchronized public void send(String args) {// 发送一帧内容
		sequence++;
		String content = args;

		if (args.length() > 2) {
			if (sequence % error == 0 && sequence_flag) {
				System.out.println("simulate to lose a packet!");
				sequence_flag = !sequence_flag;
				return;
			} else if (sequence % lost == 0 && !sequence_flag) {
				System.out.println("simulate to send an error packet");
				sequence_flag = !sequence_flag;
				String temp = "";
				for (int i = 0; i < args.length() - 1; i++) {
					temp += args.charAt(i);
				}
				if (args.charAt(args.length() - 1) == '1') {
					temp += '0';
				} else
					temp += '1';
				content = temp;
			}
		}
		byte[] buf = content.getBytes();
		try {
			InetAddress address = InetAddress.getByName(addRess);
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
			socket.send(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String receive() {
		byte[] buf = new byte[2048];
		String recestr = null;
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		try {
			socket.receive(packet);
			recestr = new String(packet.getData(), 0, packet.getLength());
			if (this.addRess == "wlj") {
				InetAddress add = packet.getAddress();
				addRess = add.getHostAddress();
				port = packet.getPort();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return recestr;
	}

	/**
	 * 向里面增加待发送的确认帧ack或者接收到的确认帧sum
	 * 
	 * @param ack_1
	 * @param ack_2
	 */
	synchronized public void addElement(int ack, int sum) {
		this.ack_1[head] = ack;
		this.ack_2[head] = sum;
		head = (head + 1) % MAX;
	}

	/**
	 * 判断队列里面是否有ack待发送，或者有接收到的ack
	 * 
	 * @return
	 */
	synchronized public boolean hasNext() {
		if (head == tail)
			return false;
		else
			return true;
	}

	/**
	 * 返回一组待确认ack
	 * 
	 * @return
	 */
	synchronized public String getElement() {
		String temp = "";
		temp += ack_1[tail];
		temp += ack_2[tail];
		tail = (tail + 1) % MAX;
		return temp;
	}

	synchronized public boolean getAddressFlag() {
		return this.addRess == "wlj";
	}

	/**
	 * 加载配置文件内容
	 * 
	 * @param path
	 */
	public Messager(String path) {
		File file = new File(path);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String temp = reader.readLine();
			temp = temp.replace("UDPPort=", "");
			port = Integer.parseInt(temp);
			temp = reader.readLine();
			temp = temp.replace("FilterError=", "");
			error = Integer.parseInt(temp);
			temp = reader.readLine();
			temp = temp.replace("FilterLost=", "");
			lost = Integer.parseInt(temp);
			socket = new DatagramSocket();
			InetAddress myaddress = InetAddress.getLocalHost();
			addRess = myaddress.getHostAddress();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 初始化ack队列内容
		ack_1 = new int[MAX];
		ack_2 = new int[MAX];
		tail = head = 0;
		sequence = 0;
		sequence_flag = true;
	}
}
