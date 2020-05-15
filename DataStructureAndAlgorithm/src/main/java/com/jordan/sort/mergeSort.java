package com.jordan.sort;

import java.util.Arrays;

/**
 * @autheor masheng
 * @description 归并排序
 * @date 2020/5/15
 */
public class mergeSort {

    public static void mergeSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;
        }
        mergeSort(arr, 0, arr.length - 1);
    }

    public static void mergeSort(int[] arr, int l, int r) {
        if (l == r) {
            return;
        }
        int mid = l + ((r - l) >> 1);
        mergeSort(arr, l, mid);
        mergeSort(arr, mid + 1, r);
        merge(arr, l, mid, r);
    }

    //两部分已经排好了，需要合并
    public static void merge(int[] arr, int l, int mid, int r) {
        //先创建一个辅助数组
        int[] help = new int[r - l + 1];
        int i = 0;
        //P1指向左半部分第一个数，p2指向右半部分第一个数
        int p1 = l;
        int p2 = mid + 1;
        //谁比较小就把谁放进help
        while (p1 <= mid && p2 <= r) {
            help[i++] = arr[p1] < arr[p2] ? arr[p1++] : arr[p2++];
        }
        //当上面的循环跳出后，p1和p2一定有一个越界，而且只有一个越界，不越界的要把剩下的拷贝进来
        while (p1 <= mid) {
            help[i++] = arr[p1++];
        }
        while (p2 <= r) {
            help[i++] = arr[p2++];
        }

        //拷贝数组
        for (int j = 0; j < help.length; j++) {
            arr[l + j] = help[j];
        }
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
            mergeSort(arr1);
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
        mergeSort(arr);
        printArray(arr);

    }
}