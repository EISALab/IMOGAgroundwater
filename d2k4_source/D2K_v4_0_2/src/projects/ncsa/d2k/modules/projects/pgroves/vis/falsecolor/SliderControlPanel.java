package ncsa.d2k.modules.projects.pgroves.vis.falsecolor;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

/**
	contains the sliders that set the max and min of the color range.
	responsible for updating the other components when they change
*/
public class SliderControlPanel extends JPanel
	implements ChangeListener{

	/** swing object controlling the minimum */
	JSlider minSlider;
	/** swing object controlling the maximum*/
	JSlider maxSlider;

	/** who we belong to, who we tell our new values to */
	FalseColorPanel parent;

	/**
		when a change is made to the maximum or minimum value the slider
		can take on, what fraction of the current range to change it
		by
		*/
	double fracChange = .05;
	/**
		the time period in milliseconds between increments of the max and
		min values when a slider is pushed to one end
		*/
	long waitTime = 250;
	
	public SliderControlPanel(FalseColorPanel fcp){

		parent = fcp;

		minSlider = new JSlider(SwingConstants.VERTICAL, 0, 100, 1);
		minSlider.setValue(0);
		minSlider.setPaintLabels(false);
		maxSlider = new JSlider(SwingConstants.VERTICAL, 0, 100, 1);
		maxSlider.setValue(100);
		maxSlider.setPaintLabels(false);
		maxSlider.addChangeListener(this);
		minSlider.addChangeListener(this);

		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.add(minSlider);
		this.add(maxSlider);
	}

	public void stateChanged(ChangeEvent e) {

		JSlider source = (JSlider)e.getSource();

		if (!source.getValueIsAdjusting()) {

			if( source == minSlider){
				parent.setSliderMin(minSlider.getValue());
			}else if (source == maxSlider){
				parent.setSliderMax(maxSlider.getValue());
			}else{
				System.out.println("SliderControls stateChanged error");
			}
		}else if(source.getValue() == 100 || source.getValue() == 0){
			//if pushed to the ends, add or subtract 5% every third of
			//a second
			while(source.getValueIsAdjusting()){
				System.out.println("Inside loop");
				source.setValueIsAdjusting(false);
				/*try{
					this.wait(waitTime);
				}catch(Exception exc){
					System.out.println("Slider Range time delay failed");
					exc.printStackTrace();
				}*/
					
					double oldMin = parent.getUserImageMin();
					double oldMax = parent.getUserImageMax();
					double oldRange = oldMax - oldMin;
					double changeIncrement = this.fracChange * oldRange;
					
					if(source == minSlider){
						if(source.getValue() < 50){
							parent.setUserImageMin(oldMin - changeIncrement);
							parent.setSliderMin(2);
							source.setValue(2);
							System.out.println("Lower Min");
						}else if(source.getValue() > 50){
							parent.setUserImageMin(oldMin + changeIncrement);
							parent.setSliderMin(98);
							source.setValue(98);
							System.out.println("Increase Min");
						}
							
					}else if (source == maxSlider){
						if(source.getValue() < 50){
							parent.setUserImageMax(oldMax - changeIncrement);
							parent.setSliderMax(2);
							source.setValue(2);
							System.out.println("Lower Max");
						}else if(source.getValue() > 50){
							parent.setUserImageMax(oldMax + changeIncrement);
							parent.setSliderMax(98);
							source.setValue(98);
							System.out.println("Increase Max");
						}
					}
				}
			}

		
	}

}//Slidercontrols

