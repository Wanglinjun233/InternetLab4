package lin.sender;

import java.util.Random;
import lin.message.Messager;
import lin.recethread.ThreadRece;
import lin.sendthread.ThreadSend;
import lin.buffer.*;

public class Main {

	/**
	 * Host1程序入口
	 * @param args
	 */
	public static void main(String[]args) {
		Buffer buffer = new Buffer();//维护数据的滑动窗口缓冲
		Messager message = new Messager("C:\\Users\\Page\\Desktop\\config.txt");//用以接收线程和发送线程之间互通数据
		ThreadSend thread1 = new ThreadSend(buffer, message);//发送线程类
		ThreadRece thread2 = new ThreadRece(message, buffer);//接收线程类
		Thread t2 = new Thread(thread2);
		t2.start();
		Thread t1 = new Thread(thread1);//开启发送服务和接收服务
		t1.start();
		Random ran = new Random();
		for(int i = 0; i < 40; i++) {//模拟发送40条数据
			String temp = "";
			int num = 0;
			for(int j = 0; j < 16; j++) {//每条数据是一串十六进制串
				num = ran.nextInt();
				if(num % 2 == 0)
					temp += '1';
				else 
					temp += '0';
			}
			while(!buffer.addOneMessage(temp, i)) {//向缓冲池内放一条数据
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
		}//等带接收线程结束
	}
}
