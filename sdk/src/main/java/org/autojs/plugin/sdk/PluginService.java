package org.autojs.plugin.sdk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class PluginService extends Service {

    protected static class Result {
        @NonNull
        private final String         error;
        @NonNull
        private final Map<String, ?> data;

        public Result(String error) {
            this(error, Collections.<String, Object>emptyMap());
        }

        public Result(@Nullable String error, Map<String, ?> data) {
            this.error = error == null ? "" : error;
            this.data = data == null ? Collections.<String, Object>emptyMap() : data;
        }

        public Result(Map<String, ?> data) {
            this("", data);
        }

        @NonNull
        public String getError() {
            return error;
        }

        @NonNull
        public Map<String, ?> getData() {
            return data;
        }

        private Map<String, ?> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("error", error);
            map.put("data", data);
            return map;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "error='" + error + '\'' +
                    ", data=" + data +
                    '}';
        }

        @SuppressWarnings("unchecked")
        protected static Result fromMap(Map<?, ?> map) {
            String         error = (String) map.get("error");
            Map<String, ?> data  = (Map<String, ?>) map.get("data");
            return new Result(error, data);
        }

        public static Result notImplemented(String action) {
            return new Result("not implemented: " + action);
        }
    }

    public interface RemoteCallback {
        Result call(String event, Map<String, Object> args) throws RemoteException;
    }

    private final IBinder mPluginBinder = new IAutoJsRemoteCall.Stub() {
        @SuppressWarnings("unchecked")
        @Override
        public Map call(String action, Map args, final IAutoJsRemoteCallback callback) throws RemoteException {
            if (action == null) {
                return new Result("action is null").toMap();
            }
            if (args == null) {
                args = Collections.emptyMap();
            }
            RemoteCallback remoteCallback = callback == null ? null : new RemoteCallback() {
                @Override
                public Result call(String event, Map<String, Object> args) throws RemoteException {
                    return Result.fromMap(callback.call(event, args));
                }
            };
            try {
                return onRemoteCall(action, (Map<String, Object>) args, remoteCallback).toMap();
            } catch (Exception e) {
                e.printStackTrace();
                return new Result(exceptionToString(e)).toMap();
            }
        }
    };

    protected abstract Result onRemoteCall(@NonNull String action, @NonNull Map<String, Object> args, @Nullable RemoteCallback callback) throws RuntimeException;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mPluginBinder;
    }

    private static String exceptionToString(Throwable e) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream           ps  = new PrintStream(bos, true);
        e.printStackTrace(ps);
        ps.close();
        return bos.toString();
    }
}
