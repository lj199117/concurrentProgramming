package chapter5;

/**
 * 最好的单例实现方式，根据类加载器的加载顺序特性实现
 * Created by 13 on 2017/5/6.
 */
public class StaticSingleton {
    private StaticSingleton() {
        System.out.println("StaticSingle is create");
    }

    private static class SingletonHolder {
        private static StaticSingleton instance = new StaticSingleton();
    }

    public static StaticSingleton getInstance() {
        return SingletonHolder.instance;
    }
}
