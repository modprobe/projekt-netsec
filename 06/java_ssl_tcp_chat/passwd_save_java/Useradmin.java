package passwd_save_java;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Useradmin implements Useradministration{

	private static  Scanner sc = new Scanner(System.in);

	public static void main(String[] args){
		while (true) {
			String input = sc.nextLine();
			Useradmin usradmin = new Useradmin();
			usradmin.matchCommand(input);
		}
	}

	@Override
	public void addUser(String username, char[] password) {
		password = Arrays.copyOf(password, 20);
		if (findUserLine(username) != null) {
			System.out.println("User already exists");
		}
		else {
			try {
				final Random r = new SecureRandom();
				byte[] salt = new byte[32];
				r.nextBytes(salt);

				MessageDigest digest = MessageDigest.getInstance("SHA-512");
				digest.update(salt);

				digest.update(charArrayToBytesUTFCustom(password));
				password = null;

				byte[] output = digest.digest();

				for (int i = 0; i < 1000; i++) {
					output = digest.digest(output);
				}

				String str = username +":" +
						bytesToHex(salt) + ":" +
						bytesToHex(output);
				Path file = Paths.get("password.txt");

				try {
					List<String> lines = Files.readAllLines(file);
					lines.add(str);
					Files.write(file, lines, StandardCharsets.UTF_8);
					System.out.println("User " + username + " added");
				}
				catch (IOException e) {
					System.err.println("Unexpected Error occured.");
				}
			} 
			catch (NoSuchAlgorithmException e) {
				System.err.println("SHA-512 Encryption not available.");
			}
		}
	}

	@Override
	public boolean checkUser(String username, char[] password) {
		password = Arrays.copyOf(password, 20);
		List<String> userline = findUserLine(username);
		if (userline != null) {
			byte[] salt = hexStringToByteArray(userline.get(0));
			try {
				MessageDigest digest = MessageDigest.getInstance("SHA-512");
				digest.update(salt);
				digest.update(charArrayToBytesUTFCustom(password));
				password = null;
				byte[] output = digest.digest();

				for (int i = 0; i < 1000; i++) 
				{
					output = digest.digest(output);
				}
				return bytesToHex(output).equals(userline.get(1));
			}
			catch (NoSuchAlgorithmException e){
				System.err.println("SHA-512 not available");
			}
		}
		return false;
	}

	
	private void matchCommand(String input){
		Pattern adduser = Pattern.compile("addUser (\\w{1,10})");
		Pattern checkuser = Pattern.compile("checkUser (\\w{1,10})");
		Pattern exit = Pattern.compile("exit");

		Matcher madduser = adduser.matcher(input);
		Matcher mcheckuser = checkuser.matcher(input);
		Matcher mexit = exit.matcher(input);

		boolean au = madduser.matches();
		boolean cu = mcheckuser.matches();
		boolean ex = mexit.matches();

		if (au) {
			System.out.println("Password: ");
			addUser(madduser.group(1), sc.nextLine().toCharArray());
		} else if (cu) {
			System.out.println("Password: ");
			System.out.println(checkUser(mcheckuser.group(1),sc.nextLine().toCharArray()));
		} else if (ex) {
			System.exit(0);
		}
		else {
			System.out.println("Not a valid Command");
		}
	}

	private List<String> findUserLine(String username){

		Pattern userline = Pattern.compile(username + ":(\\w{64,64}):(\\w{128,128})");
		Path file = Paths.get("password.txt");
		try {
			List<String> lines = Files.readAllLines(file);
			for (String line : lines) {
				Matcher matcher = userline.matcher(line);
				if(matcher.matches()) {
					return Arrays.asList(matcher.group(1),matcher.group(2));
				}
			}
		} catch (IOException e){
			System.err.println("Unexpected Error occured.");
		}
		return null;
	}

	private String bytesToHex(byte[] bytes) {

		final char[] hexArray = "0123456789abcdef".toCharArray();
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] hexStringToByteArray(String s) {

		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}

	public static byte[] charArrayToBytesUTFCustom(char[] buffer) {
		byte[] b = new byte[buffer.length << 1];
		for(int i = 0; i < buffer.length; i++) {
			int bpos = i << 1;
			b[bpos] = (byte) ((buffer[i]&0xFF00)>>8);
			b[bpos + 1] = (byte) (buffer[i]&0x00FF);
		}
		buffer = null;
		return b;
	}
}


