package com.jordan.sort;

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
        for (int i = 0; i < arr.length; i++) {
            //先认为最小的值是i
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                //如果j比最小值小，把最小值索引改为j,否则保持原来的
                minIndex = (arr[j] < arr[minIndex]) ? j : minIndex;
            }
            swap(arr, i, minIndex);
        }
    }

    public static void swap(int[] arr, int i, int j) {
        arr[i] = arr[i] ^ arr[j];
        arr[j] = arr[i] ^ arr[j];
        arr[i] = arr[i] ^ arr[j];
    }
}