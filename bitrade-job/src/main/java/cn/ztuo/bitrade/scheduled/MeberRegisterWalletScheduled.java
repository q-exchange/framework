package cn.ztuo.bitrade.scheduled;

import cn.ztuo.bitrade.service.MemberService;
import cn.ztuo.bitrade.util.JDBCUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

//@Component
@Slf4j
public class MeberRegisterWalletScheduled {
    @Autowired
    JDBCUtils jdbcUtils;
    @Autowired
    MemberService memberService ;

    @Scheduled(cron = "0 40 16 * * ? ")
    public void synchMemberRegisterWallet(){

//        List<Member> memberList = memberService.findAllMemberId();

        jdbcUtils.synchronization2MemberRegisterWallet(null,"HTL");
    }


}
