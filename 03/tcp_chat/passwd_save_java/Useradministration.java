package passwd_save_java;

public interface Useradministration {
	public void addUser(String username, char[] password);
	public boolean checkUser(String username, char[] password);
}
