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
		
	}
}
