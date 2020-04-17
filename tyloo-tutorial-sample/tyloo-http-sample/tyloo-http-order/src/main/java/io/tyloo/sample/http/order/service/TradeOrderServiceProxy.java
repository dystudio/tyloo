package io.tyloo.sample.http.order.service;

import io.tyloo.sample.http.capital.api.CapitalTradeOrderService;
import io.tyloo.sample.http.capital.api.dto.CapitalTradeOrderDto;
import io.tyloo.sample.http.redpacket.api.RedPacketTradeOrderService;
import io.tyloo.sample.http.redpacket.api.dto.RedPacketTradeOrderDto;
import io.tyloo.api.Tyloo;
import io.tyloo.api.Propagation;
import io.tyloo.api.TylooTransactionContext;
import io.tyloo.context.MethodTylooTransactionContextEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TradeOrderServiceProxy {

    @Autowired
    CapitalTradeOrderService capitalTradeOrderService;

    @Autowired
    RedPacketTradeOrderService redPacketTradeOrderService;

    /*the propagation need set Propagation.SUPPORTS,otherwise the recover doesn't work,
      The default value is Propagation.REQUIRED, which means will begin new transaction when recover.
    */
    @Tyloo(propagation = Propagation.SUPPORTS, confirmMethod = "record", cancelMethod = "record", transactionContextEditor = MethodTylooTransactionContextEditor.class)
    public String record(TylooTransactionContext tylooTransactionContext, CapitalTradeOrderDto tradeOrderDto) {
        return capitalTradeOrderService.record(tylooTransactionContext, tradeOrderDto);
    }

    @Tyloo(propagation = Propagation.SUPPORTS, confirmMethod = "record", cancelMethod = "record", transactionContextEditor = MethodTylooTransactionContextEditor.class)
    public String record(TylooTransactionContext tylooTransactionContext, RedPacketTradeOrderDto tradeOrderDto) {
        return redPacketTradeOrderService.record(tylooTransactionContext, tradeOrderDto);
    }
}
