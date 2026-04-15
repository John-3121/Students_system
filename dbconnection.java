import java.sql.Connection;
import java.sql.SQLException;
import java.sql.*;
import java.io.*;

public class dbconnection{
			private static final String url= "jdbc:mysql://localhost:3306/students_databsse";
			private static final String uname="carline";
			private static final String password="";
	public static Connection dbcn() throws Exception{
	        return DriverManager.getConnection(url,uname,password);
	}
}
