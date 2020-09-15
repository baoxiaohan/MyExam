package InputHandle;
/**
 * Created by bxh
 */

import DP_DFIM.DP_DFIM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @progran MyExam
 * @author bxh
 * @description
 * @date 2020/9/2 20:02
 */
public class InputHandle {
    static class NowRun implements Runnable{
        String args[];
        int mode;
        CountDownLatch countDownLatch;
        List<String>list;
        public NowRun(String []args,List<String>list, int mode, CountDownLatch countDownLatch){
            this.args = args;
            this.mode = mode;
            this.countDownLatch = countDownLatch;
            this.list = list;
        }
        @Override
        public void run() {
            try {
                String s = null;
                if (mode%2!=0) {
                    DP_DFIM dp_dfim = args.length == 6 ? new DP_DFIM(Integer.valueOf(args[1]), Integer.valueOf(args[2]),
                            Double.valueOf(args[3]),
                            Integer.valueOf(args[4]), Integer.valueOf(args[5]))
                            : args.length == 4 ? new DP_DFIM(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]))
                            : new DP_DFIM(10, 5, mode);
                    System.out.println("支持度："+dp_dfim.canTree.number+" W："+dp_dfim.canTree.W+
                    " disValue:"+dp_dfim.canTree.disValue+" 版本号"+dp_dfim.canTree.banbenhao+" PER_START_SIZE:"+
                    dp_dfim.canTree.PER_START_SIZE);
                        for (String s1 : list) {
                            dp_dfim.soluton(s1);
                        }
                    countDownLatch.countDown();
                } else {
                    DP_DFIM dp_dfim = args.length == 6 ? new DP_DFIM(Integer.valueOf(args[1]), Integer.valueOf(args[2]),
                            Double.valueOf(args[3]),
                            Integer.valueOf(args[4]), Integer.valueOf(args[5])+1)
                            : args.length == 4 ? new DP_DFIM(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3])+1)
                            : new DP_DFIM(10, 5, mode);
                    System.out.println("支持度："+dp_dfim.canTree.number+" W："+dp_dfim.canTree.W+
                            " disValue:"+dp_dfim.canTree.disValue+" 版本号"+dp_dfim.canTree.banbenhao+" PER_START_SIZE:"+
                            dp_dfim.canTree.PER_START_SIZE);
                    for (String s1 : list) {
                        dp_dfim.soluton(s1);
                    }
                    countDownLatch.countDown();
                }

        }catch(Exception e){
            e.printStackTrace();
        }
    }}


    static List<String> readFile(String str) throws Exception {
        BufferedReader bufferedReader = null;

        if (str!=null) {
            if (str.equals("2")) {
                    bufferedReader = new BufferedReader(new FileReader("F:\\bxh\\kosarak.dat"));
            } else if(!str.equals("1")){
                try {
                    bufferedReader = new BufferedReader(new FileReader(str));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }else{
                bufferedReader = new BufferedReader(new FileReader("D:\\kosarak.dat"));
            }
        }else{
            bufferedReader = new BufferedReader(new FileReader("D:\\kosarak.dat"));
        }
        List<String> list = new LinkedList<>();
        String s = null;
        while ((s = bufferedReader.readLine()) != null) {
           list.add(s);
        }
        return list;
    }
    public static void main(String[] args) throws Exception {

        //BufferedReader bufferedReader = new BufferedReader(new FileReader("F:\\bxh\\kosarak.dat"));
        List<String> inputStr  = null;
        if (args != null && args.length != 0) {
            inputStr = readFile(args[0]);
        }else{
            inputStr = readFile(null);
        }
        String []arg = {"1","10","10","0.85","1000","1"};
        CountDownLatch countDownLatch = new CountDownLatch(2);
        arg[2] = "15";
        arg[3] = "0.85";
        countDownLatch = new CountDownLatch(2);
        new Thread(new NowRun(arg,inputStr,1,countDownLatch)).start();
        new Thread(new NowRun(arg,inputStr,2,countDownLatch)).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        arg[2] = "15";
        arg[5] = "3";
        arg[3] = "0.84";
        countDownLatch = new CountDownLatch(2);
        new Thread(new NowRun(arg,inputStr,3,countDownLatch)).start();
        new Thread(new NowRun(arg,inputStr,4,countDownLatch)).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        arg[2] = "20";
        arg[5] = "5";
        arg[3] = "0.83";
        countDownLatch = new CountDownLatch(2);
        new Thread(new NowRun(arg,inputStr,5,countDownLatch)).start();
        new Thread(new NowRun(arg,inputStr,6,countDownLatch)).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        arg[2] = "25";
        arg[5] = "7";
        arg[3] = "0.80";
        countDownLatch = new CountDownLatch(2);
        new Thread(new NowRun(arg,inputStr,7,countDownLatch)).start();
        new Thread(new NowRun(arg,inputStr,8,countDownLatch)).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
