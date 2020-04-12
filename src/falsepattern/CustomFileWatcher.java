package falsepattern;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
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
    private AtomicReference<List<Runnable>> fileChangeCallbacks = new AtomicReference<>(new ArrayList<>());

    private CustomFileWatcher() {
        isManager = true;
    }
    public CustomFileWatcher(WatchService sourceService) {
        isManager = false;
        this.watcher = sourceService;
        customWatchers.get().add(this);
    }

    public synchronized void addCallback(Runnable callback) {
        if (isManager) {
            //We do not do that here
            throw new RuntimeException("Tried to add callback to manager file watcher");
        }
        fileChangeCallbacks.get().add(callback);
    }

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
            if (watchKey.pollEvents().size() > 0) {
                fileChangeCallbacks.get().forEach((Runnable::run));
            }
            watchKey.reset();
        }
    }
}


//Also used plugins:
// Atom Material Icons
// Material Theme UI
// Rainbow Brackets