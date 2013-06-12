package ncsa.d2k.modules.projects.pgroves.test;

import ncsa.d2k.modules.core.control.ObjectPasser;

import java.awt.*;
import java.awt.event.*;

public class VisFiringTest extends ObjectPasser implements ActionListener{

	Object obj;
	Frame fr;

	public void doit() throws Exception{

		obj = pullInput(0);

		fr = new Frame();
		Button but = new Button("Push");
		but.addActionListener(this);
		fr.add(but);
		fr.pack();
		fr.show();
	}

	public void actionPerformed(ActionEvent e){
		pushOutput(obj, 0);
		fr.dispose();
	}
		
}
