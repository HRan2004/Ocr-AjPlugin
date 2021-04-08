package org.autojs.plugin.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Keep;

import java.util.Map;

@Keep
public abstract class Plugin implements ServiceConnection {

    public interface RemoteCallback {
        Map<String, ?> call(String event, Map<String, ?> args) throws RemoteException;
    }

    public static class Connection {
        private final IAutoJsRemoteCall mRemoteCall;

        public Connection(IAutoJsRemoteCall mRemoteCall) {
            this.mRemoteCall = mRemoteCall;
        }

        @SuppressWarnings("rawtypes")
        public Map<String, ?> call(String action, Map<String, ?> args, final RemoteCallback callback) throws RemoteException {
            Log.i("Plugin", "call: action = " + action + ", args = " + args + ", callback = " + callback);
            PluginService.Result result = PluginService.Result.fromMap(mRemoteCall.call(action, args, callback == null ? null : new IAutoJsRemoteCallback.Stub() {
                @Override
                public Map call(String event, Map args) throws RemoteException {
                    //noinspection unchecked
                    return callback.call(event, args);
                }
            }));
            Log.i("Plugin", "call: action = " + action + ", result = " + result);
            if (!result.getError().isEmpty()) {
                throw new RemoteException(result.getError());
            }
            return result.getData();
        }

    }

    private final Object  mRuntime;
    private final Object  mTopLevelScope;
    private final Context mContext;
    private final Context mSelfContext;

    private Connection mConnection   = null;
    private boolean    mDisconnected = false;

    public Plugin(Context context, Context selfContext, Object runtime, Object topLevelScope) {
        mRuntime = runtime;
        mTopLevelScope = topLevelScope;
        mContext = context;
        mSelfContext = selfContext;
    }

    public Context getSelfContext() {
        return mSelfContext;
    }

    public Object getRuntime() {
        return mRuntime;
    }

    public Object getTopLevelScope() {
        return mTopLevelScope;
    }

    public Context getContext() {
        return mContext;
    }

    public abstract String getAssetsScriptDir();

    public ComponentName getService() {
        return null;
    }

    @Keep
    public static int getVersion() {
        return PluginRegistry.VERSION;
    }

    public Connection getConnection() {
        return mConnection;
    }

    public boolean isDisconnected() {
        return mDisconnected;
    }

    public Connection requireConnection() throws RemoteException {
        if (mConnection != null) {
            return mConnection;
        }
        if (mDisconnected) {
            throw new RemoteException("plugin disconnected: " + getClass() + ": " + this);
        }
        throw new RemoteException("plugin is not connected: " + getClass() + ": " + this);
    }

    public Connection waitForConnection() throws InterruptedException, RemoteException {
        if (mConnection != null) {
            return mConnection;
        }
        if (mDisconnected) {
            throw new RemoteException("plugin disconnected: " + getClass() + ": " + this);
        }
        synchronized (this) {
            if (mConnection != null) {
                return mConnection;
            }
            this.wait();
            if (mDisconnected) {
                throw new RemoteException("plugin disconnected: " + getClass() + ": " + this);
            }
            return mConnection;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        IAutoJsRemoteCall connection = IAutoJsRemoteCall.Stub.asInterface(service);
        try {
            service.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    onServiceDisconnected();
                }
            }, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        synchronized (this) {
            this.mConnection = new Connection(connection);
            this.notifyAll();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        onServiceDisconnected();
    }

    private void onServiceDisconnected() {
        mDisconnected = true;
        synchronized (this) {
            this.mConnection = null;
            this.notifyAll();
        }
    }
}
