package lin.buffer;

import lin.crc.CRC;

public class Buffer {

	public final int ACKMAX = 8;
	public final int MAX = 7;// Լ���������Ϊ6
	private String[] content;// �����������
	private int tail;// ����βָ��
	private int head;// ����ͷָ��
	private int pointer;// ��ǰδ����ָ��
	private boolean is_full;
	int ack;

	long tt = -1;// ��¼ʱ��������
	long time[];

	private boolean endflag;// �Ƿ�׼�������ı�־λ

	public Buffer() {
		content = new String[MAX];
		time = new long[MAX];// ��¼���뻺���ʱ��
		tail = head = pointer = 0;
		is_full = false;
		endflag = false;
		ack = 0;
	}

	/**
	 * ������ʣ���֤�߳�֮���ͬ����ϵ
	 * 
	 * @param message
	 * @return
	 */
	synchronized public boolean addOneMessage(String message, int num) {
		if (is_full)// �жϻ����Ƿ�����
			return false;
		else {
			String temp = num + "";
			while(temp.length() < 5) {
				temp = '0' + temp;
			}
			message += CRC.crcGenerate(message, true);// ���crcѭ��У����
			message = ack + message;// ���ack��ͷ
			message += temp;
			ack = (ack + 1) % ACKMAX;
			content[head] = message;
			time[head] = System.currentTimeMillis();// ����ʱ���
			head = (head + 1) % MAX;
			if ((head + 1) % MAX == tail)
				is_full = true;
			return true;
		}
	}

	synchronized public void setflag(boolean bool) {
		this.endflag = bool;
	}

	/**
	 * �������ں���
	 * 
	 * @param sum
	 */
	synchronized public void forward(int ack) {
		if (this.content[tail].charAt(0) != ack + '0') {
			System.out.println("Error in ack_identify" + this.content[tail] + "  " + ack);
		}
		this.tail = (this.tail + 1) % MAX;
		if (this.tail == this.head)// ���ڱ��
			tt = -1;
		else
			tt = time[this.tail];
		if (this.is_full)
			this.is_full = !this.is_full;
	}

	/**
	 * �ж��Ƿ�����һ֡��Ҫ������
	 */
	synchronized public boolean hasNext() {
		return pointer != head;
	}

	/**
	 * ȡ����һ�������͵�֡
	 */
	synchronized public String getElement() {
		if (pointer == tail)// ʱ����ƶ�
			tt = time[pointer];
		String temp = content[pointer];
		time[pointer] = System.currentTimeMillis();// ���浱ǰλ��ʱ���
		pointer = (pointer + 1) % MAX;
		return temp;
	}

	synchronized public long getLowtime() {
		if (tt != -1)
			return tt;
		else
			return System.currentTimeMillis();
	}

	/**
	 * ����endflag
	 * 
	 * @return
	 */
	synchronized public boolean getendflag() {
		return endflag;
	}

	/**
	 * ���ô�����ָ��
	 */
	synchronized public void resetPointer() {
		pointer = tail;
	}

	/**
	 * ��ȡ�����ڵ���������
	 * 
	 * @return
	 */
	synchronized public int getsum() {
		int sum = 0;
		int i = pointer;
		for (; i != head;) {
			sum++;
			i = (i + 1) % MAX;
		}
		return sum;
	}
}
