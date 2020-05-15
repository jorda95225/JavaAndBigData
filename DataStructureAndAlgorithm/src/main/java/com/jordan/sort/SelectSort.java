package com.jordan.sort;

import java.util.Arrays;

/**
 * @autheor masheng
 * @description 选择排序
 * @date 2020/5/14
 */
public class SelectSort {

    public static void selectSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        for (int i = 0; i < arr.length - 1; i++) {
            //先认为最小的值是i
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                //如果j比最小值小，把最小值索引改为j,否则保持原来的
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
//                minIndex = (arr[j] < arr[minIndex]) ? j : minIndex;
            }
            swap(arr, i, minIndex);
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
            selectSort(arr1);
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
        selectSort(arr);
        printArray(arr);

    }
}