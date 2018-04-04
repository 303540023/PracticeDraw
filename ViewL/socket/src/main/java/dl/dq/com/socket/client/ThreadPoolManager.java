package dl.dq.com.socket.client;

/**
 * Created by liqing on 17-10-17.
 */

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolManager {

    private static final String TAG = ThreadPoolManager.class.getSimpleName();

    private static final int MAXIMUM_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2 - 1;
    private static final int CORE_POOL_SIZE = 2;
    private static final int KEEP_ALIVE_TIME = 1;

    private static ThreadPoolManager _inst;

    private static final BlockingQueue<Runnable> mRunnableQueue = new LinkedBlockingQueue<Runnable>(
            128);

    private static final ThreadFactory mThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(final Runnable r) {
            return new Thread(r, "BQT_TR#" + mCount.getAndIncrement());
        }
    };

    private static final ThreadPoolExecutor mExecutor = new ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
            mRunnableQueue, mThreadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());

    public synchronized static ThreadPoolManager getInstance() {
        if (_inst == null) {
            _inst = new ThreadPoolManager();
        }
        return _inst;
    }

    public static Executor getExecutor(){
        return mExecutor;
    }

    private ThreadPoolManager() {
    }

    public void setThreadPoolParam(int core_pool_size, int maximum_pool_size, int keep_alive, TimeUnit time_unit) {
        mExecutor.setCorePoolSize(core_pool_size);
        mExecutor.setMaximumPoolSize(maximum_pool_size);
        mExecutor.setKeepAliveTime(keep_alive, time_unit);
    }

    public void executeRunnable(final Runnable command) {
        Runnable runnable = new Runnable() {
            public void run() {
                command.run();
            }
        };
        mExecutor.execute(runnable);
    }
}