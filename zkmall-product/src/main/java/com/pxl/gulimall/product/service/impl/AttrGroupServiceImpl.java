package com.pxl.zkmall.product.service.impl;

import com.pxl.zkmall.product.dao.AttrAttrgroupRelationDao;
import com.pxl.zkmall.product.entity.AttrAttrgroupRelationEntity;
import com.pxl.zkmall.product.entity.AttrEntity;
import com.pxl.zkmall.product.service.AttrService;
import com.pxl.zkmall.product.vo.AttrGroupRelationVo;
import com.pxl.zkmall.product.vo.AttrGroupWithAttrsVo;
import com.pxl.zkmall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pxl.common.utils.PageUtils;
import com.pxl.common.utils.Query;

import com.pxl.zkmall.product.dao.AttrGroupDao;
import com.pxl.zkmall.product.entity.AttrGroupEntity;
import com.pxl.zkmall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrService attrService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<AttrGroupEntity>();
       if(!StringUtils.isEmpty(key)){
           queryWrapper.and((obj)->{
               obj.eq("attr_group_id", key).or().like("attr_group_name", key);
           });
       }
        if (catelogId == 0){
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params), queryWrapper);
            return new PageUtils(page);
        }else {
            queryWrapper.eq("catelog_id", catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),queryWrapper);
            return new  PageUtils(page);
        }

    }

    /**
     * 移除属性的关联关系
     * @param vos
     */
    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationDao.deleteBatchRelation(entities);
    }

    /**
     * 根据分类id查出所有的分组以及这些组里面的属性
     * @param catelogId
     * @return
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(Long catelogId) {
        //1、查询所有分组
        List<AttrGroupEntity> groupEntities = this.list(
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

       // 2、查询所有属性
        List<AttrGroupWithAttrsVo> collect = groupEntities.stream().map((item) -> {
            AttrGroupWithAttrsVo attrVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(item, attrVo);
            List<AttrEntity> attrs = attrService.getRelation(attrVo.getAttrGroupId());
            attrVo.setAttrs(attrs);
            if (attrs != null){
                return attrVo;
            }
            return null;
        }).collect(Collectors.toList());
        collect.removeIf(Objects::isNull);
        return collect;
    }

    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        //1、查出当前spu对应的所有属性分组信息以及当前分组下的所有属性对应的值。

        List<SpuItemAttrGroupVo> vos = this.baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
        return vos;
    }

}