package org.sainnr.wgc.hypertext.io;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author sainnr
 * @since 28.11.13
 */
public class DBConnector {
    static String defaultDbName = "wsc_aksw";
    static boolean oldMode = false;

    public static final String DB_AKSW = "wsc_aksw";
    public static final String DB_MUSEUM = "wsc_museum";
    public static final String DB_SSTU = "wsc_sstu";

    public static final String DB_AKSW_OLD = "sitegraph_aksw";
    public static final String DB_MUSEUM_OLD = "sitegraph_museum";
    public static final String DB_SSTU_OLD = "sitegraph";
    private DataSource ds;
    private String dsName;

    public DBConnector() {
    }

    public DBConnector(String dsName) {
        this.dsName = dsName;
    }

    public static void setDefaultDBName(String domain) {
        if (domain == null || domain.equals("")){
            return;
        }
        String dbName = defaultDbName;
        if (domain.equals("aksw.org")){
            dbName = DB_AKSW;
        } else if (domain.equals("sstu.ru")){
            dbName = DB_SSTU;
        } else if (domain.equals("museum.seun.ru")){
            dbName = DB_MUSEUM;
        }
        defaultDbName = dbName;
    }

    public Connection getConnection() throws SQLException {
        if (ds == null){
            ds = (dsName != null && !dsName.equals("") ? getMysqlDS(dsName) : getMysqlDS());
        }
        assert ds != null;
        return ds.getConnection();
    }

    private static DataSource lookUpDataSource(String name){
        try {
            Context c = new InitialContext();
            return (DataSource) c.lookup("java:comp/env/" + name);
        } catch (NamingException e) {
            return null;
        }
    }

    private static DataSource getMysqlDS() throws SQLException {
        return DBConnector.getMysqlDS(defaultDbName);
    }

    private static DataSource getMysqlDS(String name) throws SQLException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("vlsa");
        dataSource.setPassword("vlsa");
        dataSource.setUrl("jdbc:mysql://localhost:3306/" + name + "?useServerPrepStmts=false&rewriteBatchedStatements=true");
        return dataSource;
    }

}
