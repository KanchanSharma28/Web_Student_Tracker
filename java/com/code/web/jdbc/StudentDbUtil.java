package com.code.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {
	
	
	private static DataSource dataSource;
	
	
	public StudentDbUtil(DataSource theDataSource)
	{
		dataSource = theDataSource;
	}
	
	public List<Student> getStudents() throws Exception {
		
		List<Student> students = new ArrayList<>();
		
        Connection myConn=null;
	    Statement myStmt = null;
		ResultSet myRs =null;
		
		
		try {
		//get Connection
		myConn = dataSource.getConnection();
		
		//create sql statement
		String sql="select * from student order by last_name"; 
		
		myStmt = myConn.createStatement();
		
		// execute query
		myRs = myStmt.executeQuery(sql);
		

		//process result set
		
		while(myRs.next()) {
			
			int id = myRs.getInt("id");
			String firstName = myRs.getString("first_name");
			String lastName = myRs.getString("last_name");
			String email = myRs.getString("email");
			
			Student tempStudent = new Student(id,firstName,lastName,email);
			
			students.add(tempStudent);
			
			
		}
		
			
			return students;
		}
		finally {
			//close JDBC objects
			close(myConn, myStmt, myRs);	
			
		}
	
	}
	
	private static void close(Connection myConn, Statement myStmt, ResultSet myRs) {
		
		try {
			if(myRs != null)
			{
				myRs.close();
			}
			
			if(myStmt != null)
			{
				myStmt.close();
			}
			
			if(myConn != null)
			{
				myConn.close();
			}
			
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void addStudent(Student theStudent) throws Exception {
		
		Connection myConn = null;
		PreparedStatement myStmt=null;
		
		try {
		//create sql for insert
			myConn = dataSource.getConnection();
		
		//set the param value
			String sql = "insert into student"
				    	 + "(first_name, last_name, email)"
					     + "values(?,?,?)";
			
			myStmt = myConn.prepareStatement(sql);
		
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
		
			//execute sql insert
			myStmt.execute();
		}
	    finally {
		//clean up jdbc objects
	    	close(myConn,myStmt,null);
	    }
	}

	public Student getStudent(String theStudentId) throws Exception {
		
		Student theStudent = null;
		
		Connection myConn=null;
	    PreparedStatement myStmt = null;
		ResultSet myRs =null;
		int studentId;
		
		try {
			 
			//convert id to int
			studentId = Integer.parseInt(theStudentId);
			
			//get connection to database
			myConn = dataSource.getConnection();
			
			//create sql
			String sql = "select * from student where id =?";
			
			//create prepared statement
			myStmt = myConn.prepareStatement(sql);
			
			//set params
			myStmt.setInt(1,studentId);
			
			//execute statement
			myRs = myStmt.executeQuery();
			
			//retrieve data
			if(myRs.next()) {
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");
				
				//use studentId
				theStudent = new Student(studentId,firstName,lastName,email);
			}
			else {
				throw new Exception("Could not find student id: " + studentId);
			}
			
		return theStudent;
     	}
		finally {
			//clean up JDBC object
          			close(myConn,myStmt,myRs);
		}
	}

	public void updateStudent(Student theStudent) throws Exception {
		
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
	   
		try {
		myConn = dataSource.getConnection();
	
	String sql = "update student "
			      + "set first_name=? , last_name=?, email=?"
			      + "where id=?";
	
	myStmt = myConn.prepareStatement(sql);
	
	myStmt.setString(1, theStudent.getFirstName());
	myStmt.setString(2, theStudent.getLastName());
	myStmt.setString(3, theStudent.getEmail());
	myStmt.setInt(4, theStudent.getId());
	
	myStmt.execute();
	
	}
		finally {
			close(myConn,myStmt,null);
		}
	}

	public void deleteStudent(String theStudentId) throws Exception {
		
		Connection myConn=null;
		PreparedStatement myStmt = null;
		
		try {
			
			int studentId = Integer.parseInt(theStudentId);
			
			myConn = dataSource.getConnection();
			
			String sql = "delete from student where id =?";
			
			myStmt = myConn.prepareStatement(sql);
			
			myStmt.setInt(1, studentId);
			
			myStmt.execute();
			
		}
		finally {
			close(myConn, myStmt, null);
		}
		
	}

	public List<Student> searchStudents(String theSearchName) throws Exception {
		
		List<Student> students = new ArrayList<>();
		
		Connection myConn=null;
	    PreparedStatement myStmt = null;
		ResultSet myRs =null;
		int studentId;
		
		try {
			myConn = dataSource.getConnection();
		
		if(theSearchName != null && theSearchName.trim().length() > 0)
		{
		
			String sql = "select * from student where lower(first_name) like ? or lower(last_name) like ?";
		myStmt = myConn.prepareStatement(sql);
		
		String theSearchNameLike = "%" + theSearchName.toLowerCase() + "%";
		
		myStmt.setString(1, theSearchNameLike);
		myStmt.setString(2, theSearchNameLike);
	
		}
		else {
			String sql = "select * from student order by last_name";
			
			myStmt = myConn.prepareStatement(sql);
		}
		
		myRs = myStmt.executeQuery();
		
		while(myRs.next())
		{
            int id = myRs.getInt("id");
            String firstName = myRs.getString("first_name");
            String lastName = myRs.getString("last_name");
            String email = myRs.getString("email");
            
            Student tempStudent = new Student(id, firstName, lastName, email);
            
            students.add(tempStudent); 
		}
		return students;
		
		
		}
			finally {
				close(myConn,myStmt,myRs);
			}
	}
	
	
}
