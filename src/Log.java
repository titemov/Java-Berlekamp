import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.Date;

public class Log {
    public void initialEntry(){
        try{
            RandomAccessFile raf = new RandomAccessFile("log.txt","rw");
            Date date = new Date();
            String s = date.toString();
            int len = (int) raf.length();
            raf.seek(len);
            raf.write(("\n\n\n\n|||||||||| "+s+" ||||||||||\n").getBytes());
            raf.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void writeLog(String s, boolean newLine) {
        try{
            RandomAccessFile raf = new RandomAccessFile("log.txt","rw");
            int len = (int) raf.length();
            raf.seek(len);
            if (newLine) {
                raf.write(("\r\n" + s).getBytes());
            }else{
                raf.write((s).getBytes());
            }

            raf.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String[] getText(){
        int lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("log.txt"))) {
            while (reader.readLine() != null) {
                lineCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        String[] s = new String[lineCount];
        try (BufferedReader br = new BufferedReader(new FileReader("log.txt"))) {
            String line;
            int i=0;
            while ((line = br.readLine()) != null) {
                s[i]=line+"\n";
                i++;
            }
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
