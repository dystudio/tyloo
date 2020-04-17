package io.tyloo.api;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 *
 * 事务上下文
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 12:01 2019/4/6
 *
 */
public class TylooTransactionContext implements Serializable {

    private static final long serialVersionUID = -8199390103169700387L;

    private TylooTransactionXid xid;

    private int status;

    private final Map<String, String> attachments = new ConcurrentHashMap<String, String>();

    public TylooTransactionContext() {

    }

    public TylooTransactionContext(TylooTransactionXid xid, int status) {
        this.xid = xid;
        this.status = status;
    }

    public void setXid(TylooTransactionXid xid) {
        this.xid = xid;
    }

    public TylooTransactionXid getXid() throws CloneNotSupportedException {
        return xid.clone();
    }

    public void setAttachments(Map<String, String> attachments) {
        if (attachments != null && !attachments.isEmpty()) {
            this.attachments.putAll(attachments);
        }
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }


}
