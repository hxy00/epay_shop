package com.emt.shoppay.util;

import org.apache.http.util.TextUtils;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class SystemUtil {

	/**
	 * @category 获取一个整数的随机数，范围在[min, max]
	 * @param min
	 * @param max
	 * @return
	 */
	public static long getRandom(int min, int max) {
		if (min >= max) {
			max = min + 1;
		}
		Random random = new Random();
		int jjj = Math.abs(random.nextInt(max));
		return jjj % (max - min + 1) + min;
	}

	/**
	 * @category 生成一个GUID串
	 * @return GUID
	 */
	public static String newGuid() {
		RandomGUID rdmGUID = new RandomGUID();
		return rdmGUID.toString();
	}

	private static class RandomGUID {
		public String valueBeforeMD5 = "";
		public String valueAfterMD5 = "";
		private static Random myRand;
		private static SecureRandom mySecureRand;

		private static String s_id;

		/*
		 * Static block to take care of one time secureRandom seed. It takes a
		 * few seconds to initialize SecureRandom. You might want to consider
		 * removing this static block or replacing it with a
		 * "time since first loaded" seed to reduce this time. This block will
		 * run only once per JVM instance.
		 */

		static {
			mySecureRand = new SecureRandom();
			long secureInitializer = mySecureRand.nextLong();
			myRand = new Random(secureInitializer);
			try {
				s_id = InetAddress.getLocalHost().toString();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

		}

		/*
		 * Default constructor. With no specification of security option, this
		 * constructor defaults to lower security, high performance.
		 */
		public RandomGUID() {
			getRandomGUID(false);
		}

		/*
		 * Constructor with security option. Setting secure true enables each
		 * random number generated to be cryptographically strong. Secure false
		 * defaults to the standard Random function seeded with a single
		 * cryptographically strong random number.
		 */
		public RandomGUID(boolean secure) {
			getRandomGUID(secure);
		}

		/*
		 * Method to generate the random GUID
		 */
		private void getRandomGUID(boolean secure) {
			MessageDigest md5 = null;
			StringBuffer sbValueBeforeMD5 = new StringBuffer();

			try {
				md5 = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				System.out.println("Error: " + e);
			}

			try {
				long time = System.currentTimeMillis();
				long rand = 0;

				if (secure) {
					rand = mySecureRand.nextLong();
				} else {
					rand = myRand.nextLong();
				}

				// This StringBuffer can be a long as you need; the MD5
				// hash will always return 128 bits. You can change
				// the seed to include anything you want here.
				// You could even stream a file through the MD5 making
				// the odds of guessing it at least as great as that
				// of guessing the contents of the file!
				sbValueBeforeMD5.append(s_id);
				sbValueBeforeMD5.append(":");
				sbValueBeforeMD5.append(Long.toString(time));
				sbValueBeforeMD5.append(":");
				sbValueBeforeMD5.append(Long.toString(rand));

				valueBeforeMD5 = sbValueBeforeMD5.toString();
				md5.update(valueBeforeMD5.getBytes());

				byte[] array = md5.digest();
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < array.length; ++j) {
					int b = array[j] & 0xFF;
					if (b < 0x10)
						sb.append('0');
					sb.append(Integer.toHexString(b));
				}

				valueAfterMD5 = sb.toString();

			} catch (Exception e) {
				System.out.println("Error:" + e);
			}
		}

		/*
		 * Convert to the standard format for GUID (Useful for SQL Server
		 * UniqueIdentifiers, etc.) Example:
		 * C2FEEEAC-CFCD-11D1-8B05-00600806D9B6
		 */
		public String toString() {
			String raw = valueAfterMD5.toUpperCase();
			StringBuffer sb = new StringBuffer();
			sb.append(raw.substring(0, 8));
			sb.append("-");
			sb.append(raw.substring(8, 12));
			sb.append("-");
			sb.append(raw.substring(12, 16));
			sb.append("-");
			sb.append(raw.substring(16, 20));
			sb.append("-");
			sb.append(raw.substring(20));

			return sb.toString();
		}
	}

	public static String getClassPath() {
		URL url = Thread.currentThread().getContextClassLoader().getResource("");
		return url.getPath().substring(1);
	}

	public static boolean isMobileNo(String mobile) {
		if (TextUtils.isEmpty(mobile)) {
			return false;
		}
		/**
		 * Pattern pattern =
		 * Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		 * Matcher matcher = pattern.matcher(mobile); return matcher.matches();
		 */
		return mobile.length() == 11 && mobile.substring(0, 1).equals("1");
	}

	/**
	 * 防止sql注入
	 * @param sql
	 * @return
	 */
	public static String SQLInjection(String sql) {
		return sql.replaceAll(".*([';]+|(--)+).*", " ");
	}

	/*
	 * Demonstraton and self test of class
	 */
	public static void main(String args[]) {
		System.out.println(getClassPath());

		for (int i = 0; i < 100; i++) { // 这是生成10000个此32位编码,唯一性的哦
			// RandomGUID myGUID = new RandomGUID();
			// System.out.println("Seeding String=" + myGUID.valueBeforeMD5);
			// System.out.println("rawGUID=" + myGUID.valueAfterMD5);
			// System.out.println("RandomGUID=" + RandomGUID.toString());
			// System.out.println(newGuid());
		}
	}

}
