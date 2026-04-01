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
		   
		public void update_student(int student_id,student std){
				String update_query = "UPDATE students SET name =?, age =?, course=? WHERE id ="+ student_id;
				try(Connection con = dbconnection.dbcn()){
						PreparedStatement pstmt = con.prepareStatement(update_query);
						pstmt.setString(1,std.name);
						pstmt.setInt(2,std.age);
						pstmt.setString(3,std.course);
						pstmt.executeUpdate();
					}catch(Exception e){
							System.out.println(e.getMessage());
						}
			
			}
			
		public void search_student(String student_name){
				try (Connection con = dbconnection.dbcn()){
						String query = "SELECT * FROM students where name = ?";
						PreparedStatement pst = con.prepareStatement(query);
						pst.setString(1,student_name);
						//----------
						//Statement st = con.createStatement();
						ResultSet rs = pst.executeQuery();
	
						while(rs.next()){
							 System.out.println(rs.getInt(1) + "."+ rs.getString(2) +" "+"Age:"+ rs.getInt(3) +" "+"Course: "+ rs.getString(4));
								System.out.println("---------------------");
							}
					
					}
					catch(Exception e){
						System.out.println(e.getMessage());
						}
			
			}
			public void Check_login_info(String email,String password){
				 try(Connection con = dbconnection.dbcn()){
							Scanner sc = new Scanner(System.in);
							String query ="SELECT roles FROM Admin WHERE email = ? AND password =?";
							PreparedStatement pst = con.prepareStatement(query);
							Admin admin = new Admin();
							Normal_user normal_user = new Normal_user();
							
							pst.setString(1,email);
							pst.setString(2,password);
							
							ResultSet rs = pst.executeQuery();
							int user_id;
							if(rs.next()){
								String roles = rs.getString("roles");
								user_id = rs.getInt("id");
							
								if(roles.equals("admin")){
									 System.out.println("Login successfully");
									  admin.options(new studentsDAO(),sc);
									
								
									}else if(roles.equals("Normal_user")){
									
										normal_user.options(new studentsDAO(),sc);
									}
								}
								
							System.out.println("Wrong name or password");
							return user_id;s	
							
							
					 
				  }catch(Exception e){
						 System.out.println(e.getMessage());
						 }			 
				}
			
		public void user_storeinf(String email,String password,String role){
				 try(Connection con = dbconnection.dbcn()){
							String query ="INSERT INTO Admin(email,password,roles) VALUES(?,?,?)";
							PreparedStatement pst = con.prepareStatement(query);
							pst.setString(1,email);
							pst.setString(2,password);
							pst.setString(3,role);
							pst.executeUpdate();
					 
				  }catch(Exception e){
						 System.out.println(e.getMessage());
						 }			 
		}
		
	
		
	}

