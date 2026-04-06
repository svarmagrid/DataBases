package org.example.datasource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class SingleConnectionDataSource implements DataSource {

    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    public SingleConnectionDataSource(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("Creating NEW connection...");
            connection = DriverManager.getConnection(url, user, password);
        } else {
            System.out.println("Reusing SAME connection...");
        }

        return new ConnectionWrapper(connection); // 🔥 important change
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    // Leave others unimplemented
    public <T> T unwrap(Class<T> iface) { throw new UnsupportedOperationException(); }
    public boolean isWrapperFor(Class<?> iface) { return false; }
    public java.io.PrintWriter getLogWriter() { return null; }
    public void setLogWriter(java.io.PrintWriter out) {}
    public void setLoginTimeout(int seconds) {}
    public int getLoginTimeout() { return 0; }
    public java.util.logging.Logger getParentLogger() { return null; }
}
class ConnectionWrapper implements Connection {

    private final Connection delegate;

    public ConnectionWrapper(Connection delegate) {
        this.delegate = delegate;
    }

    @Override
    public void close() {

    }

    @Override
    public Statement createStatement() throws SQLException {
        return delegate.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return delegate.prepareStatement(sql);
    }

    /**
     * @param sql an SQL statement that may contain one or more '?'
     *            parameter placeholders. Typically this statement is specified using JDBC
     *            call escape syntax.
     * @return
     * @throws SQLException
     */
    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return null;
    }

    /**
     * @param sql an SQL statement that may contain one or more '?'
     *            parameter placeholders
     * @return
     * @throws SQLException
     */
    @Override
    public String nativeSQL(String sql) throws SQLException {
        return "";
    }

    /**
     * @param autoCommit {@code true} to enable auto-commit mode;
     *                   {@code false} to disable it
     * @throws SQLException
     */
    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {

    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public boolean getAutoCommit() throws SQLException {
        return false;
    }

    /**
     * @throws SQLException
     */
    @Override
    public void commit() throws SQLException {

    }

    /**
     * @throws SQLException
     */
    @Override
    public void rollback() throws SQLException {

    }

    @Override
    public boolean isClosed() throws SQLException {
        return delegate.isClosed();
    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return null;
    }

    /**
     * @param readOnly {@code true} enables read-only mode;
     *                 {@code false} disables it
     * @throws SQLException
     */
    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {

    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    /**
     * @param catalog the name of a catalog (subspace in this
     *                {@code Connection} object's database) in which to work
     * @throws SQLException
     */
    @Override
    public void setCatalog(String catalog) throws SQLException {

    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public String getCatalog() throws SQLException {
        return "";
    }

    /**
     * @param level one of the following {@code Connection} constants:
     *              {@code Connection.TRANSACTION_READ_UNCOMMITTED},
     *              {@code Connection.TRANSACTION_READ_COMMITTED},
     *              {@code Connection.TRANSACTION_REPEATABLE_READ}, or
     *              {@code Connection.TRANSACTION_SERIALIZABLE}.
     *              (Note that {@code Connection.TRANSACTION_NONE} cannot be used
     *              because it specifies that transactions are not supported.)
     * @throws SQLException
     */
    @Override
    public void setTransactionIsolation(int level) throws SQLException {

    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public int getTransactionIsolation() throws SQLException {
        return 0;
    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    /**
     * @throws SQLException
     */
    @Override
    public void clearWarnings() throws SQLException {

    }

    /**
     * @param resultSetType        a result set type; one of
     *                             {@code ResultSet.TYPE_FORWARD_ONLY},
     *                             {@code ResultSet.TYPE_SCROLL_INSENSITIVE}, or
     *                             {@code ResultSet.TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency a concurrency type; one of
     *                             {@code ResultSet.CONCUR_READ_ONLY} or
     *                             {@code ResultSet.CONCUR_UPDATABLE}
     * @return
     * @throws SQLException
     */
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    /**
     * @param sql                  a {@code String} object that is the SQL statement to
     *                             be sent to the database; may contain one or more '?' IN
     *                             parameters
     * @param resultSetType        a result set type; one of
     *                             {@code ResultSet.TYPE_FORWARD_ONLY},
     *                             {@code ResultSet.TYPE_SCROLL_INSENSITIVE}, or
     *                             {@code ResultSet.TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency a concurrency type; one of
     *                             {@code ResultSet.CONCUR_READ_ONLY} or
     *                             {@code ResultSet.CONCUR_UPDATABLE}
     * @return
     * @throws SQLException
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    /**
     * @param sql                  a {@code String} object that is the SQL statement to
     *                             be sent to the database; may contain on or more '?' parameters
     * @param resultSetType        a result set type; one of
     *                             {@code ResultSet.TYPE_FORWARD_ONLY},
     *                             {@code ResultSet.TYPE_SCROLL_INSENSITIVE}, or
     *                             {@code ResultSet.TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency a concurrency type; one of
     *                             {@code ResultSet.CONCUR_READ_ONLY} or
     *                             {@code ResultSet.CONCUR_UPDATABLE}
     * @return
     * @throws SQLException
     */
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return Map.of();
    }

    /**
     * @param map the {@code java.util.Map} object to install
     *            as the replacement for this {@code Connection}
     *            object's default type map
     * @throws SQLException
     */
    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

    }

    /**
     * @param holdability a {@code ResultSet} holdability constant; one of
     *                    {@code ResultSet.HOLD_CURSORS_OVER_COMMIT} or
     *                    {@code ResultSet.CLOSE_CURSORS_AT_COMMIT}
     * @throws SQLException
     */
    @Override
    public void setHoldability(int holdability) throws SQLException {

    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public Savepoint setSavepoint() throws SQLException {
        return null;
    }

    /**
     * @param name a {@code String} containing the name of the savepoint
     * @return
     * @throws SQLException
     */
    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return null;
    }

    /**
     * @param savepoint the {@code Savepoint} object to roll back to
     * @throws SQLException
     */
    @Override
    public void rollback(Savepoint savepoint) throws SQLException {

    }

    /**
     * @param savepoint the {@code Savepoint} object to be removed
     * @throws SQLException
     */
    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {

    }

    /**
     * @param resultSetType        one of the following {@code ResultSet}
     *                             constants:
     *                             {@code ResultSet.TYPE_FORWARD_ONLY},
     *                             {@code ResultSet.TYPE_SCROLL_INSENSITIVE}, or
     *                             {@code ResultSet.TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency one of the following {@code ResultSet}
     *                             constants:
     *                             {@code ResultSet.CONCUR_READ_ONLY} or
     *                             {@code ResultSet.CONCUR_UPDATABLE}
     * @param resultSetHoldability one of the following {@code ResultSet}
     *                             constants:
     *                             {@code ResultSet.HOLD_CURSORS_OVER_COMMIT} or
     *                             {@code ResultSet.CLOSE_CURSORS_AT_COMMIT}
     * @return
     * @throws SQLException
     */
    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    /**
     * @param sql                  a {@code String} object that is the SQL statement to
     *                             be sent to the database; may contain one or more '?' IN
     *                             parameters
     * @param resultSetType        one of the following {@code ResultSet}
     *                             constants:
     *                             {@code ResultSet.TYPE_FORWARD_ONLY},
     *                             {@code ResultSet.TYPE_SCROLL_INSENSITIVE}, or
     *                             {@code ResultSet.TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency one of the following {@code ResultSet}
     *                             constants:
     *                             {@code ResultSet.CONCUR_READ_ONLY} or
     *                             {@code ResultSet.CONCUR_UPDATABLE}
     * @param resultSetHoldability one of the following {@code ResultSet}
     *                             constants:
     *                             {@code ResultSet.HOLD_CURSORS_OVER_COMMIT} or
     *                             {@code ResultSet.CLOSE_CURSORS_AT_COMMIT}
     * @return
     * @throws SQLException
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    /**
     * @param sql                  a {@code String} object that is the SQL statement to
     *                             be sent to the database; may contain on or more '?' parameters
     * @param resultSetType        one of the following {@code ResultSet}
     *                             constants:
     *                             {@code ResultSet.TYPE_FORWARD_ONLY},
     *                             {@code ResultSet.TYPE_SCROLL_INSENSITIVE}, or
     *                             {@code ResultSet.TYPE_SCROLL_SENSITIVE}
     * @param resultSetConcurrency one of the following {@code ResultSet}
     *                             constants:
     *                             {@code ResultSet.CONCUR_READ_ONLY} or
     *                             {@code ResultSet.CONCUR_UPDATABLE}
     * @param resultSetHoldability one of the following {@code ResultSet}
     *                             constants:
     *                             {@code ResultSet.HOLD_CURSORS_OVER_COMMIT} or
     *                             {@code ResultSet.CLOSE_CURSORS_AT_COMMIT}
     * @return
     * @throws SQLException
     */
    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    /**
     * @param sql               an SQL statement that may contain one or more '?' IN
     *                          parameter placeholders
     * @param autoGeneratedKeys a flag indicating whether auto-generated keys
     *                          should be returned; one of
     *                          {@code Statement.RETURN_GENERATED_KEYS} or
     *                          {@code Statement.NO_GENERATED_KEYS}
     * @return
     * @throws SQLException
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return null;
    }

    /**
     * @param sql           an SQL statement that may contain one or more '?' IN
     *                      parameter placeholders
     * @param columnIndexes an array of column indexes indicating the columns
     *                      that should be returned from the inserted row or rows
     * @return
     * @throws SQLException
     */
    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return null;
    }

    /**
     * @param sql         an SQL statement that may contain one or more '?' IN
     *                    parameter placeholders
     * @param columnNames an array of column names indicating the columns
     *                    that should be returned from the inserted row or rows
     * @return
     * @throws SQLException
     */
    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return null;
    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    /**
     * @param timeout The time in seconds to wait for the database operation
     *                used to validate the connection to complete.  If the
     *                timeout period expires before the operationcompletes,
     *                this method returns false.  A value of 0 indicates a
     *                timeout is not applied to the database operation.
     * @return
     * @throws SQLException
     */
    @Override
    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    /**
     * @param name  The name of the client info property to set
     * @param value The value to set the client info property to.  If the
     *              value is null, the current value of the specified
     *              property is cleared.
     * @throws SQLClientInfoException
     */
    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    /**
     * @param properties the list of client info properties to set
     * @throws SQLClientInfoException
     */
    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    /**
     * @param name The name of the client info property to retrieve
     * @return
     * @throws SQLException
     */
    @Override
    public String getClientInfo(String name) throws SQLException {
        return "";
    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }

    /**
     * @param typeName the SQL name of the type the elements of the array map to. The typeName is a
     *                 database-specific name which may be the name of a built-in type, a user-defined type or a standard  SQL type supported by this database. This
     *                 is the value returned by {@code Array.getBaseTypeName}
     * @param elements the elements that populate the returned object
     * @return
     * @throws SQLException
     */
    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    /**
     * @param typeName   the SQL type name of the SQL structured type that this {@code Struct}
     *                   object maps to. The typeName is the name of  a user-defined type that
     *                   has been defined for this database. It is the value returned by
     *                   {@code Struct.getSQLTypeName}.
     * @param attributes the attributes that populate the returned object
     * @return
     * @throws SQLException
     */
    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    /**
     * @param schema the name of a schema  in which to work
     * @throws SQLException
     */
    @Override
    public void setSchema(String schema) throws SQLException {

    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public String getSchema() throws SQLException {
        return "";
    }

    /**
     * @param executor The {@code Executor}  implementation which will
     *                 be used by {@code abort}.
     * @throws SQLException
     */
    @Override
    public void abort(Executor executor) throws SQLException {

    }

    /**
     * @param executor     The {@code Executor}  implementation which will
     *                     be used by {@code setNetworkTimeout}.
     * @param milliseconds The time in milliseconds to wait for the database
     *                     operation
     *                     to complete.  If the JDBC driver does not support milliseconds, the
     *                     JDBC driver will round the value up to the nearest second.  If the
     *                     timeout period expires before the operation
     *                     completes, a SQLException will be thrown.
     *                     A value of 0 indicates that there is not timeout for database operations.
     * @throws SQLException
     */
    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

    }

    /**
     * @return
     * @throws SQLException
     */
    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    /**
     * @param iface A Class defining an interface that the result must implement.
     * @param <T>
     * @return
     * @throws SQLException
     */
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    /**
     * @param iface a Class defining an interface.
     * @return
     * @throws SQLException
     */
    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    // ⚠️ You must delegate all remaining methods (IDE can auto-generate)
}