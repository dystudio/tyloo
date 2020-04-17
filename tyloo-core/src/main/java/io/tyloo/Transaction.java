package io.tyloo;


import io.tyloo.api.TylooTransactionContext;
import io.tyloo.api.TylooTransactionStatus;
import io.tyloo.api.TylooTransactionXid;
import io.tyloo.common.TransactionType;

import javax.transaction.xa.Xid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 18:04 2019/4/30
 *
 */

public class Transaction implements Serializable {

    private static final long serialVersionUID = 7291423944314337931L;

    private TylooTransactionXid xid;

    private TylooTransactionStatus status;

    private TransactionType transactionType;

    private volatile int retriedCount = 0;

    private Date createTime = new Date();

    private Date lastUpdateTime = new Date();

    private long version = 1;

    private List<Subordinate> Subordinates = new ArrayList<>();

    private Map<String, Object> attachments = new ConcurrentHashMap<>();

    public Transaction() {

    }

    public Transaction(TylooTransactionContext tylooTransactionContext) throws CloneNotSupportedException {
        this.xid = tylooTransactionContext.getXid();
        this.status = TylooTransactionStatus.TRYING;
        this.transactionType = TransactionType.BRANCH;
    }

    public Transaction(TransactionType transactionType) {
        this.xid = new TylooTransactionXid();
        this.status = TylooTransactionStatus.TRYING;
        this.transactionType = transactionType;
    }

    public Transaction(Object uniqueIdentity,TransactionType transactionType) {

        this.xid = new TylooTransactionXid(uniqueIdentity);
        this.status = TylooTransactionStatus.TRYING;
        this.transactionType = transactionType;
    }

    public void addSubordinate(Subordinate Subordinate) {
        Subordinates.add(Subordinate);
    }


    public Xid getXid() throws CloneNotSupportedException {
        return xid.clone();
    }

    public TylooTransactionStatus getStatus() {
        return status;
    }


    public List<Subordinate> getSubordinates() {
        return Subordinates;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void changeStatus(TylooTransactionStatus status) {
        this.status = status;
    }


    public void commit() {

        for (Subordinate Subordinate : Subordinates) {
            Subordinate.commit();
        }
    }

    public void rollback() {
        for (Subordinate Subordinate : Subordinates) {
            Subordinate.rollback();
        }
    }

    public int getRetriedCount() {
        return retriedCount;
    }

    public void addRetriedCount() {
        this.retriedCount++;
    }

    public void resetRetriedCount(int retriedCount) {
        this.retriedCount = retriedCount;
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public long getVersion() {
        return version;
    }

    public void updateVersion() {
        this.version++;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date date) {
        this.lastUpdateTime = date;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void updateTime() {
        this.lastUpdateTime = new Date();
    }


}
