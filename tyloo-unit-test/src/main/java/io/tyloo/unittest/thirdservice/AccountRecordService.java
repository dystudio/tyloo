package io.tyloo.unittest.thirdservice;

import io.tyloo.api.TylooTransactionContext;


public interface AccountRecordService {
    public void record(TylooTransactionContext tylooTransactionContext, long accountId, int amount);

    void recordConfirm(TylooTransactionContext tylooTransactionContext, long accountId, int amount);

    void recordCancel(TylooTransactionContext tylooTransactionContext, long accountId, int amount);
}
