package io.tyloo.unit.test;

import io.tyloo.common.Subordinate;
import io.tyloo.common.Transaction;
import io.tyloo.enums.TransactionType;
import io.tyloo.serializer.KryoPoolSerializer;
import io.tyloo.serializer.ObjectSerializer;
import io.tyloo.unittest.client.TransferService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class PerformanceTest extends AbstractTestCase {

    @Autowired
    private TransferService transferService;

    @Test
    public void performanceTest() {

        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            transferService.performenceTuningTransfer();
        }

        long thenTime = System.currentTimeMillis();

        System.out.println(thenTime - currentTime);
    }

    @Test
    public void serializeTest() {

        ObjectSerializer objectSerializer = new KryoPoolSerializer();

        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < 10000; i++) {
//
            Transaction transaction = new Transaction(TransactionType.ROOT);
            transaction.getAttachments().put("abc", new Subordinate());
            byte[] bytes = objectSerializer.serialize(transaction);
            Transaction transaction1 = (Transaction) objectSerializer.deserialize(bytes);

            if (transaction.getVersion() != transaction1.getVersion()) {
                throw new Error();
            }
        }
        long thenTime = System.currentTimeMillis();

        System.out.println(thenTime - currentTime);
    }

    @Test
    public void testThreadPool() throws ExecutionException, InterruptedException {

        ExecutorService executorService = new ThreadPoolExecutor(1, 2,
                30L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>());

        Long startTime = System.currentTimeMillis();

        List<Future> futures = new ArrayList<Future>();

        for (int i = 0; i <= 1; i++) {
            futures.add(executorService.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName());

                    System.out.println(Thread.currentThread().getName() + " done");
                }
            }));
        }

        for (Future future : futures) {
            future.get();
        }

        System.out.println("cost time:" + (System.currentTimeMillis() - startTime));

    }
}
