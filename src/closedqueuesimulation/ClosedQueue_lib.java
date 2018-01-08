package closedqueuesimulation;

public class ClosedQueue_lib {
	
	private double KK = 0.145722117443322;
	private double a1, b1, c1;
	private double [][] d, a;
	private double []p, b;
	
	private double[] alpha, mu, lambda, L, R;
	private int N, NK, K;
	//ネットワーク内の総客数がn人の時のノートk゙でのスループット,平均ノード内客数,平均ノード通過時間(滞在時間)とする

	public ClosedQueue_lib(double a1, double b1, double c1, double[][] d, double[] p, double[] mu, int n, int k) {
		this.a1 = a1;
		this.b1 = b1;
		this.c1 = c1;
		this.d = d;
		this.p = p;
		this.mu = mu;
		N = n;
		NK = k-1;//ガウスで利用：拠点数-1で実施
		K = k;
		lambda = new double[K];
		L = new double[K];
		R = new double[K];
		for(int i = 0; i < L.length; i++) L[i] = 0;
	}

	public void setA(double[][] a) {
		this.a = a;
	}

	public void setB(double[] b) {
		this.b = b;
	}

	public void setAlpha(double[] alpha) {
		this.alpha = alpha;
	}

	public double[] getLambda() {
		return lambda;
	}

	public double[] getL() {
		return L;
	}

	public double[] getR() {
		return R;
	}

	public double [][] calcGravity(){
		int N = this.N-1; 
		
		double f[][] = new double[p.length][p.length];
		
		for(int i = 0; i < p.length; i++){
			for(int j = 0; j < p.length; j++){
				f[i][j] = KK*Math.pow(p[i],a1)*Math.pow(p[j],b1)/Math.pow(d[i][j],c1);
			}
		}
		//行和を１に正規化
		for(int i = 0; i < p.length; i++){
			double sum = 0;
			for(int j = 0; j < p.length; j++){
				sum += f[i][j];
			}
			for(int j = 0; j < p.length; j++){
				f[i][j] /= sum;
			}
		}
		return f;	
	}
	
	public double [] calcGauss(){
		int p;
		double pmax, s;
		double w[] = new double[NK];
		/* 前進消去（ピボット選択）*/
		for(int k = 0; k < NK-1; k++){  /* 第ｋステップ */
		      p = k;
		      pmax = Math.abs( a[k][k] );
		      for(int i = k+1; i < NK; i++){  /* ピボット選択 */
		         if(Math.abs( a[i][k] ) > pmax){
		            p = i;
		            pmax = Math.abs( a[i][k] );
		         }
		      }

		      if(p != k){  /* 第ｋ行と第ｐ行の交換　*/
		         for(int i = k; i < NK; i++){
		            /* 係数行列　*/
		            s = a[k][i];
		            a[k][i] = a[p][i];
		            a[p][i] = s;
		         }
		         /* 既知ベクトル */
		         s = b[k];
		         b[k] = b[p];
		         b[p] = s;
		      }
		/* 前進消去 */
		      for(int i = k +1; i < NK; i++){ /* 第ｉ行 */
		         w[i] = a[i][k] / a[k][k];
		         a[i][k] = 0.0;
		         /* 第ｋ行を-a[i][k]/a[k][k]倍して、第ｉ行に加える */
		         for(int j = k + 1; j < NK; j++){
		            a[i][j] = a[i][j] - a[k][j] * w[i];
		         }
		         b[i] = b[i] - b[k] * w[i];
		      }
		   }
		/* 後退代入 */
		      for(int i = NK - 1; i >= 0; i--){
		         for(int j = i + 1; j < NK; j++){
		            b[i] = b[i] - a[i][j] * b[j];
		            a[i][j] = 0.0;
		         }
		         b[i] = b[i] / a[i][i];
		         a[i][i] = 1.0;
		      }
		
		return b;
	}


	public void calcAverage(){
		int n = 0;
		while(n < N){
			n++;
			//Step3 Rの更新
			for(int i = 0; i < K;i++){
				R[i] = (L[i] + 1)/mu[i];
			}
						
			//Step4 Lambdaの更新
			for(int i = 0; i < K;i++){
				double sum = 0;
				for(int j = 0; j < K; j++) sum += alpha[j]*R[j]; 
				if(i == 0) lambda[i] = n/sum;
				else lambda[i] = alpha[i]*lambda[0];
			}
						
			//Step5 Lの更新
			for(int i = 0; i < K; i++){
				L[i] = lambda[i]*R[i];
			}
			
		}
	}
}
