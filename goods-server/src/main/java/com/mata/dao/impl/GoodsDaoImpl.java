package com.mata.dao.impl;

import cn.hutool.json.JSONUtil;
import com.mata.dao.GoodsDao;
import com.mata.pojo.Goods;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GoodsDaoImpl implements GoodsDao {
    @Autowired
    private RestHighLevelClient client;

    /**
     * 添加和修改商品
     *
     * @param goods 商品对象
     */
    public void addGoods(Goods goods) throws IOException {
        String goodsToJson = JSONUtil.toJsonStr(goods);
        //1：准备Request对象
        IndexRequest request = new IndexRequest("goods").id(goods.getId().toString());
        //2：准备Json
        request.source(goodsToJson, XContentType.JSON);
        //3:发请求
        client.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 根据id查商品
     *
     * @param id 商品id
     */
    @Override
    public Goods getGoodById(Long id) throws IOException {
        //1：准备Request对象
        GetRequest request = new GetRequest("goods", id.toString());
        //2:发请求 获取响应
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        //3:解析操作
        String goodJson = response.getSourceAsString();
        Goods good = JSONUtil.toBean(goodJson, Goods.class);
        return good;
    }

    /**
     * 根据keyword查商品
     *
     * @param keyword 搜索关键词
     * @param page    当前页数
     */
    @Override
    public List<Goods> getGoodsByKeyword(String keyword, Integer page) throws IOException {
        //创建请求
        SearchRequest request = new SearchRequest("goods");
        //请求为空查所有
        if (keyword == null || "".equals(keyword)) {
            request.source()
                    .query(QueryBuilders.matchAllQuery());
        } else {
            request.source().query(QueryBuilders.matchQuery("all", keyword));
        }
        //分页 一页20个
        request.source().from((page - 1) * 20).size(20);
        //发请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        return handleResponse(response);
    }


    private List<Goods> handleResponse(SearchResponse response) {
        //解析操作
        SearchHits searchHits = response.getHits();
        //查询结果数组
        SearchHit[] hits = searchHits.getHits();
        List<Goods> goodList = new ArrayList<>();
        for (SearchHit hit : hits) {
            //4.3获得source
            String json = hit.getSourceAsString();
            Goods good = JSONUtil.toBean(json, Goods.class);
            if (good.getStock()>0){
                goodList.add(good);
            }
        }
        return goodList;
    }

    /**
     * 修改库存
     *
     * @param id 商品id
     * @param stock 当前库存
     */
    @Override
    public void updateStockGoods(Long id,Integer stock)  throws IOException {
        //1：准备Request对象
        UpdateRequest request = new UpdateRequest("goods", id.toString());
        //2:准备参数
        request.doc(
                "stock",stock
        );
        //3：发送请求
        client.update(request,RequestOptions.DEFAULT);
    }

    /**
     * 删除商品
     * @param id 商品id
     */
    @Override
    public void deleteGood(Long id) throws IOException {
        //1：准备Request对象
        DeleteRequest request = new DeleteRequest("goods", id.toString());
        //2:发送请求
        client.delete(request,RequestOptions.DEFAULT);
    }

}

