import java.util.*;
import java.sql.*;
public class Normal_user extends User{
	public Normal_user(String email,String password){
		super(email,password);
	}
	public Normal_user(){}
		public void options(studentsDAO st_dao ,Scanner sn){
			boolean exit = true;
		while(exit){
				System.out.println("1.Add student");
				System.out.println("2.View all student");
				System.out.println("3.remove student");
				System.out.println("4.Update student");
				System.out.println("5.Search student");
				System.out.println("6.exit");
				int user_input = sn.nextInt();
				sn.nextLine();
				
					switch(user_input){
					case 1:
						try{
							System.out.println("Enter name");
							String name = sn.nextLine();
							System.out.println("Enter age");
							int age = sn.nextInt();
							sn.nextLine();
							System.out.println("Enter course");
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
							System.out.println("Enter the id desired to update");
							int id = sn.nextInt();
							sn.nextLine();
							System.out.println("Enter name");
							String name = sn.nextLine();
							System.out.println("Enter age");
							int age = sn.nextInt();
							sn.nextLine();
							System.out.println("Enter course");
							String course = sn.nextLine();
							st_dao.update_student(id,new student(name,age,course));
							System.out.println("Info updated sucessfully");
							

						break;
					case 5:
						System.out.println("search Student name");
						String st_name = sn.nextLine();
						st_dao.search_student(st_name);
						break;
					case 6:
						exit=false;
						System.out.println("bye");
						break;
					
					}
			
			
			}
			
		
     }

	
}
