package com.bala.learning.learning.bonecp;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Transaction {

	static Connection con;
	public static void main(String args[]) throws Exception{
		ConnectionFactory factory = ConnectionFactory.getInstance(args[0]);
		con = factory.getConnection();
		DatabaseMetaData meta = con.getMetaData();
		String table = "Transaction_2015_09_10";
		ResultSet rs = meta.getTables(null, null, table , null);
		if (!rs.next()) {
			createTable(table);
		}
	}

	private static void createTable(String tblName) throws SQLException {
		try {
			String sqlCreate = "CREATE TABLE IF NOT EXISTS "
					+ tblName
					+ " (id BIGINT NOT NULL AUTO_INCREMENT, campaign_id INTEGER, advertiser_id INTEGER, publisher_id INTEGER,amount_spent DOUBLE, revenue DOUBLE, request_type VARCHAR(5), date VARCHAR(25), status VARCHAR(60), created_on TIMESTAMP, PRIMARY KEY(id))";

			Statement stmt = con.createStatement();
			stmt.execute(sqlCreate);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Could not create table " + tblName);

		}
	}
	
	private static void insertTable(){
		
	}
}
