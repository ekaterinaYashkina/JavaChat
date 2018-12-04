public interface ConnectionHandler {

    void onConnectionReady(Connection connection);
    void onReceiveString(Connection connection, String value);
    void onDisconnect(Connection connection);
    void onException(Connection connection, Exception e);
}
