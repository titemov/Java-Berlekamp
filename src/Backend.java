import java.util.ArrayList;
import java.util.Objects;

public class Backend {
    private int mod;

    private int modulo(int num, int mod){
        if(num>0){
            return (num+mod)%mod;
        }else{
            return((num%mod+mod)%mod);
        }
    }

    private int deg(ArrayList<Integer> coefficients){
        while(!coefficients.isEmpty() && coefficients.getLast()==0){
            coefficients.removeLast();
        }
        return(coefficients.size()-1);
    }

    private int[] nextRow(ArrayList<Integer> inputCoeffs,int[] a,int t){
        int n= a.length;
        int[] result = new int[n];

        for(int j=(n-1);j>0;j--){
            result[j] = modulo(a[j-1]-(t * inputCoeffs.get(j)),this.mod);
        }
        result[0] = modulo(-1 * (t * inputCoeffs.get(0)),this.mod);

        return result;
    }


    private int[][] buildQMatrix(Polynomial inputPoly, int n){
        int[][] result = new int[n][n];

        int[] a = new int[n];
        a[0]=1;//first row = {1,0,0,...,0}

        ArrayList<int[]> rows = new ArrayList<>();

        for(int k=0;k<(this.mod*(n-1))+1;k++){
            rows.add(a);
            if(k<(this.mod*(n-1))){
                int t = a[n-1];
                int[] newRow = nextRow(inputPoly.getCoefficients(),a,t);
                a = newRow;
            }
        }

        for(int i=1;i<result.length;i++){//i=1, first row - trivial
            for(int j=0;j<result[0].length;j++){
                result[i][j]=rows.get(i*this.mod)[j];
            }
        }

        return result;
    }

    public Polynomial getTempVPoly(int[] kernel,Polynomial vPoly){
        Log logger = new Log();
        Polynomial result = null;
        for(int s=0;s<mod;s++){
            ArrayList<Integer> resultCoeffs = new ArrayList<>();
            for(int i=0;i<kernel.length;i++){
                resultCoeffs.add(kernel[i]);
            }

            ArrayList<Integer> sCoeffs = new ArrayList<>();
            sCoeffs.add(s);
            Polynomial sPoly = new Polynomial(sCoeffs,mod);

            result = new Polynomial(resultCoeffs,mod);
            result=result.substract(sPoly);

            logger.writeLog("func resultPoly: ",true);
            result.logPolynomial();
            logger.writeLog("func vPoly: ",true);
            vPoly.logPolynomial();

            Polynomial temp = result.gcd(vPoly);

            if(temp.getCoefficients().size()==1 && temp.getCoefficients().get(0)==1){
                logger.writeLog("GCD = 1 (S="+s+")",true);
            }else{
                result=temp;
                logger.writeLog("func res: ",true);
                temp.logPolynomial();
                logger.writeLog("S="+s,false);
                break;
            }
        }
        return result;
    }

    public void run(ArrayList<Integer> coefficients,int mod){
        Log logger = new Log();
        Kernel k = new Kernel();
        this.mod=mod;

        while(!coefficients.isEmpty() && coefficients.getLast()==0){
            coefficients.removeLast();
        }

        Polynomial inputPoly = new Polynomial(coefficients,this.mod);
        System.out.print("input: ");
        inputPoly.printPolynomial();
        System.out.print(" mod "+mod);

        logger.writeLog("input: ",true);
        inputPoly.logPolynomial();
        logger.writeLog(" mod "+mod,false);

        logger.writeLog("User input mod: "+mod+"\n",true);

        int n = deg(inputPoly.getCoefficients());

        int[][] matrixQ = buildQMatrix(inputPoly,n);
        matrixQ[0][0]=1;

        logger.writeLog("MATRIX Q:\n ",true);
        Matrix.logMat(matrixQ);

        int[][] matrixE = new int[n][n];
        for(int i=0;i<n;i++){
            matrixE[i][i]=1;
        }

        logger.writeLog("MATRIX E:\n ",true);
        Matrix.logMat(matrixE);

        int[][] matrixA = Matrix.modMatrix(Matrix.substract(matrixQ,matrixE),mod);

        logger.writeLog("MATRIX A (Q-E):\n ",true);
        Matrix.logMat(matrixA);

        ArrayList<int[]> kernel = k.findKernel(matrixA,mod);
        if(Objects.isNull(kernel)){
            String str = "Kernel size <= 1; This polynomial cannot be factorized";
            System.out.println(str);
            logger.writeLog(str,true);
            return;
        }

        int r = kernel.size();

        logger.writeLog("KERNEL:\n",true);
        for(int i=0;i<kernel.size();i++){
            logger.writeLog("v["+i+"] = ",false);
            Matrix.logMat(new int[][]{kernel.get(i)});
        }

        logger.writeLog("Finding gcd's :",true);
        //PLEASE IMPROVE EVERYTHING BELOW

        ArrayList<Polynomial> wPolys = new ArrayList<>();
        ArrayList<Polynomial> result = new ArrayList<>();

        Polynomial vPoly = null;
        for(int s=0;s<mod;s++){
            ArrayList<Integer> vPolyCoeffs = new ArrayList<>();
            for(int m=0;m<kernel.get(1).length;m++){
                vPolyCoeffs.add(kernel.get(1)[m]);
            }

            ArrayList<Integer> sCoeffs = new ArrayList<>();
            sCoeffs.add(s);
            Polynomial sPoly = new Polynomial(sCoeffs,mod);

            vPoly = new Polynomial(vPolyCoeffs,mod);
            vPoly=vPoly.substract(sPoly);

            logger.writeLog("inputPoly: ",true);
            inputPoly.logPolynomial();
            logger.writeLog("vPoly: ",true);
            vPoly.logPolynomial();

            Polynomial temp = inputPoly.gcd(vPoly);


            if(temp.getCoefficients().size()==1 && temp.getCoefficients().get(0)==1){
                logger.writeLog("GCD = 1 (S="+s+")\n",true);
            }else{

                logger.writeLog("Found factor: ",true);
                temp.logPolynomial();
                logger.writeLog(" (S="+s+")\n",false);

                wPolys.add(temp);
            }
        }

        logger.writeLog("======================================",true);

        if(wPolys.size()==r){
            printResult(inputPoly,wPolys);
            return;
        }


        for(int w = 0;w<wPolys.size();w++) {
            Polynomial tempVPoly = new Polynomial(wPolys.get(w).getCoefficients(), this.mod);
            for (int i = 2; i < kernel.size(); i++) {
                if (i != kernel.size() - 1) {
                    tempVPoly = getTempVPoly(kernel.get(i), tempVPoly);
                } else {
                    ArrayList<Integer> vPolyCoeffs = new ArrayList<>();
                    for (int m = 0; m < kernel.getLast().length; m++) {
                        vPolyCoeffs.add(kernel.getLast()[m]);
                    }
                    vPoly = new Polynomial(vPolyCoeffs, mod);
                    for (int s = 0; s < mod; s++) {
                        ArrayList<Integer> sCoeffs = new ArrayList<>();
                        sCoeffs.add(s);
                        Polynomial sPoly = new Polynomial(sCoeffs, mod);

                        vPoly = vPoly.substract(sPoly);

                        logger.writeLog("inputPoly: ",true);
                        tempVPoly.logPolynomial();
                        logger.writeLog("vPoly: ",true);
                        vPoly.logPolynomial();


                        Polynomial temp = tempVPoly.gcd(vPoly);

                        if (temp.getCoefficients().size() == 1 && temp.getCoefficients().get(0) == 1) {
                            logger.writeLog("GCD = 1 (S="+s+")",true);
                        } else {
                            logger.writeLog("kernel.getLast found factor: ",true);
                            temp.logPolynomial();
                            logger.writeLog(" (S="+s+")\n",false);

                            result.add(temp);
                            if (result.size()+1 == r) {
                                for(int wElem=0;wElem<wPolys.size();wElem++){
                                    if(wElem!=w){
                                        result.add(wPolys.get(wElem));
                                    }
                                }
                                printResult(inputPoly,result);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void printResult(Polynomial inputPoly, ArrayList<Polynomial> result){
        Log logger = new Log();

        System.out.println("\n=============== RESULT ===============");
        inputPoly.printPolynomial();
        System.out.print(" = ");

        logger.writeLog("=============== RESULT ===============\n",true);
        inputPoly.logPolynomial();
        logger.writeLog(" = ",false);

        for(int i=0;i<result.size();i++){
            result.get(i).printPolynomial();
            logger.writeLog("(",false);
            result.get(i).logPolynomial();
            logger.writeLog(")",false);
        }
        System.out.print(" mod "+this.mod);
        logger.writeLog(" mod "+this.mod,false);
    }
}
