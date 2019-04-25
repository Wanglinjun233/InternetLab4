package lin.buffer;

import lin.crc.CRC;

public class Buffer {

	public final int ACKMAX = 8;
	public final int MAX = 7;// 约定最大数量为6
	private String[] content;// 存放数据数组
	private int tail;// 队列尾指针
	private int head;// 队列头指针
	private int pointer;// 当前未发送指针
	private boolean is_full;
	int ack;

	long tt = -1;// 记录时间沿下线
	long time[];

	private boolean endflag;// 是否准备结束的标志位

	public Buffer() {
		content = new String[MAX];
		time = new long[MAX];// 记录放入缓冲的时间
		tail = head = pointer = 0;
		is_full = false;
		endflag = false;
		ack = 0;
	}

	/**
	 * 互斥访问，保证线程之间的同步关系
	 * 
	 * @param message
	 * @return
	 */
	synchronized public boolean addOneMessage(String message, int num) {
		if (is_full)// 判断缓冲是否已满
			return false;
		else {
			String temp = num + "";
			while(temp.length() < 5) {
				temp = '0' + temp;
			}
			message += CRC.crcGenerate(message, true);// 添加crc循环校验码
			message = ack + message;// 添加ack表头
			message += temp;
			ack = (ack + 1) % ACKMAX;
			content[head] = message;
			time[head] = System.currentTimeMillis();// 设置时间截
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
	 * 滑动窗口后移
	 * 
	 * @param sum
	 */
	synchronized public void forward(int ack) {
		if (this.content[tail].charAt(0) != ack + '0') {
			System.out.println("Error in ack_identify" + this.content[tail] + "  " + ack);
		}
		this.tail = (this.tail + 1) % MAX;
		if (this.tail == this.head)// 窗口变空
			tt = -1;
		else
			tt = time[this.tail];
		if (this.is_full)
			this.is_full = !this.is_full;
	}

	/**
	 * 判断是否有下一帧需要被发送
	 */
	synchronized public boolean hasNext() {
		return pointer != head;
	}

	/**
	 * 取出下一个待发送的帧
	 */
	synchronized public String getElement() {
		if (pointer == tail)// 时间截移动
			tt = time[pointer];
		String temp = content[pointer];
		time[pointer] = System.currentTimeMillis();// 保存当前位置时间截
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
	 * 返回endflag
	 * 
	 * @return
	 */
	synchronized public boolean getendflag() {
		return endflag;
	}

	/**
	 * 重置待发送指针
	 */
	synchronized public void resetPointer() {
		pointer = tail;
	}

	/**
	 * 获取窗口内的数据总数
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
