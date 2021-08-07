package 頻出パータンマイニング;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Enumerate {
	static ArrayList<String> dictionary = new ArrayList<>();
	static ArrayList<int []> database = new ArrayList<int []>();


	static boolean contain(int i,ArrayList<Integer> x, int [] transaction) {

		//[i or x]のlistを作る
		Set<Integer> check = new HashSet<Integer>();
		check.add(i);

		for(int j=0;j<x.size();j++){
			check.add(x.get(j));
		}


		int cnt=0;
		for(int j=0;j<transaction.length;j++){
			for(Integer key:check){
				if(key == transaction[j]){
					cnt++;
				}
			}
		}

		if(cnt == check.size()){
			return true;
		}


		return false;
	}


	static void readData(String inputFileName) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(inputFileName));
		String buffer;
		// Data読み込み
		while ((buffer = br.readLine()) != null) {
			String[] ary = buffer.split(" ", 0);
			int[] transaction = new int[ary.length];
			for (int i = 0; i < ary.length; i++) {
				String item = ary[i];
				int index = dictionary.indexOf(item);
				if (index == -1) {
					index = dictionary.size();
					dictionary.add(item);
				}

				transaction[i] = index;

			}
			Arrays.sort(transaction);
			database.add(transaction);
		}
		br.close();
	}

//全ての部分集合を出力
	static void print(ArrayList<Integer> x) {
		String [] print_list = new String[x.size()];
		for(int i=0;i<x.size();i++){
			print_list[i] = dictionary.get(x.get(i));
		}
		System.out.println(Arrays.toString(print_list));
	}


	void mine(ArrayList<Integer> x) throws Exception {

//		System.out.println(x);
		print(x);
		int tail;

		if (x.size() == 0) {
			tail = -1;
		} else{
			tail = x.get(x.size() - 1);
		}

		for (int i = tail + 1; i < dictionary.size(); i++) {
			int cnt =0;
			for(int [] t: database){
				if(contain(i,x,t)){
					cnt++;
				}
			}

			if(cnt >=2){
				x.add(i);
				mine(x);
				x.remove(x.size()-1);
			}
		}
	}



	public static void main(String[] args) throws Exception {

		readData("src/s.txt");

		long start=System.nanoTime();

		Enumerate busket = new Enumerate();

		busket.mine(new ArrayList<Integer>());

		long end=System.nanoTime();


		System.err.println((end-start)/1000000+"ミリ秒");


	}

}
