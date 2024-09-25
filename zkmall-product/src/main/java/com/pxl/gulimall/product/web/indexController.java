package com.pxl.zkmall.product.web;

import com.pxl.zkmall.product.entity.CategoryEntity;
import com.pxl.zkmall.product.service.CategoryService;
import com.pxl.zkmall.product.vo.Catalog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@Controller
public class indexController {

    @Autowired
    CategoryService categoryService;


    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
    //TODO 1、查出首页1级分类
     List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();

     model.addAttribute("categorys",categoryEntities);
        return "index";
    }

    // index/catalog.json
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public  Map<String, List<Catalog2Vo>> getCatalogJson(){
        Map<String, List<Catalog2Vo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;
    }

    @GetMapping("/write")
    @ResponseBody
    public String write(){
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        String s="";
        RLock rLock = lock.writeLock();

        try{
            rLock.lock();
            System.out.println("写锁加锁成功。。。。"+Thread.currentThread().getId());
            s = UUID.randomUUID().toString();
            Thread.sleep(10000);
            redisTemplate.opsForValue().set("writeValue",s);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            rLock.unlock();
            System.out.println("写锁释放成功。。。。"+Thread.currentThread().getId());
        }
        return s;
    }

    @GetMapping("/read")
    @ResponseBody
    public String read(){
        RReadWriteLock writeLock = redissonClient.getReadWriteLock("rw-lock");
        RLock rLock = writeLock.writeLock();
        String s = "";
        rLock.lock();
        try {
            s = redisTemplate.opsForValue().get("writeValue");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return s;
    }

    /**
     * 车库停车
     * 3车位
     */
    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.acquire();//获取一个信号量，占一个信号量
        return "ok";
    }

    @GetMapping("/go")
    @ResponseBody
    public String go() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release();
        return "ok";
    }

    /**
     * 放假了，锁门
     */
    @GetMapping("/lockDoor")
    @ResponseBody
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(3);
        door.await();//等待闭锁都完成

        return "放假了";
    }

    @GetMapping("/gogogo/{id}")
    @ResponseBody
    public String lockDoor(@PathVariable("id") Long id) throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown();

        return id+"班的人都走了";


    }
}

