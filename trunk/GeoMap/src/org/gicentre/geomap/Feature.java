package org.gicentre.geomap;

// *****************************************************************************************
/** Identifies the behaviour of all features.
 *  @author Iain Dillingham and Jo Wood, giCentre, City University London.
 *  @version 1.0, 8th January, 2012
 */
// *****************************************************************************************

/* This file is part of giCentre's geoMap library. geoMap is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * geoMap is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * source code (see COPYING.LESSER included with this source code). If not, see
 * http://www.gnu.org/licenses/.
 */
public interface Feature
{
	/** Should report the type of feature.
	 *  @return Type of feature
	 */
	public abstract FeatureType getType();
	
    /** Should draw the feature in the parent sketch.
     *  @param transformer Class that will trasform between geographic and screen coordinates.
     */
    public abstract void draw(Geographic transformer);
    
    /** Should report the number of vertices that make up the feature definition.
     *  @return number of vertices that make up the feature.
     */
    public abstract int getNumVertices();
    
    /** Should tests whether the given point is contained within the feature. The definition
     *  of 'contains' will depend on what type of feature is being tested. Coordinates should
     *  be in the same geographic units as the feature.
     *  @param x x coordinate in geographic coordinates.
     *  @param y y coordinate in geographic coordinates.
     *  @return True if the given point is contained within the feature, false if not.
     */
    public abstract boolean contains(float x, float y);
}