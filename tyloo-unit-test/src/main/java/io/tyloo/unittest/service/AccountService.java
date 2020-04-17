package io.tyloo.unittest.service;

import io.tyloo.api.TylooTransactionContext;


public interface AccountService {

    void transferTo(TylooTransactionContext tylooTransactionContext, long accountId, int amount);

    void transferToConfirm(TylooTransactionContext tylooTransactionContext, long accountId, int amount);

    void transferToCancel(TylooTransactionContext tylooTransactionContext, long accountId, int amount);

    void transferToWithNoTransactionContext(long accountId, int amount);

    void transferToConfirmWithNoTransactionContext(long accountId, int amount);

    void transferToCancelWithNoTransactionContext(long accountId, int amount);

    void transferFrom(TylooTransactionContext tylooTransactionContext, long accountId, int amount);

    void transferFromConfirm(TylooTransactionContext tylooTransactionContext, long accountId, int amount);

    void transferFromCancel(TylooTransactionContext tylooTransactionContext, long accountId, int amount);

    void transferToWithMultipleTier(TylooTransactionContext tylooTransactionContext, long accountId, int amount);

    void transferFromWithMultipleTier(TylooTransactionContext tylooTransactionContext, long accountId, int amount);
}
