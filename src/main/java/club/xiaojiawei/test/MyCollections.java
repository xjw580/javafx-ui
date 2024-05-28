package club.xiaojiawei.test;

import java.util.List;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/28 9:19
 */
public class MyCollections {

    public static<E> FXArrayList<E> buildList(E... strings){
        return new FXArrayList<>(List.of(strings));
    }

    public static<E> FXArrayList<E> buildList(){
        return new FXArrayList<>();
    }
}
