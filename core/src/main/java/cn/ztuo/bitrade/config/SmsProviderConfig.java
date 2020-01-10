package cn.ztuo.bitrade.config;

import cn.ztuo.bitrade.vendor.provider.SMSProvider;
import cn.ztuo.bitrade.vendor.provider.support.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsProviderConfig {

    @Value("${sms.gateway:}")
    private String gateway;
    @Value("${sms.username:}")
    private String username;
    @Value("${sms.password:}")
    private String password;
    @Value("${sms.sign:}")
    private String sign;
    @Value("${sms.internationalGateway:}")
    private String internationalGateway;
    @Value("${sms.internationalUsername:}")
    private String internationalUsername;
    @Value("${sms.internationalPassword:}")
    private String internationalPassword;


    @Bean
    public SMSProvider getSMSProvider(@Value("${sms.driver:}") String driverName) {
        if (StringUtils.isEmpty(driverName)) {
            return new M5cSMSProvider(gateway, username, password, sign);
        }
        if (driverName.equalsIgnoreCase(ChuangRuiSMSProvider.getName())) {
            return new ChuangRuiSMSProvider(gateway, username, password, sign);
        } else if (driverName.equalsIgnoreCase(EmaySMSProvider.getName())) {
            return new EmaySMSProvider(gateway, username, password);
        }else if (driverName.equalsIgnoreCase(HuaXinSMSProvider.getName())) {
            return new HuaXinSMSProvider(gateway, username, password,internationalGateway,internationalUsername,internationalPassword,sign);
        } else if(driverName.equalsIgnoreCase(TwoFiveThreeProvider.getName())){
            return new TwoFiveThreeProvider(gateway,username,password,sign);
        } else if(driverName.equalsIgnoreCase(QinPengSMSProvider.getName())){
            return new QinPengSMSProvider(gateway,username,password);
        } else if(driverName.equalsIgnoreCase(YunpianSMSProvider.getName())){
            return new YunpianSMSProvider(gateway,password);
        } else if(driverName.equalsIgnoreCase(M5cSMSProvider.getName())){
            return new M5cSMSProvider(gateway, username, password, sign);
        } else {
            return null;
        }
    }
}
