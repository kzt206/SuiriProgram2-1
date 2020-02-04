package suiri2_1;

import java.io.PrintWriter;
import java.nio.DoubleBuffer;

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
		
		//Setting parameters
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
		
		
		//Number of grids
		double AMP = 10000.1;
		int IXL1 = (int) (XL1*AMP);
		int IXL2 = (int) (XL2*AMP);
		int IXL3 = (int) (XL3*AMP);
		int IDX = (int)(DX*AMP);
		int IEND = (IXL1+ IXL2 + IXL3)/IDX + 1;
 		
		for(int i = 0;i<IEND;i++) {
			X[i] = DX*i;
		}
		
		// ファイルの出力
		try(PrintWriter pWriter = new PrintWriter("OutputFiles/singular.txt")){
			pWriter.println("test");
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		//限界水深の計算 : HC
		double HC = Math.pow((Math.pow(Q, 2.)/DG/ANGC),0.333333);
		
		//特異点位置の計算 : XS
		double S1 = Math.pow(AN*Q,2.)/Math.pow(HC, 3.333333);
		double S2 = Math.pow((S1-ANGS)/ANGC, 2.);
		double XS = XL1 + XL2*0.5 + S2*Math.pow(R0, 2.)/(1.0 + S2);
		
		//特定点（鞍形点）位置の水面勾配
		double S3 = 3.333333 * Math.pow(AN*Q,2.)/Math.pow(HC, 4.3333333);
		double S4 = 3.0*Math.pow(Q, 2.)/DG/Math.pow(HC, 4.);
		double D2 = -1. * Math.pow(R0, 2.)/Math.pow((Math.pow(R0, 2.)-Math.pow(XS-XL1-XL2*0.5, 2.)),1.5);
		double DHS1 = (S3 + Math.pow(Math.pow(S3, 2.)-4.*ANGC*D2*S4, 0.5))/2.0/S4;
		double DHS2 = (S3 - Math.pow(Math.pow(S3, 2.)-4.*ANGC*D2*S4, 0.5))/2.0/S4;
		
		//*******************************************************************
		//     特異点から下流への水面形追跡
		//*******************************************************************
		
		//IINI : 上流端から数えた特異点直下流の格子番号
		int IINI = (int)(XS/DX) + 2;
		
		//DXI : 特異点とその直下流の格子点の座標の差
		//H(IINI) : 特異点直下流の格子点の水深
		double DXI = DX*(IINI-1) - XS;
		H[IINI] = HC + DXI * DHS2;
		
		double PARA1,PARA2,PARA3;
		
		if(X[IINI] >= XL1 && X[IINI] <= (XL1 + XL2)) {
			PARA1 = X[IINI] - XL2 * 0.5 - XL1;
			PARA2 = Math.pow(Math.pow(R0, 2.)-Math.pow(PARA1, 2.),0.5);
			PARA3 = Math.pow(Math.pow(R0, 2.)-Math.pow(XL2*0.5, 2.),0.5);
			// DELT : 路床高さ
			DELT[IINI] = PARA2 - PARA3;
			DDELT[IINI] = -PARA1/PARA2;
		}
		
		if(X[IINI] < XL1 || X[IINI] > (XL1+XL2)) {
			DELT[IINI] = 0.0;
			DDELT[IINI] = 0.0;
		}
		
		// HDUM:疑似等流水深の計算（存在しない場合は１００．０）
		if((ANGS-ANGC*DDELT[IINI]) <= 0.0) {
			HDUM[IINI] = 100.;
		}else {
			HDUM[IINI] = Math.pow((Math.pow(AN, 2.) * Math.pow(Q, 2.)/(ANGS - ANGC*DDELT[IINI])),0.3);
		}
		
		// 水面形の追跡（ルンゲ・クッタ法）
		for(int i = IINI +1 ;i<=IEND;i++) {
			
			
			
		}
		
		
	}
}
