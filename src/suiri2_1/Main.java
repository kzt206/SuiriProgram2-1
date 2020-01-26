package suiri2_1;

import java.io.PrintWriter;

public class Main {
	public static void main(String... args) {
		
		
		int NN = 300;
		double[] H = new double[NN];
		double[] X = new double[NN];
		double[] DELT = new double[NN];
		double[] HDUM = new double[NN];
		double[] DDELT = new double[NN];
		double[] ELPR = new double[NN];
		double[] ETOUPR = new double[NN];
		double[] ECRPR = new double[NN];
		double[] HPR = new double[NN];
		double[] ELBPR = new double[NN];
		double[] DBDXPR = new double[NN];
		
		//パラメータの設定
		double XL1 = 0.12;
		double XL2 = 0.12;
		double XL3 = 0.12;
		double R0  = 0.10;
		double ANGS = 1/500.;
		double ANGC = Math.sqrt((1.0 - Math.pow(ANGS, 2.)));
		double AN = 0.0095;
		double Q  = 0.0076;
		double DG = 9.8;
		double DX = 0.005;
		
		double AMP = 10000.1;
		int IXL1 = (int) (XL1/AMP);
		int IXL2 = (int) (XL2/AMP);
		int IXL3 = (int) (XL3/AMP);
		int IDX = (int)(DX/AMP);
		int IEND = (IXL1+ IXL2 + IXL3)/IDX + 1;
 		
		for(int i = 0;i<IEND;i++) {
			X[i] = DX*i;
		}
		
		// ファイル出力
		try(PrintWriter pWriter = new PrintWriter("OutputFiles/singular.txt")){
			pWriter.println("test");
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
	}
}
