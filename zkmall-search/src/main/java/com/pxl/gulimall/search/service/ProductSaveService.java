package com.pxl.zkmall.search.service;

import com.pxl.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {
   boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
