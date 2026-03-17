import java.sql.Connection;
import java.sql.SQLException;
import java.io.*;
import java.util.Scanner;
import java.sql.*;


public class studentsDAO {
		
		 		 
		 public  void addStudent(student std) {
			 try(Connection con = dbconnection.dbcn()){
					 String insert_query = "INSERT INTO students(name,age,course) VALUES(?,?,?)";
				 	 PreparedStatement pstmt = con.prepareStatement(insert_query);
				 	 pstmt.setString(1,std.name);
				 	 pstmt.setInt(2,std.age);
				 	 pstmt.setString(3,std.course);
				 	 pstmt.executeUpdate();
					

				 }catch(Exception e){
						System.out.println(e.getMessage());
					 
					 }
					
			 }
			 
		public void viewAllStudents(){
					try (Connection con = dbconnection.dbcn()){
						String query = "SELECT * FROM students";
						Statement st = con.createStatement();
						ResultSet rs = st.executeQuery(query);
						while(rs.next()){
							 System.out.println(rs.getInt(1) + "."+ rs.getString(2) +" "+"Age:"+ rs.getInt(3) +" "+"Course: "+ rs.getString(4));
								System.out.println("---------------------");
							}
					
					}
					catch(Exception e){
						System.out.println(e.getMessage());
						}
				 }
				 
	   public void remove_student(int student_id){
				try(Connection  con = dbconnection.dbcn()){
					String delete_query = "DELETE FROM  students where id  =?";
					PreparedStatement pstmt = con.prepareStatement(delete_query);
					pstmt.setInt(1,student_id);
					pstmt.executeUpdate();
					System.out.println("Student sucessfully remove");
					}catch(Exception  e)
					{
						System.out.println(e.getMessage());
						}
		   
		   }
	}

