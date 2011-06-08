package org.jruby.runtime.profile;

import org.jruby.util.collections.IntHashMap;

import java.util.HashMap;
import java.util.Map;

public class MethodDataMap {

    private final Invocation[] invocations;

    public MethodDataMap(Invocation... invocations) {
        this.invocations = invocations;
    }

    public long totalDuration() {
        long totalDuration = 0;
        for (Invocation invocation : invocations) {
            totalDuration += invocation.getDuration();
        }
        return totalDuration;
    }

    public Map<Integer, MethodData> methodData() {
        Map<Integer, MethodData> methods = new HashMap();
        for (Invocation invocation : invocations) {
            addInvocation(invocation, methods);
        }
        return methods;
    }

    private void addInvocation(Invocation top, Map<Integer, MethodData> methods) {
        MethodData data = methods.get(0);
        if (data == null) {
            data = new MethodData(0);
            methods.put(0, data);
        }
        data.invocations.add(top);
        mergeInvocation(methods, top);
    }

    private void mergeInvocation(Map<Integer, MethodData> methods, Invocation inv) {
        for (IntHashMap.Entry<Invocation> entry : inv.getChildren().entrySet()) {
            Invocation child = entry.getValue();
            int serial = child.getMethodSerialNumber();
            MethodData data = methods.get(serial);
            if (data == null) {
                data = new MethodData(serial);
                methods.put(serial, data);
            }
            data.invocations.add(child);
            mergeInvocation(methods, child);
        }
    }

}
