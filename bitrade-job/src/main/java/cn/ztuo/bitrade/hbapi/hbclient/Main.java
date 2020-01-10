package cn.ztuo.bitrade.hbapi.hbclient;

import cn.ztuo.bitrade.hbapi.response.*;
import com.alibaba.fastjson.JSONObject;
import cn.ztuo.bitrade.hbapi.request.DepthRequest;
import cn.ztuo.bitrade.hbapi.request.IntrustOrdersDetailRequest;

import java.io.IOException;
import java.util.List;

public class Main {

    static final String API_KEY = "bd6abd7c-84dfe8af-fdd41e55-0195e";
    static final String API_SECRET = "ff48f9ac-33801703-396bbd37-09843";
    static final String PRIVATE_KEY = "MIGEAgEAMBAGByqGSM49AgEGBSuBBAAKBG0wawIBAQQgPpEt7R5JjNZwYY23YwPuRbR3sGOcNfKX8z8SHzJ1ddGhRANCAARuVFc2ZvQvEz8wR+QGDYDSnSGuW05snj581XRhdBPtxHhSkY5VEg++rYXXuw9upEYFW70fqp8ByZddcFBIKULB";
  
    
    public static void main(String[] args) {
        try {
            apiSample();
        } catch (ApiException e) {
            System.err.println("API Error! err-code: " + e.getErrCode() + ", err-msg: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    static void apiSample() {
        // create ApiClient using your api key and api secret:
        ApiClient client = new ApiClient(API_KEY,API_SECRET,PRIVATE_KEY);
        // get symbol list:
//        System.out.print("获取火币交易对"+JSONObject.toJSON(client.getSymbols()) );
//
//        //获取 K 线
//        //------------------------------------------------------ kline -------------------------------------------------------
//        KlineResponse kline1 = client.kline("btcusdt", "1min", "1");
//        KlineResponse kline2 = client.kline("ethusdt", "1min", "1");
//        KlineResponse kline3 = client.kline("ethbtc", "1min", "10");
//        KlineResponse kline4 = client.kline("htusdt", "1min", "1");
//        KlineResponse kline5 = client.kline("hteth", "1min", "10");
//        KlineResponse kline6 = client.kline("htbtc", "1min", "1");
//        System.out.print("获取K线图"+JSONObject.toJSON(kline1) );

        //------------------------------------------------------ merged -------------------------------------------------------

        MergedResponse merged = client.merged("ethusdt");
        Ticks tick = (Ticks) merged.getData();
        System.out.print("获取联合详情>>>>>"+JSONObject.toJSON(merged) );

        //------------------------------------------------------ depth -------------------------------------------------------

        DepthRequest depthRequest = new DepthRequest();
        depthRequest.setSymbol("btcusdt");
        depthRequest.setType("step0");
        DepthResponse depth = client.depth(depthRequest);
        System.out.print("获取深度资源>>>>>"+JSONObject.toJSON(depth) );

        //------------------------------------------------------ trade -------------------------------------------------------
        TradeResponse trade = client.trade("ethusdt");
        System.out.print("获取trade>>>"+JSONObject.toJSON(trade));

        //------------------------------------------------------ historyTrade -------------------------------------------------------
        HistoryTradeResponse historyTrade = client.historyTrade("ethusdt", "20");
        System.out.print("获取历史 >>>>>>"+JSONObject.toJSON(historyTrade));

        //------------------------------------------------------ historyTrade -------------------------------------------------------
        DetailResponse detailTrade = client.detail("ethusdt");
        System.out.print("获取详细>>>>>>"+JSONObject.toJSON(detailTrade));

        //------------------------------------------------------ symbols -------------------------------------------------------
        SymbolsResponse symbols = client.symbols("btcusdt");
        print(JSONObject.toJSON(symbols));

        //------------------------------------------------------ Currencys -------------------------------------------------------
        CurrencysResponse currencys = client.currencys("btcusdt");
        System.out.print("查询币种精度>>>>>"+JSONObject.toJSON(currencys));

        //------------------------------------------------------ Currencys -------------------------------------------------------
        TimestampResponse timestamp = client.timestamp();
        System.out.print(timestamp.getDateTime()+JSONObject.toJSON(timestamp));

        //------------------------------------------------------ accounts -------------------------------------------------------
        AccountsResponse accounts = client.accounts();
        System.out.print("获取账户信息>>>>"+JSONObject.toJSON(accounts));

        //------------------------------------------------------ balance -------------------------------------------------------
        List<Accounts> list = (List<Accounts>) accounts.getData();
        BalanceResponse balance = client.balance(String.valueOf(list.get(0).getId()));
        BalanceResponse balance2 = client.balance(String.valueOf(list.get(1).getId()));

        System.out.print("spot>>>>>>"+JSONObject.toJSON(balance)); //spot
        System.out.print("OTC>>>>>>>"+JSONObject.toJSON(balance2));//otc

//        Long orderId = 123L;
//        if (!list.isEmpty()) {
//            // find account id:
//            Accounts account = list.get(0);
//            long accountId = account.getId();
//            // create order:
//            CreateOrderRequest createOrderReq = new CreateOrderRequest();
//            createOrderReq.accountId = String.valueOf(accountId);
//            createOrderReq.amount = "0.02";
//            createOrderReq.price = "0.1";
//            createOrderReq.symbol = "eosusdt";
//            createOrderReq.type = CreateOrderRequest.OrderType.BUY_LIMIT;
//            createOrderReq.source = "api";
//
//            //------------------------------------------------------ 创建订单  -------------------------------------------------------
//            orderId = client.createOrder(createOrderReq);
//            print(orderId);
//            // place order:
//
//            //------------------------------------------------------ 执行订单  -------------------------------------------------------
//            String r = client.placeOrder(orderId);
//            print(r);
//        }

        //------------------------------------------------------ submitcancel 取消订单 -------------------------------------------------------

//    SubmitcancelResponse submitcancel = client.submitcancel(orderId.toString());
//    print(submitcancel);

        //------------------------------------------------------ submitcancel 批量取消订单-------------------------------------------------------
//    String[] orderList = {"727554767","727554766",""};
//    String[] orderList = {String.valueOf(orderId)};
//        List orderList = new ArrayList();
//        orderList.add(orderId);
//        BatchcancelResponse submitcancels = client.submitcancels(orderList);
//        print(submitcancels);
//
//        //------------------------------------------------------ ordersDetail 订单详情 -------------------------------------------------------
//        OrdersDetailResponse ordersDetail = client.ordersDetail(String.valueOf(orderId));
//        print(ordersDetail);

        //------------------------------------------------------ ordersDetail 已经成交的订单详情 -------------------------------------------------------
//    String.valueOf(orderId)
//        MatchresultsOrdersDetailResponse matchresults = client.matchresults("714746923");
//        print(ordersDetail);

        //------------------------------------------------------ ordersDetail 已经成交的订单详情 -------------------------------------------------------
//    String.valueOf(orderId)
        IntrustOrdersDetailRequest req = new IntrustOrdersDetailRequest();
        req.symbol = "btcusdt";
        req.types = IntrustOrdersDetailRequest.OrderType.BUY_LIMIT;
//    req.startDate = "2018-01-01";
//    req.endDate = "2018-01-14";
        req.states = IntrustOrdersDetailRequest.OrderStates.FILLED;
//    req.from = "";
//    req.direct = "";
//    req.size = "";


//    public String symbol;	   //true	string	交易对		btcusdt, bccbtc, rcneth ...
//    public String types;	   //false	string	查询的订单类型组合，使用','分割		buy-market：市价买, sell-market：市价卖, buy-limit：限价买, sell-limit：限价卖
//    public String startDate;   //false	string	查询开始日期, 日期格式yyyy-mm-dd
//    public String endDate;	   //false	string	查询结束日期, 日期格式yyyy-mm-dd
//    public String states;	   //true	string	查询的订单状态组合，使用','分割		pre-submitted 准备提交, submitted 已提交, partial-filled 部分成交,
//    // partial-canceled 部分成交撤销, filled 完全成交, canceled 已撤销
//    public String from;	       //false	string	查询起始 ID
//    public String direct;	   //false	string	查询方向		prev 向前，next 向后
//    public String size;	       //false	string	查询记录大小


        //------------------------------------------------------ order 查询当前委托、历史委托 -------------------------------------------------------

        IntrustDetailResponse intrustDetail = client.intrustOrdersDetail(req);
        print(intrustDetail);


//    // get accounts:
//    List<Account> accounts1 = client.getAccounts();
//    print(accounts1);

    }

    static void print(Object obj) {
        try {
            System.out.println(JsonUtil.writeValue(obj));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
