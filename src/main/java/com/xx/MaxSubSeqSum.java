package com.xx;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName : MaxSubSeqSum
 * @Description :最大子序列
 * @Author :hjh
 * @Date:2019/10/25 22:58
 * @Version 1.0
 **/
public class MaxSubSeqSum {
    /**
     * 三次循环，性能最差
     * @param a
     * @param N
     * @return
     */
    public static int triple(int[] a,int N){
        //thisSum是当前正在统计的序列的和，maxSum是最大序列的和
        int thisSum,maxSum=0;
        int i,j,k;
        int count=0;
        Map<String,Integer> result=new HashMap<>();
        int leftIndex=0;
        int rightIndex=0;
        for(i=0;i<N;i++){ //i是序列的最左端

            for(j=i;j<N;j++){//j是序列的最左端
                thisSum=0;
                for(k=i;k<=j;k++){ //k所在循环计算每个子序列的和
                    thisSum+=a[k];
                    count++;
                }
                if(thisSum>maxSum){
                    maxSum=thisSum;
                    leftIndex=i;
                    rightIndex=j;

                }
            }
        }
        result.put("maxSum",maxSum);
        result.put("leftIndex",leftIndex);
        result.put("rightIndex",rightIndex);

        System.out.println("count="+count);
        System.out.println("result="+result);

        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("subSeq=[");
        for(int m=leftIndex;m<=rightIndex;m++){
            stringBuilder.append(a[m]);
            if(m<rightIndex){
                stringBuilder.append(",");
            }else{
                stringBuilder.append("]");
            }

        }
        System.out.println(stringBuilder.toString());
        return maxSum;
    }

    /**
     * 两次循环，k循环是个很愚蠢的行为，因为我们可以在第二层循环中完成累加。性能稍微好点
     * @param a
     * @param N
     * @return
     */
    public static int twice(int[] a,int N){
        int thisSum,maxSum=0;
        int i,j;
        int count=0;
        Map<String,Integer> result=new HashMap<>();
        int leftIndex=0;
        int rightIndex=0;
        for(i=0;i<N;i++){
            thisSum=0;
            for(j=i;j<N;j++){
                thisSum+=a[j];
                if(thisSum>maxSum){
                    maxSum=thisSum;
                    leftIndex=i;
                    rightIndex=j;
                }
                count++;
            }

        }
        result.put("maxSum",maxSum);
        result.put("leftIndex",leftIndex);
        result.put("rightIndex",rightIndex);
        System.out.println("count="+count);
        System.out.println("result="+result);
        return maxSum;

    }

    /**
     * 在线处理，性能最佳，性能跟动态规划的差不多，但动态规划能理解
     * @param a
     * @param N
     * @return
     */
    public static int onLine(int[] a,int N){
        int thisSum=0,maxSum=0;
        int i;
        int count=0;
        int leftIndex=0;
        int rightIndex=0;
        for(i=0;i<N;i++){
            count++;
            thisSum+=a[i];
            if(thisSum>maxSum){
                maxSum=thisSum;
                rightIndex=i;
            }else if(thisSum<0){
                thisSum=0;
                leftIndex=i+1;
            }
        }
        System.out.println("count="+count);
        System.out.println("left="+leftIndex+",right="+rightIndex);
        return maxSum;
    }
    public static void main(String args[]){

        int[] arr=new int[]{-2,11,-4,13,-5,2};
//        int[] arr=new int[]{-2,11,-4,13,-5,4,2};
//        int[] arr=new int[]{1,-2,11,-4,13,-5,4,2};
//        int max=triple(arr,arr.length); //计算了56次
//        int max=twice(arr,arr.length); //计算了21次
        int max=onLine(arr,arr.length); //计算了6次5
        System.out.println("max="+max);
    }
}
