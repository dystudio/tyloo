package io.tyloo.sample.http.redpacket.api;

import io.tyloo.sample.http.redpacket.api.dto.RedPacketTradeOrderDto;
import io.tyloo.api.TylooTransactionContext;


public interface RedPacketTradeOrderService {

    public String record(TylooTransactionContext tylooTransactionContext, RedPacketTradeOrderDto tradeOrderDto);
}
