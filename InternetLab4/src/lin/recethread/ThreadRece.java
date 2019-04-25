package lin.recethread;

import lin.buffer.Buffer;
import lin.crc.CRC;
import lin.message.Messager;

public class ThreadRece implements Runnable {

	final int ACKMAX = 8;
	private Messager message;
	
	public ThreadRece(Messager messa, Buffer buffer) {
		this.message = messa;
	}

	public void run() {
		int expected_ack = 0;// ±¾µØack
		int expected_frame = 1;
		int sum = 1;
		int ack = 0;
		System.out.println("expected_ack: " + expected_ack);
		System.out.println("expected_frame: " + expected_frame);
		while (true) {
			String recestr = message.receive();
			if (recestr.length() > 2) {
				// System.out.println(recestr + " ack: " + expected_ack + " sum: " + sum);
				String tempp = "";
				for (int i = 0; i < recestr.length() - 5; i++)
					tempp += recestr.charAt(i);
				recestr = tempp;
			}
			int ack_1 = ACKMAX;
			int ack_2 = ACKMAX;
			boolean flag = false;
			String content = "";
			if (recestr.charAt(0) != ACKMAX + '0') {
				System.out.println("expected_ack: " + ack);
				ack_1 = recestr.charAt(0) - '0';
				if ((ack + 1) % ACKMAX == ack_1) {
					message.addElement(ACKMAX, ack);
					ack = (ack + 1) % ACKMAX;
				} else if ((ack + 2) % ACKMAX == ack_1) {
					message.addElement(ACKMAX, ack);
					ack = (ack + 1) % ACKMAX;
					message.addElement(ACKMAX, ack);
					ack = (ack + 1) % ACKMAX;

				}
				message.addElement(ACKMAX, ack);
				ack = (ack + 1) % ACKMAX;
			}
			if (recestr.charAt(1) != ACKMAX + '0') {
				ack_2 = recestr.charAt(1) - '0';
				for (int i = 2; i < recestr.length(); i++) {
					content += recestr.charAt(i);
				}
				if (ack_2 == expected_ack) {
					String crc = CRC.crcGenerate(content, false);
					if (CRC.checkCrc(crc)) {
						sum++;
						expected_ack = (expected_ack + 1) % ACKMAX;
						expected_frame++;
						System.out.println("expected_frame: " + expected_frame);
						message.addElement(ack_2, ACKMAX);
					}
				}
			}
		}
	}
}
