/** 
 * Resources used:
 * http://www.xmailserver.org/diff2.pdf
 * https://publications.mpi-cbg.de/Wu_1990_6334.pdf
 * */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Myers {
	public static int compareND;
	public static int compareNP;
	
	public static int Myer(List<Integer> A, List<Integer> B) {
        int n = A.size();
        int m = B.size();
        int max = n + m;
        Map<Integer, Integer> V = new HashMap<Integer, Integer>();
        for (int i = -max; i < max; i++) {
        	V.put(i, 0);
        }
        int x; 
        int y;
        for (int d = 0; d < max; d++) {
            for (int k = -d; k <= d; k += 2) {
                if (k == -d || (k != d && V.get(k-1) < V.get(k+1))) {
                	x = V.get(k+1);
                	compareND++;
                }
                else {
                	x = V.get(k-1) + 1;
                	compareND++;
                }
                y = x - k;
                while (x < n && y < m && A.get(x) == B.get(y)) {
                    x += 1;
                    y += 1;
                    compareND++;
                }
                V.replace(k, x);
                if ( x >= n && y >= m) 
                	return d;
            }
        }
		return max;
    }
	
	public static int WuManberMyersMiller(List<Integer> A, List<Integer> B) {
		int M = A.size();
		int N = B.size();
		int delta = N - M;
		Map<Integer, Integer> V = new HashMap<Integer, Integer>();
		for (int i = -(M+1); i <= N+1; i++) {
			V.put(i, -1);
		}
		int p = -1;
		while (V.get(delta) != N) {
			p += 1;
			for (int k = -p; k <= delta-1; k++) {
				V.replace(k, snake(A, B, k, Math.max(V.get(k-1)+1, V.get(k+1))));
				compareNP++;
			}
			for (int k = delta+p; k >= delta+1; k--) {
				V.replace(k, snake(A, B, k, Math.max(V.get(k-1)+1, V.get(k+1))));
				compareNP++;
			}
			V.replace(delta, snake(A, B, delta, Math.max(V.get(delta-1)+1, V.get(delta+1))));
			compareNP++;
		}
		return delta + 2*p;
	}
	
	public static int snake(List<Integer> A, List<Integer> B, int k, int y) {
		int x = y - k;
		int M = A.size();
		int N = B.size();
		while (x < M && y < N && A.get(x) == B.get(y)) {
			x += 1;
			y += 1;
			compareNP++;
		}
		return y;
	}
	public static void main(String[] args) {
		Random randalf = new Random();
		List<Integer> A;
		List<Integer> B;
		List<Integer> D = new ArrayList<Integer>(Arrays.asList(10, 50, 100, 200, 400, 600, 800, 1000));
		for (int i = 0; i < 8; i++) {
			compareND = 0;
			compareNP = 0;
			A = new ArrayList<Integer>();
			B = new ArrayList<Integer>();
			int M = 4000;
			int N = 5000;
			for (int j = 0; j < N; j++) {
				if (j < M) { 
					A.add((int) (Math.floor(100 * randalf.nextDouble())));
					B.add(A.get(j));
				}
				else B.add((int) (Math.floor(100 * randalf.nextDouble())));
			}
			for (int j = 0; j < D.get(i); j++) {
				B.remove((int) (Math.floor(N * randalf.nextDouble())));
				B.add(null);
			}
			for (int j = 0; j < N-M+D.get(i); j++) {
				int index = (int) (Math.floor(N * randalf.nextDouble()));
				B.remove(index);
				B.add(index, (int) (Math.floor(100 * randalf.nextDouble())));
            }
            int k = i+1;
			Myer(A, B);
			System.out.println("ND Run " + k + ": " + compareND);
			WuManberMyersMiller(A, B);
			System.out.println("NP Run " + k + ": " + compareNP);
		}
    }
}


