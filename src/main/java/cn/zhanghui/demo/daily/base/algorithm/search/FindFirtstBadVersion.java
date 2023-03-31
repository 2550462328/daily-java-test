package cn.zhanghui.demo.daily.base.algorithm.search;

/**
 * @ClassName FindFirtstBadVersion
 * @Description: 假设你有 n 个版本 [1, 2, ..., n]，你想找出导致之后所有版本出错的第一个错误的版本。
 * <p>
 * 你可以通过调用 bool isBadVersion(version) 接口来判断版本号 version 是否在单元测试中出错。实现一个函数来查找第一个错误的版本。
 * <p>
 * 你应该尽量减少对调用 API 的次数。
 * <p>
 * 示例:
 * <p>
 * 给定 n = 5，并且 version = 4 是第一个错误的版本。
 * <p>
 * 调用 isBadVersion(3) -> false
 * 调用 isBadVersion(5) -> true
 * 调用 isBadVersion(4) -> true
 * <p>
 * 所以，4 是第一个错误的版本。
 * @Author: ZhangHui
 * @Date: 2020/10/10
 * @Version：1.0
 */
public class FindFirtstBadVersion {

    public static void main(String[] args) {
        FindFirtstBadVersion findFirtstBadVersion = new FindFirtstBadVersion();

        System.out.println(findFirtstBadVersion.firstBadVersion(2126753390));
    }

    private int index;

    /**
     * 使用递归 + 二分解决
     */
    public int firstBadVersion(int n) {
        return getBadVersion(1, n);
    }

    private int getBadVersion(int start, int end) {

        int mid = start + (end - start) / 2;

        if (start > end) {
            return index;
        }
        if (isBadVersion(mid)) {
            index = mid;
            return getBadVersion(start, mid - 1);
        } else {
            return getBadVersion(mid + 1, end);
        }
    }

    /**
     * 直接使用二分解决
     */
    public int firstBadVersion_Acvanced(int n) {

        int start = 1, end = n;

        while (start < end) {

            // 这里只能这么写，以后也只建议这种方式求中位数，而不是(start + end)/2，可能会超出int的最大范围变成负数
            int mid = start + (end - start) / 2;

            if (isBadVersion(mid)) {
                end = mid;
            } else {
                start = mid + 1;
            }
        }
        return start;
    }


    private boolean isBadVersion(int version) {
        return version >= 1702766719;
    }
}
