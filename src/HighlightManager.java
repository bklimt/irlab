
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.HashMap;

public class HighlightManager {
	static int current = 0;
	static Color[] colors = {Color.cyan, Color.magenta, Color.green, Color.yellow, Color.orange, Color.pink};
	static HashMap painters = new HashMap();

	static DefaultHighlighter.DefaultHighlightPainter getPainter(String type) {
		if ( painters.get(type) == null ) {
			DefaultHighlighter.DefaultHighlightPainter painter =
				new DefaultHighlighter.DefaultHighlightPainter(colors[current%colors.length]);
			current++;
			painters.put(type,painter);
		}
		return (DefaultHighlighter.DefaultHighlightPainter)painters.get(type);
	}
}

