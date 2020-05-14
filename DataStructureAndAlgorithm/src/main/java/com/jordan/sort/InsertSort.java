package com.jordan.sort;

/**
 * @autheor masheng
 * @description 插入排序
 * @date 2020/5/14
 */
public class InsertSort {

    public static void insertSort(int[] arr){
        if (arr == null || arr.length < 2) {
            return;
        }
        //0位置不动，要从1位置开始，0~i-1位置已经有序，表示该数往哪放
        for (int i = 1; i < arr.length; i++) {
            //从i-1位置开始,如果发生交换了，还要往前看能不能再交换，所以j要--
            //第一次就是相当于i-1和i位置判断，如果前面的大，就交换，再往前面判断
            for (int j = i-1; j >= 0 && arr[j] > arr[j+1] ; j--) {
                swap(arr,j,j+1);
            }
        }
    }
    public static void swap(int[] arr, int i, int j) {
        arr[i] = arr[i] ^ arr[j];
        arr[j] = arr[i] ^ arr[j];
        arr[i] = arr[i] ^ arr[j];
    }
}