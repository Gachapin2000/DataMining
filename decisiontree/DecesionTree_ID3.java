package 決定木;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class DecesionTree_ID3 {
	static ArrayList<String> dictionary = new ArrayList<>(); //データセットの説明変数名
	static ArrayList<String> sorted_label = new ArrayList<>();//目的変数

	//平均情報量の計算
	static double entropy(ArrayList<String> data){
		int p=0,n=0;
		for(String label: data){
			if(label.equals(sorted_label.get(0))){
				p++;
			}
			else{
				n++;
			}
		}
		if(p == 0 || n == 0){
			return 0.0;
		}
		double a,b,ans;
		a = (double)p/(p+n);
		b = (double)n/(p+n);

		ans = (-1*a)*Math.log(a)/Math.log(2.0) - b*Math.log(b)/Math.log(2.0);

		return ans;

	}

	//ArrayList用のsetメソッド
	static ArrayList<String> set(ArrayList<String> data ){

		 ArrayList<String> sorted_data  = new ArrayList<String>(new HashSet<>(data));
		 Collections.sort(sorted_data);

		return sorted_data;
	}

	//属性でデータを分類する
	static LinkedHashMap<String, ArrayList<String>> classifying_data(String col,String elem,LinkedHashMap<String, ArrayList<String>> data){
		LinkedHashMap<String, ArrayList<String>> classified_data = new LinkedHashMap<>();

		ArrayList<Integer> need_list = new ArrayList<>();

		for(int i=0;i<data.get(col).size();i++){
			if(data.get(col).get(i).equals(elem)){
				need_list.add(i);
			}
		}
		for(String key: data.keySet()){
			ArrayList<String> temp = new ArrayList<>();
			if(data.get(key).size()== 0){
				classified_data.put(key,temp);
			}
			else{
				for(int j:need_list){
					temp.add(data.get(key).get(j));
				}
				classified_data.put(key,temp);
			}

		}

		return classified_data;
	}
	//データ読み込み
	static LinkedHashMap<String, ArrayList<String>> readData(String inputFileName) throws Exception {
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		File f = new File(inputFileName);
		BufferedReader br = new BufferedReader(new FileReader(f));
		String buffer;
		buffer = br.readLine();
		String [] head = buffer.split(",");
		dictionary = new ArrayList<String>(Arrays.asList(head));
		// Data読み込み
		while ((buffer = br.readLine()) != null) {
			String [] index = buffer.split(",");
			ArrayList<String> temp = new ArrayList<String>(Arrays.asList(index));
			data.add(temp);

		}
		br.close();

		LinkedHashMap<String, ArrayList<String>> processed_data = new LinkedHashMap<>();

		for(int i=0;i<dictionary.size();i++){
			ArrayList<String> temp = new ArrayList<>();
			for(int j=0;j<data.size();j++){
				temp.add(data.get(j).get(i));
			}
			processed_data.put(dictionary.get(i),temp);
		}

		sorted_label = set(processed_data.get(dictionary.get(dictionary.size()-1)));
		return processed_data;
	}

	//属性ごとに分けたデータをマージ→
	static Node data_merge(ArrayList<Node> node_list){
		Node merged_Node = new Node();
		if(node_list.size() == 0){
			return merged_Node;
		}
		LinkedHashMap<String, ArrayList<String>> merged_data = new LinkedHashMap<>();
		double merged_entropy=0.0;
		ArrayList<String> merged_elem  = new ArrayList<>();
		merged_data = node_list.get(0).data;
		merged_entropy = node_list.get(0).entropy;
		merged_elem.addAll( node_list.get(0).elem);


		for(int i=1;i<node_list.size();i++){
			for(String key:node_list.get(i).data.keySet()){
				merged_data.get(key).addAll(node_list.get(i).data.get(key));
			}

			merged_entropy+=node_list.get(i).entropy;
			merged_elem.addAll( node_list.get(i).elem);
		}
		merged_Node.data = merged_data;
		merged_Node.entropy = merged_entropy;
		merged_Node.elem.addAll(merged_elem);

		return merged_Node;
	}

	static Node max_gain_data(ArrayList<Node> node_list,double all_entropy){
		Node max_gain_Node = new Node();
		double max = 0;
		for(int i=0;i<node_list.size();i++){
			if( max < all_entropy - node_list.get(i).entropy ){
				max = all_entropy - node_list.get(i).entropy;
				max_gain_Node.data = node_list.get(i).data;
				max_gain_Node.elem = node_list.get(i).elem;
				max_gain_Node.drop_num = i;
			}
		}
		return max_gain_Node;
	}
	//決定木をprintするためのもの
	static String  createlabel(ArrayList<String> label_list,String key){
		String make_label = "";
		int cnt1=0;
		int cnt2=0;

		if(label_list.size() == 0){
			make_label = key+" ['"+sorted_label.get(0)+":"+cnt1+"', '"+sorted_label.get(1)+":"+cnt2+"']";
			return make_label;
		}

		for(int i=0;i<label_list.size();i++){
			if(sorted_label.get(0).equals(label_list.get(i))){
				cnt1++;
			}
			else{
				cnt2++;
			}
		}
		make_label = key+" ['"+sorted_label.get(0)+":"+cnt1+"', '"+sorted_label.get(1)+":"+cnt2+"']";
		return make_label;
	}
	//決定木の接点を作るのに必要なデータだけ抽出
	static LinkedHashMap<String, ArrayList<String>> need_data(Node h_Node,String key){
		LinkedHashMap<String, ArrayList<String>>data = new LinkedHashMap<>();

		if(h_Node.data.get(dictionary.get(h_Node.drop_num)).size() == 0){
			return data;
		}
		for(String elem:h_Node.data.keySet()){
			ArrayList<String> temp = new ArrayList<>();
			for(int i=0;i<h_Node.data.get(elem).size();i++){
				if(h_Node.data.get(dictionary.get(h_Node.drop_num)).get(i).equals(key)){
					if(!elem.equals(dictionary.get(h_Node.drop_num))){
						temp.add(h_Node.data.get(elem).get(i));
					}
				}
			}
			data.put(elem, temp);
		}
		return data;
	}


	static void traverse(int depth, String nodeString) {

		String indent="   ";
		//String indent="\t";

		for(int i=0;i<depth;i++) {
			System.out.print("|"+indent);
		}
		System.out.println(nodeString);

//		if(depth<3) {
//			traverse(depth+1, nodeString+"0");
//			traverse(depth+1, nodeString+"1");
//		}


	}

	//id3アルゴリズム実行
	static void id3_execute(tree data){
		traverse(data.depth, data.labels);
		double all_entropy = entropy(data.node.data.get(dictionary.get(dictionary.size()-1)));

		ArrayList<Node> node_list = new ArrayList<>();

		for(String key:data.node.data.keySet()){
			ArrayList<Node> temp = new ArrayList<>();
			for(String key2: set(data.node.data.get(key))){
				Node node = new Node();
				node.data = classifying_data(key,key2,data.node.data);
				node.elem.add(key2);
				node.entropy = entropy(node.data.get(dictionary.get(dictionary.size()-1)))*node.data.get(dictionary.get(dictionary.size()-1)).size()/data.node.data.get(key).size();
				temp.add(node);
			}
			node_list.add(data_merge(temp));
		}

		node_list.remove(dictionary.size()-1);
		Node h_Node = max_gain_data(node_list, all_entropy);
		for(int i=0;i<h_Node.elem.size();i++){
			tree pre_tree = new tree();
			pre_tree.node.data = need_data(h_Node, h_Node.elem.get(i));
			pre_tree.labels = dictionary.get(h_Node.drop_num)+"="+createlabel(pre_tree.node.data.get(dictionary.get(dictionary.size()-1)), h_Node.elem.get(i));
			pre_tree.depth = data.depth+1;
			id3_execute(pre_tree);
		}


	}




	public static void main(String[] args) throws Exception {
		tree id3 = new tree();
		id3.node.data = readData("src/決定木/data.csv");
		id3.labels = createlabel(id3.node.data.get(dictionary.get(dictionary.size()-1)),dictionary.get(dictionary.size()-1));
		id3_execute(id3);

	}

}

class tree{
	String labels;//目的変数
	Node node = new Node();
	int depth=0;

	public String toString(){
		return "{"+labels+"}";
	}
}

class Node{
	double entropy=10.0;
	LinkedHashMap<String, ArrayList<String>>data = new LinkedHashMap<>();
	ArrayList<String> elem = new ArrayList<>();
	int drop_num;

	public String toString(){
		return  "{"+entropy+","+data+","+elem+","+drop_num+"}";
	}

}
