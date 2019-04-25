package lin.sendthread;

import lin.buffer.Buffer;
import lin.message.Messager;

public class ThreadSend implements Runnable {

	private final int ACKMAX = 8;// 预定ack的最大值为7

	private Buffer buffer;
	private Messager message;

	public static int getsequence(String args) {
		String temp = "";
		for (int i = args.length() - 5; i < args.length(); i++)
			temp += args.charAt(i);
		return Integer.parseInt(temp) + 1;
	}

	public ThreadSend(Buffer buff, Messager mes) {
		buffer = buff;
		message = mes;
	}

	public void run() {
		int next_frame_to_send = 1;

		System.out.println("next_frame_to_send: " + next_frame_to_send);
		while (true) {
			String to_send = "";
			int ack_1 = ACKMAX;
			int ack_2 = ACKMAX;

			if (buffer.hasNext()) {// 有待发送内容
				to_send += buffer.getElement();
				// System.out.println("notre: " + to_send + " next_frame_to_send: " +
				// next_frame_to_send);
				System.out.println("frame_resending: " + getsequence(to_send) + " ack: " + to_send.charAt(0));
				System.out.println("next_frame_to_send: " + (getsequence(to_send) + 1));
				//System.out.println("frame_sending: " + next + " ack: " + to_send.charAt(0));
				next_frame_to_send++;
				//System.out.println("next_frame_to_send: " + next_frame_to_send);
				if (message.hasNext()) {// 有待发送的确认帧或者滑动窗口需要前移
					String tempstr = message.getElement();
					ack_1 = tempstr.charAt(0) - '0';
					ack_2 = tempstr.charAt(1) - '0';
					to_send = ack_1 + to_send;// 添加确认帧，没有确认帧则添加MAX
					if (ack_2 != ACKMAX) {// 有滑动窗口前移数
						buffer.forward(ack_2);
					}
				} else {
					to_send = ACKMAX + to_send;
				}
				message.send(to_send);
				to_send = "";
			} else {// 没有待发送内容，判断是否超时重发
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while (message.hasNext()) {
					String tempstr = message.getElement();
					ack_1 = tempstr.charAt(0) - '0';
					ack_2 = tempstr.charAt(1) - '0';
					to_send = ack_1 + to_send;// 添加确认帧，没有确认帧则添加MAX
					if (ack_2 != ACKMAX) {// 有滑动窗口前移数
						buffer.forward(ack_2);
					}
					to_send += ACKMAX;
					message.send(to_send);
					to_send = "";
				}
				if (System.currentTimeMillis() - buffer.getLowtime() > 1500) {
					// System.out.println("over time and resend!");
					int resend_frame = next_frame_to_send - buffer.getsum();
					buffer.resetPointer();// 重置待发送指针
					while (buffer.hasNext()) {
						to_send = "";
						to_send += buffer.getElement();
						// System.out.println("to_send: " + to_send + " next_frame_to_send: " +
						// getsequence(to_send));
						System.out.println("frame_resending: " + getsequence(to_send) + " ack: " + to_send.charAt(0));
						System.out.println("next_frame_to_send: " + (getsequence(to_send) + 1));
						if (message.hasNext()) {// 有待发送的确认帧或者滑动窗口需要前移
							String tempstr = message.getElement();
							ack_1 = tempstr.charAt(0) - '0';
							ack_2 = tempstr.charAt(1) - '0';
							to_send = ack_1 + to_send;// 添加确认帧，没有确认帧则添加MAX
							if (ack_2 != ACKMAX) {// 滑动窗口前移
								buffer.forward(ack_2);
							}
						} else {
							to_send = ACKMAX + to_send;
						}
						message.send(to_send);
						to_send = "";// 重置to_send
					}
				}
			}
			while (message.hasNext()) {
				String tempstr = message.getElement();
				ack_1 = tempstr.charAt(0) - '0';
				ack_2 = tempstr.charAt(1) - '0';
				to_send = ack_1 + to_send;// 添加确认帧，没有确认帧则添加MAX
				if (ack_2 != ACKMAX) {// 有滑动窗口前移数
					buffer.forward(ack_2);
				}
				to_send += ACKMAX;
				message.send(to_send);
				to_send = "";
			}
		}
	}
}
