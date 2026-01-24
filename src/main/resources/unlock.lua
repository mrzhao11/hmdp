-- 比较线程标示与锁中的标示是否一致
-- 使用lua脚本保证原子操作
-- KEYS[1] 锁的key
-- ARGV[1] 线程标示
-- 如果一致则删除锁并返回1，否则返回0
if(redis.call('get', KEYS[1]) ==  ARGV[1]) then
    -- 释放锁 del key
    return redis.call('del', KEYS[1])
end
return 0