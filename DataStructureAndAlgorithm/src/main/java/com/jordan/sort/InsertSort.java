package com.jordan.sort;

import java.util.Arrays;

/**
 * @autheor masheng
 * @description 插入排序
 * @date 2020/5/14
 */
public class InsertSort {

    public static void insertSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        //0位置不动，要从1位置开始，0~i-1位置已经有序，表示该数往哪放
        for (int i = 1; i < arr.length; i++) {
            //从i-1位置开始,如果发生交换了，还要往前看能不能再交换，所以j要--
            //第一次就是相当于i-1和i位置判断，如果前面的大，就交换，再往前面判断
            for (int j = i - 1; j >= 0 && arr[j] > arr[j + 1]; j--) {
                swap(arr, j, j + 1);
            }
        }
    }

    public static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    //随机数组发生器
    public static int[] getRandomArray(int size, int value) {
        //生成[0-size]长度随机的数组
        int[] arr = new int[(int) ((size + 1) * Math.random())];
        for (int i = 0; i < arr.length; i++) {
            //一个随机数减另一个随机数
            arr[i] = (int) ((value + 1) * Math.random()) - (int) (value * Math.random());
        }
        return arr;
    }

    //准备一个绝对正确的方法,时间复杂度可以差，但要正确
    public static void comparator(int[] arr) {
        Arrays.sort(arr);
    }

    //拷贝数组
    public static int[] copyArray(int[] arr) {
        if (arr == null) {
            return null;
        }
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = arr[i];
        }
        return res;
    }

    //判断数组是否相等
    public static boolean isEqual(int[] arr1, int[] arr2) {
        if ((arr1 == null && arr2 != null) || (arr1 != null && arr2 == null)) {
            return false;
        }
        if (arr1 == null && arr2 == null) {
            return true;
        }
        if (arr1.length != arr2.length) {
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] != arr2[i]) {
                return false;
            }
        }
        return true;
    }

    //输出数组
    public static void printArray(int[] arr) {
        if (arr == null) {
            return;
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }


    public static void main(String[] args) {
        int testTime = 500000;
        int size = 100;
        int value = 100;
        boolean succeed = true;
        for (int i = 0; i < testTime; i++) {
            int[] arr1 = getRandomArray(size, value);
            int[] arr2 = copyArray(arr1);
            int[] arr3 = copyArray(arr1);
            //用两个方法进行排序
            insertSort(arr1);
            comparator(arr2);
            //验证两个数组是否相等
            if (!isEqual(arr1, arr2)) {
                succeed = false;
                printArray(arr3);
                break;
            }
        }
        System.out.println(succeed ? "排序正确" : "排序错误");
        int[] arr = getRandomArray(size, value);
        printArray(arr);
        insertSort(arr);
        printArray(arr);

    }
}