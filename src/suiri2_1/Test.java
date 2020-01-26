package suiri2_1;

import java.io.PrintWriter;

public class Test {
	public static void main(String... args) {
		try(PrintWriter pWriter = new PrintWriter("data/test2.txt")){
			pWriter.println("test");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
