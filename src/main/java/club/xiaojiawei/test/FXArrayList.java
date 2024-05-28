package club.xiaojiawei.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author 肖嘉威 xjw580@qq.com
 * @date 2024/5/28 9:15
 */
public class FXArrayList<E> extends ArrayList<E> {

    public FXArrayList(Collection<? extends E> c) {
        super(c);
    }

    public FXArrayList() {
    }

    public boolean addAll(E... elements){
        return true;
    }

    public boolean setAll(E... elements){
        return true;
    }

}
