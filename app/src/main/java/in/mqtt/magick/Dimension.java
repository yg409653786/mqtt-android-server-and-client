/*
 * Copyright 1995-2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package in.mqtt.magick;


public class Dimension implements java.io.Serializable {

    /**
     * The width dimension; negative values can be used.
     *
     * @serial
     * @see #getSize
     * @see #setSize
     * @since 1.0
     */
    public int width;

    /**
     * The height dimension; negative values can be used.
     *
     * @serial
     * @see #getSize
     * @see #setSize
     * @since 1.0
     */
    public int height;

    /*
     * JDK 1.1 serialVersionUID
     */
     private static final long serialVersionUID = 4723952579491349524L;

    /**
     * Creates an instance of <code>Dimension</code> with a width
     * of zero and a height of zero.
     */
    public Dimension() {
        this(0, 0);
    }

    /**
     * Creates an instance of <code>Dimension</code> whose width
     * and height are the same as for the specified dimension.
     *
     * @param    d   the specified dimension for the
     *               <code>width</code> and
     *               <code>height</code> values
     */
    public Dimension(Dimension d) {
        this(d.width, d.height);
    }

    /**
     * Constructs a <code>Dimension</code> and initializes
     * it to the specified width and specified height.
     *
     * @param width the specified width
     * @param height the specified height
     */
    public Dimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }


    public double getHeight() {
        return height;
    }


    public void setSize(double width, double height) {
        this.width = (int) Math.ceil(width);
        this.height = (int) Math.ceil(height);
    }


    public Dimension getSize() {
        return new Dimension(width, height);
    }


    public void setSize(Dimension d) {
        setSize(d.width, d.height);
    }


    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }


    public boolean equals(Object obj) {
        if (obj instanceof Dimension) {
            Dimension d = (Dimension)obj;
            return (width == d.width) && (height == d.height);
        }
        return false;
    }


    public int hashCode() {
        int sum = width + height;
        return sum * (sum + 1)/2 + width;
    }


    public String toString() {
        return getClass().getName() + "[width=" + width + ",height=" + height + "]";
    }
}
