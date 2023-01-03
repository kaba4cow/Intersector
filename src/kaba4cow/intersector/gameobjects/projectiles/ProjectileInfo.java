package kaba4cow.intersector.gameobjects.projectiles;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Matrices;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.files.ProjectileFile;
import kaba4cow.intersector.gameobjects.machines.Machine;
import kaba4cow.intersector.gameobjects.objectcomponents.WeaponComponent;

public class ProjectileInfo {

	public final Machine targetShip;
	public final Vector3f pos;
	public final Direction direction;
	public final float scale;
	public final float damage;
	public final ProjectileFile file;
	public final WeaponComponent weapon;
	public final int firePoint;

	public ProjectileInfo(Machine targetShip, Vector3f pos,
			Direction direction, WeaponComponent weapon, int firePoint) {
		this.targetShip = targetShip;
		this.pos = pos;
		this.direction = direction;
		this.weapon = weapon;
		this.firePoint = firePoint;
		this.scale = weapon.weaponFile.getSize() * weapon.weaponFile.getScale();
		this.damage = weapon.weaponFile.getDamage()
				* (1f + weapon.weaponFile.getDamageDeviation()
						* RNG.randomFloat(-1f, 1f));
		this.file = weapon.weaponFile.getProjectileFile();
	}

	public ProjectileInfo(Machine targetShip, Vector3f pos,
			Direction direction, WeaponComponent weapon, ProjectileFile file,
			float scale, float damage) {
		this.targetShip = targetShip;
		this.pos = new Vector3f(pos);
		this.direction = direction.copy();
		this.weapon = weapon;
		this.firePoint = 0;
		this.scale = scale;
		this.damage = damage;
		this.file = file;
	}

	private static final List<ProjectileInfo> tempList = new ArrayList<ProjectileInfo>();
	private static final Direction tempFireDirection = new Direction();
	private static final Matrix4f tempDirectionMatrix = new Matrix4f();
	private static final Matrix4f tempDynamicMatrix = new Matrix4f();
	private static final Matrix4f tempCurrentDynamicMatrix = new Matrix4f();
	private static final Vector3f tempOriginPoint = new Vector3f();
	private static final Vector3f tempWeaponSize = new Vector3f();
	private static final Vector3f tempFirePoint = new Vector3f();

	public static List<ProjectileInfo> calculateList(WeaponComponent weapon,
			Machine holder, Machine targetShip, Matrix4f shipMatrix) {
		tempList.clear();

		Matrices.set(tempDirectionMatrix,
				weapon.direction.getMatrix(null, true));
		tempOriginPoint.set(weapon.weaponFile.getOriginPoint());
		Vectors.set(tempWeaponSize,
				weapon.weaponFile.getSize() / holder.getSize());

		tempDynamicMatrix.setZero().setIdentity();
		tempDynamicMatrix.translate(weapon.pos);
		tempDynamicMatrix.scale(tempWeaponSize);
		if (weapon.canRotateYaw()) {
			Matrix4f.rotate(weapon.yaw, weapon.direction.getUp(),
					tempDynamicMatrix, tempDynamicMatrix);
		}
		if (weapon.canRotatePitch()) {
			tempDynamicMatrix.translate(tempOriginPoint);
			Matrix4f.rotate(weapon.pitch, weapon.direction.getRight(),
					tempDynamicMatrix, tempDynamicMatrix);
			tempDynamicMatrix.translate(tempOriginPoint.negate(null));
		}
		tempFireDirection.set(weapon.fireDirection);
		if (weapon.canRotateYaw())
			tempFireDirection.rotate(tempFireDirection.getUp(), weapon.yaw);
		if (weapon.canRotatePitch())
			tempFireDirection
					.rotate(tempFireDirection.getRight(), weapon.pitch);

		for (int i = 0; i < weapon.weaponFile.getFirePoints(); i++) {
			tempFirePoint.set(weapon.weaponFile.getFirePoint(i));
			Matrices.transform(tempDirectionMatrix, tempFirePoint,
					tempFirePoint);

			Matrices.set(tempCurrentDynamicMatrix, tempDynamicMatrix);
			tempCurrentDynamicMatrix.translate(tempFirePoint);
			Matrix4f.mul(tempCurrentDynamicMatrix, tempDirectionMatrix,
					tempCurrentDynamicMatrix);

			Matrix4f.mul(shipMatrix, tempCurrentDynamicMatrix,
					tempCurrentDynamicMatrix);
			Matrices.getTranslation(tempCurrentDynamicMatrix, tempFirePoint);

			Vectors.addScaled(tempFirePoint, holder.getPos(), 2f, tempFirePoint);

			tempList.add(new ProjectileInfo(targetShip, tempFirePoint,
					tempFireDirection, weapon, i));
		}
		return tempList;
	}

	public static ProjectileInfo calculate(WeaponComponent weapon, int index,
			Machine holder, Machine targetShip, Matrix4f shipMatrix) {
		Matrices.set(tempDirectionMatrix,
				weapon.direction.getMatrix(null, true));
		tempOriginPoint.set(weapon.weaponFile.getOriginPoint());
		Vectors.set(tempWeaponSize,
				weapon.weaponFile.getSize() / holder.getSize());

		tempFireDirection.set(weapon.fireDirection);
		if (weapon.canRotateYaw())
			tempFireDirection.rotate(tempFireDirection.getUp(), weapon.yaw);
		if (weapon.canRotatePitch())
			tempFireDirection
					.rotate(tempFireDirection.getRight(), weapon.pitch);
		tempFirePoint.set(weapon.weaponFile.getFirePoint(index));

		Matrices.transform(tempDirectionMatrix, tempFirePoint, tempFirePoint);

		tempDynamicMatrix.setZero().setIdentity();
		tempDynamicMatrix.translate(weapon.pos);
		tempDynamicMatrix.scale(tempWeaponSize);
		if (weapon.canRotateYaw()) {
			Matrix4f.rotate(weapon.yaw, weapon.direction.getUp(),
					tempDynamicMatrix, tempDynamicMatrix);
		}
		if (weapon.canRotatePitch()) {
			tempDynamicMatrix.translate(tempOriginPoint);
			Matrix4f.rotate(weapon.pitch, weapon.direction.getRight(),
					tempDynamicMatrix, tempDynamicMatrix);
			tempDynamicMatrix.translate(tempOriginPoint.negate(null));
		}
		tempDynamicMatrix.translate(tempFirePoint);
		Matrix4f.mul(tempDynamicMatrix, tempDirectionMatrix, tempDynamicMatrix);

		Matrix4f.mul(shipMatrix, tempDynamicMatrix, tempDynamicMatrix);
		Matrices.getTranslation(tempDynamicMatrix, tempFirePoint);

		Vectors.addScaled(tempFirePoint, holder.getPos(), 2f, tempFirePoint);

		return new ProjectileInfo(targetShip, tempFirePoint, tempFireDirection,
				weapon, index);
	}

}
