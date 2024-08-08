-- redis限流脚本
-- key参数
local key = KEYS[1]
-- 限流的次数
local limitCount = tonumber(ARGV[1])
-- 限流的时间
local limitTime = tonumber(ARGV[2])
-- 获取当前时间
local currentCount = redis.call('get', key)
-- if 获取key的当前数 > limitCount 则返回最大值
if currentCount and tonumber(currentCount) > limitCount then
    return tonumber(currentCount)
end
-- key 自增1
currentCount = redis.call("incr",key)
-- if key的值 == 1 设置过期限流过期时间
if tonumber(currentCount) == 1 then
    redis.call("expire",key,limitTime)
end
-- 返回key的值
return tonumber(currentCount)

