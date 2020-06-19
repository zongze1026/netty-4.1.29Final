package io.netty.example.myExample.recycler;
import io.netty.util.Recycler;

/**
 * Create By xzz on 2020/6/18
 */
public class User {

    private String name;

    private Integer age;

    private Recycler.Handle<User> handler;

    public User(Recycler.Handle<User> handler) {
        this.handler = handler;
    }

    public void recycler(){
        handler.recycle(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
