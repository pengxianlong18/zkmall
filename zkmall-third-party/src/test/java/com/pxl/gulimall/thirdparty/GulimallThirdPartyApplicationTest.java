package com.pxl.zkmall.thirdparty;

import com.aliyun.oss.OSSClient;
import com.pxl.zkmall.thirdparty.component.SMSUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class zkmallThirdPartyApplicationTest {


    @Autowired
    OSSClient ossClient;

    @Autowired
    SMSUtils smsUtils;

    @Test
    public   void testSms() {
        smsUtils.sendCode("17507661205","8546");
    }
   @Test
    public  void testUpload() throws FileNotFoundException {

//        String endpoint = "oss-cn-guangzhou.aliyuncs.com";
//        String accessKeyId="LTAI5tFdNH1JoufQJo37YALc";
//        String accessKeySecret="HfncNr2nXaRSaagOg9vhlhYS1Mw7VN";

//        OSS ossClient=new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);

        FileInputStream inputStream = new FileInputStream("D:\\BaiduNetdiskDownload\\谷粒商城\\谷粒商城\\课件和文档\\基础篇\\资料\\pics\\f205d9c99a2b4b01.jpg");
        ossClient.putObject("pzkmall","hahaha.jpg",inputStream);
        ossClient.shutdown();
        System.out.println("上传完成");
    }

}
