import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Hashtable;


public class Clustering {
	public static void main(String[] args) throws Exception{
		//実行環境に応じてパスを書き換えて下さい
		String inputFileName = "src/iris.data.kwansei.txt";
		BufferedReader br = new BufferedReader(new FileReader(inputFileName));
		String buffer;


		ClusterOpereation co = new ClusterOpereation();

		//irisData読み込み
		int cnt=0;
		while ((buffer = br.readLine()) != null){
			cnt++;
			co.saveIrisData(cnt,buffer);
		}
		br.close();

		//類似度(ユークリウッド距離)の計算
		for(int i=1;i<=co.iris_data.size();i++){
			Hashtable<Integer,Double> t = new Hashtable<Integer,Double>();
			for(int j =1;j<=co.iris_data.size();j++){
				t.put(j, co.calcEuclidean(co.iris_data.get(i), co.iris_data.get(j),i,j));
			}
			co.Euclidean_t.put(i,t);
		}


		for(int i=1;i<=co.iris_data.size();i++){
			pairCluster p = new pairCluster(String.valueOf(i),i,null,0.0);
			co.resultData.put(i,p);
		}



		String result = null;
		while(co.resultData.size()>1){
			double min_distance = 100;
			int match_key1 = 100;
			int match_key2 = 100;
			for(int key1:co.resultData.keySet() ){
				pairCluster p1 = co.resultData.get(key1);
				String [] p1_data = p1.pair.replace("{", "").replace("}", "").split(",");
				for(int i=0;i<p1_data.length;i++){
					for(int key2:co.resultData.keySet()){
						pairCluster p2 = co.resultData.get(key2);
						String [] p2_data = p2.pair.replace("{", "").replace("}", "").split(",");
						for(int j=0;j<p2_data.length;j++){
							int p1_num = Integer.parseInt(p1_data[i]);
							int p2_num = Integer.parseInt(p2_data[j]);
							if(min_distance >=co.Euclidean_t.get(p1_num).get(p2_num) && !p1_data[i].equals(p2_data) && key1!=key2){
								match_key1 = key1;
								match_key2 = key2;
								min_distance = co.Euclidean_t.get(p1_num).get(p2_num);
							}
						}
					}
				}
			}
			//要素の左側・右側にkeyを格納
			pairCluster p_left = co.resultData.get(match_key1);
			pairCluster p_right = co.resultData.get(match_key2);
			//matchした要素を削除
			co.resultData.remove(match_key1);
			co.resultData.remove(match_key2);

			pairCluster p = new pairCluster("{"+p_left.pair+","+p_right.pair+"}",p_left,p_right,min_distance);
			co.resultData.put(match_key1, p);
			result = "{"+p_left.pair+","+p_right.pair+"}";
//			System.out.println(resultData.size());
		}



        System.out.println("クラスター分析結果");
        System.out.println("");
		System.out.println(result);
	}


}



class ClusterOpereation{
	 Hashtable<Integer,Hashtable<Integer,Double>> Euclidean_t = new Hashtable<Integer,Hashtable<Integer,Double>>();
	 Hashtable<Integer,String []> iris_data = new Hashtable<Integer,String[]>();
	 HashMap<Integer,pairCluster> resultData = new HashMap<Integer,pairCluster>();

	 void saveIrisData(Integer num, String word){
		iris_data.put(num, word.split(",",0));
	}
	 double calcEuclidean(String[] x,String [] y,Integer i,Integer j){
		double d=0.0;
		if(i==j){
			d = 50.0;
		}
		else{
			for(int s=0;s<x.length-1;s++){
				double num1 = Double.parseDouble(x[s]);
				double num2 =  Double.parseDouble(y[s]);
				d +=(num1 - num2)*(num1 - num2) ;
			}
			d = Math.sqrt(d);
//			System.out.println(d);
		}
		return d;
	}
}


class pairCluster{
	String pair;
	Object left;
	Object right;
	double distance;

	pairCluster(String pair,Object l,Object r,double distance){
		this.pair = pair;
		this.left = l;
		this.right = r;
		this.distance = distance;
	}

}
