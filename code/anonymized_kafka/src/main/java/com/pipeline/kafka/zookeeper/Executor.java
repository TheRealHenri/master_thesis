package com.pipeline.kafka.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Executor implements Watcher, Runnable, DataMonitor.DataMonitorListener {
    DataMonitor dataMonitor;

    ZooKeeper zooKeeper;

    String znode;

    String[] executable;

    Process child;


    public Executor(String hostPort, String[] executable) throws IOException, InterruptedException, KeeperException {
        String zNodeName = "/anonStream";
        this.executable = executable;
        this.zooKeeper = new ZooKeeper(hostPort, 3000, this);
        Stat stat = zooKeeper.exists(zNodeName, false);
        if (stat == null) {
            this.znode = zooKeeper.create(zNodeName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } else {
            this.znode = zNodeName;
        }
        this.dataMonitor = new DataMonitor(zooKeeper, znode, null, this);
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: Executor <hostPort> <program> with [args ...]");
            System.exit(2);
        }
        String hostPort = args[0];
        String[] executable = new String [args.length - 1];
        System.arraycopy(args, 1, executable, 0, executable.length);
        try {
            new Executor(hostPort, executable).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            synchronized (this) {
                while (!dataMonitor.dead) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        dataMonitor.process(watchedEvent);
    }

    @Override
    public void exists(byte[] data) {
        if (data == null) {
            System.out.println("Killing process");
            child.destroy();
            try {
                child.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            child = null;
        } else {
            if (child != null) {
                System.out.println("Stopping child");
                child.destroy();
                try {
                    child.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                FileOutputStream fos = new FileOutputStream("/tmp/zookeeper/anonStreamLog.txt");
                fos.write(data);
                fos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                System.out.println("Starting child");
                child = Runtime.getRuntime().exec(executable);
                new StreamWriter(child.getInputStream(), System.out);
                new StreamWriter(child.getErrorStream(), System.err);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void closing(int rc) {
        synchronized (this) {
            notifyAll();
        }
    }

    static class StreamWriter extends Thread {
        OutputStream os;
        InputStream is;

        StreamWriter(InputStream is, OutputStream os) {
            this.is = is;
            this.os = os;
            start();
        }

        public void run() {
            byte[] b = new byte[80];
            int rc;
            try {
                while ((rc = is.read(b)) > 0) {
                    os.write(b,0,rc);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
