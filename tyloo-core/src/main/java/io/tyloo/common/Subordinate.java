package io.tyloo.common;

import io.tyloo.api.TylooTransactionContext;
import io.tyloo.api.TylooTransactionContextEditor;
import io.tyloo.api.TylooTransactionStatus;
import io.tyloo.api.TylooTransactionXid;
import io.tyloo.context.MethodContext;

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

    private MethodContext confirmMethodContext;

    private MethodContext cancelMethodContext;

    Class<? extends TylooTransactionContextEditor> transactionContextEditorClass;

    public Subordinate() {

    }

    public Subordinate(TylooTransactionXid xid, MethodContext confirmMethodContext, MethodContext cancelMethodContext, Class<? extends TylooTransactionContextEditor> transactionContextEditorClass) {
        this.xid = xid;
        this.confirmMethodContext = confirmMethodContext;
        this.cancelMethodContext = cancelMethodContext;
        this.transactionContextEditorClass = transactionContextEditorClass;
    }

    public Subordinate(MethodContext confirmMethodContext, MethodContext cancelMethodContext, Class<? extends TylooTransactionContextEditor> transactionContextEditorClass) {
        this.confirmMethodContext = confirmMethodContext;
        this.cancelMethodContext = cancelMethodContext;
        this.transactionContextEditorClass = transactionContextEditorClass;
    }

    public void setXid(TylooTransactionXid xid) {
        this.xid = xid;
    }

    public void rollback() {
        InvokeHandler.invoke(new TylooTransactionContext(xid, TylooTransactionStatus.CANCELLING.getId()), cancelMethodContext, transactionContextEditorClass);
    }

    public void commit() {
        InvokeHandler.invoke(new TylooTransactionContext(xid, TylooTransactionStatus.CONFIRMING.getId()), confirmMethodContext, transactionContextEditorClass);
    }

    public TylooTransactionXid getXid() {
        return xid;
    }

    public MethodContext getConfirmMethodContext() {
        return confirmMethodContext;
    }

    public MethodContext getCancelMethodContext() {
        return cancelMethodContext;
    }

}
