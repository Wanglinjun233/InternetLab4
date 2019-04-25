package lin.sender;

import java.util.Random;
import lin.message.Messager;
import lin.recethread.ThreadRece;
import lin.sendthread.ThreadSend;
import lin.buffer.*;

public class Main {

	/**
	 * Host1�������
	 * @param args
	 */
	public static void main(String[]args) {
		Buffer buffer = new Buffer();//ά�����ݵĻ������ڻ���
		Messager message = new Messager("C:\\Users\\Page\\Desktop\\config.txt");//���Խ����̺߳ͷ����߳�֮�以ͨ����
		ThreadSend thread1 = new ThreadSend(buffer, message);//�����߳���
		ThreadRece thread2 = new ThreadRece(message, buffer);//�����߳���
		Thread t2 = new Thread(thread2);
		t2.start();
		Thread t1 = new Thread(thread1);//�������ͷ���ͽ��շ���
		t1.start();
		Random ran = new Random();
		for(int i = 0; i < 40; i++) {//ģ�ⷢ��40������
			String temp = "";
			int num = 0;
			for(int j = 0; j < 16; j++) {//ÿ��������һ��ʮ�����ƴ�
				num = ran.nextInt();
				if(num % 2 == 0)
					temp += '1';
				else 
					temp += '0';
			}
			while(!buffer.addOneMessage(temp, i)) {//�򻺳���ڷ�һ������
				try {
					Thread.sleep(50);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		buffer.setflag(true);
		try {
			t2.join();
			t1.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//�ȴ������߳̽���
	}
}
