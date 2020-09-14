package TransactionTruncation;/**
 * Created by bxh
 */

import CanTree.CanTree;

import java.util.*;

/**
 * @progran MyExam
 * @author bxh
 * @description
 * @date 2020/9/7 22:14
 */
public class TransationTuncUtils {

    public static List<String> transTunc(String input, CanTree canTree){

        String [] strs = input.split(" ");
        List<String> ret = new LinkedList<>();
        if(strs.length<=canTree.lopt){
            ret.add(input);
            return ret;
        }
        Iterator<Set<String>> iterator = canTree.ttMap.values().iterator();
        Set<String> set = new HashSet<>();
        while(iterator.hasNext()){
            Set<String> next = iterator.next();
            StringBuilder stringBuilder = new StringBuilder("");
            for (int i = 0; i < strs.length; i++) {
                if(next.contains(strs[i])){
                    stringBuilder.append(strs[i]+" ");
                    set.add(strs[i]);
                }
            }
            ret.add(stringBuilder.substring(0,stringBuilder.length()-1));
        }
        StringBuilder stringBuilder = new StringBuilder("");
        int c = 0;
        for (int i = 0; i < strs.length; i++) {
            if(c==canTree.lopt){
                c = 0;
                ret.add(stringBuilder.substring(0,stringBuilder.length()-1));
                stringBuilder = new StringBuilder("");
            }
           if(!set.contains(strs[i])){
               stringBuilder.append(strs[i]+" ");
               c++;
           }
        }
        if(c>0){
            ret.add(stringBuilder.substring(0,stringBuilder.length()-1));
        }
        return ret;
    }
}
