package scoreRater;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Connie Shi
 * @author Abhishek Sharma 
 * @author Justin Barash
 * @author Steven Yoon
 * J.P. Morgan Code for Good 2014 (Team 10)
 *
 */
public class DAO {

	/**
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static Connection getConnectionToDatabase() throws SQLException, ClassNotFoundException {
		Connection con = null;

		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test");
		return con;
	}

	/**
	 * @param grade
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static void seeSQL(int grade) throws SQLException, ClassNotFoundException {
		int studentId = 2;
		int promptId = 1;
		
		Connection con = getConnectionToDatabase();
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT MAX(mid) FROM Message;");
		rs.next();
		
		int temp = rs.getInt("MAX(mid)");
		stmt.executeUpdate(" INSERT INTO Message VALUES("+ (++temp) +", "
				+ studentId + ", " 
				+ promptId +", " 
				+ grade +", NULL); ");
		
		System.out.println("Data added to database:\n"
				+ "Message id: " + temp + "\n"
				+ "Student id: " + studentId + "\n"
				+ "Prompt id: " + promptId + "\n"
				+ "Grade: " + grade + "\n");
		
	}

}