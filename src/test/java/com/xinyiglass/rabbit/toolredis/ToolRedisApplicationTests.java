package com.xinyiglass.rabbit.toolredis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ToolRedisApplicationTests {

	@Autowired
	private RedisUtil redisUtil;

//	@Test
	void contextLoads() {
	}

	private static void sleep(long time){
		System.out.printf("###########开始睡%d秒", time);
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println();
		System.out.printf("###########结束睡%d秒", time);
		System.out.println();
	}

	private static void sleep(){
		sleep(3);
	}

	// =============================Common=============================
	@Test
	void test_expire(){
		redisUtil.redisString.set("xyg", "666");
		System.out.println("Expire1: " + redisUtil.redisCommon.getExpire("xyg"));// -1
		System.out.println(redisUtil.redisString.get("xyg"));// 666
		redisUtil.redisCommon.expire("xyg",3);
		System.out.println("Expire2: " + redisUtil.redisCommon.getExpire("xyg"));// 2
		System.out.println(redisUtil.redisString.get("xyg"));// 666
		sleep();
		System.out.println(redisUtil.redisString.get("xyg"));// null
	}

	@Test
	void test_getExpire(){
		redisUtil.redisString.set("xyg", "666");
		System.out.println("Expire1: " + redisUtil.redisCommon.getExpire("xyg"));// -1
		System.out.println(redisUtil.redisString.get("xyg"));// 666
		redisUtil.redisCommon.expire("xyg",3);
		System.out.println("Expire2: " + redisUtil.redisCommon.getExpire("xyg"));// 2
		System.out.println(redisUtil.redisString.get("xyg"));// 666
		sleep();
		System.out.println(redisUtil.redisString.get("xyg"));// null
	}

	@Test
	void test_hasKey(){
		System.out.println("hasKey1: " + redisUtil.redisCommon.hasKey("xyg"));// false
		redisUtil.redisString.set("xyg", "666");
		System.out.println("hasKey2: " + redisUtil.redisCommon.hasKey("xyg"));// true
	}

	@Test
	void test_delOne(){
		System.out.println("hasKey1: " + redisUtil.redisCommon.hasKey("xyg"));// false
		redisUtil.redisString.set("xyg", "666");
		System.out.println("hasKey2: " + redisUtil.redisCommon.hasKey("xyg"));// true
		redisUtil.redisCommon.delOne("xyg");
		System.out.println("hasKey3: " + redisUtil.redisCommon.hasKey("xyg"));// false
	}

	@Test
	void test_delMany(){
		redisUtil.redisString.set("xyg", "666");
		redisUtil.redisString.set("xyg777", "777");
		System.out.println("hasKey1: " + redisUtil.redisCommon.hasKey("xyg"));// true
		System.out.println("hasKey2: " + redisUtil.redisCommon.hasKey("xyg777"));// true

		Long res = redisUtil.redisCommon.delMany("xyg", "xyg888");
		System.out.println("res: " + res);// 1
		System.out.println("hasKey3: " + redisUtil.redisCommon.hasKey("xyg"));// false
		System.out.println("hasKey4: " + redisUtil.redisCommon.hasKey("xyg777"));// true

		redisUtil.redisString.set("xyg", "666");
		System.out.println("hasKey5: " + redisUtil.redisCommon.hasKey("xyg"));// true
		res = redisUtil.redisCommon.delMany("xyg", "xyg777");
		System.out.println("res: " + res);// 2
		System.out.println("hasKey6: " + redisUtil.redisCommon.hasKey("xyg"));// false
		System.out.println("hasKey7: " + redisUtil.redisCommon.hasKey("xyg777"));// false
	}

	// =============================String=============================
	@Test
	void test_get(){
		redisUtil.redisString.setExp("xyg", "666", 1);
		System.out.println(redisUtil.redisString.get("xyg"));// 666
	}

	@Test
	void test_set(){
		redisUtil.redisString.setExp("xyg", "777", 1);
		System.out.println(redisUtil.redisString.get("xyg"));// 777
	}

	@Test
	void test_setExp(){
		redisUtil.redisString.setExp("xyg", "666", 3);
		System.out.println("Expire1: " + redisUtil.redisCommon.getExpire("xyg"));// 2
		System.out.println(redisUtil.redisString.get("xyg"));// 666
		sleep();
		System.out.println("Expire2: " + redisUtil.redisCommon.getExpire("xyg"));// -2
		System.out.println(redisUtil.redisString.get("xyg"));// null

		redisUtil.redisString.setExp("xyg", "666", 4000, TimeUnit.MILLISECONDS);
		System.out.println("Expire1: " + redisUtil.redisCommon.getExpire("xyg"));// 3
		System.out.println(redisUtil.redisString.get("xyg"));// 666
		sleep();
		System.out.println("Expire2: " + redisUtil.redisCommon.getExpire("xyg"));// 0
		sleep(5);
		System.out.println("Expire2: " + redisUtil.redisCommon.getExpire("xyg"));// -2
		System.out.println(redisUtil.redisString.get("xyg"));// null
	}

	@Test
	void test_incr(){
		redisUtil.redisCommon.delOne("xyg");
		System.out.println(redisUtil.redisString.get("xyg"));// null
		Long res = redisUtil.redisString.incr("xyg",2);
		System.out.println(res);// 2
		res = redisUtil.redisString.incr("xyg",3);
		System.out.println(res);// 5
		redisUtil.redisCommon.expire("xyg",1);
		sleep(1);
		System.out.println(redisUtil.redisString.get("xyg"));// null

		res = redisUtil.redisString.incr("xyg",-2);
		System.out.println(res);// -2
		res = redisUtil.redisString.incr("xyg",-6);
		System.out.println(res);// -8
		redisUtil.redisCommon.expire("xyg",1);
		sleep(1);
		System.out.println(redisUtil.redisString.get("xyg"));// null
	}

	// =============================Hash=============================

	@Test
	void test_hget(){
		redisUtil.redisHash.hset("xyg", "crm", "888");
		System.out.println(redisUtil.redisHash.hget("xyg", "crm"));// 888
	}

	@Test
	void test_hset(){
		redisUtil.redisHash.hset("xyg", "crm", "888");
		System.out.println(redisUtil.redisHash.hget("xyg", "crm"));// 888
	}

	@Test
	void test_hsetExp(){
		redisUtil.redisHash.hsetExp("xyg", "crm", "888", 1);
		System.out.println(redisUtil.redisHash.hget("xyg", "crm"));// 888
		sleep(1);
		System.out.println(redisUtil.redisHash.hget("xyg", "crm"));// null

		redisUtil.redisHash.hsetExp("xyg", "crm", "888", 1000, TimeUnit.MILLISECONDS);
		System.out.println(redisUtil.redisHash.hget("xyg", "crm"));// 888
		sleep(1);
		System.out.println(redisUtil.redisHash.hget("xyg", "crm"));// null
	}


	@Test
	void test_hmget(){
		Map map = new HashMap();
		map.put("crm", "999");
		redisUtil.redisHash.hmset("xyg", map);
		System.out.println(redisUtil.redisHash.hmget("xyg").get("crm"));// 999
	}

	@Test
	void test_hmset(){
		Map map = new HashMap();
		map.put("crm", "999");
		redisUtil.redisHash.hmset("xyg", map);
		System.out.println(redisUtil.redisHash.hmget("xyg").get("crm"));// 999
	}

	@Test
	void test_hmsetExp(){
		Map map = new HashMap();
		map.put("crm", "999");
		redisUtil.redisHash.hmsetExp("xyg", map, 1);
		System.out.println(redisUtil.redisHash.hmget("xyg").get("crm"));// 999
		sleep(1);
		System.out.println(redisUtil.redisHash.hmget("xyg").get("crm"));// null

		redisUtil.redisHash.hmsetExp("xyg", map, 1000, TimeUnit.MILLISECONDS);
		System.out.println(redisUtil.redisHash.hmget("xyg").get("crm"));// 999
		sleep(1);
		System.out.println(redisUtil.redisHash.hmget("xyg").get("crm"));// null
	}

	@Test
	void test_hdel(){
		Map map = new HashMap();
		map.put("crm", "999");
		map.put("crm2", "000");
		map.put("crm3", "111");
		redisUtil.redisHash.hmset("xyg", map);
		System.out.println(redisUtil.redisHash.hmget("xyg").get("crm"));// 999
		System.out.println(redisUtil.redisHash.hmget("xyg").get("crm2"));// 000
		System.out.println(redisUtil.redisHash.hmget("xyg").get("crm3"));// 111
		redisUtil.redisHash.hdel("xyg", "crm", "crm2");
		System.out.println(redisUtil.redisHash.hmget("xyg").get("crm"));// null
		System.out.println(redisUtil.redisHash.hmget("xyg").get("crm2"));// null
		System.out.println(redisUtil.redisHash.hmget("xyg").get("crm3"));// 111
	}

	@Test
	void test_hhasKey(){
		Map map = new HashMap();
		map.put("crm", "999");
		map.put("crm2", "000");
		redisUtil.redisHash.hmset("xyg", map);
		redisUtil.redisHash.hdel("xyg", "crm3");
		System.out.println(redisUtil.redisHash.hhasKey("xyg", "crm"));// true
		System.out.println(redisUtil.redisHash.hhasKey("xyg", "crm2"));// true
		System.out.println(redisUtil.redisHash.hhasKey("xyg", "crm3"));// false
	}

	@Test
	void test_hincr(){
		redisUtil.redisHash.hincr("xyg", "crm", 5);
		System.out.println(redisUtil.redisHash.hget("xyg", "crm"));// 5
	}

	// =============================Set=============================

	@Test
	void test_sget(){
		redisUtil.redisSet.sset("xyg", "777", "888");
		System.out.println(redisUtil.redisSet.sget("xyg").toString());// [888, 777]
	}

	@Test
	void test_sset(){
		redisUtil.redisCommon.delOne("xyg");

		redisUtil.redisSet.sset("xyg", "777", "888");
		System.out.println(redisUtil.redisSet.sget("xyg").toString());// [888, 777]

		redisUtil.redisSet.sset("xyg", "777");
		System.out.println(redisUtil.redisSet.sget("xyg").toString());// [888, 777]

		redisUtil.redisSet.sset("xyg2", "777");
		System.out.println(redisUtil.redisSet.sget("xyg2").toString());// [777]
	}

	@Test
	void test_ssetExp(){
		redisUtil.redisCommon.delOne("xyg");
		System.out.println(redisUtil.redisSet.sget("xyg").toString());// []

		redisUtil.redisSet.ssetExp("xyg", 1, "777", "888");
		System.out.println(redisUtil.redisSet.sget("xyg").toString());// [888, 777]
		sleep(1);
		System.out.println(redisUtil.redisSet.sget("xyg").toString());// []

		redisUtil.redisSet.ssetExp("xyg", 1000, TimeUnit.MILLISECONDS, "777", "888");
		System.out.println(redisUtil.redisSet.sget("xyg").toString());// [888, 777]
		sleep(1);
		System.out.println(redisUtil.redisSet.sget("xyg").toString());// []
	}

	@Test
	void test_sgetSize(){
		redisUtil.redisCommon.delOne("xyg2");
		System.out.println(redisUtil.redisSet.sget("xyg2").toString());// []

		redisUtil.redisSet.sset("xyg2", "777");
		System.out.println(redisUtil.redisSet.sgetSize("xyg2"));// 1

		redisUtil.redisSet.sset("xyg2", "777", "888");
		System.out.println(redisUtil.redisSet.sgetSize("xyg2"));// 2

		redisUtil.redisSet.sset("xyg2", "999", "000");
		System.out.println(redisUtil.redisSet.sgetSize("xyg2"));// 4

		redisUtil.redisSet.sset("xyg2", "111", "222", "333");
		System.out.println(redisUtil.redisSet.sgetSize("xyg2"));// 7

		System.out.println(redisUtil.redisSet.sget("xyg2").toString());// [000,333,222,777,888,111,999]
	}

	@Test
	void test_shasKey(){
		redisUtil.redisCommon.delOne("xyg2");
		System.out.println(redisUtil.redisSet.sget("xyg2").toString());// []

		redisUtil.redisSet.sset("xyg2", "777");
		System.out.println(redisUtil.redisSet.sget("xyg2"));// [777]

		System.out.println(redisUtil.redisSet.shasKey("xyg2","777"));// true
		System.out.println(redisUtil.redisSet.shasKey("xyg2","999"));// false
	}

	@Test
	void test_svaluesRemove(){
		redisUtil.redisCommon.delOne("xyg2");
		System.out.println(redisUtil.redisSet.sget("xyg2").toString());// []

		redisUtil.redisSet.sset("xyg2", "777", "888");
		System.out.println(redisUtil.redisSet.sget("xyg2"));// [888,777]

		System.out.println(redisUtil.redisSet.shasKey("xyg2","777"));// true
		System.out.println(redisUtil.redisSet.shasKey("xyg2","888"));// true

		redisUtil.redisSet.svaluesRemove("xyg2", "777");
		System.out.println(redisUtil.redisSet.shasKey("xyg2","777"));// false
		System.out.println(redisUtil.redisSet.shasKey("xyg2","888"));// true
	}

	// =============================ZSet=============================

	@Test
	void test_zadd(){
		redisUtil.redisCommon.delOne("xyg3");
		System.out.println(redisUtil.redisZSet.zcard("xyg3"));// 0

		System.out.println(redisUtil.redisZSet.zadd("xyg3", "crm3", 5.3));// true
		System.out.println(redisUtil.redisZSet.zadd("xyg3", "crm3", 5.2));// false
		System.out.println(redisUtil.redisZSet.zadd("xyg3", "crm1", 5.1));// true
		System.out.println(redisUtil.redisZSet.zadd("xyg3", "crm5", 5.5));// true
		System.out.println(redisUtil.redisZSet.zadd("xyg3", "crm8", 5.8));// true
		System.out.println(redisUtil.redisZSet.zadd("xyg3", "crm8", 5.9));// false

		System.out.println(redisUtil.redisZSet.zscore("xyg3","crm3"));// 5.2
		System.out.println(redisUtil.redisZSet.zcard("xyg3"));// 4

		redisUtil.redisCommon.delOne("xyg4");
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 0
		Map<Object, Double> members = new HashMap<>();
		members.put("crm1",6.1);
		members.put("crm2",6.2);
		System.out.println(redisUtil.redisZSet.zadd("xyg4",members));;// 2
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 2
	}

	@Test
	void test_zscore(){
		redisUtil.redisCommon.delOne("xyg3");
		System.out.println(redisUtil.redisZSet.zcard("xyg3"));// 0

		System.out.println(redisUtil.redisZSet.zadd("xyg3", "crm1", 5.1));// true
		System.out.println(redisUtil.redisZSet.zscore("xyg3","crm3"));// null
		System.out.println(redisUtil.redisZSet.zscore("xyg3","crm1"));// 5.1
	}

	@Test
	void test_zcard(){
		redisUtil.redisCommon.delOne("xyg3");
		System.out.println(redisUtil.redisZSet.zcard("xyg3"));// 0

		System.out.println(redisUtil.redisZSet.zadd("xyg3", "crm3", 5.3));// true
		System.out.println(redisUtil.redisZSet.zcard("xyg3"));// 1
	}

	@Test
	void test_zcount(){
		redisUtil.redisCommon.delOne("xyg4");
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 0
		Map<Object, Double> members = new HashMap<>();
		members.put("crm1",6.1);
		members.put("crm2",6.2);
		System.out.println(redisUtil.redisZSet.zadd("xyg4",members));;// 2
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 2

		System.out.println(redisUtil.redisZSet.zcount("xyg4", 6.1, 6));// 0
		System.out.println(redisUtil.redisZSet.zcount("xyg4", 6.1, 6.1));// 1
		System.out.println(redisUtil.redisZSet.zcount("xyg4", 6.1, 6.2));// 2
		System.out.println(redisUtil.redisZSet.zcount("xyg4", 6.1, 6.3));// 2
	}

	@Test
	void test_zincrBy(){
		redisUtil.redisCommon.delOne("xyg4");
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 0
		Map<Object, Double> members = new HashMap<>();
		members.put("crm1",6.1);
		members.put("crm2",6.2);
		System.out.println(redisUtil.redisZSet.zadd("xyg4",members));;// 2
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 2

		System.out.println(redisUtil.redisZSet.zincrBy("xyg4", "crm1", 1));// 7.1
		System.out.println(redisUtil.redisZSet.zincrBy("xyg4", "crm1", -5));// 2.0999999999999996
		System.out.println(redisUtil.redisZSet.zincrBy("xyg4", "crm2", -8));// -1.7999999999999998
	}

	@Test
	void test_zrange(){
		redisUtil.redisCommon.delOne("xyg4");
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 0
		Map<Object, Double> members = new HashMap<>();
		members.put("crm1",6.1);
		members.put("crm2",6.2);
		System.out.println(redisUtil.redisZSet.zadd("xyg4",members));;// 2
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 2

		System.out.println(redisUtil.redisZSet.zrange("xyg4", 1, 2));// [crm2]
		System.out.println(redisUtil.redisZSet.zrange("xyg4", 1, 1));// [crm2]
		System.out.println(redisUtil.redisZSet.zrange("xyg4", 2, 2));// []
		System.out.println(redisUtil.redisZSet.zrange("xyg4", 0, 2));// [crm1, crm2]
		System.out.println(redisUtil.redisZSet.zrange("xyg4", 0, 1));// [crm1, crm2]
		System.out.println(redisUtil.redisZSet.zrange("xyg4", -1, 1));// 倒数第一(1,1)[crm2]
		System.out.println(redisUtil.redisZSet.zrange("xyg4", -1, 0));// 倒数第一(1,0)1>0[]
		System.out.println(redisUtil.redisZSet.zrange("xyg4", -1, -1));// 倒数第一(1,1)[crm2]
		System.out.println(redisUtil.redisZSet.zrange("xyg4", -2, 1));// 倒数第二(0,1)[crm1, crm2]
		System.out.println(redisUtil.redisZSet.zrange("xyg4", -3, 1));// 倒数第三等于倒数第二(0,1)[crm1, crm2]
	}

	@Test
	void test_zreverseRange(){
		redisUtil.redisCommon.delOne("xyg4");
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 0
		Map<Object, Double> members = new HashMap<>();
		members.put("crm1",6.1);
		members.put("crm2",6.2);
		System.out.println(redisUtil.redisZSet.zadd("xyg4",members));;// 2
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 2

		System.out.println(redisUtil.redisZSet.zreverseRange("xyg4", 1, 2));// [crm1]
		System.out.println(redisUtil.redisZSet.zreverseRange("xyg4", 1, 1));// [crm1]
		System.out.println(redisUtil.redisZSet.zreverseRange("xyg4", 2, 2));// []
		System.out.println(redisUtil.redisZSet.zreverseRange("xyg4", 0, 2));// [crm2, crm1]
		System.out.println(redisUtil.redisZSet.zreverseRange("xyg4", 0, 1));// [crm2, crm1]
		System.out.println(redisUtil.redisZSet.zreverseRange("xyg4", -1, 1));// 倒数第一的下标为1(1,1)[crm1]
		System.out.println(redisUtil.redisZSet.zreverseRange("xyg4", -1, 0));// 倒数第一的下标为1(1,0)1>0[]
		System.out.println(redisUtil.redisZSet.zreverseRange("xyg4", -1, -1));// 倒数第一的下标为1(1,1)[crm1]
		System.out.println(redisUtil.redisZSet.zreverseRange("xyg4", -2, 1));// 倒数第二的下标为0(0,1)[crm2, crm1]
		System.out.println(redisUtil.redisZSet.zreverseRange("xyg4", -3, 1));// 倒数第三等于倒数第二(0,1)[crm2, crm1]
	}

	@Test
	void test_zrangeByScore(){
		redisUtil.redisCommon.delOne("xyg4");
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 0
		Map<Object, Double> members = new HashMap<>();
		members.put("crm1",6.1);
		members.put("crm2",6.2);
		System.out.println(redisUtil.redisZSet.zadd("xyg4",members));;// 2
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 2

		System.out.println(redisUtil.redisZSet.zrangeByScore("xyg4", 5.1, 6));// []
		System.out.println(redisUtil.redisZSet.zrangeByScore("xyg4", 6.1, 6.1));// [crm1]
		System.out.println(redisUtil.redisZSet.zrangeByScore("xyg4", 6.0, 6.1));// [crm1]
		System.out.println(redisUtil.redisZSet.zrangeByScore("xyg4", 6.1, 6.2));// [crm1,crm2]
		System.out.println(redisUtil.redisZSet.zrangeByScore("xyg4", -6.1, 6.2));// [crm1,crm2]
	}

	@Test
	void test_zrank(){
		redisUtil.redisCommon.delOne("xyg4");
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 0
		Map<Object, Double> members = new HashMap<>();
		members.put("crm1",6.1);
		members.put("crm2",6.2);
		members.put("crm3",5.2);
		System.out.println(redisUtil.redisZSet.zadd("xyg4",members));;// 2
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 2

		System.out.println(redisUtil.redisZSet.zrank("xyg4", "crm1"));// 1
		System.out.println(redisUtil.redisZSet.zrank("xyg4", "crm2"));// 2
		System.out.println(redisUtil.redisZSet.zrank("xyg4", "crm3"));// 0
	}

	@Test
	void test_zreverseRank(){
		redisUtil.redisCommon.delOne("xyg4");
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 0
		Map<Object, Double> members = new HashMap<>();
		members.put("crm1",6.1);
		members.put("crm2",6.2);
		members.put("crm3",5.2);
		System.out.println(redisUtil.redisZSet.zadd("xyg4",members));;// 3
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 3

		System.out.println(redisUtil.redisZSet.zreverseRank("xyg4", "crm1"));// 1
		System.out.println(redisUtil.redisZSet.zreverseRank("xyg4", "crm2"));// 0
		System.out.println(redisUtil.redisZSet.zreverseRank("xyg4", "crm3"));// 2
	}

	@Test
	void test_zremove(){
		redisUtil.redisCommon.delOne("xyg4");
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 0
		Map<Object, Double> members = new HashMap<>();
		members.put("crm1",6.1);
		members.put("crm2",6.2);
		members.put("crm3",5.2);
		System.out.println(redisUtil.redisZSet.zadd("xyg4",members));;// 3
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 3

		System.out.println(redisUtil.redisZSet.zremove("xyg4", "crm2"));// 1
		System.out.println(redisUtil.redisZSet.zcard("xyg4"));// 2
		System.out.println(redisUtil.redisZSet.zrange("xyg4", 0, 100));// [crm3,crm1]
	}

	// =============================List=============================

	@Test
	void test_lleftPush(){
		redisUtil.redisCommon.delMany("xyg5", "xyg6");
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 0
		System.out.println(redisUtil.redisList.lsize("xyg6"));// 0

		System.out.println(redisUtil.redisList.lleftPushAll("xyg5", "crm1", "crm2"));// 2
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 2

		System.out.println(redisUtil.redisList.lleftPushAll("xyg6", "crm3"));// 1
		System.out.println(redisUtil.redisList.lleftPushAll("xyg6", "crm4"));// 2
		System.out.println(redisUtil.redisList.lsize("xyg6"));// 2
	}

	@Test
	void test_lleftPop(){
		redisUtil.redisCommon.delMany("xyg5", "xyg6");
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 0
		System.out.println(redisUtil.redisList.lsize("xyg6"));// 0

		System.out.println(redisUtil.redisList.lleftPushAll("xyg5", "crm1", "crm2"));// 2
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 2
		System.out.println(redisUtil.redisList.<String>lleftPop("xyg5"));// crm2

		System.out.println(redisUtil.redisList.lleftPushAll("xyg6", "crm3"));// 1
		System.out.println(redisUtil.redisList.lleftPushAll("xyg6", "crm4"));// 2
		System.out.println(redisUtil.redisList.lsize("xyg6"));// 2
		System.out.println(redisUtil.redisList.<String>lleftPop("xyg6"));// crm4

	}

	@Test
	void test_lrightPush(){
		redisUtil.redisCommon.delMany("xyg5", "xyg6");
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 0
		System.out.println(redisUtil.redisList.lsize("xyg6"));// 0

		System.out.println(redisUtil.redisList.lrightPushAll("xyg5", "crm1", "crm2"));// 2
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 2

		System.out.println(redisUtil.redisList.lrightPushAll("xyg6", "crm7"));// 1
		System.out.println(redisUtil.redisList.lrightPushAll("xyg6", "crm8"));// 2
		System.out.println(redisUtil.redisList.lsize("xyg6"));// 2
	}

	@Test
	void test_lrightPop(){
		redisUtil.redisCommon.delMany("xyg5", "xyg6");
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 0
		System.out.println(redisUtil.redisList.lsize("xyg6"));// 0

		System.out.println(redisUtil.redisList.lrightPushAll("xyg5", "crm1", "crm2"));// 2
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 2
		System.out.println(redisUtil.redisList.<String>lrightPop("xyg5"));// crm2

		System.out.println(redisUtil.redisList.lrightPushAll("xyg6", "crm3"));// 1
		System.out.println(redisUtil.redisList.lrightPushAll("xyg6", "crm4"));// 2
		System.out.println(redisUtil.redisList.lsize("xyg6"));// 2
		System.out.println(redisUtil.redisList.<String>lrightPop("xyg6"));// crm4
	}

	@Test
	void test_lrightPopAndLeftPush(){

	}

	@Test
	void test_lsize(){
		redisUtil.redisCommon.delOne("xyg5");
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 0

		System.out.println(redisUtil.redisList.lrightPushAll("xyg5", "crm1", "crm2"));// 2
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 2
	}

	@Test
	void test_lindex(){
		redisUtil.redisCommon.delOne("xyg5");
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 0

		System.out.println(redisUtil.redisList.lrightPushAll("xyg5", "crm1", "crm2"));// 2
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 2

		System.out.println(redisUtil.redisList.<String>lindex("xyg5", 0));// crm1
		System.out.println(redisUtil.redisList.<String>lindex("xyg5", 1));// crm2
		System.out.println(redisUtil.redisList.<String>lindex("xyg5", 2));// null

		redisUtil.redisCommon.delOne("xyg6");
		System.out.println(redisUtil.redisList.<String>lindex("xyg6", 1));// null
	}

	@Test
	void test_lset(){
		redisUtil.redisCommon.delOne("xyg5");
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 0

		try {
			redisUtil.redisList.lset("xyg5", 2, "crm1");// ERR no such key
		} catch (Exception e) {
			System.out.println(e.getMessage());;
		}
		redisUtil.redisList.lleftPushAll("xyg5",  "crm2");
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 1
		try {
			redisUtil.redisList.lset("xyg5", 2, "crm1");// ERR index out of range
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		redisUtil.redisList.lset("xyg5", 0, "crm1");
		System.out.println(redisUtil.redisList.<String>lindex("xyg5", 0));// crm1
		System.out.println(redisUtil.redisList.<String>lindex("xyg5", 1));// null
	}

	@Test
	void test_lremove(){
		redisUtil.redisCommon.delOne("xyg5");
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 0

		redisUtil.redisList.lleftPushAll("xyg5",  "crm2", "crm2");
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 2
		System.out.println(redisUtil.redisList.lremove("xyg5", 0, "crm2"));// 2
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 0

		redisUtil.redisList.lleftPushAll("xyg5",  "crm2", "crm2");
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 2
		System.out.println(redisUtil.redisList.lremove("xyg5", 1, "crm2"));// 1
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 1
		System.out.println(redisUtil.redisList.<String>lindex("xyg5", 0));// crm2

		redisUtil.redisList.lleftPushAll("xyg5",  "crm1");
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 2
		System.out.println(redisUtil.redisList.lremove("xyg5", 1, "crm2"));// 1
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 1
		System.out.println(redisUtil.redisList.<String>lindex("xyg5", 0));// crm1

		redisUtil.redisList.lleftPushAll("xyg5",  "crm2");
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 2
		System.out.println(redisUtil.redisList.lremove("xyg5", -1, "crm2"));// 1
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 1
		System.out.println(redisUtil.redisList.<String>lindex("xyg5", 0));// crm1
	}

	@Test
	void test_lrange(){
		redisUtil.redisCommon.delOne("xyg5");
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 0

		System.out.println(redisUtil.redisList.lleftPushAll("xyg5",  "crm2", "crm2"));// 2
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 2

		System.out.println(redisUtil.redisList.lrange("xyg5",0, 1));// [crm2,crm2]
	}

	@Test
	void test_ltrim(){
		redisUtil.redisCommon.delOne("xyg5");
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 0

		System.out.println(redisUtil.redisList.lleftPushAll("xyg5",  "crm1", "crm2", "crm3"));// 3
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 3

		redisUtil.redisList.ltrim("xyg5",0, 1);
		System.out.println(redisUtil.redisList.lsize("xyg5"));// 2
		System.out.println(redisUtil.redisList.lrange("xyg5",0 , 1));// [crm3,crm2]
	}

}
