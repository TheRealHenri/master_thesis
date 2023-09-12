package com.pipeline.kafka.connectors;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.pipeline.kafka.utils.ConfigConstants.DEFAULT_KEY;
import static com.pipeline.kafka.utils.SchemaConstants.SYNTHETIC_DATA_CSV_SCHEMA;
import static com.pipeline.kafka.utils.SchemaConstants.SYNTHETIC_DATA_CSV_TO_STRUCT_FOR;

public class CSVSourceTask extends SourceTask {

    private Logger log = LoggerFactory.getLogger(CSVSourceTask.class);
    public static final String FILENAME_FIELD = "filename";
    public static final String POSITION_FIELD = "position";
    public static final Schema VALUE_SCHEMA = SYNTHETIC_DATA_CSV_SCHEMA;
    private String filePath;
    private String topic;
    private int batchSize;
    private InputStream inputStream;
    private BufferedReader reader = null;
    private char[] buffer;
    private int offset = 0;
    private Long streamOffSet = 0L;

    @Override
    public String version() {
        return null;
    }

    @Override
    public void start(Map<String, String> props) {
        AbstractConfig config = new AbstractConfig(CSVSourceConnector.CONFIG_DEF, props);
        filePath = config.getString(CSVSourceConnector.FILE_CONFIG);
        topic = config.getString(CSVSourceConnector.TOPIC_CONFIG);
        batchSize = config.getInt(CSVSourceConnector.TASK_BATCH_SIZE_CONFIG);
        buffer = new char[Math.min((int) Math.pow(2, batchSize), 1024)];
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        // Already reading?
        if (inputStream == null) {
            try {
                inputStream = Files.newInputStream(Paths.get(filePath));
                Map<String, Object> offset = null;
                // should be line below to continue reading from last offset, but we want to start reading anew every time
                //context.offsetStorageReader().offset(Collections.singletonMap(FILENAME_FIELD, filePath));
                // Not first time reading this file?
                if (offset != null) {
                    Object lastOffset = offset.get(POSITION_FIELD);
                    if (lastOffset != null && !(lastOffset instanceof Long))
                        throw new IllegalStateException("Offset position is the incorrect type");
                    if (lastOffset != null) {
                        log.debug("Found previous offset, trying to skip to file offset {}", lastOffset);
                        long skipLeft = (Long) lastOffset;
                        while (skipLeft > 0) {
                            try {
                                long skipped = inputStream.skip(skipLeft);
                                skipLeft -= skipped;
                            } catch (IOException e) {
                                log.error("Error while trying to seek to previous offset in file {}: {}", filePath, e);
                                throw new IllegalStateException(e);
                            }
                        }
                    }
                    streamOffSet = lastOffset == null ? 0L : (Long) lastOffset;
                } else {
                    streamOffSet = 0L;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                log.debug("Opened {} for reading", filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        boolean headerProcessed = streamOffSet != 0L;
        try {
            final BufferedReader readerCopy;
            synchronized (this) {
                readerCopy = reader;
            }
            if (readerCopy == null) {
                return null;
            }

            ArrayList<SourceRecord> records = null;

            int nread = 0;
            while (readerCopy.ready()) {
                nread = readerCopy.read(buffer, offset, buffer.length - offset);
                log.debug("Read {} bytes from {}", nread, filePath);

                if (nread >= 0) {
                    offset += nread;
                    String stringLine;
                    boolean foundOneLine = false;
                    do {
                        stringLine = extractLine();
                        if (stringLine != null) {
                            foundOneLine = true;
                            if (!headerProcessed) {
                                headerProcessed = true;
                                continue;
                            }
                            Struct structLine = SYNTHETIC_DATA_CSV_TO_STRUCT_FOR(stringLine);
                            if (records == null)
                                records = new ArrayList<>();
                            records.add(new SourceRecord(
                                    Collections.singletonMap(FILENAME_FIELD, filePath),
                                    Collections.singletonMap(POSITION_FIELD, streamOffSet),
                                    topic,
                                    null,
                                    Schema.STRING_SCHEMA,
                                    DEFAULT_KEY,
                                    VALUE_SCHEMA,
                                    structLine,
                                    System.currentTimeMillis()
                            ));
                            if (records.size() >= batchSize) {
                                return records;
                            }
                        }
                    } while (stringLine != null);

                    if (!foundOneLine && offset == buffer.length) {
                        char[] newBuffer = new char[buffer.length * 2];
                        System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                        log.debug("Increasing buffer size to {} bytes", newBuffer.length);
                        buffer = newBuffer;
                    }
                }
            }
            if (nread <= 0) {
                synchronized (this) {
                    this.wait(1000);
                }
            }
            if (records != null)
                log.debug("Returning {} records unbatched", records.size());
            return records;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String extractLine() {
        int until = -1;
        int newStart = -1;
        for (int i = 0; i < offset; i++) {
            if (buffer[i] == '\n') {
                until = i;
                newStart = i + 1;
                break;
            } else if (buffer[i] == '\r') {
                if (i + 1 >= offset)
                    return null;

                until = i;
                newStart = (buffer[i + 1] == '\n') ? i + 2 : i + 1;
                break;
            }
        }

        if (until != -1) {
            String result = new String (buffer, 0, until);
            System.arraycopy(buffer, newStart, buffer, 0, buffer.length - newStart);
            offset = offset - newStart;
            if (streamOffSet != null) {
                streamOffSet += newStart;
            }
            return result;
        } else {
            return null;
        }
    }

    @Override
    public  void stop() {
        log.info("Stopping CSV Source Task");
        synchronized (this) {
            try {
                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                    log.info("Closed input stream");
                }
                reader = null;
                buffer = new char[1024];
                streamOffSet = 0L;
                offset = 0;
            } catch (IOException e) {
                log.info("Error while closing input stream");
                e.printStackTrace();
            }
            this.notify();
        }
    }
}
