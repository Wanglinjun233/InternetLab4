package lin.crc;

public class CRC {

	private final static String genXString = "10001000000100001";

	/**
	 * ����crcУ����
	 * flagΪ���ʾ��Ӻ�׺0
	 * 
	 * @param args1
	 * @param flag
	 * @return
	 */
	public static String crcGenerate(String args1, boolean flag) {

		String fxz = args1;
		if (flag)
			for (int i = 0; i < genXString.length() - 1; i++) {
				fxz += '0';// ���油0
			}
		char[] fenzi = fxz.toCharArray();
		char[] fenmu = genXString.toCharArray();

		int index = 0;// ָ����ӵ�һ��������
		int p;

		while (true) {
			index = 0;
			for (int i = 0; i < fenzi.length; i++) {
				index = i;
				if (fenzi[i] == '1')
					break;
			}

			if (fenmu.length > fenzi.length - index)
				break;

			p = index;
			for (int i = 0; i < fenmu.length; i++) {
				fenzi[p] = operate(fenzi[p], fenmu[i]);
				p++;
			}
		}

		String result = "";
		for (int i = fenzi.length - fenmu.length + 1; i < fenzi.length; i++)
			result += fenzi[i];
		return result;
	}

	private static char operate(char a, char b) {
		if ((a == '0' && b == '0') || (a == '1' && b == '1'))
			return '0';
		else
			return '1';
	}
	
	public static boolean checkCrc(String args) {
		for(int i = 0; i < args.length(); i++) {
			if(args.charAt(i) != '0')
				return false;
		}
		return true;
	}
}
