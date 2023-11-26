package com.dash.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DatabaseManager {

    private String DB_URL = "jdbc:sqlite:/tmp/metrics/streams_metrics_";

    private Connection connection;

    private final Logger log = LoggerFactory.getLogger(DatabaseManager.class);

    public DatabaseManager(String db_url_appendix) {
        this.DB_URL += db_url_appendix;
        initializeDataBase();
    }

    private void initializeDataBase() {
        try {
            connection = getConnection();
            createTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String createMetricsTable = "CREATE TABLE IF NOT EXISTS StreamMetrics (" +
                    "    ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "    StreamId TEXT NOT NULL," +
                    "    Timestamp INTEGER NOT NULL," +
                    "    RecordSendRate REAL," +
                    "    RecordsPerRequestAvg REAL," +
                    "    ProcessLatencyAvg REAL," +
                    "    ProcessRate REAL" +
                    ");";
            stmt.execute(createMetricsTable);
        }
    }

    public void executeBatchInserts(List<DatabaseEntry> batch) {
        log.info("Executing batch inserts");
        try (Statement stmt = connection.createStatement()) {
            for (DatabaseEntry entry : batch) {
                String insert = "INSERT INTO StreamMetrics (StreamId, Timestamp, RecordSendRate, RecordsPerRequestAvg, ProcessLatencyAvg, ProcessRate) VALUES (" +
                        "'" + entry.getApplicationId() + "'," +
                        entry.getTimestamp() + "," +
                        entry.getRecordSendRate() + "," +
                        entry.getRecordsPerRequestAvg() + "," +
                        entry.getProcessLatencyAvg() + "," +
                        entry.getProcessRate() + ");";
                stmt.addBatch(insert);
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

}
