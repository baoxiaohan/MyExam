package InputHandle;/**
 * Created by bxh
 */

import DP_DFIM.DP_DFIM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
        public NowRun(String []args,int mode){
            this.args = args;
            this.mode = mode;
        }
        @Override
        public void run() {
            try {

                String s = null;
                if (mode == 1) {
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
                            Integer.valueOf(args[4]), Integer.valueOf(args[5]))
                            : args.length == 4 ? new DP_DFIM(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]))
                            : new DP_DFIM(10, 5, 1);
                    try {
                        while ((s = bufferedReader.readLine()) != null) {
                            dp_dfim.soluton(s);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                            Integer.valueOf(args[4]), Integer.valueOf(args[5]))
                            : args.length == 4 ? new DP_DFIM(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]) + 1)
                            : new DP_DFIM(10, 5, 2);
                    while ((s = bufferedReader.readLine()) != null) {
                        dp_dfim.soluton(s);
                    }
                }
        }catch(Exception e){
            e.printStackTrace();
        }
    }}
    public static void main(String[] args) throws IOException {

        //BufferedReader bufferedReader = new BufferedReader(new FileReader("F:\\bxh\\kosarak.dat"));
       new Thread(new NowRun(args,1)).start();
       new Thread(new NowRun(args,2)).start();
    }

}
