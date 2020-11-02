package com.xinyiglass.rabbit.toolredis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public final RedisCommon redisCommon = new RedisCommon();
    public final RedisString redisString = new RedisString();
    public final RedisHash redisHash = new RedisHash();
    public final RedisSet redisSet = new RedisSet();
    public final RedisZSet redisZSet = new RedisZSet();
    public final RedisList redisList = new RedisList();

    private final static Long CODE_EXCEPTION = -99L;

    /**
     * 拼接参数列表进行打印
     * @param params
     */
    private static String makeParamsString(Object... params)
    {
        StringBuilder sb = new StringBuilder();
        if (params != null && params.length > 0){
            for(Object param : params){
                if (param == null)
                    continue;
                sb.append(param.getClass() + ":" + param);
                sb.append(System.getProperty("line.separator"));
            }
            return sb.toString();
        }
        return "";
    }

    /*TODO：null when used in pipeline / transaction.还没搞懂*/

    // =============================Common=============================
    class RedisCommon{
        /**
         * 对指定key设置有效时间
         * @param key 不能为null，也不可为""
         * @param time 有效时间，单位为秒，必须大于0
         * @return true成功，false失败/异常
         */
        public Boolean expire(String key, long time) {
            return expire(key, time, TimeUnit.SECONDS);
        }

        /**
         * 对指定key设置有效时间
         * @param key 不能为null，也不可为""
         * @param time 有效时间，单位为unit，必须大于0
         * @param unit 有效时间单位
         * @return true成功，false失败/异常
         */
        public Boolean expire(String key, long time, TimeUnit unit) {
            try {
                if (!StringUtils.isEmpty(key) && time > 0)
                    return redisTemplate.expire(key, time, unit);
            } catch (Exception e) {
                log.error(makeParamsString(key, time, unit), e);
            }
            return false;
        }

        /**
         * 根据key获取有效时间
         * @param key 不能为null，也不可为""
         * @return 有效时间，单位为秒。0表示永久有效，-1表示没有这个key，-2表示这个key刚失效不久，-99表示异常
         */
        public Long getExpire(String key) {
            return getExpire(key, TimeUnit.SECONDS);
        }

        /**
         * 根据key获取有效时间
         * @param key 不能为null，也不可为""
         * @param unit 指定有效时间单位
         * @return 有效时间，单位为unit。0表示永久有效，-1表示没有这个key，-2表示这个key刚失效不久，-99表示异常
         */
        public Long getExpire(String key, TimeUnit unit) {
            try {
                if (!StringUtils.isEmpty(key))
                    return redisTemplate.getExpire(key, unit);
            } catch (Exception e) {
                log.error(makeParamsString(key, unit), e);
            }
            return CODE_EXCEPTION;
        }

        /**
         * 判断key是否存在
         * @param key 不能为null，也不可为""
         * @return true存在，false不存在/异常
         */
        public Boolean hasKey(String key) {
            try {
                if (!StringUtils.isEmpty(key))
                    return redisTemplate.hasKey(key);
            } catch (Exception e) {
                log.error(makeParamsString(key), e);
            }
            return false;
        }

        /**
         * 删除缓存，单个key
         * @param key 不能为null，也不可为""
         * @return true成功，false失败/异常
         */
        public Boolean delOne(String key) {
            try {
                if (!StringUtils.isEmpty(key))
                    return redisTemplate.delete(key);
            } catch (Exception e) {
                log.error(makeParamsString(key), e);
            }
            return false;
        }

        /**
         * 删除缓存，多个key
         * @param keys 不能为null，也不可为空，每个元素不能为null，也不可为""
         * @return 成功删除的数量，-99表示异常
         */
        public Long delMany(String... keys) {
            try {
                if (keys != null && keys.length > 0){
                    List list = (List)(
                            CollectionUtils.arrayToList(keys)
                                    .stream()
                                    .filter(key -> !StringUtils.isEmpty(key))
                                    .collect(Collectors.toList())
                    );
                    return redisTemplate.delete(list);
                }
            } catch (Exception e) {
                log.error(makeParamsString(keys), e);
            }
            return CODE_EXCEPTION;
        }
    }
    // =============================String=============================
    class RedisString{
        /**
         * 根据key获取缓存值
         * @param key 不能为null，也不可为""
         * @return 缓存值，null表示没有值，-99表示异常
         */
        public Object get(String key) {
            try {
                if (!StringUtils.isEmpty(key))
                    return redisTemplate.opsForValue().get(key);
            } catch (Exception e) {
                log.error(makeParamsString(key), e);
            }
            return CODE_EXCEPTION;
        }

        /**
         * 放入键值对，永久有效
         * @param key 不能为null，也不可为""
         * @param value 不能为null，也不可为""
         * @return true成功，false失败/异常
         */
        public Boolean set(String key, Object value) {
            try {
                if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value))
                    return false;
                if (value.getClass().equals(String.class))
                    stringRedisTemplate.opsForValue().set(key, (String)value);
                else
                    redisTemplate.opsForValue().set(key, value);
                return true;
            } catch (Exception e) {
                log.error(makeParamsString(key, value), e);
            }
            return false;
        }

        /**
         * 放入键值对，并设置有效时间
         * @param key 不能为null，也不可为""
         * @param value 不能为null，也不可为""
         * @param time 有效时间，单位为秒，必须大于0
         * @return true成功，false失败/异常
         */
        public Boolean setExp(String key, Object value, long time) {
            return setExp(key, value, time, TimeUnit.SECONDS);
        }

        /**
         * 放入键值对，并设置有效时间
         * @param key 不能为null，也不可为""
         * @param value 不能为null，也不可为""
         * @param time 有效时间，单位为unit，必须大于0
         * @param unit 有效时间单位
         * @return true成功，false失败/异常
         */
        public Boolean setExp(String key, Object value, long time, TimeUnit unit) {
            try {
                if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value) || time <= 0)
                    return false;
                if (value.getClass().equals(String.class)){
                    stringRedisTemplate.opsForValue().set(key, (String)value, time, unit);
                }else {
                    redisTemplate.opsForValue().set(key, value, time, unit);
                }
                return true;
            } catch (Exception e) {
                log.error(makeParamsString(key, value, time, unit), e);
            }
            return false;
        }

        /**
         * 递增
         * @param key 不能为null，也不可为""
         * @param delta 要增加几，可以是正负整数
         * @return 递增结果
         */
        public Long incr(String key, long delta) {
            return stringRedisTemplate.opsForValue().increment(key, delta);
        }

    }
    // =============================Hash=============================
    class RedisHash{
        /**
         * 根据key和item获取缓存值
         * @param key 不能为null，也不可为""
         * @param item 不能为null，也不可为""
         * @return 缓存值，null表示没有值，-99表示异常
         */
        public Object hget(String key, String item) {
            try {
                if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(item))
                    return redisTemplate.opsForHash().get(key, item);
            } catch (Exception e) {
                log.error(makeParamsString(key, item), e);
            }
            return CODE_EXCEPTION;
        }

        /**
         * 放入key及其对应的键值对
         * @param key 不能为null，也不可为""
         * @param item 不能为null，也不可为""
         * @param value 不能为null，也不可为""
         * @return true成功，false失败/异常
         */
        public Boolean hset(String key, String item, Object value) {
            try {
                if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(item) && !StringUtils.isEmpty(value)){
                    redisTemplate.opsForHash().put(key, item, value);
                    return true;
                }
            } catch (Exception e) {
                log.error(makeParamsString(key, item, value), e);
            }
            return false;
        }

        /**
         * 放入key及其对应的键值对，并设置有效时间
         * @param key 不能为null，也不可为""
         * @param item 不能为null，也不可为""
         * @param value 不能为null，也不可为""
         * @param time 有效时间，单位为秒，必须大于0
         * @return true成功，false失败/异常
         */
        public Boolean hsetExp(String key, String item, Object value, long time) {
            return hsetExp(key, item, value, time, TimeUnit.SECONDS);
        }

        /**
         * 放入key及其对应的键值对，并设置有效时间
         * @param key 不能为null，也不可为""
         * @param item 不能为null，也不可为""
         * @param value 不能为null，也不可为""
         * @param time 有效时间，单位为unit，必须大于0
         * @param unit 有效时间单位
         * @return true成功，false失败/异常
         */
        public Boolean hsetExp(String key, String item, Object value, long time, TimeUnit unit) {
            try {
                if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(item) && !StringUtils.isEmpty(value) && time > 0){
                    redisTemplate.opsForHash().put(key, item, value);
                    redisCommon.expire(key, time, unit);
                    return true;
                }
            } catch (Exception e) {
                log.error(makeParamsString(key, item, value, time, unit), e);
            }
            return false;
        }

        /**
         * 获取key对应的所有键值对
         * @param key 不能为null，也不可为""
         * @return key对应的所有键值对，null表示没有键值对/异常
         */
        public Map<Object, Object> hmget(String key) {
            try {
                if (!StringUtils.isEmpty(key))
                    return redisTemplate.opsForHash().entries(key);
            } catch (Exception e) {
                log.error(makeParamsString(key), e);
            }
            return null;
        }

        /**
         * 放入key及其对应的若干键值对
         * @param key 不能为null，也不可为""
         * @param map 不能为null，也不可为空
         * @return true成功，false失败/异常
         */
        public Boolean hmset(String key, Map<String, Object> map) {
            try {
                if (!StringUtils.isEmpty(key) && !CollectionUtils.isEmpty(map)){
                    redisTemplate.opsForHash().putAll(key, map);
                    return true;
                }
            } catch (Exception e) {
                log.error(makeParamsString(key, map), e);
            }
            return false;
        }

        /**
         * 放入key及其对应的若干键值对，并设置有效时间
         * @param key 不能为null，也不可为""
         * @param map 不能为null，也不可为空
         * @param time 有效时间，单位为秒，必须大于0
         * @return true成功，false失败/异常
         */
        public Boolean hmsetExp(String key, Map<String, Object> map, long time) {
            return hmsetExp(key, map, time, TimeUnit.SECONDS);
        }

        /**
         * 放入key及其对应的若干键值对，并设置有效时间
         * @param key 不能为null，也不可为""
         * @param map 不能为null，也不可为空
         * @param time 有效时间，单位为unit，必须大于0
         * @param unit 有效时间单位
         * @return true成功，false失败/异常
         */
        public Boolean hmsetExp(String key, Map<String, Object> map, long time, TimeUnit unit) {
            try {
                if (!StringUtils.isEmpty(key) && !CollectionUtils.isEmpty(map) && time > 0){
                    redisTemplate.opsForHash().putAll(key, map);
                    redisCommon.expire(key, time, unit);
                    return true;
                }
            } catch (Exception e) {
                log.error(makeParamsString(key, map, time, unit), e);
            }
            return false;
        }

        /**
         * 删除key和若干item对应的缓存值
         * @param key 不能为null，也不可为""
         * @param items 不能为null，也不可为空，每个元素不能为null，也不可为""
         * @return 成功删除的数量，-99表示异常
         */
        public Long hdel(String key, Object... items) {
            try {
                if (!StringUtils.isEmpty(key) && items != null && items.length > 0){
                    List list = (List)CollectionUtils.arrayToList(items).stream()
                            .filter(item -> !StringUtils.isEmpty(item))
                            .collect(Collectors.toList());
                    return redisTemplate.opsForHash().delete(key, list.toArray());
                }
            } catch (Exception e) {
                log.error(makeParamsString(key, items), e);
            }
            return CODE_EXCEPTION;
        }

        /**
         * 判断是否有key和item对应的缓存值
         * @param key 不能为null，也不可为""
         * @param item 不能为null，也不可为""
         * @return true存在，false不存在/异常
         */
        public Boolean hhasKey(String key, String item) {
            try {
                if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(item))
                    return redisTemplate.opsForHash().hasKey(key, item);
            } catch (Exception e) {
                log.error(makeParamsString(key, item), e);
            }
            return false;
        }

        /**
         * 递增，该方法目前报错不可用
         * @param key 不能为null，也不可为""
         * @param item 不能为null，也不可为""
         * @param by 要增加几，可以是正负浮点数
         * @return 递增结果
         */
        public Double hincr(String key, String item, double by) {
            return redisTemplate.opsForHash().increment(key, item, by);
        }

    }
    // =============================Set=============================
    class RedisSet{
        /**
         * 获取key对应的set
         * @param key 不能为null，也不可为""
         * @return 对应的set，空的LinkedHashSet表示没有对应set/异常
         */
        public Set<Object> sget(String key) {
            try {
                if (!StringUtils.isEmpty(key))
                    return redisTemplate.opsForSet().members(key);
            } catch (Exception e) {
                log.error(makeParamsString(key), e);
            }
            return new LinkedHashSet<>();
        }

        /**
         * 往key对应的set里放入若干缓存值
         * @param key 不能为null，也不可为""
         * @param values 不能为null，也不可为空，每个元素不能为null，也不可为""
         * @return 成功添加的数量，-99表示异常
         */
        public Long sset(String key, Object... values) {
            try {
                if (!StringUtils.isEmpty(key) && values != null && values.length > 0){
                    List list = (List)CollectionUtils.arrayToList(values).stream()
                            .filter(value -> !StringUtils.isEmpty(value))
                            .collect(Collectors.toList());
                    return redisTemplate.opsForSet().add(key, list.toArray());
                }
            } catch (Exception e) {
                log.error(makeParamsString(key, values), e);
            }
            return CODE_EXCEPTION;
        }

        /**
         * 往key对应的set里放入若干缓存值，并设置有效时间
         * @param key 不能为null，也不可为""
         * @param time 有效时间，单位为秒，必须大于0
         * @param values 不能为null，也不可为空，每个元素不能为null，也不可为""
         * @return 成功添加的数量，-99表示异常
         */
        public Long ssetExp(String key, long time, Object... values) {
            return ssetExp(key, time, TimeUnit.SECONDS, values);
        }

        /**
         * 往key对应的set里放入若干缓存值，并设置有效时间
         * @param key 不能为null，也不可为""
         * @param time 有效时间，单位为unit，必须大于0
         * @param unit 有效时间单位
         * @param values 不能为null，也不可为空，每个元素不能为null，也不可为""
         * @return 成功添加的数量，-99表示异常
         */
        public Long ssetExp(String key, long time, TimeUnit unit, Object... values) {
            try {
                if (!StringUtils.isEmpty(key) && time > 0 && values != null && values.length > 0){
                    List list = (List)CollectionUtils.arrayToList(values).stream()
                            .filter(value -> !StringUtils.isEmpty(value))
                            .collect(Collectors.toList());
                    Long count = redisTemplate.opsForSet().add(key, list.toArray());
                    redisCommon.expire(key, time, unit);
                    return count;
                }
            } catch (Exception e) {
                log.error(makeParamsString(key, time, unit, values), e);
            }
            return CODE_EXCEPTION;
        }

        /**
         * 获取key对应的set的大小
         * @param key 不能为null，也不可为""
         * @return set的大小，-99表示异常
         */
        public Long sgetSize(String key) {
            try {
                if (!StringUtils.isEmpty(key))
                    return redisTemplate.opsForSet().size(key);
            } catch (Exception e) {
                log.error(makeParamsString(key), e);
            }
            return CODE_EXCEPTION;
        }

        /**
         * 判断key对应的set里是否存在value
         * @param key 不能为null，也不可为""
         * @param value 不能为null，也不可为""
         * @return true存在，false不存在/异常
         */
        public Boolean shasKey(String key, Object value) {
            try {
                if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value))
                    return redisTemplate.opsForSet().isMember(key, value);
            } catch (Exception e) {
                log.error(makeParamsString(key, value), e);
            }
            return false;
        }

        /**
         * 从key对应的set里移除若干缓存值
         * @param key 不能为null，也不可为""
         * @param values 不能为null，也不可为空，每个元素不能为null，也不可为""
         * @return 成功移除的数量，-99表示异常
         */
        public Long svaluesRemove(String key, Object... values) {
            try {
                if (!StringUtils.isEmpty(key) && values != null && values.length > 0){
                    List list = (List)CollectionUtils.arrayToList(values).stream()
                            .filter(value -> !StringUtils.isEmpty(value))
                            .collect(Collectors.toList());
                    return redisTemplate.opsForSet().remove(key, list.toArray());
                }
            } catch (Exception e) {
                log.error(makeParamsString(key, values), e);
            }
            return CODE_EXCEPTION;
        }
    }
    // =============================ZSet=============================
    class RedisZSet{
        /**
         * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。
         * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，
         * 并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。
         * @param key 不能为null
         * @param member 不能为null
         * @param score
         * @return true表示第一次添加，false表示更新
         */
        public Boolean zadd(String key, Object member, double score) {
            return redisTemplate.opsForZSet().add(key, member, score);
        }

        /**
         * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。
         * 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，
         * 并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。
         * @param key 不能为null
         * @param scoreMembers 不能为null
         * @return 成功添加的数量，不包括更新的
         */
        public Long zadd(String key, Map<Object, Double> scoreMembers) {
            Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
            scoreMembers.forEach((k, v) -> {
                tuples.add(new DefaultTypedTuple<>(k, v));
            });
            return redisTemplate.opsForZSet().add(key, tuples);
        }

        /**
         * 返回有序集 key 中，成员 member 的 score 值。
         * 如果 member 元素不是有序集 key 的成员，或 key 不存在，返回 nil 。
         * @param key 不能为null
         * @param member 不能为null
         * @return 对应的score值
         */
        public Double zscore(String key, Object member) {
            return redisTemplate.opsForZSet().score(key, member);
        }

        /**
         * 返回有序集 key 的基数。
         * @param key 不能为null
         * @return key对应的集合的大小
         */
        public Long zcard(String key) {
            return redisTemplate.opsForZSet().zCard(key);
        }

        /**
         * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。
         * 关于参数 min 和 max 的详细使用方法，请参考 ZRANGEBYSCORE 命令。
         * @param key 不能为null
         * @param min
         * @param max
         * @return score值在min和max之间的数量
         */
        public Long zcount(String key, double min, double max) {
            return redisTemplate.opsForZSet().count(key, min, max);
        }

        /**
         * 为有序集 key 的成员 member 的 score 值加上增量 increment 。
         * @param key 不能为null
         * @param member 不能为null
         * @param score 正负浮点数
         * @return 递增结果（存在精度误差）
         */
        public Double zincrBy(String key, Object member, double score) {
            return redisTemplate.opsForZSet().incrementScore(key, member, score);
        }

        /**
         * 返回有序集 key 中，指定区间内的成员。
         * 其中成员的位置按 score 值递增(从小到大)来排序。
         * 具有相同 score 值的成员按字典序(lexicographical order )来排列。
         * 如果你需要成员按 score 值递减(从大到小)来排列，请使用 ZREVRANGE 命令。
         * 如果start大于end将返回空，如果start或end为负数，则翻译成倒数第几的元素的下标
         * @param key 不能为null
         * @param start 起始下标
         * @param end 终止下标
         * @return 下标在start和end之间的元素
         */
        public Set zrange(String key, long start, long end) {
            return redisTemplate.opsForZSet().range(key, start, end);
        }

        /**
         * 返回有序集 key 中，指定区间内的成员。
         * 其中成员的位置按 score 值递减(从大到小)来排列。
         * 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order)排列。
         * 除了成员按 score 值递减的次序排列这一点外， ZREVRANGE 命令的其他方面和 ZRANGE 命令一样。
         * 如果start大于end将返回空，如果start或end为负数，则翻译成倒数第几的元素的下标
         * @param key 不能为null
         * @param start 起始下标
         * @param end 终止下标
         * @return 下标在start和end之间的元素
         */
        public Set zreverseRange(String key, long start, long end) {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        }

        /**
         * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
         * 有序集成员按 score 值递增(从小到大)次序排列。
         * @param key 不能为null
         * @param min 起始score
         * @param max 终止score
         * @return score值介于min和max之间
         */
        public Set zrangeByScore(String key, double min, double max) {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        }

        /**
         * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。
         * 排名以 0 为底，也就是说， score 值最小的成员排名为 0 。
         * 使用 ZREVRANK 命令可以获得成员按 score 值递减(从大到小)排列的排名。
         * @param key 不能为null
         * @param member 不能为null
         * @return 下标
         */
        public Long zrank(String key, Object member) {
            return redisTemplate.opsForZSet().rank(key, member);
        }

        /**
         * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序。
         * 排名以 0 为底，也就是说， score 值最大的成员排名为 0 。
         * 使用 ZRANK 命令可以获得成员按 score 值递增(从小到大)排列的排名。
         * @param key 不能为null
         * @param member 不能为null
         * @return 下标
         */
        public Long zreverseRank(String key, Object member) {
            return redisTemplate.opsForZSet().reverseRank(key, member);
        }

        /**
         * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。
         * 当 key 存在但不是有序集类型时，返回一个错误。
         * @param key 不能为null
         * @param members 不能为null
         * @return 成功删除的数量
         */
        public Long zremove(String key, Object... members) {
            return redisTemplate.opsForZSet().remove(key, members);
        }

    }
    // =============================List=============================
    class RedisList{

        /**
         * 将一个或多个值 value 插入到列表 key 的表头
         * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头： 比如说，
         * 对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，
         * 这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。
         * 如果 key 不存在，一个空列表会被创建并执行 LPUSH 操作。
         * 当 key 存在但不是列表类型时，返回一个错误。
         * @param key 不能为null
         * @param values 不能为null
         * @return 成功添加的数量
         */
        public Long lleftPushAll(String key, Object... values) {
            return redisTemplate.opsForList().leftPushAll(key, values);
        }

        /**
         * 移除并返回列表 key 的头元素。
         * @param key 不能为null
         * @param <T>
         * @return
         */
        public <T> T lleftPop(String key) {
            return (T) redisTemplate.opsForList().leftPop(key);
        }

        /**
         * 将一个或多个值 value 插入到列表 key 的表尾(最右边)。
         * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：比如
         * 对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，
         * 等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c 。
         * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作。
         * 当 key 存在但不是列表类型时，返回一个错误。
         */
        public Long lrightPushAll(String key, Object... values) {
            return redisTemplate.opsForList().rightPushAll(key, values);
        }

        /**
         * 移除并返回列表 key 的尾元素。
         */
        public <T> T lrightPop(String key) {
            return (T) redisTemplate.opsForList().rightPop(key);
        }

        /**
         * 命令 RPOPLPUSH 在一个原子时间内，执行以下两个动作：
         * 将列表 source 中的最后一个元素(尾元素)弹出，并返回给客户端。
         * 将 source 弹出的元素插入到列表 destination ，作为 destination 列表的的头元素。
         */
        public <T> T lrightPopAndLeftPush(String srcKey, String dstKey) {
            return (T) redisTemplate.opsForList().rightPopAndLeftPush(srcKey, dstKey);
        }

        /**
         * 返回列表 key 的长度。
         * 如果 key 不存在，则 key 被解释为一个空列表，返回 0 .
         * 如果 key 不是列表类型，返回一个错误。
         * @param key 不能为null
         * @return 列表大小
         */
        public Long lsize(String key) {
            return redisTemplate.opsForList().size(key);
        }

        /**
         * 返回列表 key 中，下标为 index 的元素。
         * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
         * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
         * 如果 key 不是列表类型，返回一个错误。
         * @param key 不能为null
         * @param index 下标
         * @param <T>
         * @return 下标对应的元素
         */
        public <T> T lindex(String key, long index) {
            return (T) redisTemplate.opsForList().index(key, index);
        }

        /**
         * 将列表 key 下标为 index 的元素的值设置为 value 。
         * 当 index 参数超出范围，或对一个空列表( key 不存在)进行 LSET 时，返回一个错误。
         * 关于列表下标的更多信息，请参考 LINDEX 命令。
         * @param key 不能为null
         * @param index 下标
         * @param value 下标值
         */
        public void lset(String key, long index, Object value) {
            redisTemplate.opsForList().set(key, index, value);
        }

        /**
         * 根据参数 count 的值，移除列表中与参数 value 相等的元素。
         * count 的值可以是以下几种：
         * count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。
         * count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。
         * count = 0 : 移除表中所有与 value 相等的值。
         * @param key 不能为null
         * @param count
         * @param value
         * @return 成功删除的数量
         */
        public Long lremove(String key, long count, Object value) {
            return redisTemplate.opsForList().remove(key, count, value);
        }

        /**
         * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 end 指定。
         * 下标(index)参数 start 和 end 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
         * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
         * <pre>
         * 例子：
         * 获取 list 中所有数据：cache.lrange(listKey, 0, -1);
         * 获取 list 中下标 1 到 3 的数据： cache.lrange(listKey, 1, 3);
         * </pre>
         * @param key 不能为null
         * @param start 起始下标
         * @param end 终止下标
         * @return 在start和end之间的元素列表
         */
        public List lrange(String key, long start, long end) {
            return redisTemplate.opsForList().range(key, start, end);
        }

        /**
         * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
         * 举个例子，执行命令 LTRIM list 0 2 ，表示只保留列表 list 的前三个元素，其余元素全部删除。
         * 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
         * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
         * 当 key 不是列表类型时，返回一个错误。
         * @param key 不能为null
         * @param start 起始下标
         * @param end 终止下标
         */
        public void ltrim(String key, long start, long end) {
            redisTemplate.opsForList().trim(key, start, end);
        }

    }

}
