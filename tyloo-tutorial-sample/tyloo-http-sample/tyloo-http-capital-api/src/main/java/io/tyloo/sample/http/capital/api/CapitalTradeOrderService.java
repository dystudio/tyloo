package io.tyloo.sample.http.capital.api;

import io.tyloo.sample.http.capital.api.dto.CapitalTradeOrderDto;
import io.tyloo.api.TylooTransactionContext;


public interface CapitalTradeOrderService {
    public String record(TylooTransactionContext tylooTransactionContext, CapitalTradeOrderDto tradeOrderDto);
}
