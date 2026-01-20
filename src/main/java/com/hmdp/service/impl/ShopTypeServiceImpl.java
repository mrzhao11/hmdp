package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE_KEY;
import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE_TTL;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryTypeList() {
        String key = CACHE_SHOP_TYPE_KEY + UUID.randomUUID().toString();
        // 1. 从redis中查询商铺类型缓存
        String shopTypeJSON = stringRedisTemplate.opsForValue().get(key);

        List<ShopType> shopTypeList;
        // 2. 存在，直接返回
        if (StrUtil.isNotBlank(shopTypeJSON)) {
            // 2.1 命中
            shopTypeList = JSONUtil.toList(shopTypeJSON, ShopType.class);
            return Result.ok(shopTypeList);
        }

        // 3. 不存在，查询数据库
        shopTypeList = this.list(new LambdaQueryWrapper<ShopType>().orderByAsc(ShopType::getSort));

        // 4. 数据库不存在，返回错误
        if (shopTypeList == null || shopTypeList.size() == 0) {
            return Result.fail("商铺类型不存在");
        }

        // 5. 数据库存在，写入redis缓存
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shopTypeList),CACHE_SHOP_TYPE_TTL, TimeUnit.MINUTES);

        // 6. 返回
        return Result.ok(shopTypeList);
    }

}
