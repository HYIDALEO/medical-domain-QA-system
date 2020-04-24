package com.appleyk.operation;

public class QuickSort {
	public int getMiddle(int[] list,String[] list_S, int low, int high) {
        int tmp = list[low];    //数组的第一个作为中轴
        String tmp_S=list_S[low];
        while (low < high) {
            while (low < high && list[high] >= tmp) {
                high--;
            }
            list[low] = list[high];   //比中轴小的记录移到低端
            list_S[low] = list_S[high];
            while (low < high && list[low] <= tmp) {
                low++;
            }
            list[high] = list[low];   //比中轴大的记录移到高端
            list_S[high] = list_S[low];
        }
        list[low] = tmp;              //中轴记录到尾
        list_S[low] = tmp_S;
        return low;                   //返回中轴的位置
    }
	
	public void _quickSort(int[] list,String[] list_S, int low, int high) {
        if (low < high) {
            int middle = getMiddle(list,list_S, low, high);  //将list数组进行一分为二
            _quickSort(list,list_S, low, middle - 1);        //对低字表进行递归排序
            _quickSort(list,list_S, middle + 1, high);       //对高字表进行递归排序
        }
    }
	
}
