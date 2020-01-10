package cn.ztuo.bitrade.scheduled;


import cn.ztuo.bitrade.bnbapi.domain.market.OrderBook;
import cn.ztuo.bitrade.bnbapi.domain.market.OrderBookEntry;
import cn.ztuo.bitrade.bnbapi.impl.BinanceApiRestClientImpl;
import cn.ztuo.bitrade.hbapi.request.DepthRequest;
import cn.ztuo.bitrade.hbapi.response.Depth;
import com.alibaba.fastjson.JSONObject;
import cn.ztuo.bitrade.constant.BooleanEnum;
import cn.ztuo.bitrade.entity.*;
import cn.ztuo.bitrade.hbapi.hbclient.ApiClient;
import cn.ztuo.bitrade.hbapi.response.DepthResponse;
import cn.ztuo.bitrade.service.*;
import cn.ztuo.bitrade.util.MessageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;


/**
 * 交易机器人 拉升币种 拉低币种 自动交易
 */
@Component
@Slf4j
public class RobotTransactionBuyAndSellScheduled {
    @Autowired
    private ExchangeOrderService orderService;
    @Autowired
    private ExchangeCoinService exchangeCoinService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private LocaleMessageSourceService msService;
    @Autowired
    private MemberWalletService walletService;



    private static boolean proxyFlag ;

    @Value("${physical.machine.proxy.flag}")
    public void  setProxyFlag(boolean proxyFlag){
        log.info("执行 @Value proxyFlag={} ",proxyFlag);
        RobotTransactionBuyAndSellScheduled.proxyFlag =proxyFlag;
    }

    static final BigDecimal ZERO = new BigDecimal(0);
    static final String API_KEY = "bd6abd7c-84dfe8af-fdd41e55-0195e";
    static final String API_SECRET = "ff48f9ac-33801703-396bbd37-09843";
    static final String PRIVATE_KEY = "MIGEAgEAMBAGByqGSM49AgEGBSuBBAAKBG0wawIBAQQgPpEt7R5JjNZwYY23YwPuRbR3sGOcNfKX8z8SHzJ1ddGhRANCAARuVFc2ZvQvEz8wR+QGDYDSnSGuW05snj581XRhdBPtxHhSkY5VEg++rYXXuw9upEYFW70fqp8ByZddcFBIKULB";
    private  ApiClient client ;

    @PostConstruct
    public void init(){
        log.info("执行 init proxyFlag={} ",proxyFlag);
        client = new ApiClient(API_KEY, API_SECRET, PRIVATE_KEY,proxyFlag);
    }
    //卖出价格 数量
    static BigDecimal sellPrice;
    static BigDecimal sellAmount;
    //买入价格 数量
    static BigDecimal buyPrice;
    static BigDecimal buyAmount;
    private BinanceApiRestClientImpl binanceApiRestClient = new BinanceApiRestClientImpl("","");

//    private  Long sellMemberId = 66946L;//机器人买单账户
    private  Long sellMemberId = 1L;//测试机器人买单账户

//    private  Long buyMemberId  = 65859L;//机器人卖单账户
    private  Long buyMemberId  = 2L;//测试机器人卖单账户
    private  int count = 0 ;
//  htusdt   hteth  htbtc  btcusdt  ethusdt  ethbtc

    

    /**
     * 自动买卖单机器人btcusdt
     */
   /* @Scheduled(cron = "0/11 * * * * ? ")
    public void orderRobotTranscationBTCUSDT() throws  Exception{
        Long sleep = (long)(Math.random()*3000);
        log.info(">>>>>>>自动交易机器人刷单开始>>>>>>>交易对为>>>>>BTC/USDT  睡眠>>"+sleep+proxyFlag);
        Thread.sleep(sleep);
        //火币支持的交易对  btcusdt  ethusdt  ethbtc
        DepthRequest request = new DepthRequest();
        request.setSymbol("btcusdt");
        request.setType("step0");
        DepthResponse depthResponse  = client.depth(request);
        //限制交易数量
        BigDecimal limitAmount = new BigDecimal(1);
        String symbol = "BTC/USDT";
        sellOrBuyChangeByHB(depthResponse, symbol,limitAmount);
        log.info(">>>>>定时任务结束  BTC/USDT>>>");
    }*/

    /**
     * ethusdt ETH/USDT 卖单机器人
     */
    @Scheduled(cron = "0/13 * * * * ? ")
    public void orderRobotTranscationETHUSDT() throws  Exception{
        Long sleep = (long)(Math.random()*3000);
        log.info(">>>>>>>自动交易机器人刷单开始>>>>>>>交易对为>>>>>ETH/USDT 睡眠>>"+sleep);
        Thread.sleep(sleep);
        //火币支持的交易对
        DepthRequest request = new DepthRequest();
        request.setSymbol("ethusdt");
        request.setType("step0");
        DepthResponse depthResponse  = client.depth(request);
        //限制交易数量
        BigDecimal limitAmount = new BigDecimal(1);
        String symbol = "ETH/USDT";
        sellOrBuyChangeByHB(depthResponse, symbol,limitAmount);
        log.info(">>>>>定时任务结束  ETH/USDT >>>");

    }

    /**
     * htusdt HT/USDT 卖单机器人
     */
    @Scheduled(cron = "0/14 * * * * ? ")
    public void sellOrderRobotTranscationHTUSDT() throws  Exception{
        Long sleep = (long)(Math.random()*3000);
        log.info(">>>>>>>自动交易机器人刷单开始>>>>>>>交易对为>>>>>HT/USDT 睡眠>>"+sleep);
        Thread.sleep(sleep);
        //火币支持的交易对
        DepthRequest request = new DepthRequest();
        request.setSymbol("htusdt");
        request.setType("step0");
        DepthResponse depthResponse  = client.depth(request);
        String symbol = "HT/USDT";
        //限制交易数量
        BigDecimal limitAmount = new BigDecimal(100);
        sellOrBuyChangeByHB(depthResponse, symbol,limitAmount);
        log.info(">>>>>定时任务结束  HT/USDT >>>");
    }

    /**
     * ETH/BTC交易  价格为小数
     * ethbtc ETH/BTC   刷单机器人
     */
    @Scheduled(cron = "0/12 * * * * ? ")
    public void orderRobotTranscationETHBTC()  throws  Exception{
        Long sleep = (long)(Math.random()*3000);
        log.info(">>>>>>>自动交易机器人刷单开始>>>>>>>交易对为>>>>>ETH/BTC 睡眠>>"+sleep);
        Thread.sleep(sleep);
        //火币支持的交易对
        DepthRequest request = new DepthRequest();
        request.setSymbol("ethbtc");
        request.setType("step0");
        DepthResponse depthResponse  = client.depth(request);
        //限制交易数量
        BigDecimal limitAmount = new BigDecimal(1);
        String symbol = "ETH/BTC";
        sellOrBuyChangeByHB(depthResponse, symbol,limitAmount);
        log.info(">>>>>定时任务结束  ETH/BTC>>>");
    }

    /**
     * htbtc HT/BTC 卖单机器人
     */
    @Scheduled(cron = "0/16 * * * * ? ")
    public void sellOrderRobotTranscationHTBTC() throws  Exception{
        Long sleep = (long)(Math.random()*3000);
        log.info(">>>>>>>自动交易机器人刷单开始>>>>>>>交易对为>>>>>HT/BTC 睡眠>>"+sleep);
        Thread.sleep(sleep);
        //火币支持的交易对
        //火币支持的交易对
        DepthRequest request = new DepthRequest();
        request.setSymbol("htbtc");
        request.setType("step0");
        DepthResponse depthResponse  = client.depth(request);
        String symbol = "HT/BTC";
        //限制交易数量
        BigDecimal limitAmount = new BigDecimal(100);
        sellOrBuyChangeByHB(depthResponse, symbol,limitAmount);
        log.info(">>>>>定时任务结束  HT/BTC >>>");

    }
    /**
     * hteth HT/ETH 卖单机器人
     */
    @Scheduled(cron = "0/15 * * * * ? ")
    public void sellOrderRobotTranscationHTETH() throws  Exception{
        Long sleep = (long)(Math.random()*3000);
        log.info(">>>>>>>自动交易机器人刷单开始>>>>>>>交易对为>>>>>HT/ETH 睡眠>>"+sleep);
        Thread.sleep(sleep);
        //火币支持的交易对
        //火币支持的交易对
        DepthRequest request = new DepthRequest();
        request.setSymbol("hteth");
        request.setType("step0");
        DepthResponse depthResponse  = client.depth(request);
        String symbol = "HT/ETH";
        //限制交易数量
        BigDecimal limitAmount = new BigDecimal(100);
        sellOrBuyChangeByHB(depthResponse, symbol,limitAmount);
        log.info(">>>>>定时任务结束  HT/ETH >>>");
    }



//    /**
//     * BNBUSDT BNB/USDT 卖单机器人
//     */
//    @Scheduled(cron = "0/17 * * * * ? ")
//    public void sellOrderRobotTranscationBNBUSDT()throws  Exception {
//        Long sleep = (long)(Math.random()*3000);
//        log.info(">>>>>>>自动交易机器人刷单开始>>>>>>>交易对为>>>>>BNB/USDT  睡眠>>"+sleep);
//        Thread.sleep(sleep);
//        //BNB支持的交易对
//        OrderBook orderBooks =  binanceApiRestClient.getOrderBook("BNBUSDT",5);
//        String symbol = "BNB/USDT";
//        //限制交易数量
//        BigDecimal limitAmount = new BigDecimal(10);
//        sellOrBuyChangeByBNB(orderBooks, symbol,limitAmount);
//        log.info(">>>>>定时任务结束  BNB/USDT >>>");
//
//    }
//
//
//
//    /**
//     * BNBETH BNB/ETH 卖单机器人
//     */
//    @Scheduled(cron = "0/18 * * * * ? ")
//    public void sellOrderRobotTranscationBNBETH()throws  Exception {
//        Long sleep = (long)(Math.random()*3000);
//        log.info(">>>>>>>自动交易机器人刷单开始>>>>>>>交易对为>>>>>BNB/ETH 睡眠>>"+sleep);
//        Thread.sleep(sleep);
//        //BNB支持的交易对
//        OrderBook orderBooks =  binanceApiRestClient.getOrderBook("BNBETH",5);
//        String symbol = "BNB/ETH";
//        //限制交易数量
//        BigDecimal limitAmount = new BigDecimal(1);
//        sellOrBuyChangeByBNB(orderBooks, symbol,limitAmount);
//        log.info(">>>>>定时任务结束  BNB/ETH >>>");
//
//    }
//    /**
//     * BNBBTC BNB/BTC 卖单机器人
//     */
//    @Scheduled(cron = "0/19 * * * * ? ")
//    public void sellOrderRobotTranscationBNBBTC() throws  Exception{
//        Long sleep = (long)(Math.random()*3000);
//        log.info(">>>>>>>自动交易机器人刷单开始>>>>>>>交易对为>>>>>BNB/BTC  睡眠>>"+sleep);
//        Thread.sleep(sleep);
//        //BNB支持的交易对
//        OrderBook orderBooks =  binanceApiRestClient.getOrderBook("BNBBTC",5);
//        String symbol = "BNB/BTC";
//        //限制交易数量
//        BigDecimal limitAmount = new BigDecimal(1);
//        sellOrBuyChangeByBNB(orderBooks, symbol,limitAmount);
//        log.info(">>>>>定时任务结束  BNB/BTC >>>");
//
//    }

    @Scheduled(cron = "0/30 * * * * ? ")
    public void cancelOrderRobotTranscation() throws Exception {
        log.info(">>>>>>取消订单开始");
        Long a = System.currentTimeMillis()-15*60*1000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(a);
        //取消当前时间前两个个小时的订单
        long cancelTime = calendar.getTimeInMillis();
        //查询出所有符合状态的挂单
        List<ExchangeOrder> orders = orderService.queryExchangeOrderByTimeById(cancelTime);
        if (orders.size() != 0) {
            for (ExchangeOrder order : orders) {
                calendar.setTimeInMillis(order.getTime());
                log.info(">>>>>>此次取消订单的会员id" + order.getMemberId() + ">>>取消订单的id为>>>" + order.getOrderId() +
                        ">>订单创建时间为>>>" + DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss:SSS"));
                // 发送消息至Exchange系统
                kafkaTemplate.send("exchange-order-cancel",order.getSymbol(), JSONObject.toJSONString(order));
                Thread.sleep(10);
            }
        }
        log.info(">>>>>>取消订单结束>>>>>>");

    }

    /**
     * 币安自动交易订单
     * @param orderBooks
     * @param symbol
     * @param limitAmount
     */
    private void sellOrBuyChangeByBNB(OrderBook orderBooks, String symbol, BigDecimal limitAmount)throws Exception {
        //最新卖出委托单 5个 只去第一个
        OrderBookEntry orderBookSell = orderBooks.getAsks().get(0);
        //获取卖出价格 和卖出量
        sellPrice = new BigDecimal(orderBookSell.getPrice() );
        sellAmount = new BigDecimal(orderBookSell.getQty() );
        if(sellAmount.compareTo(limitAmount)==1){
            sellAmount =  new BigDecimal(Math.random()).setScale(4,BigDecimal.ROUND_DOWN).multiply(limitAmount);
        }
        //最新买入委托单
        OrderBookEntry orderBookBuy = orderBooks.getBids().get(0);
        buyPrice = new BigDecimal(orderBookBuy.getPrice() );
        buyAmount = new BigDecimal(orderBookBuy.getQty() );
        if(buyAmount.compareTo(limitAmount)==1){
            buyAmount =  new BigDecimal(Math.random()).setScale(4,BigDecimal.ROUND_DOWN).multiply(limitAmount);
        }
        if (sellPrice.compareTo(ZERO) != 1 || sellAmount.compareTo(ZERO) != 1
                || buyPrice.compareTo(ZERO) != 1 || buyAmount.compareTo(ZERO) != 1) {
            return;
        }
        ExchangeOrderType sellOrBuyType = ExchangeOrderType.LIMIT_PRICE;
        ExchangeOrderDirection sellDirection = ExchangeOrderDirection.SELL;

        //创建订单
        MessageResult messageResult = addOrder(sellMemberId, sellDirection, symbol, sellPrice, sellAmount, sellOrBuyType);
        //提交订单
        log.info(">>>>>>>卖单订单创建状态>>>>" + messageResult.getCode() + ">>>>" + messageResult.getMessage());
        Thread.sleep(100);
        //创建买入订单
        ExchangeOrderDirection buyDirection = ExchangeOrderDirection.BUY;
        //创建订单
        messageResult = addOrder(buyMemberId, buyDirection, symbol, buyPrice, buyAmount, sellOrBuyType);
        //提交订单
        log.info(">>>>>>>买单订单创建状态>>>>" + messageResult.getCode() + ">>>>>data>>" + messageResult.getMessage());
        log.info(">>>>>>自动交易完成 买卖委托单已发布");
    }
    /**
     * 火币自动交易订单
     * @param response
     * @param symbol
     * @param limitAmount
     */
    private void sellOrBuyChangeByHB(DepthResponse response, String symbol,BigDecimal limitAmount) throws Exception{
        Depth depth = response.getTick();
        //最新卖出委托单
        List<BigDecimal> asks = depth.getAsks().get(0);
        //获取卖出价格 和卖出量
        sellPrice = asks.get(0);
        sellAmount = asks.get(1);
        if(sellAmount.compareTo(limitAmount)==1){
            sellAmount =  new BigDecimal(Math.random()).setScale(4,BigDecimal.ROUND_DOWN).multiply(limitAmount);
        }
        //最新买入委托单
        List<BigDecimal> bids = depth.getBids().get(0);
        buyPrice = bids.get(0);
        buyAmount = bids.get(1);
        if(buyAmount.compareTo(limitAmount)==1){
            buyAmount =  new BigDecimal(Math.random()).setScale(4,BigDecimal.ROUND_DOWN).multiply(limitAmount);
        }
        if (sellPrice.compareTo(ZERO) != 1 || sellAmount.compareTo(ZERO) != 1
                || buyPrice.compareTo(ZERO) != 1 || buyAmount.compareTo(ZERO) != 1) {
            return;
        }
        ExchangeOrderType sellOrBuyType = ExchangeOrderType.LIMIT_PRICE;
        ExchangeOrderDirection sellDirection = ExchangeOrderDirection.SELL;

        //创建订单
        MessageResult messageResult = addOrder(sellMemberId, sellDirection, symbol, sellPrice, sellAmount, sellOrBuyType);
        //提交订单
        log.info(">>>>>>>卖单订单创建状态>>>>" + messageResult.getCode() + ">>>>" + messageResult.getMessage());
        Thread.sleep(100);
        //创建买入订单
        ExchangeOrderDirection buyDirection = ExchangeOrderDirection.BUY;
        //创建订单
        messageResult = addOrder(buyMemberId, buyDirection, symbol, buyPrice, buyAmount, sellOrBuyType);
        //提交订单
        log.info(">>>>>>>买单订单创建状态>>>>" + messageResult.getCode() + ">>>>>data>>" + messageResult.getMessage());
        log.info(">>>>>>自动交易完成 买卖委托单已发布");
    }


    private MessageResult addOrder(Long memberId, ExchangeOrderDirection direction,
                                   String symbol, BigDecimal price, BigDecimal amount, ExchangeOrderType type) {
        log.info(">>>count>>>"+count+">>>sellMemberId>>"+sellMemberId+">>>>buyMemberId>>>"+buyMemberId);
        ExchangeOrder order = new ExchangeOrder();
        if (price.compareTo(BigDecimal.ZERO) <= 0 && type == ExchangeOrderType.LIMIT_PRICE) {
            return MessageResult.error(500, msService.getMessage("EXORBITANT_PRICES"));
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return MessageResult.error(500, msService.getMessage("NUMBER_OF_ILLEGAL"));
        }
        ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(symbol);
        if (exchangeCoin == null || exchangeCoin.getEnable() != 1) {
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }

        String baseCoin = exchangeCoin.getBaseSymbol();
        String exCoin = exchangeCoin.getCoinSymbol();
        log.info("exCoin={},baseCoin={},direction={},type={}", exCoin, baseCoin, direction, type);

        Coin coin;
        if (direction == ExchangeOrderDirection.SELL) {
            coin = coinService.findByUnit(exCoin);
        } else {
            coin = coinService.findByUnit(baseCoin);
        }
        if (coin == null) {
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        //设置价格精度
        price = price.setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN);
        //设置数量精度
        if (direction == ExchangeOrderDirection.BUY && type == ExchangeOrderType.MARKET_PRICE) {
            amount = amount.setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_DOWN);
            if (amount.compareTo(exchangeCoin.getMinTurnover()) < 0) {
                return MessageResult.error(500, "成交额至少为" + exchangeCoin.getMinTurnover());
            }
        } else {
            amount = amount.setScale(exchangeCoin.getCoinScale(), BigDecimal.ROUND_DOWN);
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0 && type == ExchangeOrderType.LIMIT_PRICE) {
            return MessageResult.error(500, "invalid price");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return MessageResult.error(500, "invalid amount");
        }
        MemberWallet wallet = walletService.findByCoinAndMemberId(coin, memberId);
        if (wallet == null) {
            return MessageResult.error(500, msService.getMessage("NONSUPPORT_COIN"));
        }
        if (wallet.getIsLock() == BooleanEnum.IS_TRUE) {
            return MessageResult.error(500, "钱包已经被锁定");
        }
        if(wallet.getBalance().compareTo(BigDecimal.valueOf(100))!=1){
            count ++;
            log.info(">>>>>反转买卖账户>cishu>>>>"+count);
            long tempMeberId = buyMemberId;
            buyMemberId =sellMemberId;
            sellMemberId = tempMeberId;
        }
        //如果有最低卖价限制，出价不能低于此价,且禁止市场价格卖
        if (direction == ExchangeOrderDirection.SELL && exchangeCoin.getMinSellPrice().compareTo(BigDecimal.ZERO) > 0
                && ((price.compareTo(exchangeCoin.getMinSellPrice()) < 0) || type == ExchangeOrderType.MARKET_PRICE)) {
            return MessageResult.error(500, "非法的价格");
        }
        //查看是否启用市价买卖
        if (type == ExchangeOrderType.MARKET_PRICE) {
            if (exchangeCoin.getEnableMarketBuy() == BooleanEnum.IS_FALSE && direction == ExchangeOrderDirection.BUY) {
                return MessageResult.error(500, "不支持市场购买");
            } else if (exchangeCoin.getEnableMarketSell() == BooleanEnum.IS_FALSE && direction == ExchangeOrderDirection.SELL) {
                return MessageResult.error(500, "不支持市场出售");
            }
        }
        //限制委托数量
        if (exchangeCoin.getMaxTradingOrder() > 0 && orderService.findCurrentTradingCount(memberId, symbol, direction) >= exchangeCoin.getMaxTradingOrder()) {
            return MessageResult.error(500, "超过最大挂单数量 " + exchangeCoin.getMaxTradingOrder());
        }
        order.setMemberId(memberId);
        order.setSymbol(symbol);
        order.setBaseSymbol(baseCoin);
        order.setCoinSymbol(exCoin);
        order.setPrice(price);
        //限价买入单时amount为用户设置的总成交额
        order.setAmount(amount);
        order.setType(type);
        order.setDirection(direction);
        order.setMarginTrade(BooleanEnum.IS_FALSE);
        //机器人下单
        order.setOrderResource(ExchangeOrderResource.ROBOT);
        MessageResult mr = orderService.addOrder(memberId, order);
        if (mr.getCode() != 0) {
            return MessageResult.error(500, "提交订单失败:" + mr.getMessage());
        }
        log.info(">>>>>>>>>>订单提交完成>>>>>>>>>>");
        // 发送消息至Exchange系统
        try {
            kafkaTemplate.send("exchange-order", symbol,JSONObject.toJSONString(order));
        } catch (Exception e) {
            e.printStackTrace();
            log.info(">>>>>>>>kafka异常>>>>>+" + e);
        }
        return MessageResult.success("提交成功");
    }

    /**
     * 整数上下浮动
     * @param x
     * @param fd
     * @return
     */
//    private  static int fun1Int(int x,double fd) {
//        int y = (int) (Math.random() * (x + (1)) * fd) * (Math.random() > 0.5 ? 1 : -1);
//        return x + y;
//    }

    /**
     * 小数上下浮动
     *
     * @param x
     * @param fd
     * @return
     */
    private static BigDecimal fun1Douvble(BigDecimal x, double fd, int scale) {
        BigDecimal y;
        y = new BigDecimal(Math.random()).multiply(x.add(new BigDecimal(1))).multiply(new BigDecimal(fd));
        double z = Math.random();
        if (z > 0.5) {
            return x.add(y).setScale(scale, BigDecimal.ROUND_DOWN);
        } else {
            return x.subtract(y).setScale(scale, BigDecimal.ROUND_DOWN);
        }
    }

//    public static void main(String[] args)throws Exception {
//        System.out.println(new BigDecimal(0).compareTo(new BigDecimal(0)));
//        double fd = 0.00001;//浮动的范围
//        BigDecimal x = new BigDecimal(0.069438);//需要浮动的数字
//        //int x = -10;
//        for (int i = 0; i < 50; i++) {//循环50次，方便查看浮动的结果
//            System.out.println(fun1Douvble(x, fd, 6));//调用方法一
//            //System.out.println(fun2(x,fd));//调用方法二

//        }

//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        //取消当前时间前两个个小时的订单
//        calendar.add(Calendar.HOUR_OF_DAY, -2);
//        System.out.println(calendar.getTimeInMillis());
//        System.out.println(DateFormatUtils.format(calendar.getTime(),"yyyy-MM-dd HH:mm:ss"));
//        calendar.setTimeInMillis(1531409560665L);
//        System.out.println(DateFormatUtils.format(calendar.getTime(),"yyyy-MM-dd HH:mm:ss:SSS"));
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
//        Date date = df.parse("2018-07-12 23:32:40:665");
//        System.out.println(date.getTime());
//        for (int i = 0; i < 50; i++) {
//            buyAmount =  new BigDecimal(Math.random()).setScale(4,BigDecimal.ROUND_DOWN).multiply(new BigDecimal(1));
//            System.out.println(buyAmount);
//        }

//    }
}