import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
//
//      int n;
//      long start, end, time;
//
//      for (int i=1; i<=6; i++) {
//          n = (int)(Math.pow(3, i+5) - 1);
//          ArrayList<Integer> lst = new ArrayList<Integer>();
//          for (int j=1; j<=n; j++) {
//              lst.add(i-1);
//          }
//          Collections.shuffle(lst);
//
//          start = System.currentTimeMillis();
//          int[] res = firstExp(n);
//          end = System.currentTimeMillis();
//          time = end - start;
//
//          System.out.println("for i = " +i + ": countLinks = " + res[0] + ", numTrees = " + res[1]);
//          System.out.println("time: " + time);
//      }
//
//
//
//    }
//
//    public static int[] firstExp(int n) {
//        BinomialHeap result = new BinomialHeap();
//        int countLinks = 0;
//        int numTrees = 0;
//
//        for (int i=1; i<=n; i++) {
//            countLinks += result.insert(i, "info");
//        }
//        numTrees = result.numTrees();
//        int[] res = new int[2];
//        res[0] = countLinks;
//        res[1] = numTrees;
//        return res;
//    }
//
//    public static int[] secondExp(ArrayList<Integer> lst) {
//        BinomialHeap result = new BinomialHeap();
//        int n = lst.size();
//        int countLinks = 0;
//        int numTrees = 0;
//        int rankSum = 0;
//
//        for (int randNum: lst) {
//            countLinks += result.insert(randNum, "info");
//        }
//
//
//        int[] res = new int[3];
//
//        for (int i=0; i< n/2; i++) {
//           int[] info = result.deleteMin();
//           countLinks += info[0];
//           rankSum += info[1];
//        }
//
//        numTrees = result.numTrees();
//        res[0] = countLinks;
//        res[1] = numTrees;
//        res[2] = rankSum;
//        return res;
//    }
//
//    public static int[] thirdExp(int n) {
//        BinomialHeap result = new BinomialHeap();
//        int countLinks = 0;
//        int numTrees = 0;
//        int rankSum = 0;
//        int[] res = new int[3];
//
//        for (int i=n; i>=1; i--) {
//            countLinks += result.insert(i, "info");
//        }
//        int finish =(int)Math.pow(2,5) - 1;
//
//        while (result.size() > finish) {
//            int[] info = result.deleteMin();
//            countLinks += info[0];
//            rankSum += info[1];
//        }
//
//        numTrees = result.numTrees();
//        res[0] = countLinks;
//        res[1] = numTrees;
//        res[2] = rankSum;
//        return res;
//    }
    }
}