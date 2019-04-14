package io.github.neelkamath.goalninja;

import java.util.Date;

class ProgressEntry {
    private Date date;
    private boolean isCompleted;
    private String reason;

    ProgressEntry(Date date, boolean z, String str) {
        this.date = date;
        this.isCompleted = z;
        if (str == null) {
            str = "";
        }
        this.reason = str;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return this.isCompleted;
    }

    public void setCompleted(boolean z) {
        this.isCompleted = z;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String str) {
        if (str == null) {
            str = "";
        }
        this.reason = str;
    }
}
