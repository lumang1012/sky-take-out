package com.sky.context;

public class BaseContext {
    //调用线程进行存储
   public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

   //设置id
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    //获取id
    public static Long getCurrentId(){
        return threadLocal.get();
    }

    //移除id的存储
    public static void removeCurrentId(){
        threadLocal.remove();
    }


}
