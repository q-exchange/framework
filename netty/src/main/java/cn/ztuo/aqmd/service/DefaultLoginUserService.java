/**
 * Copyright (c) 2016-2017  All Rights Reserved.
 * 
 * <p>FileName: LoginUserDao.java</p>
 * 
 * Description: 
 * @author MrGao
 * @date 2017年10月26日
 * @version 1.0
 * History:
 * v1.0.0, , 2017年10月26日, Create
 */
package cn.ztuo.aqmd.service;

import cn.ztuo.aqmd.core.entity.CustomerMsg;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title: LoginUserDao</p>
 * <p>Description: </p>
 * netty登陆的默认服务，用户名随意,密码admin
 * @author MrGao
 * @date 2017年10月26日
 */
@SuppressWarnings("rawtypes")
public class DefaultLoginUserService implements LoginUserService {


	@Override
	public CustomerMsg findUserByLoginNo(String loginNo) {
		Map<String,String> result = new HashMap<>();
		CustomerMsg customerMsg = new CustomerMsg();
		customerMsg.setPassword("72f5cd8566580a9621de8e1b050e6e8d13c6f655b4fcd8dfe50fedaca099320b2914cc6529186aca39aba1bf46611849373dbe7576497d0db879dd070aef1904");
		customerMsg.setSalt("123456");
		return customerMsg;
	}


	@Override
	public Integer updPassword(String accountNo, String password) {
		return 1;
	}

}
