package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLNonTransientConnectionException;
import java.sql.Statement;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.BeforeClass;

import junit.framework.TestCase;
import r2rml.engine.Configuration;
import r2rml.engine.R2RMLProcessor;

/**
 * Unit test for testing the functionality of this implementation using an
 * in memory database.
 * 
 * @author Christophe Debruyne
 *
 */
public class TestSQL extends TestCase {

	private static Logger logger = LogManager.getLogger(TestSQL.class);
	private static String connectionURL = "jdbc:derby:memory:testing";

	public TestSQL(String testName) {
		super(testName);
	}

	@BeforeClass
	public static void init() throws Exception {
		Configurator.initialize(new DefaultConfiguration());
	    Configurator.setRootLevel(Level.INFO);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		try {
			logger.info("Starting in-memory database for unit tests");
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			DriverManager.getConnection(connectionURL + ";create=true").close();
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Exception during database startup.");
		}
		try {
			Connection connection = DriverManager.getConnection(connectionURL);
			Statement statement = connection.createStatement();
			statement.execute("CREATE TABLE DEPT(DEPTNO INT PRIMARY KEY, DNAME VARCHAR(30), LOC VARCHAR(30))");
			statement.execute("CREATE TABLE EMP(EMPNO INT PRIMARY KEY, ENAME VARCHAR(100), JOB VARCHAR(20), DEPTNO INT, FOREIGN KEY (DEPTNO) REFERENCES DEPT(DEPTNO))");
			statement.execute("INSERT INTO DEPT VALUES (10, 'APPSERVER', 'NEW YORK')");
			statement.execute("INSERT INTO EMP VALUES (7369, 'SMITH', 'CLERK', 10)");

			statement.execute("CREATE TABLE DEPT2(DEPTNO INT, DNAME VARCHAR(30), LOC VARCHAR(30))");
			statement.execute("CREATE TABLE EMP2(EMPNO INT, ENAME VARCHAR(100), JOB VARCHAR(20))");
			statement.execute("CREATE TABLE EMP2DEPT(EMPNO INT, DEPTNO INT)");
			statement.execute("INSERT INTO DEPT2 VALUES (10, 'APPSERVER', 'NEW YORK')");
			statement.execute("INSERT INTO DEPT2 VALUES (20, 'RESEARCH', 'BOSTON')");
			statement.execute("INSERT INTO EMP2 VALUES (7369, 'SMITH', 'CLERK')");
			statement.execute("INSERT INTO EMP2 VALUES (7369, 'SMITH', 'NIGHTGUARD')");
			statement.execute("INSERT INTO EMP2 VALUES (7400, 'JONES', 'ENGINEER')");
			statement.execute("INSERT INTO EMP2DEPT VALUES (7369, 10)");
			statement.execute("INSERT INTO EMP2DEPT VALUES (7369, 20)");
			statement.execute("INSERT INTO EMP2DEPT VALUES (7400, 10)");
			statement.close();
			connection.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Failure setting up the database.");
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		logger.info("Stopping in-memory database.");
		try {
			DriverManager.getConnection(connectionURL + ";drop=true").close();
		} catch (SQLNonTransientConnectionException ex) {
			if (ex.getErrorCode() != 45000) {
				throw ex;
			}
			// Shutdown success
		}
	}

	public void testExample01() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/01.mapping-sql-1.ttl");
		configuration.setConnectionURL(connectionURL);
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/01.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());	
	}
	
	public void testExample02() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/01.mapping-sql-2.ttl");
		configuration.setConnectionURL(connectionURL);
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/01.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());	
	}
	
	public void testExample03() {
		Configuration configuration = new Configuration();
		configuration.setMappingFile("./test/resources/01.mapping-sql-3.ttl");
		configuration.setConnectionURL(connectionURL);
		R2RMLProcessor engine = new R2RMLProcessor(configuration);
		engine.execute();
		Model model = engine.getDataset().getDefaultModel();
		Model target = ModelFactory.createDefaultModel();
		target.read("./test/resources/01.output.ttl");
		assertEquals(true, model.difference(target).isEmpty());
		assertEquals(true, target.difference(model).isEmpty());	
	}

}
