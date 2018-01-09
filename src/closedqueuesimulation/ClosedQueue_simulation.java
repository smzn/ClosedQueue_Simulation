package closedqueuesimulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class ClosedQueue_simulation {
	
	private double p[][], mu[]; //推移確率行列(今回は重力モデルから)
	private int time, node_index[];;
	Random rnd = new Random();
	private int k; //ノード数
	private int n; //系内の客数
	ArrayList<Double> eventtime[]; 
	ArrayList<String> event[];
	ArrayList<Integer> queuelength[];
	private double timerate[][];
	private double timerate2[][];
	private double correlation[][];
	ArrayList<Integer> timequeue[];
	private double d[][];
	private double speed;
	
	public ClosedQueue_simulation(double[][] p, int time, int k, int n, double[] mu, double [][]d, double speed, int[] node_index) {
		this.p = p;
		this.time = time;
		this.k = k;
		this.n = n;
		this.mu = mu;
		this.node_index = node_index;
		eventtime = new ArrayList[k];
		event = new ArrayList[k];
		queuelength = new ArrayList[k];
		timequeue = new ArrayList[k];
		for(int i = 0; i < eventtime.length; i++) eventtime[i] = new ArrayList<Double>();
		for(int i = 0; i < event.length; i++) event[i] = new ArrayList<String>();
		for(int i = 0; i < queuelength.length; i++) queuelength[i] = new ArrayList<Integer>();
		for(int i = 0; i < timequeue.length; i++) timequeue[i] = new ArrayList<Integer>();
		timerate = new double[k][n+1]; //0人の場合も入る
		timerate2 = new double[n+1][k+1]; //0人からn人のn+1, 0拠点からk拠点でのk+1拠点
		this.d = d;
		this.speed = speed;
		correlation = new double[k][k];
	}
	
	public double[][] getSimulation() {
		double service[] = new double[k];
		int queue[] = new int[k]; //各ノードのサービス中を含むキューの長さ
		double elapse = 0;
		for(int i = 0; i < this.n; i++) {
			event[0].add("arrival");
			queuelength[0].add(queue[0]);
			eventtime[0].add(elapse); //(移動時間0)
			queue[0]++; //最初はノード0にn人いるとする
		}
		service[0] = this.getExponential(mu[0]); //先頭客のサービス時間設定
		double total_queue[] = new double[k]; //各ノードの延べ系内人数
		double total_queuelength[] = new double[k]; //待ち人数
		double result[][] = new double[2][k];
		double dummy[][] = new double[n][3]; //移動時間設定用([i][0] = 移動時間, [i][1] = 移動元ノード番号, [i][2] = 移動先ノード番号)
		
		while(elapse < time) {
			double mini_service = 100000; //最小のサービス時間
			int mini_index = -1; //最小のサービス時間をもつノード
			int dummy_flag = 0; //0:dummyノードからの退去でない、1:dummyノードからの退去
			
			for(int i = 0; i < k; i++) { //待ち人数がいる中で最小のサービス時間を持つノードを算出
				if( queue[i] > 0) {
					if( mini_service > service[i]) {
						mini_service = service[i];
						mini_index = i;
					}
				}
			}
			
			//dummyノードでの残り時間を調べる
			for(int i = 0; i < dummy.length; i++) {
				if(dummy[i][0] > 0) {
					if(mini_service > dummy[i][0]) {
						mini_service = dummy[i][0];
						mini_index = i;
						dummy_flag = 1;
					}
				}
			}//dummyノードでの残り時間ここまで
			
			//通常ノードサービス時間処理
			for(int i = 0; i < k; i++) { //ノードiから退去
				total_queue[i] += queue[i] * mini_service;
				if( queue[i] > 0) service[i] -= mini_service;
				if( queue[i] > 0 ) total_queuelength[i] += ( queue[i] - 1 ) * mini_service;
				else if ( queue[i] == 0 ) total_queuelength[i] += queue[i] * mini_service;
				timerate[i][queue[i]] += mini_service; //イベント毎時登録
			}
			
			//dummyノード処理
			for(int i = 0; i < dummy.length; i++) {
				if(dummy[i][0] > 0) dummy[i][0] -= mini_service;
			}//dummyノード処理ここまで
			
			//各ノードでの人数割合(同時滞在人数) 
			for(int i = 0; i < n+1; i++) {
				int totalnumber = 0;
				for(int j = 0; j < queue.length; j++) {
					if(queue[j] == i) totalnumber ++;
				}
				timerate2[i][totalnumber] += mini_service; //イベント毎時登録
			}
			//イベント時間での待ち人数を登録
			for(int i = 0; i < k; i++) timequeue[i].add(queue[i]); //イベント毎時登録
			elapse += mini_service;
			
			if(dummy_flag == 0) { //通常ノードでの処理
				event[mini_index].add("departure"); //departure, arrival両方登録(イベント毎時登録)
				queuelength[mini_index].add(queue[mini_index]); //departure, arrival両方登録(イベント毎時登録)
				queue[mini_index] --; //通常ノードからの退去,dummyノードの場合、待ち行列は関係ない
				if( queue[mini_index] > 0) service[mini_index] = this.getExponential(mu[mini_index]); //退去後まだ待ち人数がある場合、サービス時間設定
				eventtime[mini_index].add(elapse); //経過時間の登録はイベント後,departure, arrival両方登録(イベント毎時登録)
			}
			
			//退去客の行き先決定、通常ノードからの退去はdummyノードへ、dummyノードからの退去は通常ノードへ
			if(dummy_flag == 0) {//通常ノードからの退去
				//退去後の行き先決定
				double rand = rnd.nextDouble();
				double sum_rand = 0;
				int destination_index = -1;
				for(int i = 0; i < p[0].length; i++) {
					sum_rand += p[mini_index][i];
					if( rand < sum_rand) {
						destination_index = i;
						break;
					}
				}
				if( destination_index == -1) destination_index = p[0].length -1; //乱数が1に近い時はif文にかからないので
				//退去後の行き先決定ここまで
				
				int dummy_index = -1;
				for(int i = 0; i < dummy.length; i++) {//dummyノードの空きノードを探す
					if(dummy[i][0] <= 0) {
						dummy_index = i;
						break; 
					}
				}
				dummy[dummy_index][0] = d[mini_index][destination_index] / speed; //移動速度は4km/h程度
				dummy[dummy_index][1] = mini_index;
				dummy[dummy_index][2] = destination_index;
			}
			else if ( dummy_flag == 1) { //dummyノードからの退去
				event[(int) dummy[mini_index][2]].add("arrival");
				queuelength[(int) dummy[mini_index][2]].add(queue[(int) dummy[mini_index][2]]);
				eventtime[(int) dummy[mini_index][2]].add(elapse); //(移動時間0)
				//推移先で待っている客がいなければサービス時間設定(即時サービス)
				if(queue[(int) dummy[mini_index][2]] == 0) service[(int) dummy[mini_index][2]] = this.getExponential(mu[(int) dummy[mini_index][2]]);
				queue[(int) dummy[mini_index][2]] ++;		
			}
		}
		
		for(int i = 0; i < k; i++) {
			result[0][i] = total_queue[i] / time;
			result[1][i] = total_queuelength[i] / time;
		}
		return result;
		
	}
	
	public double[][] getEvaluation() {
		int maxLength[] = new int[k];
		double result[][] = new double[3][k]; //平均系内時間、系内時間分散、最大待ち行列の長さ
		for(int k = 0; k < this.k; k++) {
			for(int i = 0; i < eventtime[k].size(); i++) {
				//System.out.println("Eventtime[" + k + "] : "+eventtime[k].get(i)+" Event : "+ event[k].get(i)+" Queuelength : "+queuelength[k].get(i));
				if( maxLength[k] < queuelength[k].get(i) ) maxLength[k] = queuelength[k].get(i);
			}
		}
		
		int arrival_number[] = new int[k];
		int departure_number[] = new int[k];
		int arrival_index[] = new int[k]; 
		int departure_index[] = new int[k];
		double systemtime[] = new double[k];
		double systemtime2[] = new double[k];
		
		for(int k = 0; k < this.k; k++) {
			for(int i = 0; i < eventtime[k].size(); i++) { //同じ客の到着と退去を探す
				if(event[k].get(i) == "arrival") {
					arrival_number[k]++;
					arrival_index[k] = i;
					for(int j = departure_index[k] + 1; j < eventtime[k].size(); j++) {
						if(event[k].get(j) == "departure") {
							departure_number[k]++;
						}
						if( arrival_number[k] == departure_number[k]) {
							departure_index[k] = j;
							systemtime[k] += eventtime[k].get(departure_index[k]) - eventtime[k].get(arrival_index[k]);
							systemtime2[k] += Math.pow(eventtime[k].get(departure_index[k]) - eventtime[k].get(arrival_index[k]),2);
							break;
						}
					}
				}
			}
		}
		
		for(int i = 0; i < k; i++) {
			result[0][i] = systemtime[i] / departure_number[i];
			result[1][i] = systemtime2[i] / departure_number[i] - Math.pow((systemtime[i] / departure_number[i]),2);
			result[2][i] = maxLength[i];
		}
		return result;
	}
	
	public double[][] getTimerate() {
		for(int k = 0; k < this.k; k++) {
			for(int i = 0; i< timerate[k].length; i++) timerate[k][i] /= time ;
			for(int i = 0; i< timerate[k].length; i++) timerate[k][i] *= 100 ;
		}
		return timerate;
	}
		
	public double[][] getTimerate2() {
		for(int n = 0; n < this.n; n++) {
			for(int i = 0; i< timerate2[n].length; i++) timerate2[n][i] /= time ;
			for(int i = 0; i< timerate2[n].length; i++) timerate2[n][i] *= 100 ;
		}
		return timerate2;
	}

		//指数乱数発生
		public double getExponential(double param) {
			return - Math.log(1 - rnd.nextDouble()) / param;
		}
		
		public double[][] getCorrelation(){//相関係数行列の作成
			//ArrayListから配列へ変換
			double elapsequeue [][] = new double[timequeue.length][timequeue[0].size()];
			for(int i = 0; i < k; i++) {
				for(int j = 0; j < timequeue[0].size(); j++) {
					elapsequeue[i][j] = timequeue[i].get(j);
				}
			}
			//相関係数行列作成
			for(int i = 0; i < k; i++) {
				for(int j = 0; j < k; j++) {
					correlation[i][j] = new PearsonsCorrelation().correlation(elapsequeue[i], elapsequeue[j]);
				}
			}
			return correlation;
		}
	
		public void getMySQL(double result[][]) {
			MySQL mysql = new MySQL(node_index);
			mysql.insertSimulation(168,node_index.length,time,2);//setting_id,facility_num,time,way_id
			mysql.insertQueue(result);
			mysql.insertCorrelation(correlation);
			mysql.insertTimerate(timerate2);
			mysql.insertEventtime(eventtime);
			mysql.insertTimequeue(timequeue);	
		}
}

