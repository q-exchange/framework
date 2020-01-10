package cn.ztuo.bitrade.bnbapi;

import cn.ztuo.bitrade.bnbapi.exception.BinanceApiException;
import cn.ztuo.bitrade.bnbapi.impl.BinanceApiRestClientImpl;
import cn.ztuo.bitrade.bnbapi.domain.market.OrderBook;

public class Main {




    public static void main(String[] args) {
        try {
            apiSample();
        } catch (BinanceApiException e) {
            System.err.println("API Error! err-code: " + e.getError() + ", err-msg: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void apiSample() {

        BinanceApiRestClientImpl apiRestClient = new BinanceApiRestClientImpl("","");
        OrderBook orderBook = apiRestClient.getOrderBook("BNBUSDT",5);
        OrderBook orderBook1 = apiRestClient.getOrderBook("BNBETH",5);
        OrderBook orderBook2 = apiRestClient.getOrderBook("BNBBTC",5);

        System.out.println(orderBook);
        apiRestClient.ping();
        System.out.println( apiRestClient.getServerTime()+">>>>>>>>");
    }


}
