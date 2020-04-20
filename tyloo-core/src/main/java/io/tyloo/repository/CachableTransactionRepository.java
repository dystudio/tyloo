package io.tyloo.repository;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.tyloo.exception.ConcurrentTransactionException;
import io.tyloo.exception.OptimisticLockException;
import io.tyloo.common.Transaction;
import io.tyloo.api.TylooTransactionXid;

import javax.transaction.xa.Xid;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/*
 *
 * 缓存事务库
 *
 * @Author:Zh1Cheung zh1cheunglq@gmail.com
 * @Date: 19:08 2019/5/12
 *
 */
public abstract class CachableTransactionRepository implements TransactionRepository {

    /**
     * 到期时间(以秒为单位)
     */
    private int expireDuration = 120;
    /**
     * 事务日志记录缓存<Xid, Transaction>
     */
    private Cache<Xid, Transaction> transactionXidTylooTransactionCache;

    /**
     * 创建事务日志记录
     */
    @Override
    public int create(Transaction transaction) throws CloneNotSupportedException {
        int result = doCreate(transaction);
        if (result > 0) {
            putToCache(transaction);
        } else {
            throw new ConcurrentTransactionException("transaction xid duplicated. xid:" + transaction.getXid().toString());
        }

        return result;
    }

    /**
     * 更新事务日志记录
     */
    @Override
    public int update(Transaction transaction) throws CloneNotSupportedException {
        int result = 0;

        try {
            result = doUpdate(transaction);
            if (result > 0) {
                putToCache(transaction);
            } else {
                throw new OptimisticLockException();
            }
        } finally {
            if (result <= 0) {
                removeFromCache(transaction);
            }
        }

        return result;
    }

    /**
     * 删除事务日志记录
     */
    @Override
    public int delete(Transaction transaction) throws CloneNotSupportedException {
        int result = 0;

        try {
            result = doDelete(transaction);

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        } finally {
            removeFromCache(transaction);
        }
        return result;
    }

    /**
     * 根据xid查找事务日志记录.
     *
     * @param tylooTransactionXid
     * @return
     */
    @Override
    public Transaction findByXid(TylooTransactionXid tylooTransactionXid) throws CloneNotSupportedException {
        Transaction transaction = findFromCache(tylooTransactionXid);

        if (transaction == null) {
            transaction = doFindOne(tylooTransactionXid);

            if (transaction != null) {
                putToCache(transaction);
            }
        }

        return transaction;
    }

    /**
     * 找出所有未处理事务日志（从某一时间点开始）.
     *
     * @return
     */
    @Override
    public List<Transaction> findAllUnmodifiedSince(Date date) throws CloneNotSupportedException {

        List<Transaction> transactions = doFindAllUnmodifiedSince(date);

        for (Transaction transaction : transactions) {
            putToCache(transaction);
        }

        return transactions;
    }

    public CachableTransactionRepository() {
        transactionXidTylooTransactionCache = CacheBuilder.newBuilder().expireAfterAccess(expireDuration, TimeUnit.SECONDS).maximumSize(1000).build();
    }

    /**
     * 放入缓存.
     *
     * @param transaction
     */
    protected void putToCache(Transaction transaction) throws CloneNotSupportedException {
        transactionXidTylooTransactionCache.put(transaction.getXid(), transaction);
    }

    /**
     * 从缓存中删除.
     *
     * @param transaction
     */
    protected void removeFromCache(Transaction transaction) throws CloneNotSupportedException {
        transactionXidTylooTransactionCache.invalidate(transaction.getXid());
    }

    /**
     * 从缓存中查找.
     *
     * @param tylooTransactionXid
     * @return
     */
    protected Transaction findFromCache(TylooTransactionXid tylooTransactionXid) {
        return transactionXidTylooTransactionCache.getIfPresent(tylooTransactionXid);
    }

    public void setExpireDuration(int durationInSeconds) {
        this.expireDuration = durationInSeconds;
    }

    /**
     * 创建事务日志记录
     *
     * @param transaction
     * @return
     */
    protected abstract int doCreate(Transaction transaction) throws CloneNotSupportedException;

    protected abstract int doUpdate(Transaction transaction) throws CloneNotSupportedException;

    protected abstract int doDelete(Transaction transaction) throws CloneNotSupportedException;

    protected abstract Transaction doFindOne(Xid xid);

    protected abstract List<Transaction> doFindAllUnmodifiedSince(Date date);
}
