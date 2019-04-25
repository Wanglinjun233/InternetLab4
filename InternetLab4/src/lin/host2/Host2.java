package lin.host2;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;

import lin.buffer.Buffer;
import lin.message.Messager;
import lin.recethread.ThreadRece;
import lin.sendthread.ThreadSend;

public class Host2 {

	/**
	 * Host1�������
	 * 
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		Buffer buffer = new Buffer();// ά�����ݵĻ������ڻ���
		Messager message = new Messager("C:\\Users\\Page\\Desktop\\config.txt");// ���Խ����̺߳ͷ����߳�֮�以ͨ����
		message.addRess = "wlj";
		try {
			message.socket = new DatagramSocket(message.port);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ThreadSend thread1 = new ThreadSend(buffer, message);// �����߳���
		ThreadRece thread2 = new ThreadRece(message, buffer);// �����߳���
		Thread t2 = new Thread(thread2);
		t2.start();
		while(message.getAddressFlag()) {
			try {
				Thread.sleep(200);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		Thread t1 = new Thread(thread1);// �������ͷ���ͽ��շ���
		t1.start();
		Random ran = new Random();
		for (int i = 0; i < 40; i++) {// ģ�ⷢ��40������
			String temp = "";
			int num = 0;
			for (int j = 0; j < 16; j++) {// ÿ��������һ��ʮ�����ƴ�
				num = ran.nextInt();
				if (num % 2 == 0)
					temp += '1';
				else
					temp += '0';
			}
			while (!buffer.addOneMessage(temp, i)) {// �򻺳���ڷ�һ������
				try {
					Thread.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		buffer.setflag(true);
		t2.join();// �ȴ������߳̽���
		t1.join();
	}
}
