package io.netty.example.myExample.recycler;

import io.netty.util.Recycler;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;

/**
 * Create By xzz on 2020/6/18
 * <p>
 * netty 对象池测试回收
 */
public class RecyclerTest {

    public static void main(String[] args) {
//        recyclerTest();

        final FastThreadLocal<String> threadLocal = new FastThreadLocal<String>(){
            @Override
            protected String initialValue() throws Exception {
                return "toms";
            }
        };

        new FastThreadLocalThread(){
            @Override
            public void run() {
                String s = threadLocal.get();
                threadLocal.set("lucy");
                threadLocal.remove();
                System.out.println(s);
            }
        }.start();


    }

    private static void recyclerTest() {
        //创建对象池
        Recycler<User> recycler = new Recycler<User>() {
            @Override
            protected User newObject(Handle<User> handle) {
                return new User(handle);
            }
        };
        //从对象池中获取user实例
        User user = recycler.get();
        user.setName("toms");
        user.setAge(18);
        //回收对象
        user.recycler();
        User user2 = recycler.get();
        System.out.println(user2.getName()+"="+user2.getAge());
    }


}
