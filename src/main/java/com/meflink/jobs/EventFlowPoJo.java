package com.meflink.jobs;

import java.io.Serializable;

public class EventFlowPoJo implements Serializable {
    private static final long serialVersionUID = 1;

    public String name;
    public long timestamp;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EventFlowPoJo{");
        sb.append("name='").append(name).append('\'');
        sb.append(", timestamp=").append(timestamp);
        sb.append('}');
        return sb.toString();
    }
}
