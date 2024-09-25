package com.pxl.zkmall.thirdparty.component;

import com.pxl.common.utils.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms") //去application.yml配置文件中读取前缀+字段名(下方字段如appCode)
@Data
@Component
public class SMSUtils {

	private String host;
	private String path;
	private String appcode;


	public void sendCode(String phone, String code) {
		String method = "GET";
		Map<String, String> headers = new HashMap<String, String>();
		//最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		headers.put("Authorization", "APPCODE " + appcode);
		Map<String, String> querys = new HashMap<String, String>();
		querys.put("mobile", phone);
		querys.put("content", "【ZKMALL商城】您的验证码"+code+"，该验证码5分钟内有效，请勿泄露于他人！");

		try {
			HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
//			System.out.println(response.toString());
			//获取response的body
			System.out.println(EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
