package com.code.web.jdbc;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class StudentControllerServlet
 */
@WebServlet("/StudentControllerServlet")
public class StudentControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
     
	
	private StudentDbUtil studentDbUtil;
	
	@Resource(name="jdbc/web_student_tracker")
	private DataSource dataSource;
	
	
    @Override
	public void init() throws ServletException {
		super.init();
		
		//create statement db util
		try {
			studentDbUtil = new StudentDbUtil(dataSource);
			
		}
		catch(Exception exc)
		{
			throw new ServletException(exc); 
		}
		
	}
   

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			//read command
			
			String theCommand = request.getParameter("command");
			
			//if command missing
			
			if(theCommand==null)
			{
				theCommand="LIST";
			}
			
			//route to method
			switch (theCommand)
			{
			case "LIST" :
				         listStudents(request,response);
				         break;
				         
			case "ADD" :
		         addStudent(request,response);
		         break;
		         
			case "LOAD" :
		         loadStudent(request,response);
		         break;
		         
			case "UPDATE" :
		         updateStudent(request,response);
		         break;
		         
			case "DELETE" :
		         deleteStudent(request,response);
		         break; 
		         
			case "SEARCH" :
		         searchStudents(request,response);
		         break;   
		         
			default :
		         listStudents(request,response);
				         
			}
			
	          //list the students ...........in MVC fashion
	          listStudents(request,response);
		  }
		catch (Exception exc) {
			throw new ServletException(exc);
		}
	
	}

	private void searchStudents(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String theSearchName = request.getParameter("theSearchName");
		
		List<Student> students = studentDbUtil.searchStudents(theSearchName);
		
		request.setAttribute("STUDENT_LIST", students);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-students.jsp");
		dispatcher.forward(request, response);
	}


	private void deleteStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//read
		String theStudentId = request.getParameter("studentId");
		
		//delete student from db
		studentDbUtil.deleteStudent(theStudentId);
		
		//send back
		listStudents(request,response);
		
	}


	private void updateStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//read
		int id = Integer.parseInt(request.getParameter("studentId"));
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		//create
		Student theStudent = new Student(id,firstName,lastName,email);
		
		
		//perform update
		studentDbUtil.updateStudent(theStudent);
		
		//send back
		listStudents(request,response);
		
	}


	private void loadStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
				//create new student
				
				String theStudentId = request.getParameter("studentId");
			
				
				Student theStudent = studentDbUtil.getStudent(theStudentId);
				
				request.setAttribute("THE_STUDENT" ,theStudent);
				
				RequestDispatcher dispatcher = request.getRequestDispatcher("/update-student-form.jsp");
				dispatcher.forward(request, response);
				}


	private void addStudent(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//read student
		
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		
		//create new student
		
		Student theStudent = new Student(firstName,lastName,email);
		
		//add student to db
		
		StudentDbUtil.addStudent(theStudent);
		
		//send back to main page
		listStudents(request,response);
		
	}


	private void listStudents(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		//get student from db util
		List<Student> students = studentDbUtil.getStudents();
		
		//add students to the request
		request.setAttribute("STUDENT_LIST" , students);
		
		
		//send to JSP page (view)
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-students.jsp");
		dispatcher.forward(request, response);
		
	}

}
