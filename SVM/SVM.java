import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;

public class SVM {
	public static void main(String[] args) throws Exception{
		//実行環境に応じてパスを書き換えて下さい
		String inputFileName = "src/iris.data.kwansei.txt";
		String trainFileName = "src/train.txt";
		String testFileName = "src/test.txt";

		BufferedReader br = new BufferedReader(new FileReader(inputFileName));
		BufferedWriter bw = new BufferedWriter(new FileWriter(trainFileName));
		BufferedWriter bt = new BufferedWriter(new FileWriter(testFileName));

		//フォーマットチェンジ
		format c = new format();
		//ランダム生成
		random_List d = new random_List();
		d.generate();

//		for(int i=0;i<10;i++){
//			System.out.println(d.random_list[i]);
//		}

		String buffer;
		int cnt=0;
		int num=0;

		while((buffer = br.readLine()) != null){
			c.Change(buffer);
			if(d.random_list[cnt] != num){
					bw.write(c.a+"\n");
//					System.out.println(c.a);
					c.a = "";
			}
			else{
				bt.write(c.a+"\n");
//				System.out.println(c.a);
				c.a = "";
				if(cnt<9){
					cnt++;
				}
			}
			num++;
		}

		br.close();
		bw.close();
		bt.close();
		//実行環境に応じてパスを書き換えて下さい
		Runtime r = Runtime.getRuntime();
		Process train_p = r.exec("src/svm-train.exe src/train.txt src/model.txt");
		Process test_p = r.exec("src/svm-predict.exe src/test.txt src/model.txt src/output.txt");

		train_p.waitFor();
		test_p.waitFor();

		System.out.println("実行コマンド:学習");
		System.out.println("");
		execute.execute_print(train_p);
		System.out.println("");
		System.out.println("実行コマンド:テスト");
		System.out.println("");
		execute.execute_print(test_p);

	}

}

class format{
	String a="";

	void Change(String word){
		String [] result = word.split(",",0);
		if(result[result.length-1].equals("Iris-setosa")){
			this.a += "+1";
		}
		else{
			this.a+="-1";
		}
		for(int i=0;i<result.length-1;i++){

			String b = " "+(i+1)+":";
			this.a+=b+result[i];
		}
	}
}

class random_List{
	Random generator = new Random();
	int [] random_list = new int[10];
	boolean [] check_list = new boolean [100];
	int cnt=0;
	void generate(){
		for(int i=0;i<100;i++){
			check_list[i] = false;
		}
		while(cnt!=10){
			int i = generator.nextInt(100);
			if(check_list[i] == false){
				random_list[cnt] = i;
				cnt++;
				check_list[i] = true;
			}
		}
		Arrays.sort(random_list);
	}
}

class execute{
	static String r;
	static void execute_print(Process p){
		try{
	        InputStream i = p.getInputStream();
	        InputStreamReader reader = new InputStreamReader(i,"Shift_JIS");
	        BufferedReader br = new BufferedReader(reader);
	        while ((r = br.readLine()) != null) {
	            System.out.println(r);
	        }
		}catch(IOException ex){
			ex.printStackTrace();
		}

	}
}

