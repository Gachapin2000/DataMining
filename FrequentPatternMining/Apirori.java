package 頻出パータンマイニング;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;



public class Apirori {

	static Vector<String[]> readdata = new Vector<String[]>();//元データ
	static Vector<ArrayList<Integer>> processed_data = new Vector<ArrayList<Integer>>(); //トランザクションデータの集合
	static Hashtable<String,Integer> original_data  = new Hashtable<String,Integer>();
	static Hashtable<ArrayList<Integer>,Integer> itemset  = new Hashtable<ArrayList<Integer>,Integer>();
	static Vector<ArrayList<Integer>> nouseCombiData = new Vector<ArrayList<Integer>>();
	static Set<Integer> unique_item = new HashSet<Integer>();
	static ArrayList<Integer> d =   new ArrayList<Integer>();


	//データ読み込み
	static void readData(String inputFileName) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(inputFileName));
		String buffer;
		// Data読み込み
		while ((buffer = br.readLine()) != null) {
			readdata.add(buffer.split(" ", 0));
		}
		br.close();
	}
//	文字データを数的データに変換
	static void  dataChange(){
		//元データを番号に置き換える
		for(int i=0;i<readdata.size();i++){
			ArrayList<Integer> n_list = new ArrayList<Integer>();
			for(int j=0;j<readdata.get(i).length;j++){
				for(String key: original_data.keySet()){
					if(key.equals(readdata.get(i)[j])){
						n_list.add(original_data.get(key));

					}
				}
				}
			processed_data.add(n_list);
			}
	}
//itemsetを作る
	static void itemset(){
		HashSet<String> set=new HashSet<>();
		//重複なしのアイテムの集合を取る
		for(int i=0;i<readdata.size();i++){
			for(int j=0;j<readdata.get(i).length;j++){
				set.add(readdata.get(i)[j]);
				}
			}
		int cnt=0;
		//original_data→String型のデータをInt型で保持 例)[A,B,C]→[1,2,3] アイテムセットの集合
		//itemset→アイテムセットの集合でsupport値の出現回数を記録する。
		for(String s:set){
			cnt++;
			unique_item.add(cnt);

			original_data.put(s,cnt);
			ArrayList<Integer> n = new ArrayList<Integer>();
			n.add(original_data.get(s));
			itemset.put(n,0);
			}
	}



	//アイテムセットの出現回数を記録する
	static void  supportCount(){
		   for(int i=0;i<processed_data.size();i++){
				for(ArrayList<Integer> key:itemset.keySet()){
					if(processed_data.get(i).containsAll(key)){
						itemset.put(key,itemset.get(key)+1);
					}
				}
		   }

	}

	//最小支持度で判定
	static void thresholdjudge(int threshold){
		//最小支持度より小さい組み合わせをnouseCombiDataに代入
		for(ArrayList<Integer> key:itemset.keySet()){
			if(threshold>itemset.get(key)){
				nouseCombiData.add(key);
			}
		}
		//最小支持度より小さい組み合わせをitemsetから削除
		for(int i=0;i<nouseCombiData.size();i++){
			itemset.remove(nouseCombiData.get(i));
		}
        nouseCombiData.clear();

	}

	//アイテムセットを更新
	static void updateItemSet(int conbiNum){
		Hashtable<ArrayList<Integer>,Integer> tempitemset  = new Hashtable<ArrayList<Integer>,Integer>();
		ArrayList<Integer> b = new ArrayList<Integer>();
		//アイテムセットを１つのArrayListに全て結合
        for(ArrayList<Integer> key:itemset.keySet()){
        	b.addAll(key);
        }
        //アイテムセットの集合で不要になったアイテムがあるかをチェック
        for(Integer key:unique_item){
        	if(!b.contains(key)){
        		d.add(key);
        	}
        }
        //不要になったアイテムをアイテムセットの集合から削除
        for(int i=0;i<d.size();i++){
        	unique_item.remove(d.get(i));
        }


        ArrayList<Integer> n2 = new ArrayList<>(unique_item);
        //次の組み合わせを作成
        for(ArrayList<Integer> key: getCombi(n2,conbiNum)){
        	tempitemset.put(key,0);
        }
        itemset = tempitemset;
	}
//結果の出力
	static void print(){
		System.out.println(itemset);
	}
//nCrの組み合わせを生成
	static void setCombi(ArrayList<Integer> n, Integer r, Set<ArrayList<Integer>> result){
		if(n.size() == r){
			result.add(n);
			return;
		}
		for(int i=0;i<n.size();i++){
            ArrayList<Integer> temp = new ArrayList<Integer>();
            temp.addAll(n);
            temp.remove(i);
            setCombi(temp,r,result);
		}
	}

	static Set<ArrayList<Integer>> getCombi(ArrayList<Integer> n, Integer r){
		Set<ArrayList<Integer>> result = new HashSet<ArrayList<Integer>>();
		setCombi(n,r,result);
		return result;
	}




	public static void main(String[] args) throws Exception{

	   readData("src/sample.txt");
	   itemset();
	   dataChange();
	   int i = 2;
	   while(itemset.size()>1){
		   supportCount();
		   thresholdjudge(2);//最小支持度は任意で選んで下さい
		   print();
		   updateItemSet(i);
		   i++;
	   }




	}
}




