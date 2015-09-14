package com.bala.learning.learning.bonecp;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionFactory {
	private static final Logger LOG = Logger.getLogger(ConnectionFactory.class);
	private static final String PROP_FILE = "db.properties";
	private static final String DB_HOST = "db.host";
	private static final String DB_NAME = "db.name";
	private static final String DB_USERNAME = "db.username";
	private static final String DB_PASSWORD = "db.password";

	private Connection conn;
	static final Object _lock = new Object();
	HikariConfig config = new HikariConfig();
	HikariDataSource ds;

	private ConnectionFactory(String confDir) throws ConfigurationException {
		final Configuration propConfig = new PropertiesConfiguration(new File(confDir, PROP_FILE));
		final String dbIpaddress = propConfig.getString(DB_HOST);
		final String dbName = propConfig.getString(DB_NAME);

		config.setJdbcUrl("jdbc:mysql://" + dbIpaddress + ":3306/" + dbName);
		config.setUsername(propConfig.getString(DB_USERNAME));
		config.setPassword(propConfig.getString(DB_PASSWORD));
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		ds = new HikariDataSource(config);
	}

	public Connection getConnection() throws SQLException {
		conn = ds.getConnection();
		return conn;
	}

	public static ConnectionFactory getInstance(String confDir) throws ConfigurationException {
		ConnectionFactory factory = null;
		if (factory == null) {
			synchronized (_lock) {
				if (factory == null)
					factory = new ConnectionFactory(confDir);
			}
		}
		return factory;
	}
}