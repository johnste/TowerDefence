package net.kassett.towerdefence.game.utils;

import java.awt.Point;

import net.kassett.towerdefence.game.objects.characters.PathableGameObject;

public interface PointValidator {

	PathableGameObject getOwner();

	boolean isValidPoint(Point from, Point to);

}
