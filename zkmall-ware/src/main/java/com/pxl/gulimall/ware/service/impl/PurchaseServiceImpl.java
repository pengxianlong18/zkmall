package com.pxl.zkmall.ware.service.impl;

import com.pxl.common.constant.WareConstant;
import com.pxl.zkmall.ware.Vo.MergeVo;
import com.pxl.zkmall.ware.Vo.PurchaseDoneVo;
import com.pxl.zkmall.ware.Vo.PurchaseItemDoneVo;
import com.pxl.zkmall.ware.entity.PurchaseDetailEntity;
import com.pxl.zkmall.ware.service.PurchaseDetailService;
import com.pxl.zkmall.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pxl.common.utils.PageUtils;
import com.pxl.common.utils.Query;

import com.pxl.zkmall.ware.dao.PurchaseDao;
import com.pxl.zkmall.ware.entity.PurchaseEntity;
import com.pxl.zkmall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    PurchaseDetailService detailService;
    @Autowired
    WareSkuService wareSkuService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
                        .eq("status",WareConstant.PurchaseStatusEnum.CREATED.getCode())
                        .or().eq("status",WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        if (purchaseId == null){
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();

        }
        // TODO 确认采购单状态是0，1
        List<Long> items = mergeVo.getItems();
        Long finalPurchaseId = purchaseId;

        List<PurchaseDetailEntity> collect = detailService.getBaseMapper().selectBatchIds(items).stream().filter((entity -> {
            return entity.getStatus() == WareConstant.PurchaseDetailStatusEnum.CREATED.getCode()
                    || entity.getStatus() == WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode();
        })).map((item) -> {
            item.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            item.setPurchaseId(finalPurchaseId);
            return item;
        }).collect(Collectors.toList());

        detailService.updateBatchById(collect);

        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setId(purchaseId);
        purchase.setUpdateTime(new Date());
        this.updateById(purchase);
    }

    /**
     *
     * @param ids 采购单的id
     */
    @Override
    public void received(List<Long> ids) {
        //1、确认当前采购单是新建或者是已领取
        List<PurchaseEntity> collect = ids.stream().map((id) -> {
            PurchaseEntity byId = this.getById(id);
            return byId;
        }).filter((item) -> {
            if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                    item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                return true;
            }
            return false;
        }).map((item)->{
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());
        //2、改变采购单的状态
        this.updateBatchById(collect);
        //3、改变采购项的状态
        collect.forEach((item)->{
          List<PurchaseDetailEntity> detailEntities =
                  detailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> collect1 = detailEntities.stream().map((entity) -> {
                PurchaseDetailEntity entity1 = new PurchaseDetailEntity();
                entity1.setId(entity.getId());
                entity1.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return entity1;
            }).collect(Collectors.toList());
            detailService.updateBatchById(collect1);
        });
    }

    @Transactional
    @Override
    public void done(PurchaseDoneVo doneVo) {
        //1、改变采购项的状态
        Long id = doneVo.getId(); //采购单id

        //2、改变采购的状态
        Boolean flag=true;
        List<PurchaseDetailEntity> updates = new ArrayList<>();

        List<PurchaseItemDoneVo> items = doneVo.getItems();
        for (PurchaseItemDoneVo item:items){
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                flag=false;
                detailEntity.setStatus(item.getStatus());
            }else {
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                //3、改变商品库存 wms_ware_sku
                //从wms_purchase_detail查数据
                PurchaseDetailEntity entity = detailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
            }
            detailEntity.setId(item.getItemId());
            updates.add(detailEntity);
        }
        detailService.updateBatchById(updates);

        //变采购项的状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag?WareConstant.PurchaseStatusEnum.FINISH.getCode():WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);

    }

}