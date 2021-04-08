package org.autojs.plugin.sdk;

interface IAutoJsRemoteCallback {

    Map call(String event, in Map args);
}
