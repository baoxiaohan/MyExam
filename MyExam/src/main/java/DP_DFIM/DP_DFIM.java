package DP_DFIM;/**
 * Created by bxh
 */

import CanTree.CanTree;

import java.util.LinkedList;
import java.util.List;

/**
 * @progran MyExam
 * @author bxh
 * @description
 * @date 2020/9/2 20:09
 */
public class DP_DFIM {
     int count = 0;
    public static int START_SIZE = 100;
    public int W = 5;
    int lopt = 0;
    List<Integer> startList = new LinkedList<>();
    public CanTree canTree;
    public DP_DFIM(int num,int W,int banbenhao){
        canTree = new CanTree(num,W,banbenhao);
    }
    public DP_DFIM(Integer number,int W,double disValue,int PER_START_SIZE,int banbenhao){
        canTree = new CanTree(number,W,disValue,PER_START_SIZE,banbenhao);
    }
    public void soluton(String s){
         //进行事务截断
        canTree.InsertTree(s);

        //can树插入数据


        //增量计算函数


        //调用隐私保护发布方法


        //进行窗口滑动


        //删除非频繁项


    }
    //re等于所有频繁项集 噪声支持度（sup1-sup）/sup的中位数
    public void computeRE(){

    }
    //fscore等于 2*pre*recall /(pre+recall)
    public void computeFscore(){

    }
}
