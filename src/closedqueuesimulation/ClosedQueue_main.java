package closedqueuesimulation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;


public class ClosedQueue_main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int N = 100, K = 12;
		double [][]d = new double[K][K];
		double p[] = new double[K];
		double mu[] = new double[K];
		
		//CSVから取り込み
		try {
		      File f = new File("csv/distance.csv");
		      BufferedReader br = new BufferedReader(new FileReader(f));
		 
		      String[][] data = new String[K][K+2];
		      String line = br.readLine();
		      for (int row = 0; line != null; row++) {
		        data[row] = line.split(",", 0);
		        line = br.readLine();
		      }
		      br.close();

		      // CSVから読み込んだ配列の中身を表示
		      for(int row = 0; row < data.length; row++) {
		        for(int col = 0; col < data[0].length; col++) {
		        		if( col < data[0].length -2 ) {
		        			d[row][col] = Double.parseDouble(data[row][col]);
		        		}
		        		else if (col == data[0].length -2) p[row] = Double.parseDouble(data[row][col]);
		        		else if (col == data[0].length -1) mu[row] = Double.parseDouble(data[row][col]);
		        }
		      } 

		    } catch (IOException e) {
		      System.out.println(e);
		    }
		//CSVから取り込みここまで
		
		//拠点間距離表、今回の利用ノード14,15,17,18,21,24,27,29,31,34,	35,41, ID = 168, 目的関数値99.858376656572
		/*
		double d[][] = { {1000000,99.60499955,75.18117736,224.522541,281.1047231,247.6783163,107.5549967,471.3517314,412.6888475,619.2591876,566.5879236,751.5004453},
			{99.60499955,1000000,99.70042189,145.6596134,285.1922216,212.00125,151.1771663,441.2260804,394.7281194,588.9744559,524.8301389,708.8082316},
			{75.18117736,99.70042189,1000000,172.5896787,209.0737091,174.1770175,51.51387248,396.1967615,337.831538,544.0780515,492.1199131,676.9012699},
			{224.522541,145.6596134,172.5896787,1000000,231.9133264,119.3524133,203.209132,323.5421831,294.5494511,466.147522,393.1747315,573.912839},
			{281.1047231,285.1922216,209.0737091,231.9133264,1000000,116.5662685,176.988878,214.5888698,143.1077297,353.8377091,322.4300273,499.4821346},
			{247.6783163,212.00125,174.1770175,119.3524133,116.5662685,1000000,172.4863298,229.6796798,184.8118637,377.9105156,319.4302435,504.4633262},
			{107.5549967,151.1771663,51.51387248,203.209132,176.988878,172.4863298,1000000,378.8237636,314.5904352,524.5219983,480.0993824,663.3300296},
			{471.3517314,441.2260804,396.1967615,323.5421831,214.5888698,229.6796798,378.8237636,1000000,75.6053595,148.4387747,109.3941727,285.6677534},
			{412.6888475,394.7281194,337.831538,294.5494511,143.1077297,184.8118637,314.5904352,75.6053595,1000000,210.9863171,184.9151989,356.9596753},
			{619.2591876,588.9744559,544.0780515,466.147522,353.8377091,377.9105156,524.5219983,148.4387747,210.9863171,1000000,91.999341,154.4916763},
			{566.5879236,524.8301389,492.1199131,393.1747315,322.4300273,319.4302435,480.0993824,109.3941727,184.9151989,91.999341,1000000,185.0340246},
			{751.5004453,708.8082316,676.9012699,573.912839,499.4821346,504.4633262,663.3300296,285.6677534,356.9596753,154.4916763,185.0340246,1000000}};
		//double p[] = {5,5,10,5,5,5,5,5,5,10,5,10};
		//double mu[] = {5,5,10,5,5,5,5,5,5,10,5,10};
		//確認用
		//double d[][] = {{1000,5,8,10,15},{5,1000,3,5,3},{8,3,1000,2,4},{10,5,2,1000,3},{15,3,4,3,1000}};//行を入力
		//double p[] = {10,15,5,5,5};//拠点人口表
		//double mu[] = {1,2,2,1,2};//サービス率
		*/
		double alpha[] = new double[mu.length],alpha1[] = new double[mu.length];		
		ClosedQueue_lib clib = new ClosedQueue_lib(1,1,0.5,d,p, mu, N, K);
		double f[][] = new double [p.length][p.length];
		f = clib.calcGravity(); //fは推移確率行列
		/* fの確認用
		double f[][] = {{0,0.11,0.254,0.073,0.066,0.07,0.106,0.051,0.054,0.088,0.046,0.08},
			{0.112,0,0.225,0.093,0.066,0.077,0.091,0.053,0.056,0.092,0.049,0.084},
			{0.13,0.113,0.001,0.086,0.078,0.085,0.157,0.057,0.061,0.096,0.051,0.086},
			{0.077,0.095,0.175,0,0.075,0.105,0.081,0.064,0.067,0.106,0.058,0.096},
			{0.067,0.066,0.155,0.073,0,0.104,0.084,0.076,0.093,0.119,0.062,0.1},
			{0.068,0.074,0.163,0.098,0.099,0,0.082,0.071,0.079,0.11,0.06,0.096},
			{0.097,0.082,0.28,0.07,0.075,0.076,0,0.052,0.057,0.088,0.046,0.078},
			{0.05,0.051,0.108,0.06,0.073,0.071,0.055,0,0.124,0.177,0.103,0.127},
			{0.054,0.055,0.119,0.064,0.092,0.081,0.062,0.126,0,0.151,0.081,0.116},
			{0.052,0.053,0.11,0.059,0.068,0.066,0.056,0.105,0.088,0.001,0.134,0.207},
			{0.046,0.048,0.099,0.056,0.061,0.062,0.05,0.105,0.081,0.23,0,0.162},
			{0.055,0.056,0.115,0.062,0.067,0.067,0.058,0.089,0.079,0.241,0.11,0.001}};
			*/
		
		//トラフィック方程式を解く準備
		double ff[][] = new double[mu.length -1][mu.length -1];
		double bb[] = new double[mu.length -1];
		for(int i = 0; i < mu.length -1; i++){
			for(int j = 0; j < mu.length -1; j++){
				if( i == j ) {
					ff[i][j] = f[j + 1][i + 1] - 1; 
				}else {
					ff[i][j] = f[j + 1][i + 1];
				}
			}
		}
		for(int i = 0;i < mu.length -1; i++){
			bb[i] = -f[0][i+1];
		}
		
		
		//alphaを求める
		clib.setA(ff);
		clib.setB(bb);
		alpha = clib.calcGauss();
		
		//alphaの配列の大きさが-1になってしまうので、元の大きさのalpha1に入れ直す
		for(int i = 0 ; i < alpha1.length; i++){
			if( i == 0) alpha1[i] = 1;
			else alpha1[i] = alpha[i-1];
		}
		
		clib.setAlpha(alpha1);
		
		//平均値解析法で平均系内人数を求める
		clib.calcAverage();
		double[] L = clib.getL();
		double[] R = clib.getR();
		double[] lambda = clib.getLambda();
		
		//理論値
		System.out.println("重力モデルパラメタ" +Arrays.deepToString(d));
		System.out.println("人気度" +Arrays.toString(p));
		System.out.println("サービス率" +Arrays.toString(mu));
		System.out.println("推移確率行列" +Arrays.deepToString(f));
		System.out.println("トラフィック方程式解" +Arrays.toString(alpha1));
		System.out.println("理論値 : 平均系内人数 = " +Arrays.toString(L));
		System.out.println("理論値 : 平均系内時間 = " +Arrays.toString(R));
		System.out.println("理論値 : スループット = " +Arrays.toString(lambda));
		
		//Simulation
		ClosedQueue_simulation qsim = new ClosedQueue_simulation(f, 100000, f.length, N, mu, d, 4000);
		System.out.println("Simulation : (平均系内人数, 平均待ち人数) = "+Arrays.deepToString(qsim.getSimulation()));
		System.out.println("Simulation : (系内時間,系内時間分散,最大待ち人数) = "+Arrays.deepToString(qsim.getEvaluation()));
		System.out.println("Simulation : (時間割合) = "+Arrays.deepToString(qsim.getTimerate()));
		System.out.println("Simulation : (同時時間割合) = "+Arrays.deepToString(qsim.getTimerate2()));
		System.out.println("Simulation : (相関係数行列) = "+Arrays.deepToString(qsim.getCorrelation()));
	}

}
