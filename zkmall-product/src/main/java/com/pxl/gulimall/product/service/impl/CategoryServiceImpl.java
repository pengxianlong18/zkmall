package com.pxl.zkmall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.pxl.zkmall.product.service.CategoryBrandRelationService;
import com.pxl.zkmall.product.vo.Catalog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pxl.common.utils.PageUtils;
import com.pxl.common.utils.Query;

import com.pxl.zkmall.product.dao.CategoryDao;
import com.pxl.zkmall.product.entity.CategoryEntity;
import com.pxl.zkmall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

import static com.alibaba.fastjson.JSON.*;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryDao categoryDao;
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查询所有分类
        List<CategoryEntity> entityList = categoryDao.selectList(null);
        //2、组装成父子的树形结构
        //2.1、找到一级分类
        List<CategoryEntity> level1Menus = entityList.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentCid() == 0;
        }).map((menu) -> {
            menu.setChildren(getChildrens(menu, entityList));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return level1Menus;
    }

    //删除
    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1、检查当前删除的菜单，是否被别的地方引用
        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return (Long[]) parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有数据
     *   @CacheEvict 失效模式
     * @param category
     */

//    @Caching(evict = {
//            @CacheEvict(value ="category",key = "'getLevel1Categorys'"),
//            @CacheEvict(value = "category",key = "'getCatalogJson'")
//    })
    @CacheEvict(value = "category",allEntries = true)
    @Override
    @Transactional
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    @Cacheable(value = {"category"},key = "#root.method.name",sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("查询数据库");
        List<CategoryEntity> categoryEntities = baseMapper
                .selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));

        return categoryEntities;
    }

    @Cacheable(value = "category",key = "#root.method.name")
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        System.out.println("查询了数据库");

        //   1、将数据库的多次查询变为1次
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //1、查询所有1级分类数据
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        //2、封装数据
        Map<String, List<Catalog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(
                k -> k.getCatId().toString(), v -> {
                    //1、每一个的一级分类，查到这个一级分类的二级分类
                    List<CategoryEntity> categoryLevel1 = getParent_cid(selectList, v.getCatId());
                    //2、封装上面的数据
                    List<Catalog2Vo> catalog2VoList = null;
                    if (categoryLevel1 != null) {
                        catalog2VoList = categoryLevel1.stream().map(l2 -> {
                            Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(),
                                    null, l2.getCatId().toString(), l2.getName());
                            // 抄当前二级分类的三级分类封装成vo
                            List<CategoryEntity> categoryLevel3 = getParent_cid(selectList, l2.getCatId());
                            if (categoryLevel3 != null) {
                                List<Catalog2Vo.Catalog3vo> catalog3vos = categoryLevel3.stream().map(l3 -> {
                                    Catalog2Vo.Catalog3vo catalog3vo = new Catalog2Vo.Catalog3vo(l2.getCatId().toString(),
                                            l3.getCatId().toString(), l3.getName());
                                    return catalog3vo;
                                }).collect(Collectors.toList());
                                catalog2Vo.setCatalog3List(catalog3vos);
                            }

                            return catalog2Vo;
                        }).collect(Collectors.toList());
                    }
                    return catalog2VoList;
                }));
        return parent_cid;
    }

    // TODO 产生堆外内存溢出 ：OutOfDirectMemoryError
    // springboot2.0以后默认使用lettuce作为操作redis客户端。使用netty进行网络通信
    // netty是异步的。netty的bug导致堆外内存溢出。解决方案：1、升级lettuce客户端。2、切换使用jedis
    public Map<String, List<Catalog2Vo>> getCatalogJson2() {
        /**
         *  1、空结果缓存：解决缓存穿透
         *  2、设置随机过期时间：解决缓存雪崩
         *  3、加锁：解决缓存击穿
         */
        // 1、加入缓存
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            System.out.println("缓存不命中，直接查询数据库");
            Map<String, List<Catalog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithRedissonLock();

        }
        System.out.println("缓存命中，直接返回数据");
        Map<String, List<Catalog2Vo>> result = parseObject(catalogJson,
                new TypeReference<Map<String, List<Catalog2Vo>>>() {
                });

        return result;
    }

    /**
     * 缓存里面的数据如何和数据库中的数据保持一致？
     *  缓存数据一致性解决方案：1、双写模式 2、失效模式
     *  1、双写模式：读写都写数据库，但是性能差
     *  2、失效模式：读请求从缓存里面查不到数据，则去数据库查询，然后将查询到的数据写入到缓存中。
     *  2.1、读请求过来，先去缓存中查，如果查不到，则去数据库中查询，然后将查询的结果放入到缓存中。
     *  2.2、更新数据库的某一条数据的时候，删除缓存中对应的这条数据。
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedissonLock() {


        RLock lock = redissonClient.getLock("catalogJson-lock");
        lock.lock();

        Map<String, List<Catalog2Vo>> dataFromDb;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }

        return dataFromDb;

    }

    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisLock() {

        //1、分布式锁，取redis占坑
        String uuid = UUID.randomUUID().toString();
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功");
            // 加锁成功
            // 设置过期时间，必须和加锁是同步的，原子操作防止死锁
            // stringRedisTemplate.expire("lock",30,TimeUnit.SECONDS);
            Map<String, List<Catalog2Vo>> dataFromDb;
            try {
                dataFromDb = getDataFromDb();
            } finally {
                String s = "if redis.call('get', KEYS[1]')==ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                Long lock1 = stringRedisTemplate.execute(new DefaultRedisScript<Long>(s, Long.class),
                        Arrays.asList("lock"), uuid);
            }
            //stringRedisTemplate.delete("lock");
            // 获取值对比+对比成功删除=原子操作，可以使用lua脚本解锁
//            String lockValue = stringRedisTemplate.opsForValue().get("lock");
//            if (uuid.equals(lockValue)){
//                stringRedisTemplate.delete("lock");
//            }
            return dataFromDb;
        } else {
            // 加锁失败，重试
            System.out.println("获取分布式锁失败，等待重试");
            try {
                Thread.sleep(300);
            } catch (Exception e) {

            }
            return getCatalogJsonFromDbWithRedisLock();
        }
    }

    private Map<String, List<Catalog2Vo>> getDataFromDb() {
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {
            Map<String, List<Catalog2Vo>> result = parseObject(catalogJson,
                    new TypeReference<Map<String, List<Catalog2Vo>>>() {
                    });
            return result;
        }
        System.out.println("查询了数据库");

        //   1、将数据库的多次查询变为1次
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        //1、查询所有1级分类数据
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        //2、封装数据
        Map<String, List<Catalog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(
                k -> k.getCatId().toString(), v -> {
                    //1、每一个的一级分类，查到这个一级分类的二级分类
                    List<CategoryEntity> categoryLevel1 = getParent_cid(selectList, v.getCatId());
                    //2、封装上面的数据
                    List<Catalog2Vo> catalog2VoList = null;
                    if (categoryLevel1 != null) {
                        catalog2VoList = categoryLevel1.stream().map(l2 -> {
                            Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(),
                                    null, l2.getCatId().toString(), l2.getName());
                            // 抄当前二级分类的三级分类封装成vo
                            List<CategoryEntity> categoryLevel3 = getParent_cid(selectList, l2.getCatId());
                            if (categoryLevel3 != null) {
                                List<Catalog2Vo.Catalog3vo> catalog3vos = categoryLevel3.stream().map(l3 -> {
                                    Catalog2Vo.Catalog3vo catalog3vo = new Catalog2Vo.Catalog3vo(l2.getCatId().toString(),
                                            l3.getCatId().toString(), l3.getName());
                                    return catalog3vo;
                                }).collect(Collectors.toList());
                                catalog2Vo.setCatalog3List(catalog3vos);
                            }

                            return catalog2Vo;
                        }).collect(Collectors.toList());
                    }
                    return catalog2VoList;
                }));

        stringRedisTemplate.opsForValue().set("catalogJson",
                toJSONString(parent_cid), 1, TimeUnit.DAYS);
        return parent_cid;
    }


    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithLocalLock() {

        // 只要是一把锁，就能锁住需要这个锁的所有进程
        // 1、  synchronized (this) ：SpringBoot所有组件在容器中都是单例的
        // TODO 本地锁 synchronized JUC（Lock） 在分布式情况下，想要锁住所有，必须需要使用分布式锁
        // 2、  分布式锁  redisson  zookeeper  redis  (lua脚本)
        synchronized (this) {
            // 得到锁以后，应该取缓存中确认异常，如果没有才需要继续查询
            return getDataFromDb();
        }
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
//        return baseMapper.selectList(
//                new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));

        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid)
                .collect(Collectors.toList());
        return collect;
    }


    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

}