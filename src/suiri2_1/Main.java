package suiri2_1;

import java.io.PrintWriter;
import java.nio.DoubleBuffer;
import java.security.spec.ECParameterSpec;
import java.util.EventListenerProxy;
import java.util.IllegalFormatCodePointException;
import java.util.function.DoubleToLongFunction;

import org.omg.CosNaming._BindingIteratorImplBase;

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

		// Setting parameters
		double XL1 = 0.12;
		double XL2 = 0.12;
		double XL3 = 0.12;
		double R0 = 0.10;
		double ANGS = 1 / 500.;
		double ANGC = Math.sqrt((1.0 - Math.pow(ANGS, 2.)));
		double AN = 0.0095;
		double Q = 0.0076;
		double DG = 9.8;
		double DX = 0.005;

		// Number of grids
		double AMP = 10000.1;
		int IXL1 = (int) (XL1 * AMP);
		int IXL2 = (int) (XL2 * AMP);
		int IXL3 = (int) (XL3 * AMP);
		int IDX = (int) (DX * AMP);
		int IEND = (IXL1 + IXL2 + IXL3) / IDX + 1;

		for (int i = 0; i < IEND; i++) {
			X[i] = DX * i;
		}

		// ファイルの出力
		try (PrintWriter pWriter = new PrintWriter("OutputFiles/singular.txt")) {
			pWriter.println("test");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 限界水深の計算 : HC
		double HC = Math.pow((Math.pow(Q, 2.) / DG / ANGC), 0.333333);

		// 特異点位置の計算 : XS
		double S1 = Math.pow(AN * Q, 2.) / Math.pow(HC, 3.333333);
		double S2 = Math.pow((S1 - ANGS) / ANGC, 2.);
		double XS = XL1 + XL2 * 0.5 + S2 * Math.pow(R0, 2.) / (1.0 + S2);

		// 特定点（鞍形点）位置の水面勾配
		double S3 = 3.333333 * Math.pow(AN * Q, 2.) / Math.pow(HC, 4.3333333);
		double S4 = 3.0 * Math.pow(Q, 2.) / DG / Math.pow(HC, 4.);
		double D2 = -1. * Math.pow(R0, 2.) / Math.pow((Math.pow(R0, 2.) - Math.pow(XS - XL1 - XL2 * 0.5, 2.)), 1.5);
		double DHS1 = (S3 + Math.pow(Math.pow(S3, 2.) - 4. * ANGC * D2 * S4, 0.5)) / 2.0 / S4;
		double DHS2 = (S3 - Math.pow(Math.pow(S3, 2.) - 4. * ANGC * D2 * S4, 0.5)) / 2.0 / S4;

		// *******************************************************************
		// 特異点から下流への水面形追跡
		// *******************************************************************

		// IINI : 上流端から数えた特異点直下流の格子番号
		int IINI = (int) (XS / DX) + 2;

		// DXI : 特異点とその直下流の格子点の座標の差
		// H(IINI) : 特異点直下流の格子点の水深
		double DXI = DX * (IINI - 1) - XS;
		H[IINI] = HC + DXI * DHS2;

		double PARA1, PARA2, PARA3, PARA4, PARA5, PARA6, PARA7;
		double DDELT0 = 0.0;
		double DDELT1 = 0.0;
		double DDELT2 = 0.0;

		if (X[IINI] >= XL1 && X[IINI] <= (XL1 + XL2)) {
			PARA1 = X[IINI] - XL2 * 0.5 - XL1;
			PARA2 = Math.pow(Math.pow(R0, 2.) - Math.pow(PARA1, 2.), 0.5);
			PARA3 = Math.pow(Math.pow(R0, 2.) - Math.pow(XL2 * 0.5, 2.), 0.5);
			// DELT : 路床高さ
			DELT[IINI] = PARA2 - PARA3;
			DDELT[IINI] = -PARA1 / PARA2;
		}

		if (X[IINI] < XL1 || X[IINI] > (XL1 + XL2)) {
			DELT[IINI] = 0.0;
			DDELT[IINI] = 0.0;
		}

		// HDUM:疑似等流水深の計算（存在しない場合は１００．０）
		if ((ANGS - ANGC * DDELT[IINI]) <= 0.0) {
			HDUM[IINI] = 100.;
		} else {
			HDUM[IINI] = Math.pow((Math.pow(AN, 2.) * Math.pow(Q, 2.) / (ANGS - ANGC * DDELT[IINI])), 0.3);
		}

		// 水面形の追跡（ルンゲ・クッタ法）
		for (int i = IINI + 1; i <= IEND; i++) {
			if (X[i - 1] < XL1 || X[i - 1] >= (XL1 + XL2)) {
				DELT[i - 1] = 0.0;
				DDELT[i - 1] = 0.0;
				DDELT0 = DDELT[i - 1];
				DDELT1 = 0.;
				DDELT2 = 0.;
			}
			// ルンゲクッタ法で計算するための前処理
			if (X[i - 1] >= XL1 && X[i - 1] < (XL1 + XL2)) {
				PARA1 = X[i - 1] - XL2 * 0.5 - XL1;
				PARA2 = Math.pow(R0 * R0 - PARA1 * PARA1, 0.5);
				PARA3 = Math.pow(R0 * R0 - (XL2 * 0.5) * (XL2 * 0.5), 0.5);
				DELT[i - 1] = PARA2 - PARA3;
				DDELT[i - 1] = -PARA1 / PARA2;
				DDELT0 = DDELT[i - 1];
				PARA4 = X[i - 1] + DX * 0.5 - XL2 * 0.5 - XL1;
				PARA5 = Math.pow(R0 * R0 - PARA4 * PARA4, 0.5);
				DDELT1 = -PARA4 / PARA5;
				PARA6 = X[i - 1] + DX - XL2 * 0.5 - XL1;
				PARA7 = Math.pow(R0 * R0 - PARA6 * PARA6, 0.5);
				DDELT2 = -PARA6 / PARA7;
			}
			// 疑似等流水深の計算（存在しない場合 HDUM = 100.0とする)
			if ((ANGS - ANGC * DDELT2) <= 0.) {
				HDUM[i] = 100.;
			} else {
				HDUM[i] = Math.pow(AN * AN * Q * Q / (ANGS - ANGC * DDELT2), 0.3);
			}

			// 水深の計算（ルンゲ・クッタ法）
			double ED1 = AN * AN * Q * Q / Math.pow(H[i - 1], 3.3333333);
			double DUM1 = DX * (ANGS - ANGC * DDELT0 - ED1) / (ANGC - Q * Q / DG / Math.pow(H[i - 1], 3));
			double HDUM2 = H[i - 1] + DUM1 * 0.5;
			double ED2 = AN * AN * Q * Q / Math.pow(HDUM2, 3.333333333);
			double DUM2 = DX * (ANGS - ANGC * DDELT1 - ED2) / (ANGC - Q * Q / DG / Math.pow(HDUM2, 3.));
			double HDUM3 = H[i - 1] + DUM2 * 0.5;
			double ED3 = AN * AN * Q * Q / Math.pow(HDUM3, 3.33333333);
			double DUM3 = DX * (ANGS - ANGC * DDELT1 - ED3) / (ANGC - Q * Q / DG / Math.pow(HDUM3, 3.));
			double HDUM4 = H[i - 1] + DUM3;
			double ED4 = AN * AN * Q * Q / Math.pow(HDUM4, 3.33333333);
			double DUM4 = DX * (ANGS - ANGC * DDELT2 - ED4) / (ANGC - Q * Q / DG / Math.pow(HDUM4, 3.));

			H[i] = H[i - 1] + (DUM1 + 2. * (DUM2 + DUM3) + DUM4) / 6.;
		}
		// プリント用変数への置き換え
		for (int i = IINI; i <= IEND; i++) {
			HPR[i] = H[i];
			ELPR[i] = H[i] + DELT[i];
			ETOUPR[i] = HDUM[i] + DELT[i];
			ECRPR[i] = HC + DELT[i];
			ELBPR[i] = DELT[i];
			DBDXPR[i] = DDELT[i];

		}

		// ***********************************************************************************
		// 特異点から上流への水面形の追跡
		// *****************************************************************************
		//
		// INII;下流端から数えた特定点直上流の格子点番号
		IINI = IEND - IINI + 2;
		int IINI0 = IINI;

		// DXI:特異点と直上流の格子点との距離
		DXI = DX - DXI;

		// 特異点と格子点が非常に近接している場合、計算開始の格子を一つ上流側にずらす
		if (DXI <= DX * 0.1) {
			H[IINI] = HC = DXI * DHS2;
			// 格子点をずらす前にIINI点の疑似等流水深を計算
			PARA1 = XL2 * 0.5 + XL3 - X[IINI];
			PARA2 = Math.pow(R0 * R0 - PARA1 * PARA1, 0.5);
			PARA3 = Math.pow(R0 * R0 - (XL2 * 0.5) * (XL2 * 0.5), 0.5);
			DELT[IINI] = PARA2 - PARA3;
			DDELT[IINI] = PARA1 / PARA2;
			// 疑似等流水深の計算（存在しない場合 HDUM = 100.0 とする。 )
			if ((ANGS + ANGC * DDELT[IINI]) <= 0.0) {
				HDUM[IINI] = 0.0;
			} else {
				HDUM[IINI] = Math.pow(AN * AN * Q * Q / (ANGS + ANGC * DDELT[IINI]), 0.3);
			}

			// 計算開始の格子点を一つ上流にずらす
			IINI = IINI + 1;
			DXI = DXI + DX;

		}
		
		//ずらした格子点の水深
		H[IINI] = HC - DXI * DHS2;
		
		for(int i = IINI + 1 ;i<=IEND;i++) {
			// X[i] : ここでは下流端からの距離を示す。
			X[i] = DX*(i-1);
			//路床形状の計算
			if(X[i-1] < XL3 || X[i-1] >= (XL2+XL3)) {
				DELT[i-1] = 0.0;
				DDELT[i-1] = 0.;
				DDELT0 = 0.;
				DDELT1 = 0.;
				DDELT2 = 0.;
			}
			
			if(X[i-1] >= XL3 && X[i-1] < (XL3+XL2)) {
				PARA1 = XL2 * 0.5 + XL3 - X[i-1];
				PARA2 = Math.pow(R0*R0-PARA1*PARA1, 0.5);
				PARA3 = Math.pow(R0*R0-(XL2*0.5)*(XL2*0.5), 0.5);
				DDELT[i-1] = PARA2 - PARA3;
				DDELT0 = DDELT[i-1];
				DDELT[i-1] = PARA1/PARA2;
				PARA4 = XL2 * 0.5 + XL3 - X[i-1] -DX*0.5;
				PARA5 = Math.pow(R0*R0-PARA4*PARA4, 0.5);
				DDELT1=PARA4/PARA5;
				PARA6=XL2*0.5 + XL3-X[i-1] -DX;
				PARA7 = Math.pow(R0*R0-PARA6*PARA6, 0.5);
				DDELT2 = PARA6/PARA7;
			}
			// 疑似等流水深の計算（存在しない場合HDUM = 100.0）とする
			if(ANGS + ANGC*DDELT2 <= 0.) {
				HDUM[i-1] = 100.;
			}else {
				HDUM[i-1] = Math.pow(AN*AN*Q*Q/(ANGS + ANGC*DDELT2), 0.3);
			}
			
			//水深の計算
			double ED1 = AN*AN*Q*Q/Math.pow(H[i-1], 3.33333);
			double DUM1 = DX*(-ANGS-ANGC*DDELT0+ED1)/(ANGC-Q*Q/DG/H[i-1]*H[i-1]*H[i-1]);
			double HDUM2 = H[i-1] + DUM1 * 0.5;
			double ED2 = AN*AN*Q*Q/Math.pow(HDUM2, 3.33333);
			double DUM2 = DX*(-ANGS-ANGC*DDELT1+ED2)/(ANGC-Q*Q/DG/HDUM2*HDUM2*HDUM2);
			double HDUM3 = H[i-1] + DUM2 * 0.5;
			double ED3 = AN*AN*Q*Q/Math.pow(HDUM3, 3.33333);
			double DUM3 = DX*(-ANGS-ANGC*DDELT1+ED3)/(ANGC-Q*Q/DG/HDUM3*HDUM3*HDUM3);
			double HDUM4 = H[i-1] + DUM3;
			double ED4 = AN*AN*Q*Q/Math.pow(HDUM4, 3.33333);
			double DUM4 = DX*(-ANGS-ANGC*DDELT2+ED4)/(ANGC-Q*Q/DG/HDUM4*HDUM4*HDUM4);
			
			H[i] = H[i-1] + (DUM1 + 2.0*(DUM2+DUM3) + DUM4)/6.;
			
		}
		
		//プリント用変数への置き換え
		for(int i  = IINI0;i<=IEND;i++) {
			// IMOTO; 上流端から数えた格子点番号
			int IMOTO = IEND - i +1;
			HPR[IMOTO] = H[i];
			ELPR[IMOTO] = H[i] + DELT[i];
			ETOUPR[IMOTO] = HDUM[i] + DELT[i];
			ECRPR[IMOTO] = HC + DELT[i];
			ELBPR[IMOTO] = DELT[i];
			DBDXPR[IMOTO] = DDELT[i];
		}
		
		// ********************************************************************
		//     結果の印刷
		// ********************************************************************
		System.out.println("Tokki wo Koeru suimenkei no keisan.");
		System.out.printf("Singular point XS=%10.6f(m),SuimenKoubai DH/DX=%8.4f, or \n", XS,DHS1,DHS2);
		System.out.printf("Parameter : XL1=%8.4f(m), XL2=%7.3f(m), XL3=%7.3f(m), Slope=%8.4f, manning coef. = %7.3f,"
				+ "Tani-haba ryuryou=%8.4f(m2/s)\n", XL1,XL2,XL3,ANGS,AN,Q);
		System.out.printf("      X(m),     H(m),   SUII(m), TOURYU(m),GENKAI(m),  ZB(m)\n");
		for(int i=0;i<=IEND;i++) {
			System.out.printf("  %8.4f  %8.4f  %8.4f  %8.4f  %8.4f  %8.4f\n", X[i],HPR[i],ELPR[i],ETOUPR[i],ECRPR[i],ELBPR[i]);
		}
		
		
	} // main method
} // class end
