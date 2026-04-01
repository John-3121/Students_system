import java.util.Scanner;
public class main{
		 static String role;
		 public static Scanner sn(){
			 return new Scanner(System.in);
			 }
			 public static studentsDAO st_dao(){return new studentsDAO();}
		 
	public static void main(String[] args){
		    boolean exit= true;
		    studentsDAO st_dao = st_dao();
			Scanner sn = sn();
			
			while(exit){
				System.out.println("Welcome to Student record ssystem");
			
				System.out.println("1,Sign in");
				System.out.println("2.Sign up");
				int log = sn.nextInt();
				
				if(log==1){sign_in();}
				   else if(log == 2){sign_up();}
					
			
			
					
				}
	}
		private static void sign_in(){
			try{
				  Scanner sn = sn();
				      studentsDAO st_dao = st_dao();
				System.out.println("Enter your email;");
				String email = sn.nextLine();
				System.out.println("Enter your password:");
				String password = sn.nextLine();
				st_dao.Check_login_info(email,password);
			
				
				}
				catch(Exception e){
				}
			}
		private static void sign_up(){
				try{
				
					studentsDAO st_dao = st_dao();
					Scanner sn = sn();
					System.out.println("Email");
					String email = sn.nextLine();
					System.out.println("Password");
					String password = sn.nextLine();
					System.out.println("1.Admin/n2.Normal_user");
					int option = sn.nextInt();
					
					if(option == 1){
						role = "admin";
						st_dao.user_storeinf(email,password,role);
							
						}else if(option == 2){
							role = "Normal_user";
							st_dao.user_storeinf(email,password,role);
							}
					
					}catch(Exception e){
						System.out.println(e.getMessage());
					}
			
				
			
			}
	
	
	}
