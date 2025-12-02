import java.util.ArrayList;
import java.util.Arrays;

public class Kernel {
    private int modulo(int num, int mod){
        if(num>0){
            return (num+mod)%mod;
        }else{
            return((num%mod+mod)%mod);
        }
    }

    private int modInverse(int num,int mod){
        num=modulo(num,mod);
        for(int i=0;i<mod;i++){
            if(num*i%mod==(mod-1)){
                return i;
            }
        }
        return 0;
    }
    public ArrayList<int[]> findKernel(int[][] A,int mod){
        Log logger = new Log();
        ArrayList<int[]> result = new ArrayList<>();
        int n=A.length;
        int r=0;
        int[] c= new int[n];
        for(int i=0;i<n;i++){
            c[i]=-1;
        }

        logger.writeLog("FIND THE KERNEL:\n",true);

        for(int k=0;k<n;k++){
            int[] v = new int[n];
            boolean exists = false;

            logger.writeLog(">>> Step k="+k+":",true);

            for(int j=0;j<n;j++){
                if(A[k][j]!=0 && c[j]<0){
                    logger.writeLog("FOUND: "+A[k][j]+" (j="+j+")",true);

                    int multiplyBy = modInverse(A[k][j],mod)-mod;
                    if(multiplyBy==-mod) continue;
                    multiplyBy=modulo(multiplyBy,mod);

                    logger.writeLog("SUITABLE: "+A[k][j]+" (j="+j+")",true);
                    exists=true;

                    logger.writeLog("multiplyBy: (-1/("+multiplyBy+"*"+A[k][j]+")) mod "+mod+" = 1",true);

                    int[] newColumn = new int[n];
                    for(int columnElem=0;columnElem<n;columnElem++){
                        newColumn[columnElem]=modulo(A[columnElem][j]*multiplyBy,mod);
                        A[columnElem][j]=newColumn[columnElem];
                    }

                    logger.writeLog("\n>>> After j-column multiplied:\n ",true);
                    Matrix.logMat(A);

                    for(int m=0;m<n;m++){
                        if(m!=j){
                            int[] columnToAdd = new int[n];
                            for(int elem=0;elem<n;elem++){
                                columnToAdd[elem]=modulo(newColumn[elem]*A[k][m],mod);
                            }
                            for(int l=0;l<n;l++){
                                A[l][m]=modulo(A[l][m]+columnToAdd[l],mod);
                            }
                        }
                    }
                    logger.writeLog(">>> After i-columns multiplied (i!=j)\n ",true);
                    Matrix.logMat(A);
                    c[j]=k;
                }
            }
            if(!exists){
                logger.writeLog("=======   SKIPPED   =======",true);
                r+=1;
                logger.writeLog("c="+Arrays.toString(c),true);
                for(int j=0;j<n;j++) {
                    for (int s = 0; s < n; s++) {
                        if (c[s] == j) {
                            v[j] = A[k][s];
                        } else if (j == k) {
                            if(v[j]==0) {
                                v[j] = 1;
                            }
                        } else {
                            if(v[j]==0) {
                                v[j] = 0;
                            }
                        }
                    }
                }
                result.add(v);
                logger.writeLog("v["+k+"]: ",true);
                Matrix.logMat(new int[][]{v});
            }
        }
        logger.writeLog("r: "+r,true);
        logger.writeLog("Matrix A:\n ",true);
        Matrix.logMat(A);
        //r=1;
        if(r<=1) return null;

        return result;
    }
}
