
import java.io.*;

class TxtFilter implements FilenameFilter {
	public boolean accept(File dir, String name) {
		return !name.toUpperCase().equals("CVS");
	}
}

class FileComparator implements java.util.Comparator {
	public int compare(Object o1, Object o2) {
		if ( o1.equals(o2) ) {
			return 0;
		} else {
			String s1 = o1.toString();
			String s2 = o2.toString();
			Integer n1 = new Integer(s1.split("\\.")[0]);
			Integer n2 = new Integer(s2.split("\\.")[0]);
			if ( n1.intValue() < n2.intValue() ) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	public boolean equals(Object o1, Object o2) {
		return o1.equals(o2);
	}
}

public class EmailRepository {
	private String path = null;
	private File directory = null;

	public EmailRepository(String path) {
		this.path = path;
		directory = new File(path);
	}

	public String[] getEmailIDs() {
		String[] files = directory.list(new TxtFilter());
		/*
		int[] ids = new int[files.length];
		for ( int i=0; i<files.length; i++ ) {
			String id = files[i].split("\\.")[0];
			ids[i] = (new Integer(id)).intValue();
		}
		return ids;
		*/
		java.util.Arrays.sort(files, new FileComparator());
		return files;
	}

	public String getEmailSource(String id) throws FileNotFoundException, IOException {
		String filename = id; //(new Integer(id)).toString() + ".txt";
		FileReader reader = new FileReader(new File(directory,filename));

		String source = "";
		char[] buffer = new char[256];
		buffer[255] = 0;
		int read = reader.read(buffer,0,255);
		while ( read != -1 ) {
			source += new String(buffer,0,read);
			read = reader.read(buffer,0,255);
		}

		source = source.replaceAll("\\r","");
		source = source.replaceAll("\\t"," ");

		reader.close();
		return source;
	}

	public int getDate(String id) {
		String[] parts = id.split("\\.");
		return (new Integer(parts[0])).intValue();
	}

	public int getCurrentDate() {
		int ans = 0;
		String[] ids = getEmailIDs();
		for ( int i=0; i<ids.length; i++ ) {
			if ( getDate(ids[i]) > ans ) {
				ans = getDate(ids[i]);
			}
		}
		return ans;
	}
}

