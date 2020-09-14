package InputHandle;/**
 * Created by bxh
 */

import DP_DFIM.DP_DFIM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
        public NowRun(String []args, int mode, CountDownLatch countDownLatch){
            this.args = args;
            this.mode = mode;
            this.countDownLatch = countDownLatch;
        }
        @Override
        public void run() {
            try {
                String s = null;
                if (mode%2!=0) {
                    System.out.println("线程1");
                    BufferedReader bufferedReader = null;
                    try {
                        bufferedReader = new BufferedReader(new FileReader("D:\\kosarak.dat"));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (args != null && args.length != 0) {
                        if (args[0].equals("2")) {
                            try {
                                bufferedReader = new BufferedReader(new FileReader("F:\\bxh\\kosarak.dat"));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else if(!args[0].equals("1")){
                            try {
                                bufferedReader = new BufferedReader(new FileReader(args[0]));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    DP_DFIM dp_dfim = args.length == 6 ? new DP_DFIM(Integer.valueOf(args[1]), Integer.valueOf(args[2]),
                            Double.valueOf(args[3]),
                            Integer.valueOf(args[4]), Integer.valueOf(args[5])+1)
                            : args.length == 4 ? new DP_DFIM(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3])+1)
                            : new DP_DFIM(10, 5, mode);
                    try {
                        while ((s = bufferedReader.readLine()) != null) {
                            dp_dfim.soluton(s);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    countDownLatch.countDown();
                } else {
                    System.out.println("线程2");
                    BufferedReader bufferedReader = null;
                    try {
                        bufferedReader = new BufferedReader(new FileReader("D:\\kosarak_1.dat"));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (args != null && args.length != 0) {
                        if (args[0].equals("2")) {
                            try {
                                bufferedReader = new BufferedReader(new FileReader("F:\\bxh\\kosarak_1.dat"));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else if(!args[0].equals("1")){
                            try {
                                bufferedReader = new BufferedReader(new FileReader(args[0]));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    DP_DFIM dp_dfim = args.length == 6 ? new DP_DFIM(Integer.valueOf(args[1]), Integer.valueOf(args[2]),
                            Double.valueOf(args[3]),
                            Integer.valueOf(args[4]), Integer.valueOf(args[5])+1)
                            : args.length == 4 ? new DP_DFIM(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3])+1)
                            : new DP_DFIM(10, 5, mode);
                    while ((s = bufferedReader.readLine()) != null) {
                        dp_dfim.soluton(s);
                    }
                    countDownLatch.countDown();
                }

        }catch(Exception e){
            e.printStackTrace();
        }
    }}
    public static void main(String[] args) throws IOException {

        //BufferedReader bufferedReader = new BufferedReader(new FileReader("F:\\bxh\\kosarak.dat"));
        String []arg = {"1","5","10","0.90","1000","1"};
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(new NowRun(arg,1,countDownLatch)).start();
        new Thread(new NowRun(arg,2,countDownLatch)).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        arg[2] = "10";
        arg[5] = "3";
        arg[3] = "0.87";
        countDownLatch = new CountDownLatch(2);
        new Thread(new NowRun(args,1,countDownLatch)).start();
        new Thread(new NowRun(args,2,countDownLatch)).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        arg[2] = "15";
        arg[5] = "5";
        arg[3] = "0.85";
        countDownLatch = new CountDownLatch(2);
        new Thread(new NowRun(args,1,countDownLatch)).start();
        new Thread(new NowRun(args,2,countDownLatch)).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        arg[2] = "20";
        arg[5] = "5";
        arg[3] = "0.83";
        countDownLatch = new CountDownLatch(2);
        new Thread(new NowRun(args,1,countDownLatch)).start();
        new Thread(new NowRun(args,2,countDownLatch)).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        arg[2] = "25";
        arg[5] = "5";
        arg[3] = "0.80";
        countDownLatch = new CountDownLatch(2);
        new Thread(new NowRun(args,1,countDownLatch)).start();
        new Thread(new NowRun(args,2,countDownLatch)).start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
