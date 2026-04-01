import java.util.Scanner;

 abstract public class User{
	String email;
	String password;
	public User(){}
	public User(String email,String password){
		this.email = email;
		this.password = password;
		}
		
		public String get_email(){
		return email;
		}
		public String get_password(){
		return password;
		}
		
		public abstract void options(studentsDAO st_dao ,Scanner sn,int user_id);
	
		
	
	}
