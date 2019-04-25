package lin.test;

import lin.crc.CRC;

public class Test {

	public static void main(String[] args) {
		System.out.println(CRC.crcGenerate("01000110000111100101010010010100", false));
	}
}
