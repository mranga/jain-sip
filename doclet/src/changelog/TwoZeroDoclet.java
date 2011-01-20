package changelog;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;


public class TwoZeroDoclet {
    private static PrintWriter pw;
    static HashSet<String> processed = new HashSet<String>();
    
	public static boolean start(RootDoc root) {
	    try {
	        File changelog = new File("change-log.html");
	        pw = new PrintWriter(new FileWriter(changelog));
	        pw.println("<html>");
	        pw.println("<body>");
	        pw.println("<h1>New additions in version 2.0</h1>");  
	        processClasses(root.classes());
	        processed.clear();
	        pw.println("<h1>Deprecated in version 2.0</h1>");
	        processDeprecation(root.classes());
	        pw.println("</body>");
	        pw.println("</html>");
	        pw.flush();
	        pw.close();
	        return true;
	    } catch(Exception ex) {
	        ex.printStackTrace();
	        return false;
	    }
	}
	
	public static void processClasses(ClassDoc[] classes) throws Exception {
	   
	    
		for (ClassDoc classDoc : classes) {
		 	printDoc(classDoc);
			MethodDoc[] methods = classDoc.methods();
			// Interfaces
			processClasses(classDoc.interfaces());
			// Inner Classes
			processClasses(classDoc.innerClasses());
			// Methods
			for (MethodDoc doc : methods) {
				printDoc(doc);
			}
			// Constructors
			for (ConstructorDoc doc : classDoc.constructors()) {
				printDoc(doc);
			}
			// Fields
			for (Doc doc : classDoc.fields()) {
				printDoc(doc);
			}
			// Enums
			for (Doc doc : classDoc.enumConstants()) {
				printDoc(doc);
			}
		}
		
		
	     
	}
	public static void processDeprecation(ClassDoc[] classes) throws Exception {
	     
        for (ClassDoc classDoc : classes) {
              printDeprecatedDoc(classDoc);
              MethodDoc[] methods = classDoc.methods();
              // Interfaces
              processClasses(classDoc.interfaces());
              // Inner Classes
              processClasses(classDoc.innerClasses());
              // Methods
              for (MethodDoc doc : methods) {
                  printDeprecatedDoc(doc);
              }
              // Constructors
              for (ConstructorDoc doc : classDoc.constructors()) {
                  printDeprecatedDoc(doc);
              }
              // Fields
              for (Doc doc : classDoc.fields()) {
                  printDeprecatedDoc(doc);
              }
              // Enums
              for (Doc doc : classDoc.enumConstants()) {
                  printDeprecatedDoc(doc);
              }
          }
	}
	public static void printDoc(Doc doc) {
	    if (processed.contains(doc.toString())) return;
	    processed.add(doc.toString());
        
		Tag[] tags = doc.tags("since");
		if (tags.length > 0 && tags[0].text().contains("2.0")) {
		    pw.println("<hr></hr>");
		    pw.print("<h2>");
		    pw.print(doc);
		    pw.println("</h2>");
			pw.println(doc.commentText());
		}
	}
	
	public static void printDeprecatedDoc(Doc doc) {
	    if (processed.contains(doc.toString())) return;
        processed.add(doc.toString());
     
	    Tag[] tags = doc.tags("deprecated");
	    if (tags.length > 0 && tags[0].text().contains("2.0") ) {
	        pw.println("<hr></hr>");
	        pw.print("<h2>");
	        pw.print(doc);
	        pw.println("</h2>");
	        
            pw.println(doc.commentText());
	    }
	}
}
