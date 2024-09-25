package com.pxl.zkmall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.pxl.zkmall.product.entity.AttrEntity;
import com.pxl.zkmall.product.service.AttrAttrgroupRelationService;
import com.pxl.zkmall.product.service.AttrService;
import com.pxl.zkmall.product.service.CategoryService;
import com.pxl.zkmall.product.vo.AttrGroupRelationVo;
import com.pxl.zkmall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.pxl.zkmall.product.entity.AttrGroupEntity;
import com.pxl.zkmall.product.service.AttrGroupService;
import com.pxl.common.utils.PageUtils;
import com.pxl.common.utils.R;



/**
 * 属性分组
 *
 * @author pengxianlong
 * @email pengxianlon@gmail.com
 * @date 2024-05-16 21:22:05
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    AttrService attrService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    AttrAttrgroupRelationService relationService;


    /**
     *获取分类下所有分组&关联属性
     * /product/attrgroup/{catelogId}/withattr
     */
    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId){

        //1、查看当前分类下的所有属性分组
        //2、查看每个属性分组的所有属性
       List<AttrGroupWithAttrsVo> vos = attrGroupService.getAttrGroupWithAttrsByCatelogId(catelogId);
        return R.ok().put("data",vos);
    }

    //  /product/attrgroup/{attrgroupId}/attr/relation
    @GetMapping("/{attrgroupId}/attr/relation")
    public R attRelation(@PathVariable("attrgroupId") Long attrgroupId){
       List<AttrEntity> attrEntityList= attrService.getRelation(attrgroupId);
       return R.ok().put("data",attrEntityList);
    }

    //  /product/attrgroup/{attrgroupId}/noattr/relation
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R noAttRelation(@PathVariable("attrgroupId") Long attrgroupId,
                            @RequestParam Map<String, Object> params){
        PageUtils page = attrService.getNoRelation(params,attrgroupId);
        return R.ok().put("page",page);
    }

    /**
     * 添加属性与分组关联关系
     * /product/attrgroup/attr/relation
     */
    @PostMapping("/attr/relation")
    public R addAttrReation(@RequestBody List<AttrGroupRelationVo> vos){
        relationService.saveBatch(vos);
        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId){
        PageUtils page = attrGroupService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        Long catelogId = attrGroup.getCatelogId();
        Long [] path=categoryService.findCatelogPath(catelogId);

        attrGroup.setCatelogPath(path);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));
        return R.ok();
    }


    /**
     * 移除关联关系
     * @param vos
     * @return
     */
    //  /product/attrgroup/attr/relation/delete
    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody AttrGroupRelationVo[] vos){
        attrGroupService.deleteRelation(vos);
        return R.ok();
    }

}
