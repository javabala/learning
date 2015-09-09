package com.bala.learning.learning.redis;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

import org.aredis.cache.AsyncRedisClient;
import org.aredis.cache.AsyncRedisConnection;
import org.aredis.cache.AsyncRedisFactory;
import org.aredis.cache.RedisCommand;
import org.aredis.cache.RedisCommandInfo;
import org.aredis.util.pool.AsyncObjectPool;

/*
 * A connection pool is required for commands that use WATCH with MULTI and EXEC.
 *
 * The below program demonstrates the use of WATCH with MULTI and EXEC to add 3 to the number stored
 * in the key hello. This is just for showing the use of WATCH, MULTI, EXEC. You could accomplish
 * the same thing using a single INCRBY command.
 */
public class ConnectionPool {
	public static void main(String args[]) throws Exception {
		String key = "hello";
		AsyncRedisFactory f = new AsyncRedisFactory(null);
		AsyncRedisClient aredis = f.getClient("localhost");
		// Set an initial value for this test
		aredis.submitCommand(RedisCommand.SETEX, key, "300", "5").get();
		int i, maxTries = 10;
		AsyncObjectPool<AsyncRedisConnection> pool = f.getConnectionPool("localhost");
		AsyncRedisConnection con = null;
		i = 0;
			Future<RedisCommandInfo[]> future = null;
			try {
				con = pool.syncBorrow(0);
				// "WATCH" the key so that the transaction fails if another
				// client writes
				// to the key in between
				con.sendCommand(RedisCommand.WATCH, key);
				// Get the current value as an integer
				int val = con.submitCommand(RedisCommand.GET, key).get().getIntResult(0);
				val += 3;
				// Update the value in a multi-exec transaction
//				future = con.submitCommands(new RedisCommandInfo[] { new RedisCommandInfo(RedisCommand.MULTI),
//						new RedisCommandInfo(RedisCommand.SETEX, key, "300", String.valueOf(val)),
//						new RedisCommandInfo(RedisCommand.EXEC) });
				Future<RedisCommandInfo> incr = con.submitCommand(new RedisCommandInfo(RedisCommand.INCR, "1-1"));
				System.out.println(incr.get().getResult());
				Future<RedisCommandInfo> incr1 = con.submitCommand(new RedisCommandInfo(RedisCommand.INCR, "1-2"));
				System.out.println(incr1.get().getResult());
				con.submitCommand(new RedisCommandInfo(RedisCommand.LPUSH, "1", "1"));
				con.submitCommand(new RedisCommandInfo(RedisCommand.LPUSH, "1", "2"));
				con.submitCommand(new RedisCommandInfo(RedisCommand.LPUSH, "1", "3"));
				final Set<Integer> blockedAdIds = new HashSet<>();
//				AsyncHandler<RedisCommandInfo> mHandler = new AsyncHandler<RedisCommandInfo>() {
//
//					@Override
//					public void completed(RedisCommandInfo arg0, Throwable arg1) {
//						Object[] rawResponses = (Object[]) arg0.getResult();
//						for(Object rawResponse : rawResponses){
//							blockedAdIds.add(String.valueOf(rawResponse));
//						}
//					}
//
//				};
				Future<RedisCommandInfo> result = con.submitCommand(new RedisCommandInfo(RedisCommand.LRANGE, "1", 0, -1));
				Object[] res = (Object[]) result.get().getResult();
				for(Object rawResponse : res){
					blockedAdIds.add(Integer.parseInt(rawResponse.toString()));
				}
				System.out.println("ad id contains : "+blockedAdIds.contains("1")+" blockedAdIds : "+blockedAdIds);

			} finally {
				// Note that the connection is returned to pool as soon as the
				// EXEC command
				// is submitted without waiting for it to finish which is Ok.
				pool.returnToPool(con);
			}
			// On success of EXEC results will contain an Array of CommandInfo
			// objects corresponding to the
			// result of execution of each of the commands between MULTI and
			// exec
//			Object results[] = (Object[]) future.get()[2].getResult();
//			if (results != null) {
//				System.out.println(
//						"Result of SETEX command in Transaction: " + ((RedisCommandInfo) results[0]).getResult());
//				break;
//			}
			// Retries will not be executed in this example because it is a
			// single client
		if (i >= maxTries) {
			System.out.println("Addition to key: " + key + " failed. Is your Redis Server up?");
		}
	}

}