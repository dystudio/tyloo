package io.tyloo;

import io.tyloo.api.TylooTransactionContext;
import io.tyloo.api.TylooTransactionContextEditor;
import io.tyloo.api.TylooTransactionStatus;
import io.tyloo.api.TylooTransactionXid;

import java.io.Serializable;

/*
 *
 * 事务参与者
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 20:00 2019/6/5
 *
 */

public class Subordinate implements Serializable {

    private static final long serialVersionUID = 4127729421281425247L;

    private TylooTransactionXid xid;

    private InvocationContext confirmInvocationContext;

    private InvocationContext cancelInvocationContext;

    Class<? extends TylooTransactionContextEditor> transactionContextEditorClass;

    public Subordinate() {

    }

    public Subordinate(TylooTransactionXid xid, InvocationContext confirmInvocationContext, InvocationContext cancelInvocationContext, Class<? extends TylooTransactionContextEditor> transactionContextEditorClass) {
        this.xid = xid;
        this.confirmInvocationContext = confirmInvocationContext;
        this.cancelInvocationContext = cancelInvocationContext;
        this.transactionContextEditorClass = transactionContextEditorClass;
    }

    public Subordinate(InvocationContext confirmInvocationContext, InvocationContext cancelInvocationContext, Class<? extends TylooTransactionContextEditor> transactionContextEditorClass) {
        this.confirmInvocationContext = confirmInvocationContext;
        this.cancelInvocationContext = cancelInvocationContext;
        this.transactionContextEditorClass = transactionContextEditorClass;
    }

    public void setXid(TylooTransactionXid xid) {
        this.xid = xid;
    }

    public void rollback() {
        Terminator.invoke(new TylooTransactionContext(xid, TylooTransactionStatus.CANCELLING.getId()), cancelInvocationContext, transactionContextEditorClass);
    }

    public void commit() {
        Terminator.invoke(new TylooTransactionContext(xid, TylooTransactionStatus.CONFIRMING.getId()), confirmInvocationContext, transactionContextEditorClass);
    }

    public TylooTransactionXid getXid() {
        return xid;
    }

    public InvocationContext getConfirmInvocationContext() {
        return confirmInvocationContext;
    }

    public InvocationContext getCancelInvocationContext() {
        return cancelInvocationContext;
    }

}
