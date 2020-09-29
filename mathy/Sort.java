package mathy;

import java.util.Vector;


/**
 *
 */
public class Sort {

    public double[] A, B, orig;
    long copyCt;

    /**
     * @param go
     */
    public Sort(double[] go) {

        orig = new double[go.length];

        A = util.MoreArray.copy(go);
        orig = util.MoreArray.copy(go);
    }

    /**
     *
     */
    public void printArray() {

        for (int i = 0; i < A.length; i++)
            System.out.println(i + "\t" + A[i]);
    }

    /**
     *
     */
    public void printArrayComp() {

        for (int i = 0; i < A.length; i++)
            System.out.println(i + "\t" + A[i] + "\t" + orig[i]);
    }


    /**
     * NOT TESTED
     *
     * @param loc1
     * @param loc2
     */
    public void swap(int loc1, int loc2) { // swaps within array A

        double temp = A[loc1];
        A[loc1] = A[loc2];
        A[loc2] = temp;
        copyCt += 3;
    }

    /**
     * NOT TESTED
     *
     * @param start
     * @param end
     */
    public void bubbleSort(int start, int end) {
        for (int top = end; top > start; top--)
            for (int i = start; i < top; i++)
                if ((A[i] > A[i + 1]))
                    swap(i, i + 1);
    }

    /**
     * NOT TESTED
     *
     * @param start
     * @param end
     */
    public void selectionSort(int start, int end) {
        for (int top = end; top > start; top--) {
            int max = start;
            for (int i = start + 1; i <= top; i++)
                if ((A[i] > A[max]))
                    max = i;
            swap(max, top);
        }
    }

    /**
     * NOT TESTED
     *
     * @param start
     * @param end
     */
    public void insertionSort(int start, int end) {

        for (int insert = start + 1; insert <= end; insert++) {

            double temp = A[insert];

//System.out.println("insertionSort "+insert+"\t"+temp);

            copyCt++;
            int i = insert - 1;
            while (i >= start && (A[i] > temp)) {
                A[i + 1] = A[i];
                copyCt++;
                i--;
            }
            A[i + 1] = temp;
            copyCt++;
        }

//printArrayComp();
    }

    /**
     * @param from1
     * @param to1
     * @param from2
     * @param to2
     * @param count
     * @param posInB
     */
    public void doMerge(int from1, int to1, int from2, int to2, int count, int posInB) {
        for (int i = 0; i < count; i++) {
            if (from2 > to2)
                B[posInB++] = A[from1++];
            else if (from1 > to1)
                B[posInB++] = A[from2++];
            else if ((A[from1] < A[from2]))
                B[posInB++] = A[from1++];
            else
                B[posInB++] = A[from2++];
            copyCt++;
        }
    }

    /**
     * HALF TESTED
     *
     * @param start
     * @param end
     */
    public void mergeSort(int start, int end) {
        int length = end - start + 1;
        int sortLength = 1;
        while (sortLength < length) {
            int from1 = start;
            while (from1 <= end) {
                int from2 = from1 + sortLength;
                int to1 = from2 - 1;
                int to2 = from2 + sortLength - 1;
                if (to1 >= end)
                    doMerge(from1, end, 0, -1, end - from1 + 1, from1 - start);
                else if (to2 >= end)
                    doMerge(from1, to1, from2, end, end - from1 + 1, from1 - start);
                else
                    doMerge(from1, to1, from2, to2, 2 * sortLength, from1 - start);
                from1 = to2 + 1;
            }
            for (int i = 0; i < length; i++)
                A[start + i] = B[i];
            copyCt += length;
            sortLength *= 2;
        }
    }

    /**
     * NOT TESTED
     *
     * @param lo
     * @param hi
     * @return
     */
    public int quickSortStep(int lo, int hi) {
        double temp = A[hi];
        copyCt++;
        while (hi > lo) {
            while (hi > lo && (A[lo] <= temp))
                lo++;
            if (hi > lo) {
                A[hi] = A[lo];
                copyCt++;
                hi--;
                while (hi > lo && (A[hi] >= temp))
                    hi--;
                if (hi > lo) {
                    A[lo] = A[hi];
                    copyCt++;
                    lo++;
                }
            }
        }
        A[hi] = temp;
        copyCt++;
        return hi;
    }

    /**
     * NOT TESTED
     *
     * @param start
     * @param end
     */
    public void quickSort(int start, int end) {

        System.out.println("quicksort " + start + "\t" + end);

        if (end > start) {
            int mid = quickSortStep(start, end);
            if (mid - start > end - mid) {
                quickSort(mid + 1, end);
                quickSort(start, mid - 1);
            } else {
                quickSort(start, mid - 1);
                quickSort(mid + 1, end);
            }
        }
//printArrayComp();
    }


    /**
     * Sorts the Treatment or Control data into a new Vector. When set ='x' the 'rank' field in Distrib is populated.
     *
     * @param a
     * @param set
     * @param direction
     * @return
     */
    public final static Vector sort(Vector a, char set, String direction) {
        //System.out.println("SIZE OF VECTOR FOR SORTING "+a.size());
        Vector ret = new Vector();
        int rank = 0;
        int count = 0;
        double lastcur = 0;
        while (a.size() > 0) {
            int k = -1;
            if (direction.equals("min"))
                k = mathy.stat.findMinPosDistrib(a);
            else if (direction.equals("max"))
                k = mathy.stat.findMaxPosDistrib(a);
            //System.out.println("next rank "+k);
            if (k == -1)
                break;
            Distrib now = (Distrib) a.elementAt(k);
            //System.out.println("next rank "+k+"\t"+now.val);
            if (count == 0)
                lastcur = now.val;
            else {
                if (direction.equals("min")) {
                    if (now.val > lastcur)
                        rank++;
                } else if (direction.equals("max")) {
                    if (now.val < lastcur)
                        rank++;
                }
            }
            if (set != 'x') {
                ret.addElement((Distrib) now);
                a.removeElementAt(k);
            }
            if (set == 'x') {
                now.rank = rank;
                //System.out.println(count+"\tRANK: "+rank+"\tsize: "+a.size()+"\tSET: "+now.set);
                //System.out.println(a.size()+"\t"+rank);
                ret.addElement((Distrib) now);
                a.removeElementAt(k);
            }
            lastcur = now.val;
            count++;
        }
        return ret;
    }

}
