package com.github.falsepattern.util;

import com.github.falsepattern.succ4j.Utilities;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

//Evil threaded class
//TODO separate manager and watcher classes
public class CustomFileWatcher implements Runnable {
    private static final Thread watcherThread;
    private static final CustomFileWatcher rootWatcher;
    private static final AtomicReference<List<CustomFileWatcher>> customWatchers = new AtomicReference<>(new ArrayList<>());
    private static final AtomicBoolean shouldRun = new AtomicBoolean(true);
    static {
        rootWatcher = new CustomFileWatcher();
        watcherThread = new Thread(rootWatcher);
        watcherThread.start();
    }

    public synchronized static void terminate() {
        shouldRun.set(false);
        while(watcherThread.isAlive()) {
            watcherThread.interrupt();
        }
    }


    private final boolean isManager;
    private WatchService watcher;
    private final AtomicReference<Map<String, Runnable>> fileChangeCallbacks = new AtomicReference<>(new HashMap<>());

    private CustomFileWatcher() {
        isManager = true;
    }
    public CustomFileWatcher(WatchService sourceService) {
        isManager = false;
        this.watcher = sourceService;
        customWatchers.get().add(this);
    }

    public synchronized void addCallback(String relativeOrAbsolutePath, Runnable callback) {
        if (isManager) {
            //We do not do that here
            throw new RuntimeException("Tried to add callback to manager file watcher");
        }
        fileChangeCallbacks.get().put(Utilities.absolutePath(relativeOrAbsolutePath), callback);
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        if (isManager) {
            //Manager code
            while (shouldRun.get()) {
                customWatchers.get().forEach(CustomFileWatcher::runSingle);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    private synchronized void runSingle() {
        WatchKey watchKey;
        while ((watchKey = watcher.poll()) != null) {
            List<WatchEvent<?>> events = watchKey.pollEvents();
            for (WatchEvent<?> event: events) {
                String path = ((Path)event.context()).toAbsolutePath().toString();
                if (fileChangeCallbacks.get().containsKey(path)) {
                    fileChangeCallbacks.get().get(path).run();
                }
            }
            watchKey.reset();
        }
    }
}
