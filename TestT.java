

package testt;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 *
 * @author xhd0216
 */
public class TestT {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws UnsupportedEncodingException {
        String url = "http://www.guancha.cn";
        testCode PD = new testCode(url);
	      int nThreads = 4;
        ArrayList<ThreadDemo> ths = new ArrayList<ThreadDemo>();
        for(int i = 0; i < nThreads; i++){
            ThreadDemo t = new ThreadDemo("Thread - "+i, PD);
            ths.add(t);
            t.start();
        }
        
	
  	try {
        for(ThreadDemo j : ths){
          j.join();
          }
	  	}
	  catch( Exception e) {
	  	System.out.println("Interrupted");
	  	}
    }
}
class testCode{
    private Queue<String> q = new LinkedList<String>();
    private HashSet<String> hs = new HashSet<String> ();
    private Integer count = 0;
    private PrintWriter writer;
    private String url;
    testCode(String u) throws UnsupportedEncodingException{
        url = u;
        q.add(u);
        hs.add(u);
        try{
            writer = new PrintWriter("guancha-result.txt", "UTF-8");
        }
        catch (FileNotFoundException e){
            System.out.println("file open error");
        }
    }
    private void processURL(String page){
        Document doc;
        Elements links;
        try{
            doc = Jsoup.connect(page).get();
            links = doc.select("a");
        }
        catch(Exception e){
            System.out.println("can not open page");
            return;
        }
        synchronized(this){
            writer.println(doc.title());
        }
        for(Element l : links){
            String ref = l.attr("href");
            if(ref.length() > 5 && ref.charAt(0) == '/'){
                ref = url + ref;
            }
            else{
                continue;
            }
            if(ref.charAt(ref.length()-1) != 'l'){
                continue;
            }
            synchronized(this){
                if(hs.contains(ref)){
                    continue;
                }
                else{
                    hs.add(ref);
                    q.add(ref);
                }
            }
        }
        
    }
    public void calc(String t) throws InterruptedException{
	  while(count < 500){
      String temp  = "*";
	    synchronized (this){
		    if(!q.isEmpty()){
		    temp = q.remove();
		    count++;
		    notify();
		}
		else{
		    wait();
		}
	 }
  if(temp.length()> 1){
    System.out.println("count "+count + " " + t+ " works on link:");
    System.out.println(temp);
    processURL(temp);
    }
	}
	writer.close();
  }

}

class ThreadDemo extends Thread {
    private Thread t;
    private String threadName;
    testCode  PD;
    
    ThreadDemo( String name,  testCode pd){
	threadName = name;
	PD = pd;
	
    }
    public void run() {
	try{
	    PD.calc(threadName);
	}catch (InterruptedException e){
	    System.out.println("Oops...");
	}
    }
    
    public void start ()
    {
	System.out.println("Starting " +  threadName );
	if (t == null)
	    {
		t = new Thread (this, threadName);
		t.start ();
	    }
    }
    
}

