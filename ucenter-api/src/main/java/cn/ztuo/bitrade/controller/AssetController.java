package cn.ztuo.bitrade.controller;


import cn.ztuo.bitrade.core.Convert;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import cn.ztuo.bitrade.constant.CommonStatus;
import cn.ztuo.bitrade.constant.PageModel;
import cn.ztuo.bitrade.constant.TransactionType;
import cn.ztuo.bitrade.entity.*;
import cn.ztuo.bitrade.entity.transform.AuthMember;
import cn.ztuo.bitrade.service.*;
import cn.ztuo.bitrade.system.CoinExchangeFactory;
import cn.ztuo.bitrade.util.MessageResult;
import cn.ztuo.bitrade.util.PredicateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static cn.ztuo.bitrade.constant.SysConstant.SESSION_MEMBER;

@RestController
@RequestMapping("/asset")
@Slf4j
public class AssetController extends BaseController{
    @Autowired
    private MemberWalletService walletService;
    @Autowired
    private MemberTransactionService transactionService;
    @Autowired
    private CoinExchangeFactory coinExchangeFactory;
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private LockPositionRecordService lockPositionRecordService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private LocaleMessageSourceService sourceService;
    @Autowired
    private AssetsService assetsService;


    /**
     * 获取所有枚举类型
     * @return
     */
    @RequestMapping(value = "transaction_type",method = RequestMethod.GET)
    public MessageResult findAllTransactionType(){
        TransactionType[] s = TransactionType.values();
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i <s.length ; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key",s[i].getOrdinal());
            jsonObject.put("name",s[i].getCnName());
            list.add(jsonObject);
        }
        return success(list);
    }


    /**
     * 用户钱包信息
     *
     * @param member
     * @return
     */
    @RequestMapping("wallet")
    public MessageResult findWallet(@SessionAttribute(SESSION_MEMBER) AuthMember member) {
        List<MemberWallet> wallets = walletService.findAllByMemberId(member.getId());
        wallets.forEach(wallet -> {
            CoinExchangeFactory.ExchangeRate rate = coinExchangeFactory.get(wallet.getCoin().getUnit());
            if (rate != null) {
                wallet.getCoin().setUsdRate(rate.getUsdRate());
                wallet.getCoin().setCnyRate(rate.getCnyRate());
                wallet.getCoin().setSgdRate(rate.getSgdRate());
            } else {
                log.info("unit = {} , rate = null ", wallet.getCoin().getUnit());
            }
        });
        MessageResult mr = MessageResult.success("success");
        mr.setData(wallets);
        return mr;
    }

    /**
     * 查询特定类型的记录
     *
     * @param member
     * @param pageNo
     * @param pageSize
     * @param type
     * @return
     */
    @RequestMapping("transaction")
    public MessageResult findTransaction(@SessionAttribute(SESSION_MEMBER) AuthMember member, int pageNo, int pageSize,
                                         TransactionType type,String symbol,String startTime, String endTime) throws ParseException {
        log.info("pageNo={},pageSize={},type={},symbol={},startTime={},endTime={}",pageNo,pageSize,type,symbol,startTime,endTime);
        Page page = transactionService.queryByMember(member.getId(), pageNo, pageSize, type,startTime,endTime,symbol);
        return success(page);
    }

    /**
     * 查询所有记录
     *
     * @param member
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping("transaction/all")
    public MessageResult findTransaction(
            @SessionAttribute(SESSION_MEMBER) AuthMember member,
            HttpServletRequest request,
            int pageNo,
            int pageSize,
            @RequestParam(value = "symbol",required = false) String symbol) throws ParseException {
        TransactionType type = null;
        if (StringUtils.isNotEmpty(request.getParameter("type"))) {
            type = TransactionType.valueOfOrdinal(Convert.strToInt(request.getParameter("type"), 0));
        }
        String startDate = "";
        String endDate = "";
        if (StringUtils.isNotEmpty(request.getParameter("dateRange"))) {
            String[] parts = request.getParameter("dateRange").split("~");
            startDate = parts[0].trim();
            endDate = parts[1].trim();
        }

        log.info("type={},sDa={}",request.getParameter("type"),request.getParameter("dateRange"));
        log.info("type={},startDate={},endDate={}",type,startDate,endDate);
        return success(transactionService.queryByMember(member.getId(), pageNo, pageSize, type, startDate, endDate,symbol));
    }

    @RequestMapping("wallet/{symbol}")
    public MessageResult findWalletBySymbol(@SessionAttribute(SESSION_MEMBER) AuthMember member, @PathVariable String symbol) {
        MessageResult mr = MessageResult.success("success");
        mr.setData(walletService.findByCoinUnitAndMemberId(symbol, member.getId()));
        return mr;
    }

    @RequestMapping("wallet/reset-address")
    public MessageResult resetWalletAddress(@SessionAttribute(SESSION_MEMBER) AuthMember member, String unit) {
        try {
            JSONObject json = new JSONObject();
            json.put("uid", member.getId());
            kafkaTemplate.send("reset-member-address", unit, json.toJSONString());
            return MessageResult.success("提交成功");
        } catch (Exception e) {
            return MessageResult.error("未知异常");
        }
    }

    @PostMapping("lock-position")
    public MessageResult lockPositionRecordList(@SessionAttribute(SESSION_MEMBER) AuthMember member,
                                                @RequestParam(value = "status",required = false) CommonStatus status,
                                                @RequestParam(value = "coinUnit",required = false)String coinUnit,
                                                PageModel pageModel){
        ArrayList<BooleanExpression> booleanExpressions = new ArrayList<>();
        if(status!=null){
            booleanExpressions.add(QLockPositionRecord.lockPositionRecord.status.eq(status));
        }
        if(coinUnit!=null){
            Coin coin=coinService.findByUnit(coinUnit);
            if(coin==null){
                return MessageResult.error(sourceService.getMessage("COIN_ILLEGAL"));
            }
            booleanExpressions.add(QLockPositionRecord.lockPositionRecord.coin.eq(coin));
        }
        booleanExpressions.add(QLockPositionRecord.lockPositionRecord.memberId.eq(member.getId()));
        Predicate predicate=PredicateUtils.getPredicate(booleanExpressions);
        Page<LockPositionRecord> lockPositionRecordList=lockPositionRecordService.findAll(predicate,pageModel);
        MessageResult result=MessageResult.success();
        result.setData(lockPositionRecordList);
        return result;
    }


    /**
     * 根据币种和用户获取钱包余额
     *
     * @param coin
     * @param member
     * @return
     */
    @RequestMapping(value = "getAssetForCoinType/{coin}", method = RequestMethod.GET)
    public MessageResult getAssetForCoinType(@PathVariable("coin") String coin, @SessionAttribute(SESSION_MEMBER) AuthMember member) {
        log.info("----->根据币种类型获取钱包余额----->coin={}, member={}", coin, member);
        BigDecimal[] assetsArray = assetsService.getAssetsForCoin(member.getId(), coin);
        return success(assetsArray);
    }


    /**
     * 获取资产
     *
     * @param member   用户
     * @param pageNo   页码
     * @param pageSize 每页记录数
     * @param symbol   币种
     * @param type     类型
     * @return
     */
    @RequestMapping(value = "getAssetPage", method = RequestMethod.POST)
    public MessageResult getAssetPage(@SessionAttribute(SESSION_MEMBER) AuthMember member, @RequestParam("pageNo") Integer pageNo,
                                      @RequestParam("pageSize") Integer pageSize, @RequestParam("symbol") String symbol,
                                      @RequestParam("type") TransactionType type) {
        Page<MemberTransaction> assetsRecord = assetsService.getAssetsRecord(pageNo, pageSize, member.getId(), symbol, type);
        return success(assetsRecord);
    }


    /**
     * 查询总资产
     *
     * @param member
     * @return
     */
    @RequestMapping(value = "getTotalAssets")
    public MessageResult getTotalAssets(@SessionAttribute(SESSION_MEMBER) AuthMember member) {
        log.info("---->查询总资产---member={}", member);
        MessageResult messageResult = new MessageResult();
        BigDecimal[] totalAssets = assetsService.getTotalAssetsService(member.getId());
        log.info("---->查询结果---->totalAssets={}", totalAssets);
        messageResult.setData(totalAssets);
        return messageResult;
    }

}
