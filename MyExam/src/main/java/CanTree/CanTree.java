package CanTree;/**
 * Created by bxh
 */

import TransactionTruncation.TransationTuncUtils;
import org.apache.commons.math3.distribution.LaplaceDistribution;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.stream.Collectors;

/**
 * @progran MyExam
 * @author bxh
 * @description
 * @date 2020/9/3 9:40
 */
public class CanTree {
    interface Compare{
        public boolean compare(Object x,Object y);
    }
    long line = 0;
    public int banbenhao = 1;
    public static class TreeNode{
        String val;
        int windowsFlag = 1;
        int count;
        public int subSup;
        public int pathTailSup;
        public TreeNode(String val,int count){
            this.val = val;
            this.count = count;
        }
        public TreeNode(String val,int count,int W){
            this.val = val;
            this.count = count;
            this.windowsFlag = W;
        }
        public TreeNode clone(){
            return new TreeNode(val,count,windowsFlag);
        }
         public void add(int count){
            this.count += count;
        }
        TreeNode father = null;
        Map<String,TreeNode> sonMap = new LinkedHashMap<String,TreeNode>();
    }

    public static class FIM{
        List<String> list = new LinkedList<>();
        int count = 0;

        public FIM(List<String> list,int count){
            this.count = count;
            this.list = list;
        }

        @Override
        public String toString() {
            return "FIM{" +
                    "list=" + list+
                    ", count=" + count +
                    '}';
        }
    }
    class TreeStuct{
        public TreeStuct(){
            count = 0;
            list = new LinkedList<TreeNode>();
        }
        Integer count;
        List<TreeNode> list;
    }
    class Cache{
        FIM x;
        FIM y;

        public Cache(FIM x, FIM y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cache cache = (Cache) o;
            if(x.list.size()!=cache.x.list.size()||
                    y.list.size()!=cache.y.list.size())
                return false;
            if(equal(x.list,cache.x.list)&&equal(y.list,cache.y.list)){
                return true;
            }
            return false;
        }

        boolean equal(List<String> l1,List<String>l2){
            Iterator<String> iterator1 = l1.iterator();
            Iterator<String> iterator2 = l2.iterator();
            while (iterator1.hasNext()){
                String x = iterator1.next();
                String y = iterator2.next();
                if(!x.equals(y)){
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
    ConcurrentHashMap<Cache,Double> DisCache = new ConcurrentHashMap<>();
    Map<String,TreeNode> roots = new HashMap();
    int coreSize =  Runtime.getRuntime().availableProcessors()+1;
    public ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            coreSize,
            coreSize*2,
            10,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(100000)
    );
    Map<String,TreeStuct> setMap = new HashMap<>();
    List<FIM> publishIM = new LinkedList<CanTree.FIM>();
    Map<Integer,List<FIM>> windowsMap = new HashMap<>();
    public Map<Integer,Set<String>> ttMap = new HashMap<>();
    public Integer number = 5;
    public int lopt;
    public double disValue = 0.9;
    public int WFlag = 1;
    List<Integer> startList = new LinkedList<>();
    int count = 0;
    public int W = 5;
    public static int PER_START_SIZE = 500;
    static int PER_SIZE = 500;
    double dist = disValue;
    double laplaceMechanismCount(long realCountResult, double epsilon) {
        LaplaceDistribution ld = new LaplaceDistribution(0, 1 / epsilon);
        double noise = ld.sample();
        return realCountResult+noise;
    }
    public CanTree(Integer number,int W,double disValue,int PER_START_SIZE,int banbenhao){
        this.number = number;
        this.disValue = disValue;
        this.W = W;
        this.PER_START_SIZE = PER_START_SIZE;
        this.PER_SIZE = PER_START_SIZE;
        this.banbenhao = banbenhao;
        if(banbenhao%2==0){
            this.number = (int)Math.ceil(laplaceMechanismCount(number,1));
        }
    }
    public CanTree(Integer number,int W,int banbenhao){
        this.number = number;
        this.W = W;
        this.banbenhao = banbenhao;
        if(banbenhao%2==0){
            this.number = (int)Math.ceil(laplaceMechanismCount(number,1));
        }
    }
    /*public CanTree(List<String> sortList){
        this.sortList = sortList;
    }*/

   /* public CanTree(String []strings,List<String> list){
        this.sortList = list;
        TreeNode treeNode = new TreeNode("",0);
        for (String string : strings) {
            addTreeNode(string,treeNode);
        }
    }*/

    public CanTree(List<List<TreeNode>> lists){

        for (List<TreeNode> list : lists) {
            TreeNode treeNode = list.get(0).clone();
            if(!roots.containsKey(treeNode.val)){
                roots.put(treeNode.val,treeNode);
                if(!setMap.containsKey(treeNode.val)) {
                    TreeStuct treeStuct = new TreeStuct();

                    setMap.put(treeNode.val,treeStuct);
                }
                setMap.get(treeNode.val).count += treeNode.count;
                setMap.get(treeNode.val).list.add(treeNode);

            }
            TreeNode tmp = roots.get(treeNode.val);
            for (int i = 1; i < list.size(); i++) {
                TreeNode now = list.get(i).clone();
                if(tmp.sonMap.containsKey(now.val)){
                    tmp.sonMap.get(now.val).add(now.count);
                }else{
                    if(!setMap.containsKey(now.val)) {
                        TreeStuct treeStuct = new TreeStuct();
                        setMap.put(now.val, treeStuct);
                    }
                    setMap.get(now.val).list.add(now);
                    tmp.sonMap.put(now.val,now);
                    now.father = tmp;
                }
                tmp = tmp.sonMap.get(now.val);
            }
        }
    }
    public void InsertTree(String str){
        line++;
        if(windowsMap.size()<W){
            //
            startList.add(str.length());
            addTree(str,WFlag);
            count++;
            if(count%PER_START_SIZE==0){
                //输出频繁项集
                publish();
            }
            if(count == W*PER_START_SIZE){
                Collections.sort(startList);
                lopt = startList.get((int) (W*PER_START_SIZE*0.75));
                startList = null;
                count = 0;
            }
            return;
        }
        List<String> list = TransationTuncUtils.transTunc(str, this);
        for (String s1 : list) {
            addTree(s1,WFlag);
            count++;
        }
        calPerSize();
        if(count>(PER_SIZE)){
            //检查事务增量
            if(growthOutBound()){
                //进行窗口滑动,会更新WFlag
                System.out.println(line+":==============进行窗口滑动=================");
                long t = System.currentTimeMillis();
                slideWindow();
                System.out.println(line+":==============窗口滑动成功================="+" 耗时："+(System.currentTimeMillis()-t));
            }
            count = 0;
        }

    }

    void calPerSize(){
        if(dist>disValue){
            PER_SIZE = PER_START_SIZE;
            return;
        }
        if(disValue-dist>0.2){
            PER_SIZE  = 20 * PER_START_SIZE;
        }
        else if(disValue-dist>0.1){
            PER_SIZE  = 10 * PER_START_SIZE;
        }else if(disValue-dist>0.05){
            PER_SIZE = 5*PER_START_SIZE;
        }else if(disValue-dist>0.01){
            PER_SIZE = 2*PER_START_SIZE;
        }
        else if(disValue-dist<0.01){
            PER_SIZE = PER_START_SIZE;
        }
    }
    public void addTree(String str,int W){
       // System.out.println(line+":-------------插入事务-------------");
        long t1 = System.currentTimeMillis();
        //str根据sortList排序
        TreeNode now = null;
        //插入成功
        String[] split = str.split(" ");
        Arrays.sort(split,(x,y)->{
            return Integer.compare(Integer.valueOf(x),Integer.valueOf(y));
        });
        Map<String,Integer> map_tmp = new TreeMap<>((x,y)->{
            return Integer.compare(Integer.valueOf(x),Integer.valueOf(y));
        });
        for (String s : split) {
            if(!map_tmp.containsKey(s)){
                map_tmp.put(s,0);
            }
            map_tmp.put(s,map_tmp.get(s)+1);
        }
        for (String x : map_tmp.keySet()) {
            int count = map_tmp.get(x);
            if(count>0){
                if(now==null){
                    if(roots.containsKey(x)){
                        roots.get(x).add(count);
                        roots.get(x).windowsFlag = W;
                        now = roots.get(x);
                    }else{
                        now = new TreeNode(x,count,W);
                        if(!setMap.containsKey(x)){
                            setMap.put(x,new TreeStuct());
                        }
                        setMap.get(x).list.add(now);
                        setMap.get(x).count += now.count;
                        roots.put(now.val,now);
                    }
                }else{
                    if(now.sonMap.containsKey(x)){
                        now.sonMap.get(x).add(count);
                        now.sonMap.get(x).windowsFlag = W;
                        now =  now.sonMap.get(x);
                    } else{
                        TreeNode treeNode = new TreeNode(x, count,W);
                        now.sonMap.put(treeNode.val,treeNode);
                        treeNode.father = now;
                        now = treeNode;
                        if(!setMap.containsKey(x)){
                            setMap.put(x,new TreeStuct());
                        }
                        setMap.get(x).list.add(treeNode);
                        setMap.get(x).count += treeNode.count;
                    }
                }
            }
      //      System.out.println(line+":-------------插入事务成功-------------"+(System.currentTimeMillis()-t1));
        }


    }
    String getMapFirst(Map<String,Object> map){
        Iterator<String> iterator = map.keySet().iterator();
        return iterator.next();
    }
    TreeNode getMapFirstValue(Map<String, TreeNode> map){
        Iterator<TreeNode> iterator = map.values().iterator();
        return iterator.next();
    }
    //搜索获取频繁项集
    public List<FIM>  doFIM(CanTree canTree,TreeNode node){
        List<FIM> ret = new LinkedList<FIM>();
        if(node!=null&&node.count<number){
            return ret;
        }
        if(canTree.roots.size()==1&&isOnePath(canTree.roots.values().stream().collect(Collectors.toList()).get(0))){
            //求排列组合
            TreeNode treeNode = canTree.roots.values().stream().collect(Collectors.toList()).get(0);
            List<TreeNode> list = new LinkedList<CanTree.TreeNode>();
            //已经筛选了频繁项集了
            while (treeNode!=null&&treeNode.count>=number){
                list.add(treeNode);
                if(treeNode.sonMap.size()>0) {
                    treeNode = getMapFirstValue(treeNode.sonMap);
                }else
                    treeNode = null;
            }
            List<CanTree.FIM> fims = get(list, new LinkedList<TreeNode>(), 0);
            if (node != null) {
                for (CanTree.FIM fim : fims) {
                    fim.list.add(node.val);
                    fim.count = Math.min(fim.count, node.count);
                }
            }

            return fims;
        }else{
                for (Map.Entry<String, TreeStuct> entry : canTree.setMap.entrySet()) {
                    //找到其所有条件模式
                    String nowStr = entry.getKey();
                    TreeStuct treeStuct = entry.getValue();
                    if(treeStuct.count<number){
                        continue;
                    }
                    List<List<TreeNode>> paths = findPaths(treeStuct.list);
                    if (paths.size() == 0) {
                        //  ret.addAll(get(paths.get(0), new LinkedList<TreeNode>(), 0));
                        continue;
                    }
                    CanTree canTree1 = new CanTree(paths);
                    List<FIM> fims = doFIM(canTree1, treeStuct.list.get(0));
                    ret.addAll(fims);
                }
            for (Map.Entry<String, TreeStuct> entry : canTree.setMap.entrySet()){
                    if(entry.getValue().count<number){
                        continue;
                    }
                    List<String> list = new LinkedList<>();
                    list.add(entry.getKey());
                    ret.add(new FIM(list,entry.getValue().count));
            }
            if (node != null) {
                for (CanTree.FIM fim : ret) {
                    fim.list.add(node.val);
                    fim.count = Math.min(fim.count, node.count);
                }
            }
            return ret;
        }

    }
    void deleteNoFrequent(TreeNode treeNode,Compare compare){
        if(treeNode==null) {
            Iterator<TreeNode> iterator1 = roots.values().iterator();
            while(iterator1.hasNext()){
                TreeNode node = iterator1.next();
                if(compare.compare(node.count,number)){
                    deleteSubTree(node);
                    iterator1.remove();
                    //删除该子树
                }else{
                    Iterator<TreeNode> iterator = node.sonMap.values().iterator();
                    while (iterator.hasNext()){
                        TreeNode treeNode1 = iterator.next();
                        if(compare.compare(treeNode1.count,number)){
                            //删除该子树
                            deleteSubTree(treeNode1);
                            iterator.remove();
                        }else{
                            deleteNoFrequent(treeNode1,compare);
                        }
                    }
                }
            }
        }else{
            Iterator<TreeNode> iterator = treeNode.sonMap.values().iterator();
            while (iterator.hasNext()){
                TreeNode treeNode1 = iterator.next();
                if(compare.compare(treeNode1.count,number)){
                    //删除该子树
                    deleteSubTree(treeNode1);
                    iterator.remove();
                }else{
                    deleteNoFrequent(treeNode1,compare);
                }
            }
        }
    }
    public void publish(){
        System.out.println(line+":-------------搜索频繁项集-------------");
        long t = System.currentTimeMillis();
        List<CanTree.FIM> fims = doFIM(this, null);
        t = System.currentTimeMillis()-t;
        System.out.println(line+":-------------搜索频繁项集成功------------- 耗时:"+t);
        if(t>50000){
            //删除不频繁项集
            long t1 = System.currentTimeMillis();
            System.out.println("---------------删除不频繁项----------------");
            deleteNoFrequent(null,(x,y)->{
                return  (int) x<=(int) y;
            });
            System.out.println("---------------删除不频繁项-------------耗时"+(System.currentTimeMillis()-t1));
        }
        else if(t>20000){
            //删除不频繁项集
            long t1 = System.currentTimeMillis();
            System.out.println("---------------删除不频繁项----------------");
            deleteNoFrequent(null,(x,y)->{
                return  (int) x<(int) y/2;
            });
            System.out.println("---------------删除不频繁项-------------耗时"+(System.currentTimeMillis()-t1));
        }
        else if(t>10000){
            //删除不频繁项集
            long t1 = System.currentTimeMillis();
            System.out.println("---------------删除不频繁项----------------");
            deleteNoFrequent(null,(x,y)->{
                return  (int) x<(int) y/4;
            });
            System.out.println("---------------删除不频繁项-------------耗时"+(System.currentTimeMillis()-t1));
        }
        publishIM = fims;
        try {
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("F:\\bxh\\output"+banbenhao+".txt",true));

            bufferedWriter.write("----------------------------------------\n");
            for (int i = 0; i < fims.size(); i++) {
                FIM x = fims.get(i);
                StringBuilder stringBuilder = new StringBuilder(i+": ");
                for (String s : x.list) {
                    stringBuilder.append(s+" ");
                }
                //隐私版本
                if(banbenhao%2==0){
                    x.count = (int) Math.floor(laplaceMechanismCount(x.count,1));
                    if(x.count<number){
                        x.count = number;
                    }
                }
                stringBuilder.append("count:"+x.count+"\n");
                try {
                    bufferedWriter.write(stringBuilder.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            bufferedWriter.write("----------------------------------------\n");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        windowsMap.put(WFlag,fims);

        WFlag = (WFlag)%(W+1)+1;
        if(windowsMap.size()>W) {
            windowsMap.remove(WFlag);
        }
    }
    public boolean growthOutBound(){
        long t = System.currentTimeMillis();
        System.out.println(line+":-------------growth搜索频繁项集-------------");
        List<FIM> fims = doFIM(this, null);
        t = (System.currentTimeMillis()-t);
        System.out.println(line+":-------------growth搜索频繁项集成功------------- 耗时:"+t);
        if(t>50000){
            //删除不频繁项集
            long t1 = System.currentTimeMillis();
            System.out.println("---------------删除不频繁项----------------");
            deleteNoFrequent(null,(x,y)->{
                return  (int) x<(int) y;
            });
            System.out.println("---------------删除不频繁项-------------耗时"+(System.currentTimeMillis()-t1));
        }
        else if(t>20000){
            //删除不频繁项集
            long t1 = System.currentTimeMillis();
            System.out.println("---------------删除不频繁项----------------");
            deleteNoFrequent(null,(x,y)->{
                return  (int) x<(int) y/2;
            });
            System.out.println("---------------删除不频繁项-------------耗时"+(System.currentTimeMillis()-t1));
        }
        else if(t>10000){
            //删除不频繁项集
            long t1 = System.currentTimeMillis();
            System.out.println("---------------删除不频繁项----------------");
            deleteNoFrequent(null,(x,y)->{
                return  (int) x<(int) y/4;
            });
            System.out.println("---------------删除不频繁项-------------耗时"+(System.currentTimeMillis()-t1));
        }
        double v = calDis(fims, this.publishIM);
        dist = v;
        if(v>disValue){
            //进行发布和滑动
            publish();
            return true;
         }
         return false;
    }
    //
    public void slideWindow(){
        int tmp = WFlag-1;
        if(tmp==0){
            tmp = W+1;
        }
        int first = tmp+2<=(W+1)?(tmp+2):(tmp-4);

        List<FIM> fims = windowsMap.get(first);
        List<CanTree.FIM> cal = cal(fims);
        for (FIM fim : cal) {
            List<String> list = fim.list;
            List<TreeNode> path = findPath(list);
            for (TreeNode treeNode : path) {
                if(fim.count==0){
                    break;
                }
                if(treeNode.count-treeNode.subSup>=fim.count){
                    //将所有修改为treeNode.count- fim.count
                    change(list,treeNode,fim.count);
                    fim.count = 0;
                }else{
                    //将所有修改为treeNode.subSup
                    change(list,treeNode,treeNode.count-treeNode.subSup);
                    fim.count -= treeNode.count-treeNode.subSup;
                }
            }
        }

        //删除所有id为划出窗口或者支持度为0的节点
        delete(first,null);

    }

    void delete(int wflag,TreeNode treeNode){
        if(treeNode==null) {
            Iterator<TreeNode> iterator1 = roots.values().iterator();
            while(iterator1.hasNext()){
                TreeNode node = iterator1.next();
                if(node.windowsFlag == wflag||node.count==0){
                    deleteSubTree(node);
                    iterator1.remove();
                    //删除该子树
                }else{
                    Iterator<TreeNode> iterator = node.sonMap.values().iterator();
                    while (iterator.hasNext()){
                        TreeNode treeNode1 = iterator.next();
                        if(treeNode1.windowsFlag == wflag||treeNode1.count==0){
                            //删除该子树
                            deleteSubTree(treeNode1);
                            iterator.remove();
                        }else{
                            delete(wflag,treeNode1);
                        }
                    }
                }
            }
        }else{
            Iterator<TreeNode> iterator = treeNode.sonMap.values().iterator();
            while (iterator.hasNext()){
                TreeNode treeNode1 = iterator.next();
                if(treeNode1.windowsFlag == wflag||treeNode1.count==0){
                    //删除该子树
                    deleteSubTree(treeNode1);
                    iterator.remove();
                }else{
                    delete(wflag,treeNode1);
                }
            }
        }
    }

    void deleteSubTree(TreeNode treeNode){
        if(setMap.containsKey(treeNode.val)) {
            setMap.get(treeNode.val).list.remove(treeNode);
            setMap.get(treeNode.val).count -= treeNode.count;
            if (setMap.get(treeNode.val).list.size() == 0) {
                setMap.remove(treeNode.val);
            }
        }
        for (TreeNode node : treeNode.sonMap.values()) {
            deleteSubTree(node);
        }
    }
    void change(List<String> list,TreeNode node,int count){
        node.count = node.count-count;
        setMap.get(node.val).count -= count;
        for (int i = 1; i < list.size(); i++) {
            node = node.sonMap.get(list.get(i));
            node.count = node.count-count;
            setMap.get(node.val).count -= count;
        }
    }

    public List<TreeNode> findPath(List<String> list){
        //flag true则搜寻连续的串
            List<TreeNode> ret = new LinkedList<TreeNode>();
            TreeStuct treeStuct = setMap.get(list.get(0));
            if(treeStuct==null){
                return ret;
            }
            for (TreeNode treeNode : treeStuct.list) {
                int count = 1;
                TreeNode now = treeNode;
                while(count<list.size()) {
                    if(count == list.size()-1){
                        String xx = list.get(count);
                        if(now.sonMap.containsKey(xx)){
                            count++;
                            now = now.sonMap.get(xx);
                            long c = now.sonMap.values().stream().filter(x->{
                                return x!=null;
                            }).map((x) -> {
                                return x.count;
                            }).reduce(0,Integer::sum);
                            if(c!=0){
                                treeNode.subSup = (int) c;
                            }
                            treeNode.pathTailSup = now.count;
                            ret.add(treeNode);
                            break;
                        }else{
                            break;
                        }

                    }
                    String x = list.get(count);
                    if(now.sonMap.containsKey(x)){
                        count++;
                        now = now.sonMap.get(x);
                    }else{
                        break;
                    }
                }
            }
            return ret;
    }
    //返回最短的路径
    public List<TreeNode> findPath(List<String> list,int start,TreeNode startNode){
        //flag true则搜寻连续的串

            //寻找不连续的
        String x = list.get(start);
        List<TreeNode> sonList = (List<TreeNode>) startNode.sonMap.values();
        List<TreeNode> ret = new LinkedList<TreeNode>();
        for (TreeNode treeNode : sonList) {
            if(treeNode.val.equals(x)){
                List<TreeNode> path = findPath(list, start + 1, startNode.sonMap.get(x));
                if(path.size()>0){
                    path.add(0,startNode);
                    if(ret.size()==0||path.size()<ret.size()){
                        ret = path;
                    }
                }
            }else if(list.contains(treeNode.val)){
                continue;
            }else{
                List<TreeNode> path = findPath(list, start, treeNode);
                if(path.size()>0){
                    path.add(0,startNode);
                    if(ret.size()==0||path.size()<ret.size()){
                        ret = path;
                    }
                }
            }
        }
       return ret;
    }
    //求封闭项集
    List<FIM> cal(List<FIM> l){
        List<FIM> list = new LinkedList<FIM>();
        list.addAll(l);
        list.sort((x,y)->{
            return -Integer.compare(x.list.size(),y.list.size());
        });
        for (int i = 0; i < list.size(); i++) {
            FIM x = list.get(i);
            for (int j = i-1; j >=0; j--) {
                FIM y = list.get(j);
                if(isFSet(x,y)){
                    x.count -= y.count;
                }
            }
        }
        return list;
    }
    boolean isFSet(FIM x,FIM y){
        if(x.list.size()>=y.list.size()){
            return false;
        }
        int i = 0;
        int j = 0;
        while(i<x.list.size()&&j<y.list.size()){
           String m = x.list.get(i);
           String n = y.list.get(j);
           if(m==n){
               i++;
               j++;
           }else{
               j++;
           }
        }
        if(i==x.list.size()){
            return true;
        }
        return false;
    }
    class CalDisRunner implements Runnable{
        List<FIM> list1;
        List<FIM> list2;
        DoubleAccumulator ret;
        CountDownLatch countDownLatch;
        int start;
        int step;
        CalDisRunner(List<FIM> list1,List<FIM> list2,int start, int step,DoubleAccumulator ret,CountDownLatch countDownLatch){
            this.list1 = list1;
            this.list2 = list2;
            this.ret = ret;
            this.countDownLatch = countDownLatch;
            this.start = start;
            this.step = step;
        }
        @Override
        public void run() {
            double y = 0;
            int i = start;
            for (; i < start+step&&i<list1.size(); i++) {
                FIM x = list1.get(i);
                for (FIM fim : list2) {
                    Cache cache = new Cache(x,fim);
                    double v = editDistance(cache);
                    y = y + v;
                }
            }
           // System.out.println(Thread.currentThread().getName()+"领取了任务"+start+"-"+i+"的计算任务");
            ret.accumulate(y/(double) (step*list2.size()));
            countDownLatch.countDown();
        }
    }
    public double calDis(List<FIM> fims1,List<FIM> fims2){
        //
        long t1 = System.currentTimeMillis();
        System.out.println(line+ ":---------------开始计算距离--------");

        DoubleAccumulator accumulator = new DoubleAccumulator( Double::sum, 0L);
        int step = 100;
        int num = (int) Math.ceil((double) fims1.size()/(double)step);
        CountDownLatch countDownLatch = new CountDownLatch(num);
        for (int i = 0; i < fims1.size(); i=i+step) {
            threadPoolExecutor.execute(new CalDisRunner(fims1,fims2,i,step,accumulator,countDownLatch));
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double x1 = accumulator.doubleValue()/(num);
        double max = Math.max(fims1.size(), fims2.size());
        double min = Math.min(fims1.size(), fims2.size());
        double x2 = (max-min)/(max+min);
        double ret = x1*0.85+x2*0.15;
        t1 = System.currentTimeMillis()-t1;
        System.out.println(line+ ":---------------计算距离--------耗时"+(t1)+" 距离为"+ret);
        if(t1>50000){
            long t2 = System.currentTimeMillis();
            System.out.println("---------------删除不频繁项----------------");
            deleteNoFrequent(null,(x,y)->{
                return (int) x< (int) y;
            });
            System.out.println("---------------删除不频繁项-------------耗时"+(System.currentTimeMillis()-t2));
        }
        else if(t1>20000){
            long t2 = System.currentTimeMillis();
            System.out.println("---------------删除不频繁项----------------");
            deleteNoFrequent(null,(x,y)->{
                return (int) x< (int) y/2;
            });
            System.out.println("---------------删除不频繁项-------------耗时"+(System.currentTimeMillis()-t2));
        }
        else if(t1>10000){
            long t2 = System.currentTimeMillis();
            System.out.println("---------------删除不频繁项----------------");
            deleteNoFrequent(null,(x,y)->{
                return (int) x< (int) y/4;
            });
            System.out.println("---------------删除不频繁项-------------耗时"+(System.currentTimeMillis()-t2));
        }
        return ret;
    }

    double editDistance(Cache cache){
        FIM f1 = cache.x;
        FIM f2 = cache.y;
       // if(!DisCache.containsKey(cache)){
            int [][]dp = new int[f1.list.size()+1][f2.list.size()+1];
            List<String> list1 = f1.list;
            List<String> list2 = f2.list;
            dp[0][0] = 0;
            for (int i = 1; i < dp.length; i++) {
                dp[i][0] = i;
            }
            for (int i = 1; i < dp[0].length; i++) {
                dp[0][i] = i;
            }
            for (int i = 0; i < list1.size(); i++) {
                String x = list1.get(i);
                for (int j = 0; j < list2.size(); j++) {
                    String y = list2.get(j);
                    if(x.equals(y)){
                        dp[i+1][j+1] = Math.min(Math.min(dp[i][j+1],dp[i+1][j]),dp[i][j]);
                    }else{
                        dp[i+1][j+1] = Math.min(Math.min(dp[i][j+1],dp[i+1][j]),dp[i][j])+1;
                    }
                }
            }
            //int dis =  dp[list1.size()][list2.size()];
            double dis = (double) dp[list1.size()][list2.size()]/(Math.max(list1.size(), list2.size()));
       //     DisCache.put(cache,dis);
     //   }
     //   double dis = DisCache.get(cache);
        int min = Math.min(f1.count,f2.count);
        int max = Math.max(f1.count,f2.count);
        return (dis * min + (max-min))/(max);
    }

    public void calTTMap(){
        Set<Integer> set = new HashSet<>();
        Integer c = 0;
        for (int i = 0; i < publishIM.size(); i++) {
            if(set.contains(i))
                continue;
            set.add(i);
            Set<String> set1 = publishIM.get(i).list.stream().collect(Collectors.toSet());
            for (int j = i+1; j < publishIM.size(); j++) {
                if(set.contains(j))
                    continue;
                Set<String> set2 = publishIM.get(j).list.stream().collect(Collectors.toSet());
                set2.addAll(set1);
                if(set2.size()<lopt){
                    set1 = set2;
                    set.add(j);
                }else{
                    break;
                }
            }

            ttMap.put(c,set1);
            c++;
        }
        //ttMap只存储频繁的项集分组，不频繁的随机分组
    }


    //从单条路径获取所有频繁项集
    List<FIM> get(List<TreeNode> list, List<TreeNode> nodeList ,int start){
        List<FIM> ret = new LinkedList<FIM>();
        if(start>=list.size()){
            return ret;
        }
        for (int i = start; i < list.size(); i++) {
            CanTree.TreeNode treeNode = list.get(i);
            List<TreeNode> tmp = new LinkedList<CanTree.TreeNode>();
            tmp.addAll(nodeList);
            tmp.add(treeNode);
            if(tmp.size()>=1) {
                ret.add(treeNode2FIM(tmp));
            }
            ret.addAll(get(list, tmp ,i+1));
        }
        return ret;
    }
    FIM treeNode2FIM(List<TreeNode> list){
        List<String> collect = list.stream().map((x) -> {
            return x.val;
        }).collect(Collectors.toList());
       Optional<Integer> c  = list.stream().map((x) -> {
            return x.count;
        }).min(Integer::compare);
        FIM fim = new FIM(collect,c.get());
        return fim;
    }
    List<List<TreeNode>> findPaths(List<TreeNode> list){
        List<List<TreeNode>> ret = new LinkedList<>();
        for (TreeNode treeNode : list) {
            /*if(treeNode.count<number)
                continue;*/
            List<TreeNode> tmpList = new LinkedList<>();
            TreeNode tmp = treeNode.father;
            while(tmp!=null){
                TreeNode now = new TreeNode(tmp.val,treeNode.count);
                tmpList.add(0,now);
                tmp = tmp.father;
            }
            if(tmpList.size()!=0) {
              //  tmpList.add(treeNode);
                ret.add(tmpList);
            }
        }
        return ret;
    }


     boolean isOnePath(TreeNode treeNode){
        if(treeNode.sonMap.size()==0)
            return true;
        if(treeNode.sonMap.size()==1){
            return isOnePath(getMapFirstValue(treeNode.sonMap));
        }
        return false;
    }


    private int calStrCharNum(String str,String x){
        String[] split = str.split(" ");
        int c = 0;
        for (String s : split) {
            if(s.equals(x)){
                c++;
            }
        }
        return c;
    }

}