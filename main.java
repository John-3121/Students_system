import java.util.Scanner;
public class main{
		 
		 
	public static void main(String[] args){
		    boolean exit= true;
		    studentsDAO st_dao = new studentsDAO();
			Scanner sn = new Scanner(System.in);
			System.out.println("Student system");
			
			while(exit){
				System.out.println("1.Add student");
				System.out.println("2.View all student");
				System.out.println("3.remove student");
				System.out.println("4.exit");
				int user_input = sn.nextInt();
				sn.nextLine();
				
				switch(user_input){
					case 1:
						try{
							System.out.println("Enter your name");
							String name = sn.nextLine();
							System.out.println("Enter your age");
							int age = sn.nextInt();
							sn.nextLine();
							System.out.println("Enter your course");
							String course = sn.nextLine();
							st_dao.addStudent(new student(name,age,course));
							System.out.println("Added sucessfully");
							
							}
						catch(Exception e){
								System.out.println(e.getMessage());
							}
						
						break;
					case 2:
						try{
							 st_dao.viewAllStudents();
							}
						catch(Exception e){
							System.out.println(e.getMessage());
							}
						
						break;
					case 3:
						System.out.println("Enter the student id you want to remove");
						int student_id = sn.nextInt();
						st_dao.remove_student(student_id);
					
						break;
					case 4:
						exit=false;
						System.out.println("bye");
						break;
					
					}

				
				}
	}
	
	
	}
