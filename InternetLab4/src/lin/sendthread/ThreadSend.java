package lin.sendthread;

import lin.buffer.Buffer;
import lin.message.Messager;

public class ThreadSend implements Runnable {

	private final int ACKMAX = 8;// Ԥ��ack�����ֵΪ7

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

			if (buffer.hasNext()) {// �д���������
				to_send += buffer.getElement();
				// System.out.println("notre: " + to_send + " next_frame_to_send: " +
				// next_frame_to_send);
				System.out.println("frame_resending: " + getsequence(to_send) + " ack: " + to_send.charAt(0));
				System.out.println("next_frame_to_send: " + (getsequence(to_send) + 1));
				//System.out.println("frame_sending: " + next + " ack: " + to_send.charAt(0));
				next_frame_to_send++;
				//System.out.println("next_frame_to_send: " + next_frame_to_send);
				if (message.hasNext()) {// �д����͵�ȷ��֡���߻���������Ҫǰ��
					String tempstr = message.getElement();
					ack_1 = tempstr.charAt(0) - '0';
					ack_2 = tempstr.charAt(1) - '0';
					to_send = ack_1 + to_send;// ���ȷ��֡��û��ȷ��֡�����MAX
					if (ack_2 != ACKMAX) {// �л�������ǰ����
						buffer.forward(ack_2);
					}
				} else {
					to_send = ACKMAX + to_send;
				}
				message.send(to_send);
				to_send = "";
			} else {// û�д��������ݣ��ж��Ƿ�ʱ�ط�
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
					to_send = ack_1 + to_send;// ���ȷ��֡��û��ȷ��֡�����MAX
					if (ack_2 != ACKMAX) {// �л�������ǰ����
						buffer.forward(ack_2);
					}
					to_send += ACKMAX;
					message.send(to_send);
					to_send = "";
				}
				if (System.currentTimeMillis() - buffer.getLowtime() > 1500) {
					// System.out.println("over time and resend!");
					int resend_frame = next_frame_to_send - buffer.getsum();
					buffer.resetPointer();// ���ô�����ָ��
					while (buffer.hasNext()) {
						to_send = "";
						to_send += buffer.getElement();
						// System.out.println("to_send: " + to_send + " next_frame_to_send: " +
						// getsequence(to_send));
						System.out.println("frame_resending: " + getsequence(to_send) + " ack: " + to_send.charAt(0));
						System.out.println("next_frame_to_send: " + (getsequence(to_send) + 1));
						if (message.hasNext()) {// �д����͵�ȷ��֡���߻���������Ҫǰ��
							String tempstr = message.getElement();
							ack_1 = tempstr.charAt(0) - '0';
							ack_2 = tempstr.charAt(1) - '0';
							to_send = ack_1 + to_send;// ���ȷ��֡��û��ȷ��֡�����MAX
							if (ack_2 != ACKMAX) {// ��������ǰ��
								buffer.forward(ack_2);
							}
						} else {
							to_send = ACKMAX + to_send;
						}
						message.send(to_send);
						to_send = "";// ����to_send
					}
				}
			}
			while (message.hasNext()) {
				String tempstr = message.getElement();
				ack_1 = tempstr.charAt(0) - '0';
				ack_2 = tempstr.charAt(1) - '0';
				to_send = ack_1 + to_send;// ���ȷ��֡��û��ȷ��֡�����MAX
				if (ack_2 != ACKMAX) {// �л�������ǰ����
					buffer.forward(ack_2);
				}
				to_send += ACKMAX;
				message.send(to_send);
				to_send = "";
			}
		}
	}
}
