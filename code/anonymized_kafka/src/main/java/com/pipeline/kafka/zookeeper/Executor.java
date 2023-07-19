package com.pipeline.kafka.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.*;

public class Executor implements Watcher, Runnable, DataMonitor.DataMonitorListener {

    String filename;

    DataMonitor dataMonitor;

    ZooKeeper zooKeeper;

    String[] executable;

    Process child;


    public Executor(String hostPort, String znode, String filename, String[] executable) throws IOException {
        this.filename = filename;
        this.executable = executable;
        this.zooKeeper = new ZooKeeper(hostPort, 3000, this);
        this.dataMonitor = new DataMonitor(zooKeeper, znode, null, this);
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Usage: Executor <host> <port> <filename> <program> with [args ...]");
            System.exit(2);
        }
        String hostPort = args[0];
        String znode = args[1];
        String filename = args[2];
        String[] executable = new String [args.length - 3];
        System.arraycopy(args, 3, executable, 0, executable.length);
        try {
            new Executor(hostPort, znode, filename, executable).run();
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
                FileOutputStream fos = new FileOutputStream(filename);
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
    public void closing(KeeperException.Code rc) {
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
