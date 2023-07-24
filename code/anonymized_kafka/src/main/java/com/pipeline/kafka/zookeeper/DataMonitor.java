package com.pipeline.kafka.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Arrays;

public class DataMonitor implements Watcher, AsyncCallback.StatCallback {

    ZooKeeper zooKeeper;

    String znode;

    Watcher chainedWatcher;

    boolean dead;

    DataMonitorListener listener;

    byte prevData[];

    public DataMonitor(ZooKeeper zooKeeper, String znode, Watcher chainedWatcher, DataMonitorListener listener) {
        this.zooKeeper = zooKeeper;
        this.znode = znode;
        this.chainedWatcher = chainedWatcher;
        this.listener = listener;
        zooKeeper.exists(znode, true, this, null);
    }

    public interface DataMonitorListener {
        /**
         * The existence status of the node has changed.
         */
        void exists(byte data[]);

        /**
         * The ZooKeeper session is no longer valid.
         *
         * @param rc
         * the ZooKeeper reason code
         */
        void closing(int rc);
    }

    public void processResult(int rc, String path, Object ctx, Stat stat) {
        boolean exists;

        switch (KeeperException.Code.get(rc)) {
            case OK:
                exists = true;
                break;
            case NONODE:
                exists = false;
                break;
            case SESSIONEXPIRED:
            case NOAUTH:
                dead = true;
                listener.closing(rc);
                return;
            default:
                // retry
                zooKeeper.exists(znode, true, this, null);
                return;
        }
        byte[] b = null;
        if (exists) {
            try {
                b = zooKeeper.getData(znode, false, null);
            } catch (KeeperException e) {
                // Watcher handles this
                e.printStackTrace();
            } catch (InterruptedException e) {
                return;
            }
        }
        if ((b == null && b != prevData) || !Arrays.equals(prevData, b)) {
            listener.exists(b);
            prevData = b;
        }
    }


    public void process(WatchedEvent event) {
        String path = event.getPath();
        if (event.getType() == Watcher.Event.EventType.None) {
            // state of connection has changed
            switch (event.getState()) {
                case SyncConnected:
                    // In this particular example we don't need to do anything
                    // here - watches are automatically re-registered with
                    // server and any watches triggered while the client was
                    // disconnected will be delivered (in order of course)
                    break;
                case Expired:
                    // Finito
                    dead = true;
                    listener.closing(KeeperException.Code.SESSIONEXPIRED.intValue());
                    break;
            }
        } else {
            if (path != null && path.equals(znode)) {
                // something has changed on the node, let's find out
                zooKeeper.exists(znode, true, this, null);
            }
        }
        if (chainedWatcher != null) {
            chainedWatcher.process(event);
        }
    }
}
