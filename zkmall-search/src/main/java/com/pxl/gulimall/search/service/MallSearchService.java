package com.pxl.zkmall.search.service;

import com.pxl.zkmall.search.vo.SearchParam;
import com.pxl.zkmall.search.vo.SearchResult;

public interface MallSearchService {
    /**
     *
     * @param param 检索的所有参数
     * @return  返回检索的结果
     */
    SearchResult search(SearchParam param);
}
