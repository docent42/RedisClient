import redis.clients.jedis.Jedis;
import redis.clients.jedis.ListPosition;

public class JedisClient
{
    private static final String redisHost = "localhost";
    private static final Integer redisPort = 6379;
    private static final int usersCount = 20;
    private static final String key = "reg_user:id";
    private static final long SLEEP = 700;

    private static Jedis client;

    private static void firstStart()
    {

        for (int i = 1; i <= usersCount; i++)
                client.rpush(key, Integer.toString(i));

    }

    public static void main(String[] args) throws InterruptedException
    {
        client = new Jedis(redisHost, redisPort);

        //###################################################################
        //                                                                 ##
         firstStart();// ДЛЯ ПОВТОРНОГО ЗАПУСКА ПОСТАВИТЬ КОММЕНТАРИЙ !!!! ##
        //                                                                 ##
        //###################################################################

        Long userListLength = client.llen(key);

        for (;;)
        {
            for (long i = 0; i < userListLength; i++)
            {
                String currentUser = client.lindex(key,i);

                System.out.printf("Показываем на экране пользователя <%s>%n",currentUser);

                if (Math.round(Math.random() * 10) >= 9)
                {
                    long rnd = randomUserPopup(userListLength, currentUser);
                    if (rnd < i) i--;
                }
                Thread.sleep(SLEEP);
            }
        }
    }

    private static long randomUserPopup(Long length,String currentUser)
    {
        boolean flag = true;
        String luckyUser = "";
        long rnd = 0;

        while (flag)
        {
            rnd = Math.round(Math.random() * (length - 1));// random user index
            luckyUser = client.lindex(key,rnd);// user from redis
            flag = luckyUser.equals(currentUser);
        }

        client.lrem(key,0,luckyUser);
        client.linsert(key, ListPosition.AFTER,currentUser,luckyUser);

        System.out.printf("!!! Пользователь <%s> за деньги пролез без очереди  !!!%n",luckyUser);
        return rnd;
    }

}
