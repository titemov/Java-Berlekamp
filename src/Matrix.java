import java.util.Arrays;

public class Matrix {
    private static int modulo(int num, int mod){
        if(num>0){
            return (num+mod)%mod;
        }else{
            return((num%mod+mod)%mod);
        }
    }

    public static int[][] modMatrix(int[][] m,int mod){
        int[][] result = new int[m.length][m[0].length];

        for(int i=0;i<m.length;i++){
            for(int n=0;n<m[0].length;n++){
                result[i][n]=modulo(m[i][n],mod);
            }
        }
        return result;
    }

    public static int[][] substract(int[][] m1, int[][] m2){
        if (m1[0].length!= m2.length)
        {
            System.out.println("DIFFERENT SIZE");
            return null;
        }
        int[][] result = new int[m1.length][m1[0].length];
        for(int i=0;i<m1.length;i++){
            for(int n=0;n<m1[0].length;n++){
                result[i][n]=m1[i][n]-m2[i][n];
            }
        }
        return result;
    }
    public static double[][] multiply(double[][] m1, double[][] m2){
        if (m1[0].length!= m2.length)
        {
            System.out.println("DIFFERENT SIZE");
            return null;
        }
        double[][] result=new double[m1.length][m2[0].length];
        for (int i=0;i<m1.length;i++){
            for (int n=0;n<m2[0].length;n++){
                for (int m=0; m<m1[0].length;m++){
                    result[i][n]+=m1[i][m]*m2[m][n];
                }
            }
        }
        return result;
    }

    private static double determinant(double[][] mat){
        int n = mat.length;
        if (n == 2)
            return (mat[0][0] * mat[1][1]) - (mat[0][1] * mat[1][0]);
        int det = 0;
        for (int i = 0; i < n; i++) {
            int sign = ((i & 1) == 0) ? +1 : -1;
            det += sign * mat[0][i] * determinant( createSubMatrix(mat ,0 , i) );
        }
        return det;
    }
    private static double[][] createSubMatrix(double[][] mat , int excludingRow , int excludingCol){
        int n = mat.length;
        double[][] newMatrix = new double[n - 1][n - 1];
        int rPtr = -1;
        for (int i = 0; i < n; i++) {
            if (i == excludingRow)continue;
            ++rPtr;
            int cPtr = -1;
            for (int j = 0; j < n; j++) {
                if (j == excludingCol)continue;
                newMatrix[rPtr][++cPtr] = mat[i][j];
            }
        }
        return newMatrix;
    }
    private static double[][] cofactor(double[][] mat){
        int n = mat.length;
        double[][] cofactorMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int changeSign = ((j & 1) == 0) ? 1 : -1;
                int sign = ((i & 1) == 0) ? 1 : -1;
                cofactorMatrix[i][j] = sign * changeSign * determinant(createSubMatrix(mat , i , j));
            } // end of inner loop
        }// end of outer loop
        return cofactorMatrix;
    }
    private static double[][] multiplyByConstant(double[][] mat , double constant){
        int n = mat.length;
        double[][] newMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                newMatrix[i][j] = constant * mat[i][j];
            }
        }
        return newMatrix;
    }
    private static double[][] transpose(double[][] mat){
        int n = mat.length;
        for (int i = 0; i <= n - 2; i++) {
            for (int j = i + 1; j <= n - 1; j++)
                swap(mat , i , j);
        }
        return mat;
    }

    private static void swap (double[][] mat, int i, int j) {
        double tmp = mat[j][i];
        mat[j][i] = mat[i][j];
        mat[i][j] = tmp;
    }
    public static double[][] inverseOf(double[][] mat){
        int n = mat.length;
        double det = determinant(mat);
        // Check to see if matrix is singular
        if (det == 0)
            throw new IllegalArgumentException("Determinant of matrix equals 0, no inverse exists");
        if (n == 1)
            throw new IllegalArgumentException("Matrix is a vector");
        if (n == 2){
            // Compute inverse matrix elements 2*2 matrix
            double[][] ans = new double[n][n]; // Collect the results into a new matrix
            ans[0][0] = mat[1][1] / det;
            ans[0][1] = -mat[0][1] / det;
            ans[1][0] = -mat[1][0] / det;
            ans[1][1] = mat[0][0] / det;
            return ans;
        }
        // inverse matrix for 3*3 and up
        return multiplyByConstant(transpose(cofactor(mat)) , 1 / det );
    }

    public static void printMatrix(int[][] mat){
        for(int i=0;i<mat.length;i++){
            for (int n=0;n<mat[0].length;n++){
                System.out.print(mat[i][n]+" ");
            }
            System.out.print("\n");
        }
        System.out.print("\n");
    }

    public static void logMat(int[][] mat){
        Log log = new Log();
        for(int i=0;i<mat.length;i++){
            for (int n=0;n<mat[0].length;n++){
                log.writeLog(+mat[i][n]+", ",false);
            }
            log.writeLog("\n ",false);
        }
        log.writeLog("\n",false);
    }
}
