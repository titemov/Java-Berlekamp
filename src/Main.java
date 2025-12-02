import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static ArrayList<Integer> reverseTheArray(ArrayList<Integer> array){
        ArrayList<Integer> result= new ArrayList<>();
        int size = array.size();

        for(int i=0;i<size;i++){
            result.add(array.get(size-1-i));
        }
        return result;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Log logger = new Log();
        logger.initialEntry();
        //examples
        // 1 0 1 0 10 10 8 2 8 mod 13 (ans: (1X^4 + 2X^3 + 3X^2 + 4X^1 + 6X^0)(1X^1 + 3X^0)(1X^3 + 8X^2 + 4X^1 + 12X^0) )
        // 1 0 1 0 10 10 8 2 8 5 13 11 5 MOD 97 (ans: (1X^1 + 92X^0)(1X^2 + 61X^1 + 91X^0)(1X^1 + 37X^0)(1X^8 + 4X^7 + 2X^6 + 76X^5 + 74X^4 + 26X^3 + 11X^2 + 40X^1 + 52X^0) )
        try {
            System.out.println("Enter coefficients using \"space\" as separator (for example: \"1 0 4 1\"):");
            String str = scanner.nextLine();

            String[] temp = str.split(" ");
            ArrayList<Integer> temp2 = new ArrayList<>();
            for(int i=0;i< temp.length;i++){
                temp2.add(Integer.parseInt(temp[i]));
            }

            if(temp2.get(0)!=1) throw new Exception("Polynomial must be normalized!");

            ArrayList<Integer> coeffs = reverseTheArray(temp2);

            logger.writeLog("User input:"+Arrays.toString(str.split(" "))+"\n",true);

            System.out.println("Enter prime number (mod):");
            int mod = scanner.nextInt();
            if(mod<2 || mod>(Math.pow(2,16)-1)) throw new Exception();

            Backend backend = new Backend();
            backend.run(coeffs,mod);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}