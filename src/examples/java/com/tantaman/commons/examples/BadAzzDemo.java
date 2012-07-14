/*
 * Copyright 2011 Matt Crinklaw-Vogt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.tantaman.commons.examples;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Collection;
import java.util.LinkedList;

import com.tantaman.commons.collections.BadAzz;
import com.tantaman.commons.examples.BadAzzDemo.Layer;

/**
 * BadAzz is a wrapper around a homogeneous collection that invokes a given
 * function on all members of the collection.
 * @author tantaman
 *
 */
public class BadAzzDemo {
	public static void main(String[] args) {
		Collection<Layer> layers = new LinkedList<Layer>();
		
		layers.add(new DemoLayer());
		layers.add(new DemoLayer());
		layers.add(new DemoLayer());
		
		Layer $layers = BadAzz.create(layers, Layer.class);
		
		$layers.clip(null);
		$layers.paint(null);
		$layers.resize(new Dimension(800, 600));
	}
	


	 public static interface Layer {
		public void paint(Graphics2D g2d);
		public void resize(Dimension newSize);
		public void clip(Path2D clippingRegion);
	}
}

class DemoLayer implements Layer {
	private static int layerNum = 0;
	private final int mLayerNum;
	
	public DemoLayer() {
		mLayerNum = ++layerNum;
	}
	
	@Override
	public void clip(Path2D clippingRegion) {
		System.out.println("Clipping layer: " + mLayerNum);
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		System.out.println("Painting layer: " + mLayerNum);
	}
	
	@Override
	public void resize(Dimension newSize) {
		System.out.println("Resizing layer: " + mLayerNum);
	}
}