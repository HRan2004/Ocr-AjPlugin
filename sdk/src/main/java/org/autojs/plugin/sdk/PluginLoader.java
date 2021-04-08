package org.autojs.plugin.sdk;

import android.content.Context;

public interface PluginLoader {

    Plugin load(Context context, Context selfContext, Object runtime, Object topLevelScope);
}
