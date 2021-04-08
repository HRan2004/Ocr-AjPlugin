// IAutoJsRemoteCall.aidl
package org.autojs.plugin.sdk;

import org.autojs.plugin.sdk.IAutoJsRemoteCallback;

interface IAutoJsRemoteCall {

    Map call(String action, in Map args, IAutoJsRemoteCallback callback);
}
