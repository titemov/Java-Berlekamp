import java.util.ArrayList;

public class Polynomial {
    //polynomial operations
    private ArrayList<Integer> coefficients = new ArrayList<>();
    private int mod;
    public Polynomial(ArrayList<Integer> coeffs,int mod){
        this.mod=mod;
        for(int i=0;i<coeffs.size();i++){
            this.coefficients.add(modulo(coeffs.get(i),this.mod));
        }
        removeLeadingZeros();
    }

    public ArrayList<Integer> getCoefficients() {
        return this.coefficients;
    }

    private void removeLeadingZeros(){
        while(!this.coefficients.isEmpty() && this.coefficients.getLast()==0){
            this.coefficients.removeLast();
        }
    }

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

    private int lc(ArrayList<Integer> coefficients){
        while(!coefficients.isEmpty() && coefficients.getLast()==0){
            coefficients.removeLast();
        }
        return(coefficients.get(coefficients.size()-1));
    }

    private int modInverse(int num,int mod){
        num=modulo(num,mod);
        for(int i=0;i<mod;i++){
            if(num*i%mod==1){
                return i;
            }
        }
        return 0;//unreachable
    }

    private Polynomial monicalView(){
        int leadingCoeff = lc(this.coefficients);

        int multiplyBy = modInverse(leadingCoeff,this.mod);

        ArrayList<Integer> result = new ArrayList<>();
        for(int i=0;i<this.coefficients.size();i++){
            result.add(modulo(this.coefficients.get(i)*multiplyBy,this.mod));
        }
        return new Polynomial(result,this.mod);
    }

    public Polynomial add(Polynomial pol){
        ArrayList<Integer> result = new ArrayList<>();
        int maxSize = Math.max(this.coefficients.size(),pol.getCoefficients().size());

        for(int i=0;i<maxSize;i++){
            int coeff1 = 0;
            int coeff2 = 0;
            if(i<this.coefficients.size()) coeff1 = this.coefficients.get(i);
            if(i<pol.getCoefficients().size()) coeff2 = pol.getCoefficients().get(i);
            result.add(modulo((coeff1+coeff2),this.mod));
        }

        while(!result.isEmpty() && result.getLast()==0){
            result.removeLast();
        }

        return new Polynomial(result,this.mod);
    }

    public Polynomial substract(Polynomial pol){
        ArrayList<Integer> result = new ArrayList<>();
        int maxSize = Math.max(this.coefficients.size(),pol.getCoefficients().size());

        for(int i=0;i<maxSize;i++){
            int coeff1 = 0;
            int coeff2 = 0;
            if(i<this.coefficients.size()) coeff1 = this.coefficients.get(i);
            if(i<pol.getCoefficients().size()) coeff2 = pol.getCoefficients().get(i);

            result.add(modulo((coeff1-coeff2),this.mod));
        }

        while(!result.isEmpty() && result.getLast()==0){
            result.removeLast();
        }

        return new Polynomial(result,this.mod);
    }

    public Polynomial multiply(Polynomial pol){
        int[] initialFill = new int[this.coefficients.size() + pol.coefficients.size() - 1];//filled with zeros by default
        ArrayList<Integer> result = new ArrayList<>();
        for(int i=0;i<initialFill.length;i++){
            result.add(initialFill[i]);
        }

        for(int i=0;i<this.coefficients.size();i++){
            for(int n=0;n<pol.coefficients.size();n++){
                int temp=result.get(i+n);
                result.set(i+n, temp + modulo(this.coefficients.get(i)*pol.coefficients.get(n),this.mod));

                temp=result.get(i+n);
                result.set(i+n,modulo(temp,this.mod));
            }
        }

        return new Polynomial(result,this.mod);
    }

    public Polynomial[] division(Polynomial b){
        int m=deg(this.coefficients);
        int n=deg(b.getCoefficients());
        if(n>m){
            int temp = m;
            m=n;
            n=temp;
        }

        ArrayList<Integer> rCoefs = new ArrayList<>();
        rCoefs.addAll(this.coefficients);

        ArrayList<Integer> qCoefs = new ArrayList<>();

        for(int k=m-n;k>=0;k--){
            int q = rCoefs.get(n+k)/lc(b.getCoefficients());
            qCoefs.addFirst(q);
            for(int j=n+k-1;j>=k;j--){
                rCoefs.set(j,rCoefs.get(j)-q*b.getCoefficients().get(j-k));
            }
            rCoefs.removeLast();
        }
        return new Polynomial[]{new Polynomial(qCoefs,this.mod),new Polynomial(rCoefs,this.mod)};
    }

    public Polynomial[] euclidDivision(Polynomial b){
        ArrayList<Integer> temp = new ArrayList<>();
        temp.add(0);
        Polynomial q = new Polynomial(new ArrayList<>(temp),this.mod);
        Polynomial r = new Polynomial(new ArrayList<>(this.coefficients),this.mod);

        int d = deg(b.getCoefficients());
        int c = lc(b.getCoefficients());

        while(deg(r.getCoefficients())>=d){
            ArrayList<Integer> s = new ArrayList<>();
            int sdeg = deg(r.getCoefficients())-d;
            int scoef = (lc(r.getCoefficients()) * modInverse(c,this.mod))%this.mod;//==1
            for(int i=0;i<sdeg;i++){
                s.add(0);
            }
            s.add(scoef);
            Polynomial sPoly = new Polynomial(s,this.mod);
            q = q.add(sPoly);
            sPoly = sPoly.multiply(b);
            r = r.substract(sPoly);
        }

        return new Polynomial[]{q,r};
    }

    public Polynomial gcd(Polynomial b){
        ArrayList<Polynomial> r = new ArrayList<>();
        r.add(new Polynomial(this.coefficients,this.mod));
        r.add(new Polynomial(b.getCoefficients(),this.mod));

        for(int i=1; (r.get(i).getCoefficients().size())!=0;i++){
            r.add(r.get(i-1).euclidDivision(r.get(i))[1]);
        }

        return r.get(r.size()-2).monicalView();
    }

    public void logPolynomial(){
        Log logger = new Log();
        boolean firstTerm = true;

        for(int i=this.coefficients.size()-1;i>=0;i--){
            if(this.coefficients.get(i)!=0){
                if(!firstTerm){
                    logger.writeLog(" + ",false);
                }else{
                    firstTerm=false;
                }
                logger.writeLog(this.coefficients.get(i)+"X^"+i,false);
            }
        }
        if(firstTerm){
            logger.writeLog("0",false);//If polynomial is null (containing zeros)
        }
        //logger.writeLog("\n",false);
    }

    public void printPolynomial(){
        boolean firstTerm = true;
        System.out.print("(");
        for(int i=this.coefficients.size()-1;i>=0;i--){
            if(this.coefficients.get(i)!=0){
                if(!firstTerm){
                    System.out.print(" + ");
                }else{
                    firstTerm=false;
                }
                System.out.print(this.coefficients.get(i)+"X^"+i);
            }
        }
        if(firstTerm){
            System.out.print("0");//If polynomial is null (containing zeros)
        }
        System.out.print(")");
    }
}
