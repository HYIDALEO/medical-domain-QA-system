package question.classify;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class Testmain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		INDArray data = Nd4j.rand(new int[]{3,5});
		System.out.println(data);
	}

}
