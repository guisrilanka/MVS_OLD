package com.gui.mdt.thongsieknavclient.rp4;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//



import java.util.HashMap;

public class Monitor {
    private static Object m_LockGateway = new Object();
    private static HashMap<Object, Monitor.LockData> m_Locks = new HashMap();

    public Monitor() {
    }

    public static void enter(Object lockObject) {
        if (lockObject == null) {
            throw new IllegalArgumentException("lockObject was null");
        } else {
            while(!tryEnter(lockObject, 0L)) {
                Thread.yield();
            }

        }
    }

    public static void exit(Object lockObject) {
        if (lockObject == null) {
            throw new IllegalArgumentException("lockObject was null");
        } else {
            synchronized(m_LockGateway) {
                if (!m_Locks.containsKey(lockObject)) {
                    throw new IllegalStateException("Monitor.exit called on object that is not currently locked.");
                } else {
                    Monitor.LockData lData = (Monitor.LockData)m_Locks.get(lockObject);
                    if (lData.owningThread != Thread.currentThread()) {
                        throw new IllegalStateException("Monitor.exit called on object that is lock by another thread.");
                    } else {
                        --lData.lockCount;
                        if (lData.lockCount == 0L) {
                            m_Locks.remove(lockObject);
                        }

                    }
                }
            }
        }
    }

    public static boolean tryEnter(Object lockObject, long timeout) {
        boolean result = false;
        if (lockObject == null) {
            throw new IllegalArgumentException("lockObject was null");
        } else if (timeout < 0L) {
            throw new IllegalArgumentException("timeout was negative");
        } else {
            long startTime = System.currentTimeMillis();

            do {
                synchronized(m_LockGateway) {
                    Monitor.LockData lData;
                    if (!m_Locks.containsKey(lockObject)) {
                        lData = new Monitor().new LockData();
                        lData.owningThread = Thread.currentThread();
                        lData.lockCount = 1L;
                        m_Locks.put(lockObject, lData);
                        result = true;
                    } else {
                        lData = (Monitor.LockData)m_Locks.get(lockObject);
                        if (lData.owningThread == Thread.currentThread()) {
                            ++lData.lockCount;
                            result = true;
                        }
                    }
                }

                Thread.yield();
            } while(System.currentTimeMillis() - startTime <= timeout && !result);

            return result;
        }
    }

    private class LockData {
        public Thread owningThread;
        public long lockCount;

        private LockData() {
            this.owningThread = null;
            this.lockCount = 0L;
        }
    }
}
