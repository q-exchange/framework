package cn.ztuo.bitrade.service;

import cn.ztuo.bitrade.constant.BooleanEnum;
import cn.ztuo.bitrade.constant.TransactionType;
import cn.ztuo.bitrade.dao.*;
import cn.ztuo.bitrade.entity.*;
import cn.ztuo.bitrade.pagination.Criteria;
import cn.ztuo.bitrade.pagination.Restrictions;
import cn.ztuo.bitrade.system.CoinExchangeFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: dupinyan
 * @Description:
 * @Date: 2019/10/16 17:07
 * @Version: 1.0
 */
@Slf4j
@Service
public class AssetsService {

    @Autowired
    private MemberWalletDao memberWalletDao;

    @Autowired
    private CoinExchangeFactory coinExchangeFactory;

    @Autowired
    private MemberTransactionDao memberTransactionDao;

    @Autowired
    private LeverWalletRepository leverWalletRepository;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private OtcWalletDao otcWalletDao;

    @Autowired
    private LoanRecordRepository loanRecordRepository;


    /**
     * 根据币种类型和用户id获取钱包信息
     *
     * @param userId 用户id
     * @param coin   币种类型
     * @return 资产数组
     */
    public BigDecimal[] getAssetsForCoin(Long userId, String coin) {
        // 根据用户id和币种查询
        MemberWallet wallet = memberWalletDao.getAssetsForCoin(userId, coin);
        BigDecimal balance = wallet.getBalance();
        BigDecimal frozenBalance = wallet.getFrozenBalance();
        CoinExchangeFactory.ExchangeRate exchangeRate = coinExchangeFactory.get(coin);
        BigDecimal cnyRate = exchangeRate.getCnyRate();
        BigDecimal balanceCny = balance.multiply(cnyRate);
        BigDecimal balanceFrozen = balance.add(frozenBalance).multiply(cnyRate);
        return new BigDecimal[]{balance, frozenBalance, balanceCny, balanceFrozen};
    }


    public Page<MemberTransaction> getAssetsRecord(Integer pageNo, Integer pageSize, Long memberId, String symbol, TransactionType type) {
        Sort sort = Criteria.sortStatic("createTime.desc");
        PageRequest pageRequest = new PageRequest(pageNo - 1, pageSize, sort);

        Page<MemberTransaction> result = memberTransactionDao.findAll(new Specification<MemberTransaction>() {
            @Override
            public Predicate toPredicate(Root<MemberTransaction> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("memberId"), memberId));
                if (StringUtils.isNotBlank(symbol)) {
                    predicates.add(cb.equal(root.get("symbol"), symbol));
                }
                if (type != null) {
                    predicates.add(cb.equal(root.get("type"), type));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        }, pageRequest);
        return result;
    }




    /**
     * 根据用户查询用户余额
     *
     * @param userId
     * @return
     */
    public BigDecimal[] getTotalAssetsService(Long userId) {
        BigDecimal totalAssets = BigDecimal.ZERO;
        List<LeverWallet> sumByCoin = leverWalletRepository.getSumByCoin(userId);
        log.info("sumByCoin={}", sumByCoin);
        List<OtcWallet> otcWalletList = otcWalletDao.getSumByCoin(userId);
        log.info("otcWalletList={}", otcWalletList);
        List<MemberWallet> memberWalletList = memberWalletDao.getSumByCoin(userId);
        log.info("memberWalletList={}", memberWalletList);
        Member member = memberDao.findOne(userId);

        for (OtcWallet otcWallet : otcWalletList) {
            if (!"USDT".equals(otcWallet.getCoin().getName())) {
                CoinExchangeFactory.ExchangeRate exchangeRate = coinExchangeFactory.get(otcWallet.getCoin().getUnit());
                BigDecimal usdRate = exchangeRate.getUsdRate();
                log.info("otcWallet的usdRate={}", usdRate);
                BigDecimal usdAmount = otcWallet.getBalance().multiply(usdRate);
                totalAssets = totalAssets.add(usdAmount);
            } else {
                totalAssets = totalAssets.add(otcWallet.getBalance());
            }

        }
        for (MemberWallet memberWallet : memberWalletList) {
            if (!"USDT".equals(memberWallet.getCoin().getName())) {
                CoinExchangeFactory.ExchangeRate exchangeRate = coinExchangeFactory.get(memberWallet.getCoin().getUnit());
                BigDecimal usdRate = exchangeRate.getUsdRate();
                log.info("memberWallet的usdRate={}", usdRate);
                BigDecimal usdAmount = memberWallet.getBalance().multiply(usdRate);
                totalAssets = totalAssets.add(usdAmount);
            } else {
                totalAssets = totalAssets.add(memberWallet.getBalance());
            }

        }
        // 未还
        BigDecimal totalLoanAmount = BigDecimal.ZERO;
        // 利息
        BigDecimal totalInterestAmount = BigDecimal.ZERO;
        for (LeverWallet leverWallet : sumByCoin) {
            if (!"USDT".equals(leverWallet.getCoin().getName())) {
                CoinExchangeFactory.ExchangeRate exchangeRate = coinExchangeFactory.get(leverWallet.getCoin().getUnit());
                BigDecimal usdRate = exchangeRate.getUsdRate();
                log.info("leverWallet的usdRate={}", usdRate);
                BigDecimal usdCount = leverWallet.getBalance().multiply(usdRate);
                totalAssets = totalAssets.add(usdCount);
            }
            // 是否查询为归还
            List<LoanRecord> loanRecordList = loanRecordRepository.findByMemberIdAndLeverCoinAndRepayment(member.getId(), leverWallet.getLeverCoin(), BooleanEnum.IS_FALSE);
            for (LoanRecord loanRecord : loanRecordList) {
                CoinExchangeFactory.ExchangeRate exchangeRate = coinExchangeFactory.get(loanRecord.getCoin().getUnit());
                BigDecimal usdRate = exchangeRate.getUsdRate();
                totalLoanAmount = totalLoanAmount.add(loanRecord.getAmount()).multiply(usdRate);
                totalInterestAmount = totalInterestAmount.add(loanRecord.getLoanBalance()).multiply(usdRate);
            }
        }
        totalAssets = totalAssets.subtract(totalLoanAmount).subtract(totalInterestAmount);
        CoinExchangeFactory.ExchangeRate exchangeRate = coinExchangeFactory.get("USDT");
        BigDecimal cnyRate = exchangeRate.getCnyRate();
        log.info("--->usdt转换cny费率---->cnyRate={}", cnyRate);
        BigDecimal totalCnyAssets = totalAssets.multiply(cnyRate);
        log.info("用户userId={}总资产为totalAssets={}, totalCnyAssets={}", userId, totalAssets, totalCnyAssets);
        return new BigDecimal[]{totalAssets, totalCnyAssets};
    }


}
