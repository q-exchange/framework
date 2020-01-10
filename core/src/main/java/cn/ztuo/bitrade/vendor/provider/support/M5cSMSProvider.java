package cn.ztuo.bitrade.vendor.provider.support;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import cn.ztuo.bitrade.util.Md5;
import cn.ztuo.bitrade.util.MessageResult;
import cn.ztuo.bitrade.vendor.provider.SMSProvider;
import lombok.extern.slf4j.Slf4j;

/**
 * 美联软通短信接口实现类
 */
@Slf4j
public class M5cSMSProvider implements SMSProvider {

	private String gateway;
	private String username;
	private String password;
	private String apiKey;
	
	
	

	public M5cSMSProvider(String gateway, String username, String password, String apiKey) {
		this.gateway = gateway;
		this.username = username;
		this.password = password;
		this.apiKey = apiKey;
	}

	public static String getName() {
		return "m5c";
	}

	@Override
	public MessageResult sendSingleMessage(String mobile, String content) throws Exception {
		log.info("============sms content==========={}", content);

		// 连接超时及读取超时设置
		System.setProperty("sun.net.client.defaultConnectTimeout", "30000"); // 连接超时：30秒
		System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时：30秒

		// 新建一个StringBuffer链接
		StringBuffer buffer = new StringBuffer();

		// String encode = "GBK";
		// //页面编码和短信内容编码为GBK。重要说明：如提交短信后收到乱码，请将GBK改为UTF-8测试。如本程序页面为编码格式为：ASCII/GB2312/GBK则该处为GBK。如本页面编码为UTF-8或需要支持繁体，阿拉伯文等Unicode，请将此处写为：UTF-8

		String encode = "UTF-8";

		String result = "";

		String contentUrlEncode = URLEncoder.encode("【火腿交易所】" + content, encode); // 对短信内容做Urlencode编码操作。注意：如

		// 把发送链接存入buffer中，如连接超时，可能是您服务器不支持域名解析，请将下面连接中的：【m.5c.com.cn】修改为IP：【115.28.23.78】
		buffer.append(gateway + "?username=" + username + "&password_md5="
				+ Md5.MD5(password) + "&mobile=" + mobile + "&apikey=" + apiKey + "&content=" + contentUrlEncode
				+ "&encode=" + encode);

		System.out.println(buffer); //调试功能，输入完整的请求URL地址

		// 把buffer链接存入新建的URL中
		URL url = new URL(buffer.toString());

		// 打开URL链接
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// 使用POST方式发送
		connection.setRequestMethod("POST");

		// 使用长链接方式
		connection.setRequestProperty("Connection", "Keep-Alive");

		// 发送短信内容
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

		// 获取返回值
		result = reader.readLine();

		// 输出result内容，查看返回值，成功为success，错误为error，详见该文档起始注释
		System.out.println(result);
		return parse2Result(result);

	}

	private MessageResult parse2Result(String result) {
		System.out.println(result);
		MessageResult mr = new MessageResult(500, "系统错误");
		if(result.split(":")[0].equals("success")){
			mr.setCode(0);
			mr.setMessage("发送成功");
		}
		return mr;
	}

	@Override
	public MessageResult sendLoginMessage(String ip, String phone) throws Exception {
		String content = sendLoginMessage(ip) + "【BD】";
		return sendSingleMessage(content, phone);
	}
	
}
