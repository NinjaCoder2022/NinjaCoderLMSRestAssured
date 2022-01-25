package com.lms.api.dbmanager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Dbmanager {

	String url = "jdbc:postgresql://localhost:5432/LMS_DB";
	String user = "postgres";
	String password = "postgres";
	Statement stmt;
	Connection con;
	ResultSet rs;
	boolean chknull;
	private String query;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void createConnection() throws SQLException {
		con = DriverManager.getConnection(url, user, password);
		System.out.println("Connection Created ");
	}

	public ResultSet executeQuery() throws SQLException {
		stmt = con.createStatement();
		rs = stmt.executeQuery(query);
		System.out.println("Query Executed ");
		return rs;
	}

	public void closeConnection() throws SQLException {
		con.close();
		System.out.println("Connection Closed ");
	}

	public ArrayList<String> dbvalidationSkill(String id) throws SQLException {

		chknull = false;
		String query = "select * from tbl_lms_skill_master where skill_id ='" + id + "'";
		setQuery(query);
		createConnection();
		ResultSet rs = executeQuery();

		while (rs.next()) {
			String skillId = rs.getString(1);
			String skill_name = rs.getString(2);
			String creation_time = rs.getString(3);
			String last_mod_time = rs.getString(4);

			ArrayList<String> resultlist = new ArrayList<String>();
			resultlist.add(skillId);
			resultlist.add(skill_name);
			resultlist.add(creation_time);
			resultlist.add(last_mod_time);
			System.out.println("Record from Database : " + resultlist);

			return resultlist;

		}
		// last step
		closeConnection();
		if (chknull == false) {
			ArrayList<String> deleteChk = new ArrayList<String>();
			deleteChk.add("Deleted");
			return deleteChk;
		} else
			return null;
	}

	// For User API
	public ArrayList<String> dbvalidationUser(String id) throws SQLException {
		chknull = false;
		String query = "select * from tbl_lms_user where user_id ='" + id + "'";
		setQuery(query);
		createConnection();
		ResultSet rs = executeQuery();

		while (rs.next()) {
			String user_id = rs.getString(1);
			String name = rs.getString(2);
			String phone_number = rs.getString(4);
			String location = rs.getString(5);
			String time_zone = rs.getString(6);
			String linkedin_url = rs.getString(7);
			String education_ug = rs.getString(8);
			String education_pg = rs.getString(9);
			String comments = rs.getString(10);
			String visa_status = rs.getString(11);
			String creation_time = rs.getString(12);
			String last_mod_time = rs.getString(13);

			ArrayList<String> resultlist = new ArrayList<String>();
			resultlist.add(user_id);
			resultlist.add(name);
			resultlist.add(phone_number);
			resultlist.add(location);
			resultlist.add(time_zone);
			resultlist.add(linkedin_url);
			resultlist.add(education_ug);
			resultlist.add(education_pg);
			resultlist.add(comments);
			resultlist.add(visa_status);
			resultlist.add(creation_time);
			resultlist.add(last_mod_time);
			System.out.println("Record from Database : " + resultlist);

			chknull = true;
			return resultlist;

		}
		// last step
		closeConnection();
		if (chknull == false) {
			ArrayList<String> deleteChk = new ArrayList<String>();
			deleteChk.add("Deleted");
			return deleteChk;
		} else
			return null;

	}

	public ArrayList<String> dbvalidationUserSkillMap(String id) throws SQLException {
		chknull = false;
		String query = "select * from tbl_lms_userskill_map where user_skill_id ='" + id + "'";
		setQuery(query);
		createConnection();
		ResultSet rs = executeQuery();

		while (rs.next()) {
			String user_skill_id = rs.getString(1);
			String user_id = rs.getString(2);
			String skill_id = rs.getString(3);
			String months_of_exp = rs.getString(4);
			String creation_time = rs.getString(5);
			String last_mod_time = rs.getString(6);

			ArrayList<String> resultlist = new ArrayList<String>();
			resultlist.add(user_skill_id);
			resultlist.add(user_id);
			resultlist.add(skill_id);
			resultlist.add(months_of_exp);
			resultlist.add(creation_time);
			resultlist.add(last_mod_time);
			System.out.println("Record from Database : " + resultlist);

			return resultlist;

		}

		// last step
		closeConnection();
		if (chknull == false) {
			ArrayList<String> deleteChk = new ArrayList<String>();
			deleteChk.add("Deleted");
			return deleteChk;
		} else
			return null;

	}

}
