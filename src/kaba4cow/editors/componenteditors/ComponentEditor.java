package kaba4cow.editors.componenteditors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JSpinner;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.intersector.gameobjects.objectcomponents.ObjectComponent;

public interface ComponentEditor {

	public default JButton createRotateButton(float rotation,
			Direction.Coordinate coordinate, ComponentEditorSettings settings) {
		String label = (rotation < 0 ? "-" : "+") + Maths.abs(rotation) + "deg";
		JButton button = new JButton(label);
		button.setToolTipText(label);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ObjectComponent component = getRotatableComponent();
				if (component != null) {
					boolean local = settings.isRotateLocal();
					boolean aroundCenter = settings.isRotateAroundCenter();
					component.rotate(coordinate, Maths.toRadians(rotation),
							local, aroundCenter);
					Vector3f pos = component.getPos();
					getComponentXSpinner().setValue(pos.x * getMul());
					getComponentYSpinner().setValue(pos.y * getMul());
					getComponentZSpinner().setValue(pos.z * getMul());
				}
			}
		});
		return button;
	}

	public default JButton createApplyRotationButton(JSpinner rotSpinner,
			Direction.Coordinate coordinate, ComponentEditorSettings settings) {
		JButton button = new JButton("APPLY");
		button.setToolTipText("apply");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ObjectComponent component = getRotatableComponent();
				if (component != null) {
					float rotation = EditorUtils.getFloatValue(rotSpinner);
					boolean local = settings.isRotateLocal();
					boolean aroundCenter = settings.isRotateAroundCenter();
					component.rotate(coordinate, Maths.toRadians(rotation),
							local, aroundCenter);
					Vector3f pos = component.getPos();
					getComponentXSpinner().setValue(pos.x * getMul());
					getComponentYSpinner().setValue(pos.y * getMul());
					getComponentZSpinner().setValue(pos.z * getMul());
					rotSpinner.setValue(0f);
				}
			}
		});
		return button;
	}

	public default JButton createResetRotationsButton() {
		JButton button = new JButton("Reset Rotations");
		button.setToolTipText("reset");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ObjectComponent component = getRotatableComponent();
				if (component != null)
					component.resetRotations();
			}
		});
		return button;
	}

	public ObjectComponent getRotatableComponent();

	public JSpinner getComponentXSpinner();

	public JSpinner getComponentYSpinner();

	public JSpinner getComponentZSpinner();

	public float getMul();
}
